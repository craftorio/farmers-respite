package umpaz.farmersrespite.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class FRDamageTypes {
   public static final ResourceKey<DamageType> WITHER_ROOT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("farmersrespite", "wither_root"));

   public static void bootstrap(BootstrapContext<DamageType> context) {
      context.register(WITHER_ROOT, new DamageType("farmersrespite.wither_root", 0.0F));
   }
}
