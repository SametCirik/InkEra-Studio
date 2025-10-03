package inkera.ui.sliders;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle; // Eklenen yeni import
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * HueSlider için özel UI sınıfı.
 * Track (Yol) üzerine gökkuşağı (Hue) renk gradyanı çizer.
 * Slider'ın hareket ederken "iz" bırakması sorununu, yeniden çizim alanını (thumbRect)
 * doğru boyutlandırarak ve zorunlu temizlik yaparak çözer.
 */
public class CustomHueSliderUI extends BasicSliderUI {

    // İşaretçinin (Thumb) boyutunu sabit tutuyoruz.
    private static final int THUMB_SIZE = 14; 
    
    public CustomHueSliderUI(JSlider slider) {
        super(slider);
    }

    // **********************************************************
    // KRİTİK ÇÖZÜM 5: THUMB HAREKETİNİ YÖNETMEK VE ESKİ ALANI TEMİZLEMEYİ ZORUNLU KILMAK
    // Bu metod, sağdan sola hareket ederken eksik kalan temizliği garanti eder.
    // **********************************************************
    
    public void setThumbLocation(int x, int y) {
        // 1. Konum güncellenmeden önce mevcut (eski) thumbRect'in sınırlarını alıyoruz.
        // thumbRect'in boyutunu 5 piksel büyüterek, taşan izleri de kapsamasını sağlıyoruz.
        Rectangle oldBounds = new Rectangle(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        oldBounds.grow(5, 5); // 5 piksellik ekstra pay bırakıyoruz.

        // 2. super metodu çağırarak thumbRect'i yeni konuma taşıyoruz.
        // Bu, yeni alanı çizim için işaretler.
        super.setThumbLocation(x, y);

        // 3. Eski alanı (oldBounds) zorunlu olarak yeniden çizim listesine ekliyoruz.
        // Bu, özellikle sağdan sola hareket ederken atlanan alanın temizlenmesini sağlar.
        slider.repaint(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height);
    }
    
    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        
        // thumbRect'in dikey ortasını, track'in dikey ortasına sabitler.
        thumbRect.y = trackRect.y + (trackRect.height / 2) - (thumbRect.height / 2);
    }


    // **********************************************************
    // HATA DÜZELTMESİ: Erişim belirleyicisini 'protected' olarak ayarlama.
    // **********************************************************
    
    protected Insets getContentInsets(JSlider slider) {
        // Üst ve alt kenarlara boşluk ekleyerek Thumb'ın sığacağı alanı genişletir.
        return new Insets(5, 0, 5, 0); 
    }
    
    // GÜVENLİK TEMİZLİĞİ 1: Tüm bileşeni temizle.
    @Override
    public void paint(Graphics g, javax.swing.JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }
    
    @Override
    public void paintTrack(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int trackY = trackRect.y + trackRect.height / 2 - 5; 
            int trackHeight = 10; 

            // GÜVENLİK TEMİZLİĞİ 2: Track alanını temizle.
            g2d.setColor(slider.getBackground());
            g2d.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);

            // Renk gradyanını çizme
            float step = 1.0f / (float) trackRect.width; 
            for (int i = 0; i < trackRect.width; i++) {
                float hue = i * step; 
                Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
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

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // GÜVENLİK TEMİZLİĞİ 3: Thumb alanını temizle.
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
