package net.bigmangohead.crystalworks.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModBlocks;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CrusherCategory implements IRecipeCategory<CrusherRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(CrystalWorksMod.MOD_ID, "crushing");
    public static final ResourceLocation TEXTURE = new ResourceLocation(CrystalWorksMod.MOD_ID,
            "textures/gui/crusher_gui.png");

    public static final RecipeType<CrusherRecipe> CRUSHER_TYPE =
            new RecipeType<>(UID, CrusherRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CrusherCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRUSHER.get()));
    }


    @Override
    public RecipeType<CrusherRecipe> getRecipeType() {
        return CRUSHER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block." + CrystalWorksMod.MOD_ID + ".crusher");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrusherRecipe crusherRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addItemStack(crusherRecipe.getIngredients().get(0).getItems()[0].copyWithCount(crusherRecipe.getInputCount()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 59).addItemStack(crusherRecipe.getResultItem(null));
    }
}
