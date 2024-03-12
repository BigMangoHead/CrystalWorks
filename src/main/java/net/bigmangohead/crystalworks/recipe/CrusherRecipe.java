package net.bigmangohead.crystalworks.recipe;

import com.google.gson.JsonObject;
import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CrusherRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItem;
    private final int inputCount;
    private final ItemStack output;
    private final ResourceLocation id;
    private final double recipeTimeModifier;

    public CrusherRecipe(Ingredient inputItem, int inputCount, ItemStack output, double recipeTimeModifier, ResourceLocation id) {
        this.inputItem = NonNullList.withSize(1, inputItem);
        this.inputCount = inputCount;
        this.output = output;
        this.id = id;
        this.recipeTimeModifier = recipeTimeModifier;
    }


    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return inputItem.get(0).test(simpleContainer.getItem(0));
    }
    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.inputItem;
    }

    public int getInputCount() {
        return this.inputCount;
    }

    public double getRecipeTimeModifier() {
        return this.recipeTimeModifier;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CrushingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrusherRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "crushing";
    }

    public static class CrushingRecipeSerializer implements RecipeSerializer<CrusherRecipe> {
        public static final CrushingRecipeSerializer INSTANCE = new CrushingRecipeSerializer();
        public static final ResourceLocation ID = new ResourceLocation(CrystalWorksMod.MOD_ID, "crushing");

        @Override
        public CrusherRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));

            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            int ingredientCount = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(jsonObject, "ingredient"), "count");

            double recipeTimeModifier = GsonHelper.getAsDouble(jsonObject, "recipetimemodifier");

            return new CrusherRecipe(ingredient, ingredientCount, result, recipeTimeModifier, resourceLocation);


        }

        @Override
        public @Nullable CrusherRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            //Receive Recipe Input
            int inputCount = friendlyByteBuf.readInt();
            Ingredient input = Ingredient.fromNetwork(friendlyByteBuf);

            //Receive Recipe Output
            ItemStack output = friendlyByteBuf.readItem();

            //Receive Recipe Time Modifier
            double recipeTimeModifier = friendlyByteBuf.readDouble();


            return new CrusherRecipe(input, inputCount, output, recipeTimeModifier, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrusherRecipe crusherRecipe) {
            //Send Recipe Input
            friendlyByteBuf.writeInt(crusherRecipe.getInputCount());
            Ingredient ingredient = crusherRecipe.getIngredients().get(0);
            ingredient.toNetwork(friendlyByteBuf);

            //Send Recipe Output
            friendlyByteBuf.writeItemStack(crusherRecipe.getResultItem(null), false);

            //Send Recipe Time Modifier
            friendlyByteBuf.writeDouble(crusherRecipe.getRecipeTimeModifier());
        }
    }
}
