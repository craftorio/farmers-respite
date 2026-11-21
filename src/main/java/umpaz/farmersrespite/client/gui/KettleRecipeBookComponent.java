package umpaz.farmersrespite.client.gui;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import umpaz.farmersrespite.common.utility.FRTextUtils;

public class KettleRecipeBookComponent extends RecipeBookComponent {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
   );

   @Override
   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   public void hide() {
      this.setVisible(false);
   }

   @Nonnull
   @Override
   protected Component getRecipeFilterName() {
      return FRTextUtils.getTranslation("container.recipe_book.brewable");
   }

   @Override
   public void setupGhostRecipe(RecipeHolder<?> holder, List<Slot> slots) {
      ItemStack resultStack = holder.value().getResultItem(this.minecraft.level.registryAccess());
      this.ghostRecipe.setRecipe(holder);
      if (slots.get(2).getItem().isEmpty()) {
         this.ghostRecipe.addIngredient(Ingredient.of(resultStack), slots.get(2).x, slots.get(2).y);
      }

      int slot = 0;
      for (Ingredient ingredient : holder.value().getIngredients()) {
         if (slot >= 2) {
            break;
         }
         if (!ingredient.isEmpty()) {
            this.ghostRecipe.addIngredient(ingredient, slots.get(slot).x, slots.get(slot).y);
            slot++;
         }
      }
   }
}
