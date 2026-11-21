package umpaz.farmersrespite.common.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import umpaz.farmersrespite.common.block.entity.KettleBlockEntity;
import umpaz.farmersrespite.common.registry.FRBlocks;

@EventBusSubscriber(modid = "farmersrespite", bus = Bus.MOD)
public class FRDispenseItemBehaviour {
   private static void replacePotionBehaviour() {
      DispenseItemBehavior newPotionBehaviour = new OptionalDispenseItemBehavior() {
         private final DispenseItemBehavior defaultPotionBehaviour = (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(Items.POTION);

         public ItemStack execute(BlockSource source, ItemStack stack) {
            ServerLevel level = source.level();
            BlockPos pos = source.pos();
            BlockPos facingPos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
            BlockState facingState = level.getBlockState(facingPos);
            if (facingState.is((Block)FRBlocks.KETTLE.get()) && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER)) {
               BlockEntity blockEntity = level.getBlockEntity(facingPos);
               if (blockEntity instanceof KettleBlockEntity kettle) {
                  var tank = kettle.getFluidTank();
                  if ((tank.isEmpty() || tank.getFluid().getFluid().isSame(Fluids.WATER))
                     && tank.getFluidAmount() + KettleBlockEntity.WATER_BOTTLE_AMOUNT <= tank.getCapacity()) {
                     this.setSuccess(true);
                     tank.fill(new FluidStack(Fluids.WATER, KettleBlockEntity.WATER_BOTTLE_AMOUNT), FluidAction.EXECUTE);
                     kettle.setChanged();
                     this.playSound(source);
                     level.playSound((Player)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                     this.playAnimation(source, (Direction)source.state().getValue(DispenserBlock.FACING));
                     return new ItemStack(Items.GLASS_BOTTLE);
                  }
               }
            }

            this.setSuccess(true);
            return this.defaultPotionBehaviour.dispense(source, stack);
         }
      };
      DispenserBlock.registerBehavior(Items.POTION, newPotionBehaviour);
   }

   private static void replaceWaterBucketBehaviour() {
      DispenseItemBehavior newWaterBucketBehaviour = new OptionalDispenseItemBehavior() {
         private final DispenseItemBehavior defaultBucketBehaviour = (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET);

         public ItemStack execute(BlockSource source, ItemStack stack) {
            ServerLevel level = source.level();
            BlockPos pos = source.pos();
            BlockPos facingPos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
            BlockState facingState = level.getBlockState(facingPos);
            if (!facingState.is((Block)FRBlocks.KETTLE.get())) {
               this.setSuccess(true);
               return this.defaultBucketBehaviour.dispense(source, stack);
            } else {
               this.setSuccess(true);
               this.playSound(source);
               level.playSound((Player)null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               this.playAnimation(source, (Direction)source.state().getValue(DispenserBlock.FACING));
               return new ItemStack(Items.BUCKET);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, newWaterBucketBehaviour);
   }

   private static void replaceDispenseItemBehaviours() {
      replacePotionBehaviour();
      replaceWaterBucketBehaviour();
   }

   @SubscribeEvent
   public static void replaceDispenseItemBehaviours(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> replaceDispenseItemBehaviours());
   }
}
