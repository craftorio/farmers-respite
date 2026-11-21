package umpaz.farmersrespite.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
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
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import umpaz.farmersrespite.common.registry.FRItems;
import umpaz.farmersrespite.common.registry.FRRecipeSerializers;
import umpaz.farmersrespite.common.registry.FRRecipeTypes;

public class KettleRecipe implements Recipe<RecipeWrapper> {
   public static final int INPUT_SLOTS = 2;
   public static final Codec<FluidStack> FLUID_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            Codec.INT.optionalFieldOf("count", 1000).forGetter(FluidStack::getAmount)
         )
         .apply(instance, FluidStack::new)
   );
   public static final MapCodec<KettleRecipe> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(KettleRecipe::getIngredients),
            FLUID_CODEC.fieldOf("base").forGetter(KettleRecipe::getFluidIn),
            FLUID_CODEC.fieldOf("result").forGetter(KettleRecipe::getFluidOut),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(KettleRecipe::getExperience),
            Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(KettleRecipe::getBrewTime)
         )
         .apply(instance, KettleRecipe::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, KettleRecipe> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
      recipe -> recipe.inputItems,
      FluidStack.STREAM_CODEC,
      KettleRecipe::getFluidIn,
      FluidStack.STREAM_CODEC,
      KettleRecipe::getFluidOut,
      ByteBufCodecs.FLOAT,
      KettleRecipe::getExperience,
      ByteBufCodecs.VAR_INT,
      KettleRecipe::getBrewTime,
      (ingredients, fluidIn, fluidOut, experience, brewTime) -> new KettleRecipe(NonNullList.copyOf(ingredients), fluidIn, fluidOut, experience, brewTime)
   );

   private final NonNullList<Ingredient> inputItems;
   private final FluidStack fluidIn;
   private final FluidStack fluidOut;
   private final float experience;
   private final int brewTime;

   public KettleRecipe(NonNullList<Ingredient> inputItems, FluidStack fluidIn, FluidStack fluidOut, float experience, int brewTime) {
      this.inputItems = inputItems;
      this.fluidIn = fluidIn;
      this.fluidOut = fluidOut;
      this.experience = experience;
      this.brewTime = brewTime;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      return this.inputItems;
   }

   public FluidStack getFluidIn() {
      return this.fluidIn;
   }

   public FluidStack getFluidOut() {
      return this.fluidOut;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider registries) {
      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider registries) {
      return ItemStack.EMPTY;
   }

   public float getExperience() {
      return this.experience;
   }

   public int getBrewTime() {
      return this.brewTime;
   }

   @Override
   public boolean matches(RecipeWrapper inv, Level level) {
      List<ItemStack> inputs = new ArrayList<>();
      int count = 0;

      for (int slot = 0; slot < INPUT_SLOTS; slot++) {
         ItemStack stack = inv.getItem(slot);
         if (!stack.isEmpty()) {
            count++;
            inputs.add(stack);
         }
      }

      return count == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.inputItems.size();
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return FRRecipeSerializers.BREWING.get();
   }

   @Override
   public RecipeType<?> getType() {
      return FRRecipeTypes.BREWING.get();
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)FRItems.KETTLE.get());
   }

   public static class Serializer implements RecipeSerializer<KettleRecipe> {
      @Override
      public MapCodec<KettleRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, KettleRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
