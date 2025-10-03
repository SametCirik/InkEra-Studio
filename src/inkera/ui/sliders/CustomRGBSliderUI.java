package inkera.ui.sliders;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * CustomRGBSlider için özel UI sınıfı.
 * Track (Yol) üzerine Kırmızı, Yeşil veya Mavi renk gradyanı çizer.
 * Gradyan, dışarıdan ayarlanan diğer iki renk bileşenine göre dinamik olarak değişir.
 */
public class CustomRGBSliderUI extends BasicSliderUI {

    private static final int THUMB_SIZE = 14;
    
    // Slider'ın tipini tutar ("R", "G", veya "B")
    private final String sliderType;

    // Gradyanı çizmek için kullanılacak diğer iki rengin değeri (0-255)
    private int otherValue1 = 0;
    private int otherValue2 = 0;

    public CustomRGBSliderUI(JSlider slider, String type) {
        super(slider);
        // Gelen tipin null veya geçersiz olmamasını kontrol et
        this.sliderType = Objects.requireNonNull(type, "Slider type cannot be null");
    }
    
    /**
     * Gradyanı çizmek için kullanılacak diğer iki renk bileşenini ayarlar.
     * Örneğin, "R" slider'ı için bunlar G ve B değerleridir.
     * @param v1 Birinci renk bileşeni (0-255)
     * @param v2 İkinci renk bileşeni (0-255)
     */
    public void setOtherColorComponents(int v1, int v2) {
        if (this.otherValue1 != v1 || this.otherValue2 != v2) {
            this.otherValue1 = v1;
            this.otherValue2 = v2;
            slider.repaint(); // Değerler değiştiğinde gradyanı yeniden çiz
        }
    }

    // Thumb (işaretçi) boyutu ve konumu ile ilgili metotlar
    // Bunlar Saturation UI ile aynı kalabilir.
    
    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        // Thumb'ı Track'in dikey ortasına sabitler
        thumbRect.y = trackRect.y + (trackRect.height / 2) - (thumbRect.height / 2);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thumbRect.x + thumbRect.width / 2;
        int y = thumbRect.y + thumbRect.height / 2;
        
        // Dış Beyaz Halka
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - THUMB_SIZE / 2, y - THUMB_SIZE / 2, THUMB_SIZE, THUMB_SIZE);
        
        // İç Siyah Çerçeve
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(x - THUMB_SIZE / 2, y - THUMB_SIZE / 2, THUMB_SIZE, THUMB_SIZE);
        
        g2d.dispose();
    }

    /**
     * Track (yol) üzerine RGB gradyanını çizer.
     */
    @Override
    public void paintTrack(Graphics g) {
        if (slider.getOrientation() != JSlider.HORIZONTAL) {
            super.paintTrack(g);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track'i ortala
        int trackY = trackRect.y + trackRect.height / 2 - 5;
        int trackHeight = 10;

        // Renk gradyanını çizme
        for (int i = 0; i < trackRect.width; i++) {
            // Mevcut pikselin 0-255 aralığındaki renk değerini hesapla
            float valueRatio = (float) i / (float) trackRect.width;
            int currentValue = (int) (valueRatio * 255);
            
            Color pixelColor;
            
            // Slider tipine göre rengi belirle
            switch (sliderType) {
                case "R":
                    pixelColor = new Color(currentValue, otherValue1, otherValue2);
                    break;
                case "G":
                    pixelColor = new Color(otherValue1, currentValue, otherValue2);
                    break;
                case "B":
                    pixelColor = new Color(otherValue1, otherValue2, currentValue);
                    break;
                default:
                    pixelColor = Color.BLACK; // Geçersiz tip durumunda siyah çiz
            }
            
            g2d.setColor(pixelColor);
            g2d.drawLine(trackRect.x + i, trackY, trackRect.x + i, trackY + trackHeight);
        }

        // Dış çerçeveyi çiz
        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Float(trackRect.x, trackY, trackRect.width, trackHeight));
        
        g2d.dispose();
    }
}
