package inkera.ui.canvas;

import javax.swing.*;
import java.awt.*;

public class CanvasPanel extends JPanel {

    private final Canvas drawingCanvas;

    /**
     * DÜZELTME: Constructor, artık bir Canvas nesnesini parametre olarak alıyor.
     * @param canvas İçinde gösterilecek olan tuval.
     */
    public CanvasPanel(Canvas canvas) {
        this.drawingCanvas = canvas;
        
        // DÜZELTME: Layout, içine eklenen bileşeni (Canvas) otomatik olarak
        // ortalamak için GridBagLayout olarak ayarlandı.
        setLayout(new GridBagLayout());
        
        // DÜZELTME: Arka plan rengi kırmızıdan, tuvalin etrafında görünecek
        // olan nötr bir griye çevrildi.
        setBackground(Color.decode("#BFBFBF"));

        // Canvas'ı panele ekle. GridBagConstraints olmadan eklemek,
        // bileşenin tam ortada durmasını sağlar.
        add(drawingCanvas, new GridBagConstraints());
    }

    public Canvas getDrawingCanvas() {
        return drawingCanvas;
    }

    /**
     * DÜZELTME: DrawingWindow'dan gelen yeni boyut bilgilerine göre Canvas'ın
     * tercih edilen boyutunu güncelleyen metot eklendi.
     * @param width Yeni genişlik.
     * @param height Yeni yüksekliik.
     */
    public void updateCanvasSize(int width, int height) {
        if (drawingCanvas != null) {
            drawingCanvas.setPreferredSize(new Dimension(width, height));
            // Değişikliğin anında yansıması için paneli yeniden doğrula.
            revalidate();
        }
    }
}
