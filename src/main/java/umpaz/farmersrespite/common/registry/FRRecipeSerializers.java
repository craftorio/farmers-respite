package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.crafting.KettlePouringRecipe;
import umpaz.farmersrespite.common.crafting.KettleRecipe;

public class FRRecipeSerializers {
   public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "farmersrespite");
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BREWING = RECIPE_SERIALIZERS.register("brewing", KettleRecipe.Serializer::new);
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> KETTLE_POURING = RECIPE_SERIALIZERS.register("kettle_pouring", KettlePouringRecipe.Serializer::new);
}
