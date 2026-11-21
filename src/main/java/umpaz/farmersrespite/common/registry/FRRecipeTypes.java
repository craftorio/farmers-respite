package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.crafting.KettlePouringRecipe;
import umpaz.farmersrespite.common.crafting.KettleRecipe;

public class FRRecipeTypes {
   public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "farmersrespite");
   public static final DeferredHolder<RecipeType<?>, RecipeType<KettleRecipe>> BREWING = RECIPE_TYPES.register("brewing", () -> registerRecipeType("brewing"));
   public static final DeferredHolder<RecipeType<?>, RecipeType<KettlePouringRecipe>> KETTLE_POURING = RECIPE_TYPES.register(
      "kettle_pouring", () -> registerRecipeType("kettle_pouring")
   );

   public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
      return new RecipeType<T>() {
         @Override
         public String toString() {
            return "farmersrespite:" + identifier;
         }
      };
   }
}
