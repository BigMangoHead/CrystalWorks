package net.bigmangohead.crystalworks.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.screen.menu.PlateFormerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PlateFormerScreen extends AbstractContainerScreen<PlateFormerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CrystalWorksMod.MOD_ID, "textures/gui/crusher_gui.png");

    public PlateFormerScreen(PlateFormerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelX = 100000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 72, y + 33, 176, 0, 32, menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);

        graphics.drawString(this.font,
                "Energy: %d".formatted(this.menu.getEnergy()),
                this.leftPos + 8,
                this.topPos + 8,
                0x404040,
                false);
    }
}
