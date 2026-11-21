package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import umpaz.farmersrespite.data.worldgen.features.CoffeeBushFeature;

public class FRWorldgenFeatures {
   public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "farmersrespite");
   public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> COFFEE = FEATURES.register(
      "coffee_bush", () -> new CoffeeBushFeature(NoneFeatureConfiguration.CODEC)
   );
}
