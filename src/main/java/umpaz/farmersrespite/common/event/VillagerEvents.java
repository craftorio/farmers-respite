package umpaz.farmersrespite.common.event;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.BasicItemListing;

@ParametersAreNonnullByDefault
public class VillagerEvents {
   public static BasicItemListing emeraldForItemsTrade(ItemLike item, int count, int maxTrades, int xp) {
      return new BasicItemListing(new ItemStack(item, count), new ItemStack(Items.EMERALD), maxTrades, xp, 0.05F);
   }

   public static BasicItemListing itemForEmeraldTrade(ItemLike item, int maxTrades, int xp) {
      return new BasicItemListing(1, new ItemStack(item), maxTrades, xp, 0.05F);
   }
}
