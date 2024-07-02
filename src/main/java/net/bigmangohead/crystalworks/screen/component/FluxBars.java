package net.bigmangohead.crystalworks.screen.component;

import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.bigmangohead.crystalworks.util.energy.flux.IFluxStorage;
import net.bigmangohead.crystalworks.util.screen.ScreenUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class FluxBars {

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(55, 55, 55);
    private static final Component FLUX_TEXT = Component.translatable("flux.crystalworks.flux");
    private static final int BAR_SPACING = 8;

    private IFluxStorage fluxStorage;
    private final int leftX;
    private final int rightX;
    private final int topY;
    private final int bottomY;
    private final Font font;
    private ArrayList<FluxBarData> fluxBarData = new ArrayList<>();
    private ArrayList<FluxType> fluxTypesToDisplay = new ArrayList<>();

    public FluxBars(IFluxStorage fluxStorage, int leftX, int rightX, int topY, int bottomY, Font font) {
        updateFluxStorage(fluxStorage);
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;
        this.font = font;
    }

    public void updateFluxStorage(IFluxStorage fluxStorage) {
        this.fluxStorage = fluxStorage;
    }

    private void updateFluxTypeData() {
        fluxTypesToDisplay = new ArrayList<>();
        FluxType[] possibleDisplayableFluxTypes = fluxStorage.getAcceptedFluxTypes().toArray(new FluxType[0]);

        for (FluxType possibleFluxType : possibleDisplayableFluxTypes) {
            // Checks if there is currently flux in the storage or if there has been within the last 5 seconds
            if (fluxStorage.getFluxAmount(possibleFluxType) > 0 || (new Date().getTime() - fluxStorage.getLastTimeFluxChanged(possibleFluxType)) < 5000) {
                fluxTypesToDisplay.add(possibleFluxType);
            }
        }
    }

    private void updateFluxBarData() {
        ArrayList<RectangleData> barPositionData = new ArrayList<>();
        this.fluxBarData = new ArrayList<>();

        // Special handling for 0
        if (this.fluxTypesToDisplay.isEmpty()) {
            fluxBarData.add(new FluxBarData(this.leftX, this.rightX, this.topY, this.bottomY, 0, 1, null, DEFAULT_BACKGROUND_COLOR, true));
            return;
        }


        // Take inverse function of f(x) = x(x+1) to find row count
        // Note that this is actually ceil(f^-1(x - 0.25)), as the value is the same but prevents rounding errors
        int rows = ((int) (Math.sqrt(this.fluxTypesToDisplay.size()) - 0.5)) + 1;

        // Tracks the number of bars in each row for every row but the last
        // Note that the -1 and then +1 in the operation is effectively taking the ceiling of (size / rows)
        int columns = (this.fluxTypesToDisplay.size() - 1) / rows + 1;


        int barWidth = ((this.rightX - this.leftX) - BAR_SPACING * (columns - 1)) / columns;
        int barHeight = ((this.bottomY - this.topY) - BAR_SPACING * (rows - 1)) / rows;

        // Do all but the last row of bars, as they form a nice rectangle
        for (int rowPosition = 0; rowPosition < rows - 1; rowPosition ++) {
            for (int columnPosition = 0; columnPosition < columns; columnPosition ++) {
                barPositionData.add(new RectangleData(this.leftX + (barWidth + BAR_SPACING) * columnPosition, this.leftX + (barWidth + BAR_SPACING) * columnPosition + barWidth,
                        this.topY + (barHeight + BAR_SPACING) * rowPosition, this.topY + (barHeight + BAR_SPACING) * rowPosition + barHeight));
            }
        }

        int lastRowBarCount = this.fluxTypesToDisplay.size() - (rows - 1) * columns;
        // Does more weird operations to try to avoid too much rounding
        int lastRowStartPos = (this.rightX + this.leftX) / 2 - (lastRowBarCount / 2) * (BAR_SPACING + barWidth) +
                ((lastRowBarCount + 1) % 2) * (barWidth / 2) - (lastRowBarCount % 2) * (BAR_SPACING / 2);

        for (int columnPosition = 0; columnPosition < lastRowBarCount; columnPosition ++) {
            barPositionData.add(new RectangleData(lastRowStartPos + (barWidth + BAR_SPACING) * columnPosition, lastRowStartPos + (barWidth + BAR_SPACING) * columnPosition + barWidth,
                    this.topY + (barHeight + BAR_SPACING) * (rows - 1), this.topY + (barHeight + BAR_SPACING) * (rows - 1) + barHeight));
        }


        // Loop through the flux types to display and fill in their data from the rectangles
        for (int i = 0; i < fluxTypesToDisplay.size(); i++) {
            fluxBarData.add(new FluxBarData(barPositionData.get(i), fluxStorage.getFluxAmount(fluxTypesToDisplay.get(i)),
                    fluxStorage.getMaxFluxAmount(fluxTypesToDisplay.get(i)), fluxTypesToDisplay.get(i), DEFAULT_BACKGROUND_COLOR));
        }
    }

    private void drawBars(GuiGraphics graphics) {
        for (FluxBarData fluxBar : this.fluxBarData) {
            graphics.fill(fluxBar.leftX, fluxBar.topY, fluxBar.rightX, fluxBar.bottomY, fluxBar.backgroundColor.getRGB());

            if (!fluxBar.blankBar) {
                int topFillY = (int) ((fluxBar.bottomY - fluxBar.topY - 2) * (1 - fluxBar.getFillPercentage()) + fluxBar.topY + 1);
                graphics.fill(fluxBar.leftX + 1, topFillY, fluxBar.rightX - 1, fluxBar.bottomY - 1, fluxBar.fluxType.getFluxColor().getRGB());
            }
        }
    }

    private void createTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        for (FluxBarData fluxBar : this.fluxBarData) {

            if (ScreenUtils.isHoveringOverRectangle(fluxBar.leftX, fluxBar.rightX, fluxBar.topY, fluxBar.bottomY, mouseX, mouseY)) {

                Component fluxBarText;
                if (!fluxBar.blankBar) {
                    fluxBarText = fluxBar.fluxType.getTranslatedName().copy()
                            .append(" ")
                            .append(FLUX_TEXT)
                            .append(": " + fluxBar.flux + " / " + fluxBar.maxFlux)
                            .withStyle(Style.EMPTY.withColor(fluxBar.fluxType.getFluxTextColor().getRGB()));
                } else {
                    fluxBarText = Component.translatable("tooltip.crystalworks.no_flux_bar");
                }

                graphics.renderTooltip(this.font, fluxBarText, mouseX, mouseY);
                return;
            }
        }
    }

    public void handleFluxBars(GuiGraphics graphics, int mouseX, int mouseY) {
        updateFluxTypeData();
        updateFluxBarData();
        drawBars(graphics);
        createTooltip(graphics, mouseX, mouseY);
    }

    public record RectangleData(int leftX, int rightX, int topY, int bottomY) {

    }

    private record FluxBarData(int leftX, int rightX, int topY, int bottomY, int flux, int maxFlux,
                               FluxType fluxType, Color backgroundColor, boolean blankBar) {

        public FluxBarData(RectangleData rectangleData, int flux, int maxFlux, FluxType fluxType, Color backgroundColor) {
            this(rectangleData.leftX, rectangleData.rightX, rectangleData.topY, rectangleData.bottomY, flux, maxFlux, fluxType, backgroundColor, false);
        }



        public float getFillPercentage() {
            return ((float) this.flux) / this.maxFlux;
        }
    }
}
