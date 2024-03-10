package net.bigmangohead.crystalworks;

import com.mojang.logging.LogUtils;
import net.bigmangohead.crystalworks.block.CrystalBlocks;
import net.bigmangohead.crystalworks.block.entity.ModBlockEntities;
import net.bigmangohead.crystalworks.item.ModCreativeModTabs;
import net.bigmangohead.crystalworks.recipe.ModRecipes;
import net.bigmangohead.crystalworks.screen.machine.CrusherScreen;
import net.bigmangohead.crystalworks.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import net.bigmangohead.crystalworks.item.CrystalItems;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CrystalWorksMod.MOD_ID)
public class CrystalWorksMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "crystalworks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrystalWorksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CrystalItems.register(modEventBus);
        CrystalBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        ModCreativeModTabs.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(CrystalItems.SAPPHIRE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {


            MenuScreens.register(ModMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
        }
    }
}
