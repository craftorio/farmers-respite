package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.effect.CaffeinatedEffect;

public class FREffects {
   public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "farmersrespite");
   public static final DeferredHolder<MobEffect, MobEffect> CAFFEINATED = EFFECTS.register("caffeinated", CaffeinatedEffect::new);
}
