package net.bigmangohead.crystalworks.screen.component;

import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class FluxBars {

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(55, 55, 55);

    // TODO: Currently only works with vertical bars
    // Also needs to have some sort of "memory" of what's been shown recently, so that bars don't flicker in and out of existence multiple times a second.
    public static void createFluxBars(GuiGraphics graphics, FluxStorage fluxStorage, int leftX, int rightX, int topY, int bottomY, int barCount) {
        int totalFluxTypes = fluxStorage.getStoredFluxTypes().size();

        FluxType[] fluxTypesToDisplay;
        if (barCount == -1) {
            fluxTypesToDisplay =  fluxStorage.getStoredFluxTypes().toArray(new FluxType[0]);
        } else {
            fluxTypesToDisplay = null;
            // TODO
        }


        if (totalFluxTypes == 0 || fluxTypesToDisplay == null || fluxTypesToDisplay.length == 0) {
            createBar(graphics, Color.BLACK, DEFAULT_BACKGROUND_COLOR, 0, leftX, rightX, topY, bottomY);

        } else if (fluxTypesToDisplay.length == 1) {
            createBar(graphics, fluxTypesToDisplay[0].getColor(), DEFAULT_BACKGROUND_COLOR, getFluxFillPercentage(fluxStorage, fluxTypesToDisplay[0]), leftX, rightX, topY, bottomY);

        } else if (fluxTypesToDisplay.length == 2) {
            createBar(graphics, fluxTypesToDisplay[0].getColor(), DEFAULT_BACKGROUND_COLOR, getFluxFillPercentage(fluxStorage, fluxTypesToDisplay[0]), leftX, (leftX + rightX) / 2 - 4, topY, bottomY);
            createBar(graphics, fluxTypesToDisplay[1].getColor(), DEFAULT_BACKGROUND_COLOR, getFluxFillPercentage(fluxStorage, fluxTypesToDisplay[1]), (leftX + rightX) / 2 + 4, rightX, topY, bottomY);
        }
    }

    public static void createFluxBars(GuiGraphics graphics, FluxStorage fluxStorage, int leftX, int rightX, int topY, int bottomY) {
        createFluxBars(graphics, fluxStorage, leftX, rightX, topY, bottomY, -1);
    }

    private static void createBar(GuiGraphics graphics, Color foregroundColor, Color backgroundColor, float fillPercentage, int leftX, int rightX, int topY, int bottomY) {
        graphics.fill(
                leftX,
                topY,
                rightX,
                bottomY,
                backgroundColor.getRGB()
        );

        int topFillY = (int) ((bottomY - topY - 2) * (1 - fillPercentage) + topY + 1);
        graphics.fill(
                leftX + 1,
                topFillY,
                rightX - 1,
                bottomY - 1,
                foregroundColor.getRGB()
        );
    }

    private static float getFluxFillPercentage(FluxStorage fluxStorage, FluxType fluxType) {
        return ((float) fluxStorage.getFluxStorage(fluxType).getFluxStored()) / fluxStorage.getFluxStorage(fluxType).getMaxFluxStored();
    }


}
