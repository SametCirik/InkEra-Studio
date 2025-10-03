package inkera.ui.sliders;

/**
 * Hue değerini kontrol eden özel JSlider. (0 - 360)
 * Arka planında gökkuşağı gradyanı bulunur.
 */
public class CustomHueSlider extends CustomSlider {
    
    private static final int MIN = 0;
    private static final int MAX = 360;
    private static final int INITIAL = 0;

    public CustomHueSlider() {
        super(CustomSlider.TYPE_HUE, MIN, MAX, INITIAL);
        
        // HUE SLIDER'A ÖZEL GÖRSEL UI ATAMASI
        setUI(new CustomHueSliderUI(this));

        // Büyük değer aralığı nedeniyle, aralıklar (tick spacing) kaldırılabilir
        // çünkü gökkuşağı bandı etiketlerle karışabilir.
        setPaintTicks(false);
        setPaintLabels(false);
    }
}