package umpaz.farmersrespite.common.registry;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import umpaz.farmersrespite.common.advancement.StuntTeaTrigger;

public class FRAdvancments {
   /** Triggers are a registry in 1.21: registered with the others, not in common setup (the registry is frozen by then). */
   public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, "farmersrespite");
   public static final StuntTeaTrigger STUNT_TEA_BUSH = new StuntTeaTrigger();

   static {
      TRIGGERS.register("stunt_tea_bush", () -> STUNT_TEA_BUSH);
   }
}
