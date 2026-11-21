package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.block.entity.KettleBlockEntity;

public class FRBlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "farmersrespite");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KettleBlockEntity>> KETTLE = TILES.register(
      "kettle", () -> Builder.of(KettleBlockEntity::new, new Block[]{(Block)FRBlocks.KETTLE.get()}).build(null)
   );
}
