package umpaz.farmersrespite.common.fluid;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;

public class TeaFluidType extends FluidType {
   public static final ResourceLocation FLUID_STILL_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
   public static final ResourceLocation FLUID_FLOWING_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_flow");
   private final int tintColor;

   public TeaFluidType(int tintColor) {
      super(
         Properties.create()
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
      );
      this.tintColor = tintColor;
   }

   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new IClientFluidTypeExtensions() {
         public ResourceLocation getStillTexture() {
            return TeaFluidType.FLUID_STILL_TEXTURE;
         }

         public ResourceLocation getFlowingTexture() {
            return TeaFluidType.FLUID_FLOWING_TEXTURE;
         }

         public int getTintColor() {
            return TeaFluidType.this.tintColor;
         }
      });
   }
}
