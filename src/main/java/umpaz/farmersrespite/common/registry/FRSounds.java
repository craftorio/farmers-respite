package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FRSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "farmersrespite");
   public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_KETTLE_WHISTLE = SOUNDS.register(
      "block.kettle.whistle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersrespite", "block.kettle.whistle"))
   );
}
