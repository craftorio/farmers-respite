package umpaz.farmersrespite.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import umpaz.farmersrespite.client.gui.KettleScreen;
import umpaz.farmersrespite.client.gui.KettleTooltip;
import umpaz.farmersrespite.client.recipebook.FRRecipeCategories;
import umpaz.farmersrespite.common.registry.FRBlocks;
import umpaz.farmersrespite.common.registry.FRMenuTypes;

@EventBusSubscriber(modid = "farmersrespite", bus = Bus.MOD, value = Dist.CLIENT)
public class FRClientSetup {
   @SubscribeEvent
   public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
      FRRecipeCategories.init(event);
   }

   @SubscribeEvent
   public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
      BlockColor teaBushColor = (state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : FoliageColor.getDefaultColor();
      event.register(
         teaBushColor,
         new net.minecraft.world.level.block.Block[]{
            (net.minecraft.world.level.block.Block)FRBlocks.WILD_TEA_BUSH.get(), (net.minecraft.world.level.block.Block)FRBlocks.POTTED_WILD_TEA_BUSH.get()
         }
      );
   }

   @SubscribeEvent
   public static void registerCustomTooltipRenderers(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(KettleTooltip.KettleTooltipComponent.class, KettleTooltip::new);
   }

   @SubscribeEvent
   public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(FRMenuTypes.KETTLE.get(), KettleScreen::new);
   }
}
