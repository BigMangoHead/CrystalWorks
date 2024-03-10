package net.bigmangohead.crystalworks.datagen.recipe;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.CrystalBlocks;
import net.bigmangohead.crystalworks.datagen.recipe.builder.CrushingRecipeBuilder;
import net.bigmangohead.crystalworks.item.CrystalItems;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> SAPPHIRE_SMELTABLES = List.of(
            CrystalBlocks.SAPPHIRE_ORE.get(), CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get()
    );

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        oreBlasting(recipeOutput, SAPPHIRE_SMELTABLES, RecipeCategory.MISC, CrystalItems.SAPPHIRE.get(), 0.25f, 200, "sapphire");
        oreSmelting(recipeOutput, SAPPHIRE_SMELTABLES, RecipeCategory.MISC, CrystalItems.SAPPHIRE.get(), 0.25f, 100, "sapphire");


        blockRecipe(recipeOutput, CrystalItems.SAPPHIRE.get(), CrystalBlocks.SAPPHIRE_BLOCK.get());


        crusherRecipe(recipeOutput, Items.IRON_INGOT, 1, CrystalItems.IRON_DUST.get(), 1, 1, "dust");
        crusherRecipe(recipeOutput, Items.GOLD_INGOT, 2, CrystalItems.GOLD_DUST.get(), 3, 2, "dust");


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

    protected static void crusherRecipe(Consumer<FinishedRecipe> pRecipeOutput, ItemLike itemLike, int ingredientCount, ItemLike pResult, int resultCount, double pRecipeTimeModifier, String pGroup) {
        CrushingRecipeBuilder.create(Ingredient.of(itemLike), ingredientCount, pResult, resultCount, pRecipeTimeModifier, CrusherRecipe.CrushingRecipeSerializer.INSTANCE).group(pGroup).unlockedBy(getHasName(itemLike), has(itemLike)).save(pRecipeOutput, CrystalWorksMod.MOD_ID + ":" + getItemName(pResult) + "_from_crushing_" + getItemName(itemLike));
    }

    private static void blockRecipe(Consumer<FinishedRecipe> recipeOutput, Item item, Block block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
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
