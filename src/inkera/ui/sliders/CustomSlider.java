package inkera.ui.sliders;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Uygulama genelinde kullanılacak özelleştirilmiş temel JSlider sınıfı.
 * JSlider'ın yeniden çizim (repaint) sorunlarını çözmek için opaklığı (opaque) etkinleştirir.
 */
public class CustomSlider extends JSlider {

    // Slider'ın hangi HSB değerini temsil ettiğini belirten sabitler
    public static final int TYPE_HUE = 0;
    public static final int TYPE_SATURATION = 1;
    public static final int TYPE_BRIGHTNESS = 2;

    private final int sliderType;

    /**
     * CustomSlider kurucusu.
     * * @param type Slider'ın HSB türü (TYPE_HUE, TYPE_SATURATION, TYPE_BRIGHTNESS)
     * @param min Minimum değer
     * @param max Maksimum değer
     * @param initial Başlangıç değeri
     */
    public CustomSlider(int type, int min, int max, int initial) {
        super(min, max, initial);
        this.sliderType = type;
        
        // Temel ayarlar
        // ******************************************************
        // KRİTİK DEĞİŞİKLİK: setOpaque(false) -> setOpaque(true)
        // Opaklığı etkinleştirerek Swing'in bileşenin arka planını temizlemesini sağlarız.
        setOpaque(true); 
        // ******************************************************
        
        setFocusable(false);
        // Arka plan rengini (#3A3A3A) SidePanelBrush'daki koyu gri panele uydurur.
        // Opaklık True olduğu için bu renk görünür olacaktır.
        setBackground(Color.decode("#3A3A3A")); 
        setForeground(Color.decode("#B0B0B0")); // Etiket/İşaretçi rengi
        
        // NOT: CustomHueSliderUI içindeki temizleme kodunu (g2d.fillRect("#3A3A3A"))
        // bu değişiklikle birlikte kaldırmak isteyebilirsiniz, ancak şimdilik kalsın,
        // iki kat temizlik garanti olsun.
    }

    public int getSliderType() {
        return sliderType;
    }
}