package umpaz.farmersrespite.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import umpaz.farmersrespite.common.registry.FRItems;
import umpaz.farmersrespite.common.registry.FRRecipeSerializers;
import umpaz.farmersrespite.common.registry.FRRecipeTypes;

public class KettlePouringRecipe implements Recipe<RecipeWrapper> {
   private static final Codec<ItemStack> ITEM_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem))
         .apply(instance, ItemStack::new)
   );
   public static final MapCodec<KettlePouringRecipe> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(KettlePouringRecipe::getFluid),
            Codec.INT.optionalFieldOf("amount", 250).forGetter(KettlePouringRecipe::getAmount),
            ITEM_CODEC.optionalFieldOf("container", ItemStack.EMPTY).forGetter(KettlePouringRecipe::getContainer),
            ITEM_CODEC.fieldOf("output").forGetter(KettlePouringRecipe::getOutput)
         )
         .apply(instance, KettlePouringRecipe::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, KettlePouringRecipe> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.registry(BuiltInRegistries.FLUID.key()),
      KettlePouringRecipe::getFluid,
      ByteBufCodecs.VAR_INT,
      KettlePouringRecipe::getAmount,
      ItemStack.STREAM_CODEC,
      KettlePouringRecipe::getContainer,
      ItemStack.OPTIONAL_STREAM_CODEC,
      KettlePouringRecipe::getOutput,
      KettlePouringRecipe::new
   );

   private final Fluid fluid;
   private final int amount;
   private final ItemStack container;
   private final ItemStack output;

   public KettlePouringRecipe(Fluid fluid, int amount, ItemStack container, ItemStack output) {
      this.fluid = fluid;
      this.amount = amount;
      this.container = container;
      this.output = output;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> ingredient = NonNullList.create();
      ingredient.add(Ingredient.of(this.container));
      return ingredient;
   }

   @Override
   public boolean matches(RecipeWrapper inv, Level level) {
      return Ingredient.of(this.container).test(inv.getItem(4));
   }

   @Override
   public ItemStack assemble(RecipeWrapper recipeWrapper, HolderLookup.Provider registries) {
      return this.output.copy();
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public ItemStack getContainer() {
      return this.container;
   }

   public ItemStack getOutput() {
      return this.output;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider registries) {
      return this.output;
   }

   public int getAmount() {
      return this.amount;
   }

   public Fluid getFluid() {
      return this.fluid;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return FRRecipeSerializers.KETTLE_POURING.get();
   }

   @Override
   public RecipeType<?> getType() {
      return FRRecipeTypes.KETTLE_POURING.get();
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)FRItems.KETTLE.get());
   }

   @Override
   public int hashCode() {
      int result = this.container.hashCode();
      result = 31 * result + this.fluid.hashCode();
      result = 31 * result + this.amount;
      return 31 * result + this.output.hashCode();
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }
      if (!(other instanceof KettlePouringRecipe that)) {
         return false;
      }
      return this.amount == that.amount
         && this.output.equals(that.output)
         && this.fluid.equals(that.fluid)
         && this.container.equals(that.container);
   }

   public static class Serializer implements RecipeSerializer<KettlePouringRecipe> {
      @Override
      public MapCodec<KettlePouringRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, KettlePouringRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
