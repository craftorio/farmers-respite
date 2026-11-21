package umpaz.farmersrespite.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import umpaz.farmersrespite.common.registry.FREffects;

public class CaffeinatedEffect extends MobEffect {
   public CaffeinatedEffect() {
      super(MobEffectCategory.BENEFICIAL, 3348480);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("farmersrespite", "caffeinated_speed"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("farmersrespite", "caffeinated_attack_speed"), 0.2, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   @Override
   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (this == FREffects.CAFFEINATED.get() && entity instanceof ServerPlayer playerMP) {
         StatsCounter statisticsManager = playerMP.getStats();
         statisticsManager.increment(playerMP, Stats.CUSTOM.get(Stats.TIME_SINCE_REST), -(24000 * (amplifier + 1)));
      }
      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
