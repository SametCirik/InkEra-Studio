package inkera.ui.tools.lasso;

import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.Layer;
import inkera.ui.tools.DrawingTool;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LassoTool implements DrawingTool {

    private Polygon selectionPath;
    private boolean isSelecting = false;
    private boolean isFinalized = false;

    public LassoTool() {
        this.selectionPath = new Polygon();
    }

    @Override
    public Rectangle mousePressed(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        clearSelection();
        isSelecting = true;
        selectionPath.addPoint(logicalPoint.x, logicalPoint.y);
        canvas.repaint();
        return new Rectangle(logicalPoint, new Dimension(1,1));
    }

    @Override
    public Rectangle mouseDragged(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        if (isSelecting) {
            selectionPath.addPoint(logicalPoint.x, logicalPoint.y);
            // Canvas'ı yeniden çizmeye zorlayarak anlık geri bildirim sağlıyoruz.
            canvas.repaint();
        }
        return selectionPath.getBounds();
    }

    @Override
    public Rectangle mouseReleased(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color) {
        if (isSelecting) {
            isSelecting = false;
            isFinalized = true; // Seçim tamamlandı
            canvas.repaint();
        }
        return selectionPath.getBounds();
    }
    
    /**
     * Canvas'ın paintComponent metodunda çağrılacak anlık seçim çizgisini çizer.
     */
    public void drawSelectionFeedback(Graphics2D g2d, Canvas canvas) {
        if (selectionPath.npoints > 1) {
            g2d.setColor(Color.BLACK);
            // Kesik çizgi efekti için
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0));
            
            // Çizilen yolu çiz
            g2d.drawPolyline(selectionPath.xpoints, selectionPath.ypoints, selectionPath.npoints);

            // Eğer seçim bittiyse, başlangıç ve bitiş noktasını birleştirerek alanı kapat
            if (isFinalized) {
                g2d.drawLine(selectionPath.xpoints[0], selectionPath.ypoints[0], 
                             selectionPath.xpoints[selectionPath.npoints - 1], selectionPath.ypoints[selectionPath.npoints - 1]);
            }
        }
    }

    public void clearSelection() {
        selectionPath.reset();
        isSelecting = false;
        isFinalized = false;
    }

    public boolean hasSelection() {
        return isFinalized && selectionPath.npoints > 2;
    }
    
    public Polygon getSelectionPath() {
        if (hasSelection()) {
            return new Polygon(selectionPath.xpoints, selectionPath.ypoints, selectionPath.npoints);
        }
        return null;
    }

    // DrawingTool arayüzü için zorunlu metotlar (Lasso için pek anlamlı değiller)
    @Override
    public int getSize() { return 1; }

    @Override
    public float getSpacing() { return 0; }
}
