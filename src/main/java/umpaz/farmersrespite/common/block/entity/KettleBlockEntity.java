package umpaz.farmersrespite.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import umpaz.farmersrespite.common.block.KettleBlock;
import umpaz.farmersrespite.common.block.entity.container.KettleMenu;
import umpaz.farmersrespite.common.block.entity.inventory.KettleItemHandler;
import umpaz.farmersrespite.common.crafting.KettlePouringRecipe;
import umpaz.farmersrespite.common.crafting.KettleRecipe;
import umpaz.farmersrespite.common.registry.FRBlockEntityTypes;
import umpaz.farmersrespite.common.registry.FRItems;
import umpaz.farmersrespite.common.registry.FRRecipeTypes;
import umpaz.farmersrespite.common.registry.FRSounds;
import umpaz.farmersrespite.common.utility.FRTextUtils;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class KettleBlockEntity extends SyncedBlockEntity implements MenuProvider, HeatableBlockEntity, Nameable {
   public static final int WATER_BOTTLE_AMOUNT = 250;
   public static final int DRINK_DISPLAY_SLOT = 2;
   public static final int CONTAINER_SLOT = 3;
   public static final int OUTPUT_SLOT = 4;
   public static final int INVENTORY_SIZE = 5;
   private final ItemStackHandler inventory = this.createHandler();
   private final IItemHandler inputHandler = new KettleItemHandler(this.inventory, Direction.UP);
   private final IItemHandler outputHandler = new KettleItemHandler(this.inventory, Direction.DOWN);
   private final FluidTank fluidTank;
   private int brewTime;
   private int brewTimeTotal;
   private ItemStack drinkContainerStack = ItemStack.EMPTY;
   private Component customName;
   protected final ContainerData kettleData = this.createIntArray();
   private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker = new Object2IntOpenHashMap();
   private ResourceLocation lastRecipeID;

   public KettleBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)FRBlockEntityTypes.KETTLE.get(), pos, state);
      this.fluidTank = this.createFluidTank();
   }

   public static CompoundTag blockEntityData(ItemStack kettleStack) {
      CustomData data = kettleStack.get(DataComponents.BLOCK_ENTITY_DATA);
      return data == null ? new CompoundTag() : data.copyTag();
   }

   public static FluidStack getMealFromItem(ItemStack kettleStack, HolderLookup.Provider registries) {
      if (!kettleStack.is((Item)FRItems.KETTLE.get())) {
         return FluidStack.EMPTY;
      }
      CompoundTag compound = blockEntityData(kettleStack);
      return compound.contains("FluidTank") ? FluidStack.parseOptional(registries, compound.getCompound("FluidTank")) : FluidStack.EMPTY;
   }

   @Override
   public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
      super.loadAdditional(compound, registries);
      this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
      this.brewTime = compound.getInt("BrewTime");
      this.fluidTank.readFromNBT(registries, compound.getCompound("FluidTank"));
      this.brewTimeTotal = compound.getInt("BrewTimeTotal");
      this.drinkContainerStack = ItemStack.parseOptional(registries, compound.getCompound("Container"));
      if (compound.contains("CustomName", 8)) {
         this.customName = Serializer.fromJson(compound.getString("CustomName"), registries);
      }

      CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
      for (String key : compoundRecipes.getAllKeys()) {
         this.usedRecipeTracker.put(ResourceLocation.parse(key), compoundRecipes.getInt(key));
      }
   }

   public ItemStack fluidExtract(KettleBlockEntity kettle, ItemStack slotIn, ItemStack slotOut) {
      ItemStack output = ItemStack.EMPTY;
      Optional<KettlePouringRecipe> recipe = kettle.getPouringRecipe(slotIn, kettle.fluidTank.getFluid());
      boolean changed = false;
      if (recipe.isPresent() && (kettle.fluidTank.isEmpty() || kettle.fluidTank.getFluid().getFluid().isSame(recipe.get().getFluid()))) {
         if (matchesPouringContainer(slotIn, recipe.get()) && recipe.get().getAmount() <= kettle.fluidTank.getFluidAmount()) {
            for (;
               kettle.fluidTank.getFluidAmount() >= recipe.get().getAmount()
                  && (
                     output.isEmpty() && slotOut.isEmpty()
                        || ItemStack.isSameItemSameComponents(slotOut, recipe.get().getOutput().copyWithCount(output.getCount() + 1))
                  );
               changed = true
            ) {
               if (slotOut.getCount() + 1 > slotOut.getMaxStackSize()
                  || slotIn.getCount() == 0
                  || slotIn.isEmpty()
                  || slotIn.getCount() < recipe.get().getOutput().getCount()) {
                  break;
               }

               kettle.fluidTank.drain(kettle.fluidTank.getFluid().copyWithAmount(recipe.get().getAmount()), FluidAction.EXECUTE);
               slotIn.shrink(recipe.get().getContainer().getCount());
               if (output.isEmpty()) {
                  output = recipe.get().getOutput().copy();
               } else {
                  output.grow(recipe.get().getOutput().getCount());
               }
            }

            if (changed) {
               if (kettle.level.isClientSide()) {
                  kettle.level.playLocalSound(kettle.getBlockPos(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               }

               this.setChanged();
            }
         } else if (matchesPouringOutput(slotIn, recipe.get())
            && kettle.fluidTank.getFluidAmount() + recipe.get().getAmount() <= kettle.fluidTank.getCapacity()) {
            for (;
               kettle.fluidTank.getFluidAmount() + recipe.get().getAmount() <= kettle.fluidTank.getCapacity()
                  && slotIn.getCount() != 0
                  && !slotIn.isEmpty()
                  && slotIn.getCount() >= recipe.get().getContainer().getCount()
                  && (
                     output.isEmpty() && slotOut.isEmpty()
                        || ItemStack.isSameItemSameComponents(slotOut, recipe.get().getContainer().copyWithCount(output.getCount() + 1))
                  );
               changed = true
            ) {
               if (slotOut.getCount() + 1 > slotOut.getMaxStackSize()) {
                  break;
               }

               kettle.fluidTank.fill(new FluidStack(recipe.get().getFluid(), recipe.get().getAmount()), FluidAction.EXECUTE);
               slotIn.shrink(recipe.get().getOutput().getCount());
               if (output.isEmpty()) {
                  output = recipe.get().getContainer().copy();
               } else {
                  output.grow(recipe.get().getContainer().getCount());
               }
            }

            if (changed) {
               if (kettle.level.isClientSide()) {
                  kettle.level.playLocalSound(kettle.getBlockPos(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               }

               this.setChanged();
            }
         }
      }

      IFluidHandlerItem iFluidItemHandler = slotIn.getCapability(Capabilities.FluidHandler.ITEM);
      if (iFluidItemHandler != null && !slotIn.isEmpty()) {
         if (kettle.fluidTank.getFluid().isFluidEqual(iFluidItemHandler.getFluidInTank(0)) || kettle.fluidTank.getFluid().isEmpty()) {
            int amountToDrain = kettle.fluidTank.getCapacity() - kettle.fluidTank.getFluidAmount();
            int amount = iFluidItemHandler.drain(amountToDrain, FluidAction.SIMULATE).getAmount();
            if (amount > 0) {
               kettle.fluidTank.fill(iFluidItemHandler.drain(amountToDrain, FluidAction.EXECUTE), FluidAction.EXECUTE);
               if (amount <= amountToDrain) {
                  slotIn.setCount(0);
                  if (output.isEmpty()) {
                     output = iFluidItemHandler.getContainer().copy();
                  } else {
                     output.grow(iFluidItemHandler.getContainer().getCount());
                  }

                  this.setChanged();
               }
            }
         } else if (!kettle.fluidTank.getFluid().isEmpty() && iFluidItemHandler.isFluidValid(0, kettle.fluidTank.getFluid())) {
            int amountToDrain = kettle.fluidTank.getFluidAmount();
            int amount = iFluidItemHandler.fill(kettle.fluidTank.getFluid().copyWithAmount(amountToDrain), FluidAction.SIMULATE);
            if (amount > 0) {
               iFluidItemHandler.fill(kettle.fluidTank.getFluid().copyWithAmount(amountToDrain), FluidAction.EXECUTE);
               kettle.fluidTank.drain(amountToDrain, FluidAction.EXECUTE);
               if (amount <= amountToDrain) {
                  slotIn.setCount(0);
                  if (output.isEmpty()) {
                     output = iFluidItemHandler.getContainer().copy();
                  } else {
                     output.grow(iFluidItemHandler.getContainer().getCount());
                  }

                  this.setChanged();
               }
            }
         }
      }

      return output;
   }

   @Override
   public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
      super.saveAdditional(compound, registries);
      compound.putInt("BrewTime", this.brewTime);
      compound.putInt("BrewTimeTotal", this.brewTimeTotal);
      compound.put("Container", this.drinkContainerStack.saveOptional(registries));
      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, registries));
      }

      compound.put("FluidTank", this.fluidTank.writeToNBT(registries, new CompoundTag()));
      compound.put("Inventory", this.inventory.serializeNBT(registries));
      CompoundTag compoundRecipes = new CompoundTag();
      this.usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
      compound.put("RecipesUsed", compoundRecipes);
   }

   private CompoundTag writeItems(CompoundTag compound, HolderLookup.Provider registries) {
      super.saveAdditional(compound, registries);
      compound.put("Container", this.drinkContainerStack.saveOptional(registries));
      compound.put("Inventory", this.inventory.serializeNBT(registries));
      compound.put("FluidTank", this.fluidTank.writeToNBT(registries, new CompoundTag()));
      return compound;
   }

   public CompoundTag writeMeal(CompoundTag compound, HolderLookup.Provider registries) {
      if (this.getOutput().isEmpty()) {
         return compound;
      }

      ItemStackHandler drops = new ItemStackHandler(5);
      for (int i = 0; i < 5; i++) {
         drops.setStackInSlot(i, i == 2 ? this.inventory.getStackInSlot(i) : ItemStack.EMPTY);
      }

      if (this.customName != null) {
         compound.putString("CustomName", Serializer.toJson(this.customName, registries));
      }

      compound.put("Container", this.drinkContainerStack.saveOptional(registries));
      compound.put("Inventory", drops.serializeNBT(registries));
      if (!this.fluidTank.isEmpty()) {
         compound.put("FluidTank", this.fluidTank.writeToNBT(registries, new CompoundTag()));
      }

      return compound;
   }

   public static void brewingTick(Level level, BlockPos pos, BlockState state, KettleBlockEntity kettle) {
      boolean isHeated = kettle.isHeated(level, pos);
      boolean didInventoryChange = false;
      if (isHeated && kettle.hasInput()) {
         Optional<RecipeHolder<KettleRecipe>> recipe = kettle.getMatchingRecipe(new RecipeWrapper(kettle.inventory));
         if (recipe.isPresent() && kettle.canBrew(recipe.get().value(), kettle)) {
            didInventoryChange = kettle.processBrewing(recipe.get(), kettle);
         } else {
            kettle.brewTime = 0;
         }
      } else if (kettle.brewTime > 0) {
         kettle.brewTime = Mth.clamp(kettle.brewTime - 2, 0, kettle.brewTimeTotal);
      }

      ItemStack out = kettle.fluidExtract(kettle, kettle.inventory.getStackInSlot(3), kettle.inventory.getStackInSlot(4));
      if (!out.isEmpty()) {
         kettle.inventory.insertItem(4, out, false);
         didInventoryChange = true;
      }

      if (didInventoryChange) {
         kettle.inventoryChanged();
      }
   }

   public Optional<KettlePouringRecipe> getPouringRecipe(ItemStack stack, FluidStack fluid) {
      if (this.level == null || stack.isEmpty()) {
         return Optional.empty();
      }

      return this.level
         .getRecipeManager()
         .getAllRecipesFor(FRRecipeTypes.KETTLE_POURING.get())
         .stream()
         .map(RecipeHolder::value)
         .filter(
            recipe -> (fluid.isEmpty() || recipe.getFluid().isSame(fluid.getFluid()))
               && (matchesPouringContainer(stack, recipe) || matchesPouringOutput(stack, recipe))
         )
         .findFirst();
   }

   public static boolean isWaterBottle(ItemStack stack) {
      return stack.is(Items.POTION) && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
   }

   private static boolean matchesPouringContainer(ItemStack stack, KettlePouringRecipe recipe) {
      return ItemStack.isSameItemSameComponents(stack, recipe.getContainer());
   }

   private static boolean matchesPouringOutput(ItemStack stack, KettlePouringRecipe recipe) {
      if (isWaterBottle(stack)) {
         return recipe.getFluid().isSame(Fluids.WATER);
      }

      return ItemStack.isSameItemSameComponents(stack, recipe.getOutput());
   }

   public boolean isValidBrewingIngredient(ItemStack stack) {
      if (this.level == null || stack.isEmpty()) {
         return false;
      }

      return this.level
         .getRecipeManager()
         .getAllRecipesFor(FRRecipeTypes.BREWING.get())
         .stream()
         .map(RecipeHolder::value)
         .flatMap(recipe -> recipe.getIngredients().stream())
         .anyMatch(ingredient -> ingredient.test(stack));
   }

   public boolean isValidPouringContainer(ItemStack stack) {
      if (this.level == null || stack.isEmpty()) {
         return false;
      }

      if (isWaterBottle(stack)) {
         return true;
      }

      if (stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
         return true;
      }

      ItemStack existing = this.inventory.getStackInSlot(CONTAINER_SLOT);
      if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
         return true;
      }

      return this.level
         .getRecipeManager()
         .getAllRecipesFor(FRRecipeTypes.KETTLE_POURING.get())
         .stream()
         .map(RecipeHolder::value)
         .anyMatch(recipe -> matchesPouringContainer(stack, recipe) || matchesPouringOutput(stack, recipe));
   }

   public int getRequiredIngredientCount(ItemStack stack) {
      if (this.level == null || stack.isEmpty()) {
         return 0;
      }

      FluidStack fluid = this.fluidTank.getFluid();
      int maxCount = 0;

      for (RecipeHolder<KettleRecipe> holder : this.level.getRecipeManager().getAllRecipesFor(FRRecipeTypes.BREWING.get())) {
         KettleRecipe recipe = holder.value();
         if (!fluid.isEmpty() && !recipe.getFluidIn().getFluid().isSame(fluid.getFluid())) {
            continue;
         }

         int matchCount = 0;
         for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.test(stack)) {
               matchCount++;
            }
         }

         if (matchCount > maxCount) {
            maxCount = matchCount;
         }
      }

      return maxCount;
   }

   public boolean needsBothIngredientSlots(ItemStack stack) {
      return this.getRequiredIngredientCount(stack) >= 2;
   }

   public static void animationTick(Level level, BlockPos pos, BlockState state, KettleBlockEntity kettle) {
      boolean i = (Boolean)state.getValue(KettleBlock.LID);
      FluidStack mealStack = kettle.getOutput();
      if (kettle.isHeated(level, pos) && i && !mealStack.isEmpty()) {
         Direction direction = (Direction)state.getValue(KettleBlock.FACING);
         Axis direction$axis = direction.getAxis();
         double d0 = pos.getX() + 0.5;
         double d1 = pos.getY() + 0.5;
         double d2 = pos.getZ() + 0.5;
         double d4 = 0.0;
         double d5 = direction$axis == Axis.X ? direction.getStepX() * 0.52 : d4;
         double d6 = 0.0;
         double d7 = direction$axis == Axis.Z ? direction.getStepZ() * 0.52 : d4;
         if (level.random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.EFFECT, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0);
         }
      }
   }

   private void playWhistleSound() {
      if (this.level == null || this.level.isClientSide()) {
         return;
      }

      BlockState state = this.getBlockState();
      if (!(Boolean)state.getValue(KettleBlock.LID) || !this.isHeated()) {
         return;
      }

      float pitch = this.level.random.nextFloat() * 0.2F + 0.9F;
      this.level.playSound(null, this.worldPosition, (SoundEvent)FRSounds.BLOCK_KETTLE_WHISTLE.get(), SoundSource.BLOCKS, 0.25F, pitch);
   }

   private Optional<RecipeHolder<KettleRecipe>> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
      if (this.level == null) {
         return Optional.empty();
      }

      if (this.lastRecipeID != null) {
         Optional<RecipeHolder<?>> cached = this.level.getRecipeManager().byKey(this.lastRecipeID);
         if (cached.isPresent() && cached.get().value() instanceof KettleRecipe kettleRecipe
            && kettleRecipe.matches(inventoryWrapper, this.level)
            && kettleRecipe.getFluidIn().isFluidEqual(this.fluidTank.getFluid())) {
            return Optional.of(new RecipeHolder<>(cached.get().id(), kettleRecipe));
         }
      }

      Optional<RecipeHolder<KettleRecipe>> recipe = this.level
         .getRecipeManager()
         .getAllRecipesFor(FRRecipeTypes.BREWING.get())
         .stream()
         .filter(holder -> holder.value().matches(inventoryWrapper, this.level)
            && holder.value().getFluidIn().getFluid().isSame(this.fluidTank.getFluid().getFluid()))
         .findFirst();
      this.lastRecipeID = recipe.map(RecipeHolder::id).orElse(null);
      return recipe;
   }

   public ItemStack getContainer() {
      return this.drinkContainerStack;
   }

   private boolean hasInput() {
      for (int i = 0; i < 2; i++) {
         if (!this.inventory.getStackInSlot(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   protected boolean canBrew(KettleRecipe recipe, KettleBlockEntity kettle) {
      if (!this.hasInput()) {
         return false;
      } else if (this.level == null) {
         return false;
      } else {
         FluidStack resultStack = recipe.getFluidOut();
         if (resultStack.isEmpty()) {
            return false;
         } else if (!kettle.fluidTank.getFluid().getFluid().isSame(recipe.getFluidIn().getFluid())) {
            return false;
         } else {
            return kettle.fluidTank.getFluidAmount() % recipe.getFluidIn().getAmount() != 0
               ? false
               : FluidStack.areFluidStackTagsEqual(this.fluidTank.getFluid(), resultStack);
         }
      }
   }

   private boolean processBrewing(RecipeHolder<KettleRecipe> holder, KettleBlockEntity kettle) {
      KettleRecipe recipe = holder.value();
      if (this.level == null) {
         return false;
      }

      this.brewTime++;
      this.brewTimeTotal = recipe.getBrewTime();
      if (this.brewTime < this.brewTimeTotal) {
         return false;
      }

      this.brewTime = 0;
      if (recipe.getFluidOut() != null) {
         kettle.fluidTank.setFluid(recipe.getFluidOut().copyWithAmount(kettle.fluidTank.getFluidAmount()));
         if (kettle.level.isClientSide()) {
            kettle.level.playLocalSound(kettle.getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 0.8F, true);
         }

         kettle.playWhistleSound();
      }

      kettle.setRecipeUsed(holder.id());

      for (int i = 0; i < 2; i++) {
         ItemStack slotStack = this.inventory.getStackInSlot(i);
         if (slotStack.hasCraftingRemainingItem()) {
            Direction direction = ((Direction)this.getBlockState().getValue(KettleBlock.FACING)).getCounterClockWise();
            double x = this.worldPosition.getX() + 0.5 + direction.getStepX() * 0.25;
            double y = this.worldPosition.getY() + 0.7;
            double z = this.worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.25;
            ItemUtils.spawnItemEntity(
               this.level,
               this.inventory.getStackInSlot(i).getCraftingRemainingItem(),
               x,
               y,
               z,
               direction.getStepX() * 0.08F,
               0.25,
               direction.getStepZ() * 0.08F
            );
         }

         if (!slotStack.isEmpty()) {
            slotStack.shrink(1);
         }
      }

      return true;
   }

   public void setRecipeUsed(@Nullable ResourceLocation recipeId) {
      if (recipeId != null) {
         this.usedRecipeTracker.addTo(recipeId, 1);
      }
   }

   public void awardUsedRecipes(Player player, List<ItemStack> items) {
      List<RecipeHolder<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.level(), player.position());
      player.awardRecipes(usedRecipes);
      this.usedRecipeTracker.clear();
   }

   public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
      List<RecipeHolder<?>> list = Lists.newArrayList();
      ObjectIterator var4 = this.usedRecipeTracker.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var4.next();
         level.getRecipeManager().byKey(entry.getKey()).ifPresent(holder -> {
            list.add(holder);
            if (holder.value() instanceof KettleRecipe kettleRecipe) {
               splitAndSpawnExperience((ServerLevel)level, pos, entry.getIntValue(), kettleRecipe.getExperience());
            }
         });
      }

      return list;
   }

   private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
      int expTotal = Mth.floor(craftedAmount * experience);
      float expFraction = Mth.frac(craftedAmount * experience);
      if (expFraction != 0.0F && Math.random() < expFraction) {
         expTotal++;
      }

      ExperienceOrb.award(level, pos, expTotal);
   }

   public boolean isHeated() {
      return this.level == null ? false : this.isHeated(this.level, this.worldPosition);
   }

   public ItemStackHandler getInventory() {
      return this.inventory;
   }

   public FluidTank getFluidTank() {
      return this.fluidTank;
   }

   public IItemHandler getItemHandler(@Nullable Direction side) {
      return side != null && side != Direction.UP ? this.outputHandler : this.inputHandler;
   }

   public FluidStack getOutput() {
      return this.fluidTank.getFluid();
   }

   public NonNullList<ItemStack> getDroppableInventory() {
      NonNullList<ItemStack> drops = NonNullList.create();

      for (int i = 0; i < 5; i++) {
         if (i != 2) {
            drops.add(this.inventory.getStackInSlot(i));
         }
      }

      return drops;
   }

   private void moveMealToOutput() {
      ItemStack mealStack = this.inventory.getStackInSlot(2);
      ItemStack outputStack = this.inventory.getStackInSlot(4);
      int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
      if (outputStack.isEmpty()) {
         this.inventory.setStackInSlot(4, mealStack.split(mealCount));
      } else if (outputStack.getItem() == mealStack.getItem()) {
         mealStack.shrink(mealCount);
         outputStack.grow(mealCount);
      }
   }

   public Component getName() {
      return (Component)(this.customName != null ? this.customName : FRTextUtils.getTranslation("container.kettle"));
   }

   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   public void setCustomName(Component name) {
      this.customName = name;
   }

   public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
      return new KettleMenu(id, player, this, this.kettleData);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
      return this.writeItems(new CompoundTag(), registries);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(5) {
         @Override
         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
               case 0, 1 -> KettleBlockEntity.this.isValidBrewingIngredient(stack);
               case 3 -> KettleBlockEntity.this.isValidPouringContainer(stack);
               case 4 -> true;
               default -> false;
            };
         }

         @Override
         public int getSlotLimit(int slot) {
            return slot < 2 ? 1 : super.getSlotLimit(slot);
         }

         @Nonnull
         @Override
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemStack remaining = super.insertItem(slot, stack, simulate);
            if (slot < 2 && !remaining.isEmpty() && KettleBlockEntity.this.needsBothIngredientSlots(stack)) {
               int otherSlot = slot == 0 ? 1 : 0;
               ItemStack otherStack = this.getStackInSlot(otherSlot);
               if ((otherStack.isEmpty() || ItemStack.isSameItemSameComponents(otherStack, stack)) && this.isItemValid(otherSlot, stack)) {
                  remaining = super.insertItem(otherSlot, remaining, simulate);
               }
            }

            return remaining;
         }

         protected void onContentsChanged(int slot) {
            if (slot >= 0 && slot < 2) {
               KettleBlockEntity.this.lastRecipeID = null;
            }

            KettleBlockEntity.this.inventoryChanged();
         }
      };
   }

   private FluidTank createFluidTank() {
      return new FluidTank(1000) {
         protected void onContentsChanged() {
            super.onContentsChanged();
            KettleBlockEntity.this.lastRecipeID = null;
            KettleBlockEntity.this.setChanged();
            KettleBlockEntity.this.inventoryChanged();
         }
      };
   }

   private ContainerData createIntArray() {
      return new ContainerData() {
         public int get(int index) {
            return switch (index) {
               case 0 -> KettleBlockEntity.this.brewTime;
               case 1 -> KettleBlockEntity.this.brewTimeTotal;
               default -> 0;
            };
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  KettleBlockEntity.this.brewTime = value;
                  break;
               case 1:
                  KettleBlockEntity.this.brewTimeTotal = value;
            }
         }

         public int getCount() {
            return 2;
         }
      };
   }
}
