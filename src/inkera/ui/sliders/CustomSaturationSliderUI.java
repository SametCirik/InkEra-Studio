package inkera.ui.sliders;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * SaturationSlider için özel UI sınıfı.
 * Track (Yol) üzerine doygunluk (Saturation) renk gradyanı çizer.
 * Gradient, harici olarak ayarlanan HUE değerine göre dinamik olarak değişir.
 */
public class CustomSaturationSliderUI extends BasicSliderUI {

    private static final int THUMB_SIZE = 14; 
    
    // Saturation gradyanını çizmek için kullanılacak Hue.
    // Başlangıçta 0.0f (Kırmızı) olarak kalabilir, ancak dışarıdan set edilmesi beklenir.
    private float currentHue = 0.0f; 

    public CustomSaturationSliderUI(JSlider slider) {
        super(slider);
    }
    
    // **********************************************************
    // DINAMİK HUE ÖZELLİĞİ: DIŞARIDAN HUE DEĞERİNİ AYARLAMA METODU
    // **********************************************************
    /**
     * Saturation gradyanının son rengi için kullanılacak Hue değerini ayarlar.
     * @param hue Yeni Hue değeri (0.0f - 1.0f arasında)
     */
    public void setCurrentHue(float hue) {
        // Gelen değer 0-360 arası bir integer ise, 0.0f-1.0f aralığına çevirmelisiniz.
        // Eğer 0.0f - 1.0f aralığında geliyorsa direkt kullanın.
        float normalizedHue = hue; // Varsayılan olarak 0.0f - 1.0f geldiğini varsayalım.
        // Eğer 0-360 arası geliyorsa: float normalizedHue = hue / 360.0f;

        if (this.currentHue != normalizedHue) {
            this.currentHue = normalizedHue;
            // Hue değiştiğinde gradyanı yeniden çizmek için repaint çağrılır.
            slider.repaint(); 
        }
    }
    
    // **********************************************************
    // BOYUTLANDIRMA VE İZ BIRAKMAMA ÇÖZÜMLERİ
    // **********************************************************
    
    @Override
    public void setThumbLocation(int x, int y) {
        // İz bırakmama çözümü: Eski alanı yeniden çizmeye zorlar.
        Rectangle oldBounds = new Rectangle(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        oldBounds.grow(5, 5); 
        super.setThumbLocation(x, y);
        slider.repaint(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        // Thumb'ı Track'in ortasına hizala
        int trackHeight = 10; // (Hue Slider'dan gelen varsayım)
        int trackY = trackRect.y + trackRect.height / 2 - 5; // (Hue Slider'dan gelen varsayım)
        
        // Track'i yeniden hesaplamadığımız için thumbRect.y'yi trackRect'in dikey ortasına sabitleyelim
        thumbRect.y = trackRect.y + (trackRect.height / 2) - (thumbRect.height / 2);
    }

    // Bu metodun erişim belirleyicisi BasicSliderUI'da protected'dır.
    // Protected olması için public'ten protected'a çevrildi.
    protected Insets getContentInsets(JSlider slider) {
        return new Insets(5, 0, 5, 0); 
    }
    
    // Güvenlik temizliği (CustomSlider opak olduğu için bu sadece bir güvenlik adımıdır)
    @Override
    public void paint(Graphics g, javax.swing.JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }


    // **********************************************************
    // TRACK ÇİZİMİ (SATURATION GRADYAN - currentHue kullanılır)
    // **********************************************************
    
    @Override
    public void paintTrack(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Track'i ortala (Hue Slider'daki varsayımlara göre)
            int trackY = trackRect.y + trackRect.height / 2 - 5; 
            int trackHeight = 10; 

            // Track alanını temizle
            g2d.setColor(slider.getBackground());
            g2d.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);

            // Renk gradyanını çizme
            float step = 1.0f / (float) trackRect.width; // Her piksel için doygunluk artışı

            for (int i = 0; i < trackRect.width; i++) {
                float saturation = i * step; // Saturation değeri 0.0f (gri) ile 1.0f (tam renk) arasında
                
                // CRITICAL: currentHue kullanılır.
                // Parlaklık B her zaman 1.0f (tam parlaklık) olmalıdır.
                Color color = Color.getHSBColor(currentHue, saturation, 1.0f);
                
                g2d.setColor(color);
                g2d.drawLine(trackRect.x + i, trackY, trackRect.x + i, trackY + trackHeight);
            }

            // Çerçeve çizimi
            g2d.setColor(Color.BLACK);
            Rectangle2D track = new Rectangle2D.Float(
                trackRect.x, trackY, 
                trackRect.width, trackHeight
            );
            g2d.draw(track);
            
            g2d.dispose();
            
        } else {
            super.paintTrack(g);
        }
    }

    // **********************************************************
    // THUMB ÇİZİMİ
    // **********************************************************
    
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // İşaretçi alanını temizle
        g2d.setColor(slider.getBackground());
        g2d.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

        // İşaretçiyi thumbRect'in tam ortasına odakla
        int x = thumbRect.x + thumbRect.width / 2;
        int y = thumbRect.y + thumbRect.height / 2; 
        int thumbSize = THUMB_SIZE; 
        
        // Dış Beyaz Halka
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - thumbSize/2, y - thumbSize/2, thumbSize, thumbSize);
        
        // İç Siyah Nokta veya Çizgi
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(x - thumbSize/2, y - thumbSize/2, thumbSize, thumbSize);
        
        g2d.dispose();
    }
}