package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.utility.FRTextUtils;

@EventBusSubscriber(modid = "farmersrespite", bus = Bus.MOD)
public class FRCreativeTab {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "farmersrespite");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FARMERS_RESPITE_TAB = TABS.register(
      "main",
      () -> CreativeModeTab.builder().title(FRTextUtils.getTranslation("itemGroup.main")).icon(((Item)FRItems.KETTLE.get())::getDefaultInstance).build()
   );

   @SubscribeEvent
   public static void buildContents(BuildCreativeModeTabContentsEvent event) {
      buildMainTabContents(event);
      buildFoodAndDrinksTabContents(event);
      buildFunctionalBlocksContents(event);
      buildBuildingBlocksContents(event);
   }

   private static void buildMainTabContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == FARMERS_RESPITE_TAB.get()) {
         acceptBlocks(event);
         acceptItems(event);
      }
   }

   private static void buildFoodAndDrinksTabContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
         acceptFoodAndDrinkItems(event);
      }
   }

   private static void buildFunctionalBlocksContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
         acceptFunctionalBlocks(event);
      }
   }

   private static void buildBuildingBlocksContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
         acceptBuildingBlocks(event);
      }
   }

   private static void acceptBlocks(BuildCreativeModeTabContentsEvent event) {
      acceptFunctionalBlocks(event);
      acceptBuildingBlocks(event);
      acceptNaturalBlocks(event);
   }

   private static void acceptFunctionalBlocks(BuildCreativeModeTabContentsEvent event) {
      event.accept(FRItems.KETTLE.get());
   }

   private static void acceptBuildingBlocks(BuildCreativeModeTabContentsEvent event) {
      event.accept(FRItems.GREEN_TEA_LEAVES_SACK.get());
      event.accept(FRItems.YELLOW_TEA_LEAVES_SACK.get());
      event.accept(FRItems.BLACK_TEA_LEAVES_SACK.get());
      event.accept(FRItems.COFFEE_BEANS_SACK.get());
   }

   private static void acceptNaturalBlocks(BuildCreativeModeTabContentsEvent event) {
      event.accept(FRItems.WILD_TEA_BUSH.get());
      event.accept(FRItems.WILD_COFFEE_BUSH.get());
   }

   private static void acceptItems(BuildCreativeModeTabContentsEvent event) {
      acceptNaturalItems(event);
      acceptFoodAndDrinkItems(event);
   }

   private static void acceptNaturalItems(BuildCreativeModeTabContentsEvent event) {
      event.accept(FRItems.GREEN_TEA_LEAVES.get());
      event.accept(FRItems.YELLOW_TEA_LEAVES.get());
      event.accept(FRItems.BLACK_TEA_LEAVES.get());
      event.accept(FRItems.TEA_SEEDS.get());
      event.accept(FRItems.COFFEE_BERRIES.get());
      event.accept(FRItems.COFFEE_BEANS.get());
      event.accept(FRItems.ROSE_HIPS.get());
   }

   private static void acceptFoodAndDrinkItems(BuildCreativeModeTabContentsEvent event) {
      if (!event.getParentEntries().contains(((Item)FRItems.COFFEE_BERRIES.get()).getDefaultInstance())) {
         event.accept(FRItems.COFFEE_BERRIES.get());
      }

      event.accept(FRItems.COFFEE_CAKE.get());
      event.accept(FRItems.COFFEE_CAKE_SLICE.get());
      event.accept(FRItems.ROSE_HIP_PIE.get());
      event.accept(FRItems.ROSE_HIP_PIE_SLICE.get());
      event.accept(FRItems.GREEN_TEA.get());
      event.accept(FRItems.YELLOW_TEA.get());
      event.accept(FRItems.BLACK_TEA.get());
      event.accept(FRItems.ROSE_HIP_TEA.get());
      event.accept(FRItems.DANDELION_TEA.get());
      event.accept(FRItems.PURULENT_TEA.get());
      event.accept(FRItems.GAMBLERS_TEA.get());
      event.accept(FRItems.COFFEE.get());
      event.accept(FRItems.LONG_GREEN_TEA.get());
      event.accept(FRItems.LONG_YELLOW_TEA.get());
      event.accept(FRItems.LONG_BLACK_TEA.get());
      event.accept(FRItems.LONG_ROSE_HIP_TEA.get());
      event.accept(FRItems.LONG_DANDELION_TEA.get());
      event.accept(FRItems.LONG_PURULENT_TEA.get());
      event.accept(FRItems.LONG_GAMBLERS_TEA.get());
      event.accept(FRItems.LONG_COFFEE.get());
      event.accept(FRItems.LONG_APPLE_CIDER.get());
      event.accept(FRItems.STRONG_GREEN_TEA.get());
      event.accept(FRItems.STRONG_YELLOW_TEA.get());
      event.accept(FRItems.STRONG_BLACK_TEA.get());
      event.accept(FRItems.STRONG_ROSE_HIP_TEA.get());
      event.accept(FRItems.STRONG_PURULENT_TEA.get());
      event.accept(FRItems.STRONG_GAMBLERS_TEA.get());
      event.accept(FRItems.STRONG_COFFEE.get());
      event.accept(FRItems.STRONG_APPLE_CIDER.get());
      event.accept(FRItems.STRONG_MELON_JUICE.get());
      event.accept(FRItems.STRONG_HOT_COCOA.get());
      event.accept(FRItems.GREEN_TEA_COOKIE.get());
      event.accept(FRItems.NETHER_WART_SOURDOUGH.get());
      event.accept(FRItems.BLACK_COD.get());
      event.accept(FRItems.TEA_CURRY.get());
      event.accept(FRItems.BLAZING_CHILI.get());
   }
}
