package umpaz.farmersrespite.common.item;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class StrongHotCocoaItem extends DrinkableItem {
   public StrongHotCocoaItem(Properties properties) {
      super(properties, false, true);
   }

   public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
      Iterator<MobEffectInstance> itr = consumer.getActiveEffects().iterator();
      ArrayList<Holder<MobEffect>> compatibleEffects = new ArrayList<>();

      while (itr.hasNext()) {
         MobEffectInstance effect = itr.next();
         if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && effect.getCures().contains(EffectCures.MILK)) {
            compatibleEffects.add(effect.getEffect());
         }
      }

      if (compatibleEffects.size() > 0) {
         Holder<MobEffect> selected = compatibleEffects.get(level.random.nextInt(compatibleEffects.size()));
         MobEffectInstance selectedEffect = consumer.getEffect(selected);
         if (selectedEffect != null && !NeoForge.EVENT_BUS.post(new MobEffectEvent.Remove(consumer, selectedEffect, EffectCures.MILK)).isCanceled()) {
            consumer.removeEffect(selected);
         }
      }

      if (compatibleEffects.size() > 1) {
         Holder<MobEffect> selected = compatibleEffects.get(level.random.nextInt(compatibleEffects.size()));
         MobEffectInstance selectedEffect = consumer.getEffect(selected);
         if (selectedEffect != null && !NeoForge.EVENT_BUS.post(new MobEffectEvent.Remove(consumer, selectedEffect, EffectCures.MILK)).isCanceled()) {
            consumer.removeEffect(selected);
         }
      }
   }
}
