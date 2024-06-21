package net.bigmangohead.crystalworks.screen.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.menu.BasicGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasicGeneratorScreen extends AbstractContainerScreen<BasicGeneratorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CrystalWorksMod.MOD_ID, "textures/gui/crusher_gui.png");

    public BasicGeneratorScreen(BasicGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelX = 100000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderEnergyBar(guiGraphics, partialTick, mouseX, mouseY);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int energyBarHeight = this.menu.getEnergyStoredScaled();

        //background
        guiGraphics.fill(
                this.leftPos + 115,
                this.topPos + 20,
                this.leftPos + 131,
                this.topPos + 60,
                0xFF555555 //AARRGGBB
        );

        //foreground
        guiGraphics.fill(
                this.leftPos + 116,
                this.topPos + 21 + (38 - energyBarHeight),
                this.leftPos + 130,
                this.topPos + 59,
                0xFFCC2222
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int energyStored = this.menu.getEnergy();
        int maxEnergy = this.getMenu().getMaxEnergy();

        // Note: with below, the text component is re-created every frame.
        // Having some constant variables would make this more efficient.
        Component energyBarText = Component.translatable("tooltip.crystalworks.energy_bar")
                .append(Component.literal(": " + energyStored + " / " + maxEnergy));

        if (isHovering(115, 20, 16, 40, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, energyBarText, mouseX, mouseY);
        }
    }


}
