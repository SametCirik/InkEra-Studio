package inkera.ui.sliders;

/**
 * Kırmızı (R), Yeşil (G) veya Mavi (B) değerini kontrol eden özel JSlider. (0 - 255)
 * Arka planında, diğer iki renk bileşenine bağlı olarak değişen bir gradyan bulunur.
 */
public class CustomRGBSlider extends CustomSlider {
    
    private static final int MIN = 0;
    private static final int MAX = 255; // RGB değerleri 0-255 arasındadır.
    private static final int INITIAL = 128; // Varsayılan başlangıç değeri

    /**
     * Belirtilen tipe göre bir RGB slider oluşturur.
     * @param sliderType "R", "G", veya "B" olmalıdır.
     */
    public CustomRGBSlider(String sliderType) {
        // CustomSlider'ın kurucu metodunu çağırıyoruz. TYPE_HUE sadece bir yer tutucudur,
        // asıl mantık UI sınıfında işlenecektir.
        super(CustomSlider.TYPE_HUE, MIN, MAX, INITIAL);
        
        // RGB SLIDER'A ÖZEL GÖRSEL UI ATAMASI
        setUI(new CustomRGBSliderUI(this, sliderType));

        setPaintTicks(false);
        setPaintLabels(false);
    }
    
    // SidePanelBrush sınıfından UI'a kolayca erişmek için bir yardımcı metot
    public CustomRGBSliderUI getSliderUI() {
        return (CustomRGBSliderUI) getUI();
    }
}
