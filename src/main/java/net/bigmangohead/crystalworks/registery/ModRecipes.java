package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.bigmangohead.crystalworks.recipe.SimpleProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CrystalWorksMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER =
            SERIALIZERS.register("crushing", () -> CrusherRecipe.CrushingRecipeSerializer.INSTANCE);
    //public static final RegistryObject<RecipeSerializer<SimpleProcessingRecipe>> PROCESSING_SERIALIZER =
    //        SERIALIZERS.register("processing", () -> SimpleProcessingRecipe.SimpleProcessingRecipeSerializer.createSerializerFromName(""));
    public static final RegistryObject<RecipeSerializer<SimpleProcessingRecipe>> PLATE_FORMER_SERIALIZER =
            SERIALIZERS.register("plate_forming", () -> SimpleProcessingRecipe.SimpleProcessingRecipeSerializer.createSerializerFromName("plate_former"));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
