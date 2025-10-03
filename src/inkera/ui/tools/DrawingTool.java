package inkera.ui.tools;

import inkera.main.DrawingWindow;
import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.Layer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Tüm çizim araçlarının uygulaması gereken temel metotları tanımlayan arayüz.
 */
public interface DrawingTool {

    /**
     * Fare tuşuna basıldığında çağrılır.
     * @param canvas Üzerinde çizim yapılan tuval.
     * @param e Mouse olay bilgisi.
     * @param logicalPoint Tuval üzerindeki mantıksal (piksel) koordinat.
     * @param activeLayer Aktif olan katman.
     * @param color Seçili olan ana renk.
     * @return Bu işlemden etkilenen alanın (Rectangle) koordinatları.
     */
    Rectangle mousePressed(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color);

    /**
     * Fare sürüklendiğinde çağrılır.
     * @return Bu işlemden etkilenen alanın (Rectangle) koordinatları.
     */
    Rectangle mouseDragged(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color);

    /**
     * Fare tuşu bırakıldığında çağrılır.
     * @return Bu işlemden etkilenen alanın (Rectangle) koordinatları.
     */
    Rectangle mouseReleased(Canvas canvas, MouseEvent e, Point logicalPoint, Layer activeLayer, Color color);
    
    /**
     * Aracın mevcut boyutunu döndürür.
     * @return Piksel cinsinden boyut.
     */
    int getSize();

    /**
     * Aracın fırça darbeleri arasındaki boşluk oranını döndürür.
     * 1.0f = %100 (fırça boyutu kadar boşluk)
     * @return Boşluk oranı.
     */
    float getSpacing();
}
