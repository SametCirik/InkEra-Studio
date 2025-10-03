package inkera.ui.sliders;

/**
 * Doygunluk (Saturation) değerini kontrol eden özel JSlider. (0 - 100)
 * Arka planında seçilen Hue'ya göre doygunluk gradyanı bulunur.
 */
public class CustomSaturationSlider extends CustomSlider {
    
    private static final int MIN = 0;
    private static final int MAX = 100; // Doygunluk genellikle %0'dan %100'e kadar ifade edilir.
    private static final int INITIAL = 100; // Başlangıçta tam doygun varsayalım

    public CustomSaturationSlider() {
        super(CustomSlider.TYPE_SATURATION, MIN, MAX, INITIAL);
        
        // SATURATION SLIDER'A ÖZEL GÖRSEL UI ATAMASI
        setUI(new CustomSaturationSliderUI(this));

        setPaintTicks(false);
        setPaintLabels(false);
    }
}