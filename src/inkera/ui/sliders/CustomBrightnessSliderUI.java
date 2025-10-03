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
 * BrightnessSlider için özel UI sınıfı.
 * Track (Yol) üzerine siyahtan beyaza doğru Parlaklık (Brightness) gradyanı çizer.
 */
public class CustomBrightnessSliderUI extends BasicSliderUI {

    private static final int THUMB_SIZE = 14; 

    public CustomBrightnessSliderUI(JSlider slider) {
        super(slider);
    }
    
    // **********************************************************
    // BOYUTLANDIRMA VE İZ BIRAKMAMA ÇÖZÜMLERİNİ KOPYALIYORUZ
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
        thumbRect.y = trackRect.y + (trackRect.height / 2) - (thumbRect.height / 2);
    }

    protected Insets getContentInsets(JSlider slider) {
        return new Insets(5, 0, 5, 0); 
    }
    
    // Güvenlik temizliği
    @Override
    public void paint(Graphics g, javax.swing.JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }


    // **********************************************************
    // TRACK ÇİZİMİ (BRIGHTNESS GRADYAN: SİYAH -> BEYAZ)
    // **********************************************************
    
    @Override
    public void paintTrack(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int trackY = trackRect.y + trackRect.height / 2 - 5; 
            int trackHeight = 10; 

            // Track alanını temizle
            g2d.setColor(slider.getBackground());
            g2d.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);

            // Renk gradyanını çizme
            float step = 1.0f / (float) trackRect.width; // Her piksel için parlaklık artışı

            for (int i = 0; i < trackRect.width; i++) {
                float brightness = i * step; // Parlaklık değeri 0.0f (siyah) ile 1.0f (beyaz) arasında
                
                // HSB'den rengi al (Hue=0.0f, Saturation=0.0f, Brightness=değişken)
                // Hue ve Saturation sıfır olduğunda, sonuç gri tonu (siyah-beyaz) olacaktır.
                Color color = Color.getHSBColor(0.0f, 0.0f, brightness);
                
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
    // THUMB ÇİZİMİ (Diğer Slider'larla aynı)
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