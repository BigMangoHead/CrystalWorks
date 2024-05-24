package net.bigmangohead.crystalworks;

import com.mojang.logging.LogUtils;
import net.bigmangohead.crystalworks.event.ServerEvents;
import net.bigmangohead.crystalworks.network.PacketHandler;
import net.bigmangohead.crystalworks.network.packet.client.CWBlockEntityUpdateHandler;
import net.bigmangohead.crystalworks.registery.*;
import net.bigmangohead.crystalworks.screen.screen.BasicGeneratorScreen;
import net.bigmangohead.crystalworks.screen.screen.CrusherScreen;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

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

        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::specialRegistries);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register items to creative tabs
        modEventBus.addListener(this::addCreative);
        ModCreativeModTabs.register(modEventBus);

        ModCapabilities.register(modEventBus);

        // Register server events listener
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            FluxUtils.generation();
            PacketHandler.registerPacketHandling();
        });
    }

    public void registerRegistries(NewRegistryEvent event) {
        ModRegistries.register(event);
    }

    public void specialRegistries(RegisterEvent event) {
        ModFluxTypes.register(event);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SAPPHIRE);
        }
    }

    @SubscribeEvent
    public void onClientLogin(EntityJoinLevelEvent event) {
        CWBlockEntityUpdateHandler.updateLevel(event.getLevel());
    }

    @SubscribeEvent
    public void onDimensionSwitch(PlayerEvent.PlayerChangedDimensionEvent event) {
        // TODO: Check this actually works
        CWBlockEntityUpdateHandler.updateLevel(event.getEntity().level());
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
            MenuScreens.register(ModMenuTypes.BASIC_GENERATOR_MENU.get(), BasicGeneratorScreen::new);
        }
    }
}
