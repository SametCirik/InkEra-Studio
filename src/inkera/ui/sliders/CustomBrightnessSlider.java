package inkera.ui.sliders;

/**
 * Parlaklık (Brightness/Value) değerini kontrol eden özel JSlider. (0 - 100)
 * Arka planında siyahtan beyaza doğru parlaklık gradyanı bulunur.
 */
public class CustomBrightnessSlider extends CustomSlider {
    
    private static final int MIN = 0;
    private static final int MAX = 100; // Parlaklık genellikle %0'dan %100'e kadar ifade edilir.
    private static final int INITIAL = 100; // Başlangıçta tam parlak varsayalım

    public CustomBrightnessSlider() {
        super(CustomSlider.TYPE_BRIGHTNESS, MIN, MAX, INITIAL);
        
        // BRIGHTNESS SLIDER'A ÖZEL GÖRSEL UI ATAMASI
        setUI(new CustomBrightnessSliderUI(this));

        setPaintTicks(false);
        setPaintLabels(false);
    }
}