package inkera.ui.cursor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * A factory class that dynamically creates brush cursors for drawing applications.
 * This class uses the AWT API instead of modifying Java's built-in Cursor class.
 */
public class BrushCursorFactory {

    // Many operating systems don't support cursors larger than 64x64 pixels.
    private static final int MAX_CURSOR_DIMENSION = 64;

    /**
     * Creates a Cursor object representing a circular brush of a specified size and color.
     *
     * @param requestedBrushSize The desired diameter of the brush in pixels.
     * @param brushColor The color of the brush.
     * @return The newly configured Cursor object.
     */
    public static Cursor createBrushCursor(int requestedBrushSize, Color brushColor) {

        // 1. VALIDATE INPUT (PRECAUTION)
        int brushSize = requestedBrushSize;
        if (brushSize < 1) {
            brushSize = 1; // Prevent the size from being zero or negative.
        }

        // Calculate image size with padding and check against the maximum dimension.
        int padding = 2;
        int imageSize = brushSize + padding * 2;

        // If the calculated image size exceeds the max, clamp the brush size.
        if (imageSize > MAX_CURSOR_DIMENSION) {
            imageSize = MAX_CURSOR_DIMENSION;
            brushSize = imageSize - (padding * 2);
        }

        // Create a transparent image to draw on.
        BufferedImage cursorImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cursorImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- UPDATED AND SAFER DRAWING LOGIC ---

        // First, draw the main filled circle with the brush color.
        g2d.setColor(brushColor);
        g2d.fillOval(padding, padding, brushSize, brushSize);

        // Then, draw an outline of the same size for better visibility.
        g2d.setColor(Color.BLACK);
        g2d.drawOval(padding, padding, brushSize, brushSize);

        // Dispose of the graphics context.
        g2d.dispose();

        // The "hotspot" should always be the center of the image.
        Point hotspot = new Point(imageSize / 2, imageSize / 2);

        // Use the Toolkit to create our custom cursor.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createCustomCursor(cursorImage, hotspot, "BrushCursor");
    }
}