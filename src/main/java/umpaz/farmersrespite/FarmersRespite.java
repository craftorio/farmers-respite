package umpaz.farmersrespite;

import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umpaz.farmersrespite.common.FRConfiguration;
import umpaz.farmersrespite.common.registry.FRBlockEntityTypes;
import umpaz.farmersrespite.common.registry.FRBlocks;
import umpaz.farmersrespite.common.registry.FRCreativeTab;
import umpaz.farmersrespite.common.registry.FREffects;
import umpaz.farmersrespite.common.registry.FRFluids;
import umpaz.farmersrespite.common.registry.FRItems;
import umpaz.farmersrespite.common.registry.FRLootFunctions;
import umpaz.farmersrespite.common.registry.FRMenuTypes;
import umpaz.farmersrespite.common.registry.FRRecipeSerializers;
import umpaz.farmersrespite.common.registry.FRRecipeTypes;
import umpaz.farmersrespite.common.registry.FRSounds;
import umpaz.farmersrespite.common.registry.FRWorldgenFeatures;

@Mod("farmersrespite")
public class FarmersRespite {
   public static final String MODID = "farmersrespite";
   public static final Logger LOGGER = LogManager.getLogger();
   public static final RecipeBookType RECIPE_TYPE_BREWING = RecipeBookType.valueOf("FARMERSRESPITE_BREWING");

   public FarmersRespite(IEventBus modEventBus, ModContainer container) {
      container.registerConfig(ModConfig.Type.COMMON, FRConfiguration.COMMON_CONFIG);
      FREffects.EFFECTS.register(modEventBus);
      FRItems.ITEMS.register(modEventBus);
      FRBlocks.BLOCKS.register(modEventBus);
      FRFluids.FLUIDS.register(modEventBus);
      FRFluids.FLUID_TYPES.register(modEventBus);
      FRWorldgenFeatures.FEATURES.register(modEventBus);
      FRBlockEntityTypes.TILES.register(modEventBus);
      FRMenuTypes.MENU_TYPES.register(modEventBus);
      FRRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
      FRRecipeTypes.RECIPE_TYPES.register(modEventBus);
      FRLootFunctions.LOOT_FUNCTIONS.register(modEventBus);
      FRSounds.SOUNDS.register(modEventBus);
      FRCreativeTab.TABS.register(modEventBus);
      NeoForge.EVENT_BUS.register(this);
   }
}
