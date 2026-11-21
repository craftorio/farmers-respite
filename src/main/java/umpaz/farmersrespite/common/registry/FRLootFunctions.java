package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import umpaz.farmersrespite.common.loot.function.FRCopyMealFunction;

public class FRLootFunctions {
   public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, "farmersrespite");
   public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<FRCopyMealFunction>> COPY_MEAL = LOOT_FUNCTIONS.register(
      "copy_meal", () -> new LootItemFunctionType<>(FRCopyMealFunction.CODEC)
   );
}
