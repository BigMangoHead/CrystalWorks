package net.bigmangohead.crystalworks.datagen.recipe.builder;

import com.google.gson.JsonObject;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CrushingRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int resultCount;
    private final int ingredientCount;
    private final double recipeTimeModifier;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends CrusherRecipe> serializer;

    private CrushingRecipeBuilder(Ingredient pIngredient, int pIngredientCount, ItemLike pResult, int pResultCount, double recipeTimeModifier, RecipeSerializer<? extends CrusherRecipe> pSerializer) {
        this.result = pResult.asItem();
        this.ingredient = pIngredient;
        this.ingredientCount = pIngredientCount;
        this.resultCount = pResultCount;
        this.recipeTimeModifier = recipeTimeModifier;
        this.serializer = pSerializer;
    }

    public static CrushingRecipeBuilder create(Ingredient pIngredient, int pIngredientCount, ItemLike pResult, int pResultCount, double recipeTimeModifier, RecipeSerializer<? extends CrusherRecipe> pSerializer) {
        return new CrushingRecipeBuilder(pIngredient, pIngredientCount, pResult, pResultCount, recipeTimeModifier, pSerializer);
    }

    @Override
    public CrushingRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(criterionName, criterionTriggerInstance);
        return this;
    }

    @Override
    public CrushingRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() { return this.result; }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR); //I'm not exactly sure what the .requirements thing references, perhaps worth looking at again later
        consumer.accept(new CrushingRecipeBuilder.ResultRecipe(resourceLocation, this.group == null ? "" : this.group, this.ingredient, this.ingredientCount, this.result, this.resultCount, this.recipeTimeModifier, this.advancement, resourceLocation.withPrefix("recipes/crushing/"), this.serializer));
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }


    //Stores information about a given recipe, which is then loaded into JSON files with ResultRecipe.



    static class ResultRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        protected final Ingredient ingredient;
        protected final int ingredientCount;
        protected final Item result;
        protected final int resultCount;
        protected final double recipeTimeModifier;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends CrusherRecipe> serializer;

        public ResultRecipe (ResourceLocation id, String group, Ingredient ingredient, int ingredientCount, Item result, int resultCount, double recipeTimeModifier, Advancement.Builder advancement, ResourceLocation advancementId, RecipeSerializer<? extends CrusherRecipe> serializer){
            this.id = id;
            this.group = group;
            this.ingredient = ingredient;
            this.ingredientCount = ingredientCount;
            this.result = result;
            this.resultCount = resultCount;
            this.recipeTimeModifier = recipeTimeModifier;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            JsonObject ingredientJsonObject = this.ingredient.toJson().getAsJsonObject();
            ingredientJsonObject.addProperty("count", this.ingredientCount);
            jsonObject.add("ingredient", ingredientJsonObject);
            jsonObject.add("result", itemStackToJson(this.result, this.resultCount));
            jsonObject.addProperty("recipetimemodifier", this.recipeTimeModifier);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }

        private JsonObject itemStackToJson(Item item, int count) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
            jsonObject.addProperty("count", count);
            return jsonObject;
        }
    }
}
