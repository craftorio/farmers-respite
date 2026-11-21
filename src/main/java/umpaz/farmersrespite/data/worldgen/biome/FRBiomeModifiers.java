package umpaz.farmersrespite.data.worldgen.biome;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags.Biomes;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import umpaz.farmersrespite.data.worldgen.placement.FRPlacements;
import vectorwing.farmersdelight.common.world.modifier.AddFeaturesByFilterBiomeModifier;

public class FRBiomeModifiers {
   private static final ResourceKey<BiomeModifier> ADD_WILD_TEA_BUSH_FEATURE = ResourceKey.create(
      NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersrespite", "wild_tea_bush")
   );
   private static final ResourceKey<BiomeModifier> ADD_COFFEE_BUSH_FEATURE = ResourceKey.create(
      NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersrespite", "patch_coffee_bush")
   );

   public static void bootstrap(BootstrapContext<BiomeModifier> context) {
      HolderGetter<PlacedFeature> placedFeatureLookup = context.lookup(Registries.PLACED_FEATURE);
      Holder<PlacedFeature> patchWildTeaBushPlacedFeature = placedFeatureLookup.getOrThrow(FRPlacements.PATCH_WILD_TEA_BUSH);
      Holder<PlacedFeature> patchCoffeeBushPlacedFeature = placedFeatureLookup.getOrThrow(FRPlacements.PATCH_COFFEE_BUSH);
      HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
      context.register(
         ADD_WILD_TEA_BUSH_FEATURE,
         new AddFeaturesByFilterBiomeModifier(
            biomeLookup.getOrThrow(Biomes.IS_SWAMP),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{patchWildTeaBushPlacedFeature}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_COFFEE_BUSH_FEATURE,
         new AddFeaturesBiomeModifier(
            HolderSet.direct(new Holder[]{biomeLookup.getOrThrow(net.minecraft.world.level.biome.Biomes.BASALT_DELTAS)}),
            HolderSet.direct(new Holder[]{patchCoffeeBushPlacedFeature}),
            Decoration.VEGETAL_DECORATION
         )
      );
   }
}
