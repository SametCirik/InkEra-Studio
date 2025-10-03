package inkera.ui.tools.eraser;

import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.Layer;
import inkera.ui.tools.DrawingTool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class EraserTool implements DrawingTool {

    private int size = 20;
    private float hardness = 0.5f;
    private float spacing = 0.25f;

    @Override
    public Rectangle mousePressed(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        return eraseStamp(activeLayer, logicalPoint);
    }

    @Override
    public Rectangle mouseDragged(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        return eraseStamp(activeLayer, logicalPoint);
    }

    @Override
    public Rectangle mouseReleased(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        // Silgi için fare bırakıldığında özel bir işlem yapmaya gerek yok.
        return new Rectangle(logicalPoint.x, logicalPoint.y, 0, 0);
    }

    private Rectangle eraseStamp(Layer activeLayer, Point logicalPoint) {
        if (activeLayer == null || activeLayer.getImage() == null) {
            return new Rectangle();
        }

        Graphics2D g2d = activeLayer.getImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Silme işlemi için DstOut kompozitini kullanıyoruz. Bu, mevcut piksellerin
        // alfasını, çizdiğimiz şeklin alfasıyla çarparak yumuşak kenarlı bir silgi elde etmemizi sağlar.
        g2d.setComposite(AlphaComposite.DstOut);

        Point2D center = new Point2D.Float(logicalPoint.x, logicalPoint.y);
        float radius = this.size / 2.0f;
        if (radius <= 0) radius = 0.5f;

        // DstOut için renklerin önemi yok, sadece alfa kanalı önemli.
        // Ortası tamamen opak (alpha=255), kenarları tamamen şeffaf (alpha=0) olacak.
        float[] dist = {0.0f, this.hardness, 1.0f};
        Color opaque = new Color(0, 0, 0, 255);
        Color transparent = new Color(0, 0, 0, 0);
        Color[] colors = {opaque, opaque, transparent};

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);

        g2d.fill(new Ellipse2D.Float(logicalPoint.x - radius, logicalPoint.y - radius, this.size, this.size));
        g2d.dispose();

        int intRadius = (int) Math.ceil(radius);
        return new Rectangle(logicalPoint.x - intRadius, logicalPoint.y - intRadius, size, size);
    }

    @Override
    public int getSize() { return size; }
    public void setSize(int size) { this.size = Math.max(1, size); }

    @Override
    public float getSpacing() { return spacing; }
    public void setSpacing(float spacing) { this.spacing = spacing; }

    public float getHardness() { return hardness; }
    public void setHardness(float hardness) { this.hardness = Math.max(0.0f, Math.min(1.0f, hardness)); }
}
