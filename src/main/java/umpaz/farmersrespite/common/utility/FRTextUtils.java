package umpaz.farmersrespite.common.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FRTextUtils {
   public static MutableComponent getTranslation(String key, Object... args) {
      return Component.translatable("farmersrespite." + key, args);
   }
}
