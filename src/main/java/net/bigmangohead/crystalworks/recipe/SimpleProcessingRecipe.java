package net.bigmangohead.crystalworks.recipe;

import com.google.gson.JsonObject;
import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SimpleProcessingRecipe implements Recipe<SimpleContainer> {
    private final int serializerRegistryID;
    private final ItemStack input;
    private final Ingredient inputAsIngredient;
    private final ItemStack result;
    private final double recipeTimeMultiplier;
    private final double energyMultiplier;
    private final ResourceLocation recipeID;

    public SimpleProcessingRecipe(int serializerRegistryID, ItemStack input, ItemStack result, double recipeTimeMultiplier, double energyMultiplier, ResourceLocation recipeID) {
        this.serializerRegistryID = serializerRegistryID;
        this.input = input;
        this.inputAsIngredient = Ingredient.of(input);
        this.result = result;
        this.recipeTimeMultiplier = recipeTimeMultiplier;
        this.energyMultiplier = energyMultiplier;
        this.recipeID = recipeID;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        // Check that item type is correct first, then check that the item count matches
        return inputAsIngredient.test(simpleContainer.getItem(0)) && (input.getCount() <= simpleContainer.getItem(0).getCount());
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(this.inputAsIngredient);
    }

    public int getInputCount() {
        return input.getCount();
    }

    public double getRecipeTimeMultiplier() {
        return this.recipeTimeMultiplier;
    }

    public double getEnergyMultiplier() {
        return this.energyMultiplier;
    }

    @Override
    public ResourceLocation getId() {
        return recipeID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.byId(serializerRegistryID);
    }

    public int getSerializerID() {
        return serializerRegistryID;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.getType(serializerRegistryID);
    }

    public static class Type implements RecipeType<SimpleProcessingRecipe> {
        private static final HashMap<Integer, Type> recipeTypeMap = new HashMap<>();

        public static Type getType(int serializerID) {
            if (!recipeTypeMap.containsKey(serializerID)) {
                recipeTypeMap.put(serializerID, new Type());
            }
            return recipeTypeMap.get(serializerID);
        }

        public static Type getType(String machineProcessName) {
            return getType(BuiltInRegistries.RECIPE_SERIALIZER.getId(BuiltInRegistries.RECIPE_SERIALIZER.get(ResourceLocation.tryBuild(CrystalWorksMod.MOD_ID, machineProcessName))));
        }
    }

    public static class SimpleProcessingRecipeSerializer implements RecipeSerializer<SimpleProcessingRecipe> {
        private static final HashMap<String, SimpleProcessingRecipeSerializer> serializerMap = new HashMap<>();

        // Returns correct serializer or a new one if one does not exist for the given name.
        public static RecipeSerializer<SimpleProcessingRecipe> createSerializerFromName(String name) {
            if (serializerMap.containsKey(name)) {
                throw new IllegalArgumentException("Serializer already exists with the name \"" + name + "\"!");
            }
            serializerMap.put(name, new SimpleProcessingRecipeSerializer());
            return serializerMap.get(name);
        }

        public static RecipeSerializer<SimpleProcessingRecipe> getSerializerFromName(String name) {
            if (!serializerMap.containsKey(name)) {
                throw new IllegalArgumentException("There is no serializer with the name \"" + name + "\"!");
            }
            return serializerMap.get(name);
        }

        @Override
        public SimpleProcessingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(ResourceLocation.of(jsonObject.get("type").getAsString(), ':'));
            int serializerID = BuiltInRegistries.RECIPE_SERIALIZER.getId(serializer);

            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));

            double recipeTimeMultiplier = GsonHelper.getAsDouble(jsonObject, "recipetimemultiplier");
            double energyMultiplier = GsonHelper.getAsDouble(jsonObject, "energymultiplier");

            return new SimpleProcessingRecipe(serializerID, input, result, recipeTimeMultiplier, energyMultiplier, resourceLocation);
        }

        @Override
        public @Nullable SimpleProcessingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            // Receive Recipe Type
            int serializerID = friendlyByteBuf.readInt();

            // Receive Recipe Input
            ItemStack input = friendlyByteBuf.readItem();

            // Receive Recipe Output
            ItemStack output = friendlyByteBuf.readItem();

            // Receive Recipe Time Multiplier
            double recipeTimeMultiplier = friendlyByteBuf.readDouble();

            // Receiver Energy Multiplier
            double energyMultiplier = friendlyByteBuf.readDouble();

            return new SimpleProcessingRecipe(serializerID, input, output, recipeTimeMultiplier, energyMultiplier, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, SimpleProcessingRecipe processingRecipe) {
            // Send Recipe Type
            friendlyByteBuf.writeInt(processingRecipe.getSerializerID());

            // Send Recipe Input
            Ingredient ingredient = processingRecipe.getIngredients().get(0);
            friendlyByteBuf.writeItemStack(ingredient.getItems()[0], false);

            // Send Recipe Output
            friendlyByteBuf.writeItemStack(processingRecipe.getResultItem(null), false);

            // Send Recipe Time Multiplier
            friendlyByteBuf.writeDouble(processingRecipe.getRecipeTimeMultiplier());

            // Send Energy Multiplier
            friendlyByteBuf.writeDouble(processingRecipe.getEnergyMultiplier());
        }
    }
}
