package net.bigmangohead.crystalworks.datagen.recipe.builder;

import com.google.gson.JsonObject;
import net.bigmangohead.crystalworks.recipe.SimpleProcessingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ProcessingRecipeBuilder implements RecipeBuilder {
    private final String machineType;
    private final ItemStack result;
    private final ItemStack input;
    private final double recipeTimeMultiplier;
    private final double energyMultiplier;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends SimpleProcessingRecipe> serializer;

    private ProcessingRecipeBuilder(String machineType, ItemStack result, ItemStack input, double recipeTimeMultiplier, double energyMultiplier, RecipeSerializer<? extends SimpleProcessingRecipe> serializer) {
        this.machineType = machineType;
        this.result = result;
        this.input = input;
        this.recipeTimeMultiplier = recipeTimeMultiplier;
        this.energyMultiplier = energyMultiplier;
        this.serializer = serializer;
    }

    public static ProcessingRecipeBuilder create(String machineType, ItemStack result, ItemStack ingredient, double recipeTimeMultiplier, double energyMultiplier, RecipeSerializer<? extends SimpleProcessingRecipe> serializer) {
        return new ProcessingRecipeBuilder(machineType, result, ingredient, recipeTimeMultiplier, energyMultiplier, serializer);
    }

    @Override
    public ProcessingRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(criterionName, criterionTriggerInstance);
        return this;
    }

    @Override
    public ProcessingRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR); //I'm not exactly sure what the .requirements thing references, perhaps worth looking at again later
        consumer.accept(new ResultRecipe(resourceLocation, this.group == null ? "" : this.group, this.input, this.result, this.recipeTimeMultiplier, this.energyMultiplier, this.advancement, resourceLocation.withPrefix("recipes/" + this.machineType + "/"), this.serializer));
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }



    static class ResultRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        protected final ItemStack input;
        protected final ItemStack result;
        protected final double recipeTimeMultiplier;
        protected final double energyMultiplier;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends SimpleProcessingRecipe> serializer;


        ResultRecipe(ResourceLocation id, String group, ItemStack input, ItemStack result, double recipeTimeMultiplier, double energyMultiplier, Advancement.Builder advancement, ResourceLocation advancementId, RecipeSerializer<? extends SimpleProcessingRecipe> serializer) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.result = result;
            this.recipeTimeMultiplier = recipeTimeMultiplier;
            this.energyMultiplier = energyMultiplier;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("input", itemStackToJson(this.input));
            jsonObject.add("result", itemStackToJson(this.result));
            jsonObject.addProperty("recipetimemultiplier", this.recipeTimeMultiplier);
            jsonObject.addProperty("energymultiplier", this.recipeTimeMultiplier);
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

        private JsonObject itemStackToJson(ItemStack item) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
            jsonObject.addProperty("count", item.getCount());
            return jsonObject;
        }
    }
}
