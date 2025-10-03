package inkera.ui.SidePanelComponentSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;

/**
 * Halka (Hue) ve Merkez Kare (Saturation/Brightness) yapısını birleştiren 
 * gelişmiş Renk Tekerleği paneli. Etkileşim ve Dış Listener Eklendi.
 */
public class ColorWheelPanel extends JPanel {
    private Color selectedColor = Color.RED;
    private static final int RING_THICKNESS = 20;
    private static final int RING_PADDING = 5;

    private float selectedHue = 0.0f;
    private float selectedSaturation = 1.0f;
    private float selectedBrightness = 1.0f;

    private Consumer<Color> colorChangeListener;

    private enum SelectionArea { NONE, RING, BOX }
    private SelectionArea currentArea = SelectionArea.NONE;

    public ColorWheelPanel(Consumer<Color> colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
        this.setBackground(Color.decode("#454545"));
        this.setPreferredSize(new Dimension(250, 250));

        ColorSelectorMouseAdapter mouseAdapter = new ColorSelectorMouseAdapter();
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        notifyColorChange();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;
        int centerY = h / 2;
        int maxRadius = (int) (Math.min(w, h) / 2.0);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawHueRing(g2d, w, h, centerX, centerY, maxRadius);
        drawSVBox(g2d, centerX, centerY, maxRadius);
        drawSelectors(g2d, centerX, centerY, maxRadius);

        g2d.dispose();
    }

    private void drawHueRing(Graphics2D g2d, int w, int h, int centerX, int centerY, int maxRadius) {
        int outerRadius = maxRadius;
        int innerRadius = maxRadius - RING_THICKNESS;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int dx = x - centerX;
                int dy = y - centerY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > innerRadius && dist < outerRadius) {
                    float hue = (float) (Math.atan2(dy, dx) / (2 * Math.PI));
                    if (hue < 0) { hue += 1.0f; }

                    Color color = Color.getHSBColor(hue, 1.0f, 1.0f);

                    g2d.setColor(color);
                    g2d.drawLine(x, y, x, y);
                }
            }
        }

        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(new Ellipse2D.Double(centerX - outerRadius, centerY - outerRadius, 2 * outerRadius, 2 * outerRadius));
        g2d.draw(new Ellipse2D.Double(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius));
    }

    private void drawSVBox(Graphics2D g2d, int centerX, int centerY, int maxRadius) {
        int boxRadius = maxRadius - RING_THICKNESS - RING_PADDING;
        int boxSize = (int)(boxRadius * Math.sqrt(2));

        int boxX = centerX - boxSize / 2;
        int boxY = centerY - boxSize / 2;

        for (int y = 0; y < boxSize; y++) {
            for (int x = 0; x < boxSize; x++) {

                float saturation = (float) x / boxSize;
                float brightness = 1.0f - (float) y / boxSize;

                Color color = Color.getHSBColor(selectedHue, saturation, brightness);

                g2d.setColor(color);
                g2d.drawLine(boxX + x, boxY + y, boxX + x, boxY + y);
            }
        }

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(boxX, boxY, boxSize, boxSize);
    }

    private void drawSelectors(Graphics2D g2d, int centerX, int centerY, int maxRadius) {
        int selectorSize = 10;
        int boxRadius = maxRadius - RING_THICKNESS - RING_PADDING;
        int boxSize = (int)(boxRadius * Math.sqrt(2));
        int boxX = centerX - boxSize / 2;
        int boxY = centerY - boxSize / 2;

        double angle = selectedHue * 2 * Math.PI;
        int ringCenter = maxRadius - RING_THICKNESS / 2;
        int xRing = centerX + (int) (Math.cos(angle) * ringCenter);
        int yRing = centerY + (int) (Math.sin(angle) * ringCenter);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(xRing - selectorSize/2, yRing - selectorSize/2, selectorSize, selectorSize);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(xRing - selectorSize/2, yRing - selectorSize/2, selectorSize, selectorSize);

        g2d.setColor(Color.BLACK);
        g2d.fillOval(xRing - selectorSize/4, yRing - selectorSize/4, selectorSize/2, selectorSize/2);

        int xBox = boxX + (int) (selectedSaturation * boxSize);
        int yBox = boxY + (int) ((1.0f - selectedBrightness) * boxSize);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(xBox - selectorSize/2, yBox - selectorSize/2, selectorSize, selectorSize);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(xBox - selectorSize/2, yBox - selectorSize/2, selectorSize, selectorSize);

        this.selectedColor = Color.getHSBColor(selectedHue, selectedSaturation, selectedBrightness);
    }

    private void notifyColorChange() {
        this.selectedColor = Color.getHSBColor(selectedHue, selectedSaturation, selectedBrightness);

        if (colorChangeListener != null) {
            colorChangeListener.accept(this.selectedColor);
        }

        repaint();
    }

    private class ColorSelectorMouseAdapter extends MouseAdapter {

        private void updateHSB(int x, int y) {
            int w = getWidth();
            int h = getHeight();
            int centerX = w / 2;
            int centerY = h / 2;
            int maxRadius = (int) (Math.min(w, h) / 2.0);

            int dx = x - centerX;
            int dy = y - centerY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            int outerRadius = maxRadius;
            int innerRadius = maxRadius - RING_THICKNESS;

            int boxRadius = maxRadius - RING_THICKNESS - RING_PADDING;
            int boxSize = (int)(boxRadius * Math.sqrt(2));
            int boxX = centerX - boxSize / 2;
            int boxY = centerY - boxSize / 2;

            if (currentArea == SelectionArea.RING) {
                if (dist > innerRadius && dist < outerRadius) {
                    float hue = (float) (Math.atan2(dy, dx) / (2 * Math.PI));
                    if (hue < 0) { hue += 1.0f; }

                    if (Math.abs(selectedHue - hue) > 0.001f) {
                        selectedHue = hue;
                    }
                }
            } else if (currentArea == SelectionArea.BOX) {
                if (x >= boxX && x < boxX + boxSize && y >= boxY && y < boxY + boxSize) {
                    float saturation = (float) (x - boxX) / boxSize;
                    saturation = Math.max(0f, Math.min(1f, saturation));

                    float brightness = 1.0f - (float) (y - boxY) / boxSize;
                    brightness = Math.max(0f, Math.min(1f, brightness));

                    if (Math.abs(selectedSaturation - saturation) > 0.001f ||
                            Math.abs(selectedBrightness - brightness) > 0.001f) {

                        selectedSaturation = saturation;
                        selectedBrightness = brightness;
                    }
                }
            }

            notifyColorChange();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int w = getWidth(); int h = getHeight(); int centerX = w / 2; int centerY = h / 2;
            int maxRadius = (int) (Math.min(w, h) / 2.0);
            int dx = x - centerX; int dy = y - centerY; double dist = Math.sqrt(dx * dx + dy * dy);

            int outerRadius = maxRadius; int innerRadius = maxRadius - RING_THICKNESS;
            int boxRadius = maxRadius - RING_THICKNESS - RING_PADDING;
            int boxSize = (int)(boxRadius * Math.sqrt(2));
            int boxX = centerX - boxSize / 2; int boxY = centerY - boxSize / 2;

            if (dist > innerRadius && dist < outerRadius) {
                currentArea = SelectionArea.RING;
                updateHSB(x, y);
            } else if (x >= boxX && x < boxX + boxSize && y >= boxY && y < boxY + boxSize) {
                currentArea = SelectionArea.BOX;
                updateHSB(x, y);
            } else {
                currentArea = SelectionArea.NONE;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentArea != SelectionArea.NONE) {
                updateHSB(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            currentArea = SelectionArea.NONE;
            notifyColorChange();
        }
    }

    public float getSelectedHue() { return selectedHue; }
    public float getSelectedSaturation() { return selectedSaturation; }
    public float getSelectedBrightness() { return selectedBrightness; }

    public void setHSB(float h, float s, float b) {
        if (Math.abs(h - this.selectedHue) > 0.001f ||
                Math.abs(s - this.selectedSaturation) > 0.001f ||
                Math.abs(b - this.selectedBrightness) > 0.001f) {

            this.selectedHue = Math.max(0f, Math.min(1f, h));
            this.selectedSaturation = Math.max(0f, Math.min(1f, s));
            this.selectedBrightness = Math.max(0f, Math.min(1f, b));

            repaint();
        }
    }

    public Color getSelectedColor() {
        return Color.getHSBColor(selectedHue, selectedSaturation, selectedBrightness);
    }
    
    public void setColorChangeListener(Consumer<Color> listener) {
        this.colorChangeListener = listener;
    }

}
