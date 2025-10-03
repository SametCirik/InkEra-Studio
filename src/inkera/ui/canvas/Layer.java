package inkera.ui.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Layer {
    
    /**
     * Layer sınıfı, tek bir katmanın verilerini (resim, opaklık, görünürlük) tutar.
     */
    private BufferedImage image;
    private boolean isVisible = true;
    private float opacity = 1.0f;
    private String name;

    public Layer(int width, int height, String name) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.name = name;
    }

    public BufferedImage getImage() { return image; }
    public boolean isVisible() { return isVisible; }
    public float getOpacity() { return opacity; }
    public String getName() { return name; }
}

