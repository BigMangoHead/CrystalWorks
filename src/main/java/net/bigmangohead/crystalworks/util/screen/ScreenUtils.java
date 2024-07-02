package net.bigmangohead.crystalworks.util.screen;

public class ScreenUtils {
    public static boolean isHoveringOverRectangle(int rectangleLeftX, int rectangleRightX, int rectangleTopY, int rectangleBottomY, int mouseX, int mouseY) {
        return (rectangleLeftX <= mouseX && mouseX <= rectangleRightX && rectangleTopY <= mouseY && mouseY <= rectangleBottomY);
    }
}
