package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.screen.menu.BasicGeneratorMenu;
import net.bigmangohead.crystalworks.screen.menu.CrusherMenu;
import net.bigmangohead.crystalworks.screen.menu.PlateFormerMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CrystalWorksMod.MOD_ID);

    public static final RegistryObject<MenuType<BasicGeneratorMenu>> BASIC_GENERATOR_MENU =
            registerMenuType("basic_generator_menu", BasicGeneratorMenu::new);
    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER_MENU =
            registerMenuType("crusher_menu", CrusherMenu::new);
    public static final RegistryObject<MenuType<PlateFormerMenu>> PLATE_FORMER_MENU =
            registerMenuType("plate_former_menu", PlateFormerMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
