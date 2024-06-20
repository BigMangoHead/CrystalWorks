package net.bigmangohead.crystalworks.datagen.recipe;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.datagen.recipe.builder.CrushingRecipeBuilder;
import net.bigmangohead.crystalworks.datagen.recipe.builder.ProcessingRecipeBuilder;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.bigmangohead.crystalworks.recipe.SimpleProcessingRecipe;
import net.bigmangohead.crystalworks.registery.ModBlocks;
import net.bigmangohead.crystalworks.registery.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> SAPPHIRE_SMELTABLES = List.of(
            ModBlocks.SAPPHIRE_ORE.get(), ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get()
    );

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        oreBlasting(recipeOutput, SAPPHIRE_SMELTABLES, RecipeCategory.MISC, ModItems.SAPPHIRE.get(), 0.25f, 200, "sapphire");
        oreSmelting(recipeOutput, SAPPHIRE_SMELTABLES, RecipeCategory.MISC, ModItems.SAPPHIRE.get(), 0.25f, 100, "sapphire");


        blockRecipe(recipeOutput, ModItems.SAPPHIRE.get(), ModBlocks.SAPPHIRE_BLOCK.get());


        crusherRecipe(recipeOutput, Items.IRON_INGOT, 1, ModItems.IRON_DUST.get(), 1, 1, "dust");
        crusherRecipe(recipeOutput, Items.GOLD_INGOT, 2, ModItems.GOLD_DUST.get(), 3, 2, "dust");

        simpleProcessingRecipe(recipeOutput, "plate_former", Items.STICK, 2, Items.GLOW_ITEM_FRAME, 3, 0.5, 1, "weird");
        simpleProcessingRecipe(recipeOutput, "plate_former", Items.STONE, 1, Items.GRANITE, 2, 2, 0.5, "weird");

    }




    protected static void oreSmelting(Consumer<FinishedRecipe> pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pRecipeOutput, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pSuffix) {
        Iterator var9 = pIngredients.iterator();

        while(var9.hasNext()) {
            ItemLike itemlike = (ItemLike)var9.next();
            SimpleCookingRecipeBuilder.generic(Ingredient.of(new ItemLike[]{itemlike}), pCategory, pResult, pExperience, pCookingTime, pSerializer).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike)).save(pRecipeOutput, CrystalWorksMod.MOD_ID + ":" + getItemName(pResult) + pSuffix + "_" + getItemName(itemlike));
        }

    }

    protected static void crusherRecipe(Consumer<FinishedRecipe> pRecipeOutput, ItemLike inputItem, int ingredientCount, ItemLike pResult, int resultCount, double pRecipeTimeModifier, String pGroup) {
        CrushingRecipeBuilder.create(Ingredient.of(inputItem), ingredientCount, pResult, resultCount, pRecipeTimeModifier, CrusherRecipe.CrushingRecipeSerializer.INSTANCE)
                .group(pGroup).unlockedBy(getHasName(inputItem), has(inputItem)).save(pRecipeOutput, CrystalWorksMod.MOD_ID + ":" + getItemName(pResult) + "_from_crushing_" + getItemName(inputItem));
    }

    protected static void simpleProcessingRecipe(Consumer<FinishedRecipe> recipeConsumer, String machineName, ItemLike inputItem, int inputCount, ItemLike resultItem, int resultCount, double recipeTimeMultiplier, double energyMultiplier, String group) {
        RecipeSerializer<SimpleProcessingRecipe> serializer = SimpleProcessingRecipe.SimpleProcessingRecipeSerializer.getSerializerFromName(machineName);

        ProcessingRecipeBuilder.create(machineName, new ItemStack(resultItem, resultCount), new ItemStack(inputItem, inputCount), recipeTimeMultiplier, energyMultiplier, serializer)
                .group(group).unlockedBy(getHasName(inputItem), has(inputItem)).save(recipeConsumer, CrystalWorksMod.MOD_ID + ":" + getItemName(resultItem) + "_from_" + BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer).getPath() + "_" + getItemName(inputItem));
    }

    protected static void simpleProcessingRecipe(Consumer<FinishedRecipe> recipeConsumer, String machineName, ItemLike inputItem, int inputCount, ItemLike resultItem, int resultCount, String group) {
        simpleProcessingRecipe(recipeConsumer, machineName, inputItem, inputCount, resultItem, resultCount, 1.0, 1.0, group);
    }

    private static void blockRecipe(Consumer<FinishedRecipe> recipeOutput, Item item, Block block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', item)
                .unlockedBy(getHasName(item), has(item))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item, 9)
                .requires(block)
                .unlockedBy(getHasName(block), has(block))
                .save(recipeOutput);
    }
}
