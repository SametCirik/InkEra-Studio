package inkera.ui.tools.brush;

import inkera.main.DrawingWindow;
import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.Layer;
import inkera.ui.tools.DrawingTool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class BrushTool implements DrawingTool {

    private int size = 10;
    private float opacity = 1.0f;
    private float hardness = 0.8f;
    private float spacing = 0.25f;

    @Override
    public Rectangle mousePressed(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        return drawStamp(activeLayer, logicalPoint, color);
    }

    @Override
    public Rectangle mouseDragged(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        // Çizim mantığı Canvas sınıfındaki applyToolStrokeWithSpacing metodu tarafından yönetilecek,
        // bu yüzden burada sadece tek bir "damga" çiziyoruz.
        return drawStamp(activeLayer, logicalPoint, color);
    }

    @Override
    public Rectangle mouseReleased(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        // Fırça için fare bırakıldığında özel bir işlem yapmaya gerek yok.
        return new Rectangle(logicalPoint.x, logicalPoint.y, 0, 0);
    }

    private Rectangle drawStamp(Layer activeLayer, Point logicalPoint, Color color) {
        if (activeLayer == null || activeLayer.getImage() == null) {
            return new Rectangle();
        }

        Graphics2D g2d = activeLayer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Opaklığı ayarla
        int finalAlpha = (int) (color.getAlpha() * this.opacity);
        Color effectiveColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), finalAlpha);

        // Fırça sertliğine göre gradient (renk geçişi) oluştur
        Point2D center = new Point2D.Float(logicalPoint.x, logicalPoint.y);
        float radius = this.size / 2.0f;
        
        if (radius <= 0) radius = 0.5f;

        float[] dist = {0.0f, this.hardness, 1.0f};
        Color transparentColor = new Color(effectiveColor.getRed(), effectiveColor.getGreen(), effectiveColor.getBlue(), 0);
        Color[] colors = {effectiveColor, effectiveColor, transparentColor};

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);
        
        // Fırça darbesini çiz
        g2d.fill(new Ellipse2D.Float(logicalPoint.x - radius, logicalPoint.y - radius, this.size, this.size));
        g2d.dispose();

        // Etkilenen alanı hesapla ve döndür
        int intRadius = (int) Math.ceil(radius);
        return new Rectangle(logicalPoint.x - intRadius, logicalPoint.y - intRadius, size, size);
    }

    // --- Getter ve Setter'lar ---
    @Override
    public int getSize() { return size; }
    public void setSize(int size) { this.size = Math.max(1, size); }

    @Override
    public float getSpacing() { return spacing; }
    public void setSpacing(float spacing) { this.spacing = spacing; }

    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = Math.max(0.0f, Math.min(1.0f, opacity)); }

    public float getHardness() { return hardness; }
    public void setHardness(float hardness) { this.hardness = Math.max(0.0f, Math.min(1.0f, hardness)); }
}
