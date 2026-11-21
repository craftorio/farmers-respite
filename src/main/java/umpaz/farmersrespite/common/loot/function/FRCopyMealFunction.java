package umpaz.farmersrespite.common.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import umpaz.farmersrespite.common.block.entity.KettleBlockEntity;
import umpaz.farmersrespite.common.registry.FRLootFunctions;

public class FRCopyMealFunction extends LootItemConditionalFunction {
   public static final MapCodec<FRCopyMealFunction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance).apply(instance, FRCopyMealFunction::new)
   );

   private FRCopyMealFunction(List conditions) {
      super(conditions);
   }

   public FRCopyMealFunction(LootItemCondition[] conditions) {
      super(List.of(conditions));
   }

   @Override
   protected ItemStack run(ItemStack stack, LootContext context) {
      BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
      if (tile instanceof KettleBlockEntity kettle) {
         CompoundTag tag = kettle.writeMeal(new CompoundTag(), context.getLevel().registryAccess());
         if (!tag.isEmpty()) {
            CustomData existing = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            CompoundTag merged = existing.copyTag();
            merged.merge(tag);
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(merged));
         }
      }
      return stack;
   }

   @Override
   public LootItemFunctionType<FRCopyMealFunction> getType() {
      return FRLootFunctions.COPY_MEAL.get();
   }
}
