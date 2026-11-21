package umpaz.farmersrespite.common.registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import umpaz.farmersrespite.common.block.entity.container.KettleMenu;

public class FRMenuTypes {
   public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, "farmersrespite");
   public static final DeferredHolder<MenuType<?>, MenuType<KettleMenu>> KETTLE = MENU_TYPES.register("kettle", () -> IMenuTypeExtension.create(KettleMenu::new));
}
