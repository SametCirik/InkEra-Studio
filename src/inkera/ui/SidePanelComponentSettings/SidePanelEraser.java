package inkera.ui.SidePanelComponentSettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import inkera.languages.Languages;
import inkera.ui.sliders.CustomBrightnessSlider;
import inkera.ui.sliders.CustomHueSlider;
import inkera.ui.sliders.CustomRGBSlider;
import inkera.ui.sliders.CustomRGBSliderUI;
import inkera.ui.sliders.CustomSaturationSlider;
import inkera.ui.sliders.CustomSaturationSliderUI;

public class SidePanelEraser extends JPanel {

    // --- SINIF ALANLARI ---
    private boolean isUpdating = false;

    // Ortak Renk Bileşenleri
    private ColorWheelPanel colorWheel;
    private JLabel hexCodeLabel;
    private JPanel oldColorPreview;
    private JPanel newColorPreview;
    private Color oldColor = Color.BLACK;

    // HSB Slider'ları
    private CustomHueSlider hueSlider;
    private CustomSaturationSlider saturationSlider;
    private CustomBrightnessSlider brightnessSlider;

    // RGB Slider'ları
    private CustomRGBSlider redSlider;
    private CustomRGBSlider greenSlider;
    private CustomRGBSlider blueSlider;

    // --- DEĞİŞİKLİK: Sadece iki ana panel kaldı ---
    private final JPanel colorPanel;
    private final JPanel propertiesPanel;
    
 // SidePanelBrush sınıf alanlarına ekleyin
    private JTabbedPane brushTabsPanel; // Fırça listesi sekmelerini tutar
    private JPanel brushSettingsContainer; // Fırça listesi veya ayarlar panelini tutacak merkezi bileşen

    public SidePanelEraser() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // --- DEĞİŞİKLİK: Paneller yeniden adlandırıldı ve "C" paneli kaldırıldı ---
        colorPanel = createPanel("Renk Ayarları");
        propertiesPanel = createPanel("Fırça Özellikleri");
            
        // --- DEĞİŞİKLİK: Panel boyutları ayarlandı. Alt panel daha uzun. ---
        colorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        
    /*
        propertiesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600)); // Bu panelin daha fazla büyümesine izin ver
	*/
        
        add(colorPanel);
        add(Box.createVerticalStrut(5));
        add(propertiesPanel);
        // presetsPanelC ile ilgili satırlar kaldırıldı.
    //  add(Box.createVerticalGlue()); // Bu satırı kaldırıyoruz!

        setupColorPanel();
        setupPropertiesPanel(); 
    }

    private JPanel createPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#3A3A3A"));
    //  panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Renk ayarları panelini yapılandırır.
     * Ortak bileşenler (tekerlek, bilgi paneli) üste, sekmeler alta yerleştirilir.
     */
    private void setupColorPanel() {
        // --- 1. TÜM BİLEŞENLERİ OLUŞTUR ---
        
        hueSlider = new CustomHueSlider();
        saturationSlider = new CustomSaturationSlider();
        brightnessSlider = new CustomBrightnessSlider();
        redSlider = new CustomRGBSlider("R");
        greenSlider = new CustomRGBSlider("G"); 
        blueSlider = new CustomRGBSlider("B");

        Color initialColor = Color.getHSBColor(
            hueSlider.getValue() / 360.0f,
            saturationSlider.getValue() / 100.0f,
            brightnessSlider.getValue() / 100.0f
        );

        JPanel colorInfoPanel = createColorInfoPanel(initialColor);
        Consumer<Color> colorWheelListener = newColor -> updateAllColorModels(newColor, colorWheel);
        colorWheel = new ColorWheelPanel(colorWheelListener);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.decode("#3A3A3A"));
        tabbedPane.setForeground(Color.decode("#B0B0B0"));

        tabbedPane.addTab("HSB", createHSBPanel());
        tabbedPane.addTab("RGB", createRGBPanel());
        
        JPanel palettePanel = new JPanel();
        palettePanel.add(new JLabel("Renk Paleti"));
        palettePanel.setBackground(Color.decode("#3A3A3A"));
        tabbedPane.addTab("Palet", palettePanel);
        
        addHSBSliderListeners();
        addRGBSliderListeners();

        // --- 2. FİNAL YERLEŞİMİ KUR ---
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 240));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        topPanel.setBackground(Color.decode("#454545"));
        topPanel.add(colorWheel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 475));
        bottomPanel.setBackground(Color.decode("#3A3A3A"));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(colorInfoPanel, BorderLayout.NORTH);
        bottomPanel.add(tabbedPane, BorderLayout.CENTER);

        colorPanel.removeAll();
        colorPanel.add(topPanel, BorderLayout.NORTH);
        colorPanel.add(bottomPanel, BorderLayout.CENTER);
        
        updateAllColorModels(initialColor, this);

        colorPanel.revalidate();
        colorPanel.repaint();
    }

    /**
     * SADECE HSB slider'larını içeren bir panel oluşturur.
     */
    private JPanel createHSBPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode("#3A3A3A"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(new JLabel("Hue (Ton):"));
        panel.add(hueSlider);
        panel.add(Box.createVerticalStrut(5));
        
        panel.add(new JLabel("Saturation (Doygunluk):"));
        panel.add(saturationSlider);
        panel.add(Box.createVerticalStrut(5));

        panel.add(new JLabel("Brightness (Parlaklık):"));
        panel.add(brightnessSlider);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * SADECE RGB slider'larını içeren bir panel oluşturur.
     */
    private JPanel createRGBPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode("#3A3A3A"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(new JLabel("Red (Kırmızı):"));
        panel.add(redSlider);
        panel.add(Box.createVerticalStrut(5));
        
        panel.add(new JLabel("Green (Yeşil):"));
        panel.add(greenSlider);
        panel.add(Box.createVerticalStrut(5));

        panel.add(new JLabel("Blue (Mavi):"));
        panel.add(blueSlider);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    
    /**
     * Hex kodu ve renk önizleme kutucuklarını içeren paneli oluşturur.
     */
    private JPanel createColorInfoPanel(Color initialColor) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.setBackground(Color.decode("#3A3A3A"));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        String initialHex = String.format("#%06X", (0xFFFFFF & initialColor.getRGB()));
        hexCodeLabel = new JLabel(initialHex);
        hexCodeLabel.setForeground(Color.decode("#B0B0B0"));
        
        JPanel previews = new JPanel(new GridLayout(1, 2, 5, 0));
        previews.setBackground(Color.decode("#3A3A3A"));
        previews.setMaximumSize(new Dimension(70, 25));
        
        oldColorPreview = new JPanel();
        oldColorPreview.setBackground(oldColor);
        oldColorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        newColorPreview = new JPanel();
        newColorPreview.setBackground(initialColor);
        newColorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        previews.add(oldColorPreview);
        previews.add(newColorPreview);

        infoPanel.add(previews);
        infoPanel.add(Box.createHorizontalStrut(10));
        infoPanel.add(hexCodeLabel);
        infoPanel.add(Box.createHorizontalGlue());
        
        return infoPanel;
    }

    /**
     * HSB slider'ları için ChangeListener ekler.
     */
    private void addHSBSliderListeners() {
        ChangeListener hsbListener = e -> {
            float h = hueSlider.getValue() / 360.0f;
            float s = saturationSlider.getValue() / 100.0f;
            float b = brightnessSlider.getValue() / 100.0f;
            Color newColor = Color.getHSBColor(h, s, b);
            updateAllColorModels(newColor, e.getSource());
        };
        hueSlider.addChangeListener(hsbListener);
        saturationSlider.addChangeListener(hsbListener);
        brightnessSlider.addChangeListener(hsbListener);
    }
    
    /**
     * RGB slider'ları için ChangeListener ekler.
     */
    private void addRGBSliderListeners() {
        ChangeListener rgbListener = e -> {
            Color newColor = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
            updateAllColorModels(newColor, e.getSource());
        };
        redSlider.addChangeListener(rgbListener);
        greenSlider.addChangeListener(rgbListener);
        blueSlider.addChangeListener(rgbListener);
    }

    /**
     * MERKEZİ GÜNCELLEME METODU
     */
    private void updateAllColorModels(Color newColor, Object source) {
        if (newColorPreview == null || hueSlider == null || redSlider == null) {
            return;
        }
        
        if (isUpdating) return;
        isUpdating = true;

        try {
            oldColor = newColorPreview.getBackground();
            oldColorPreview.setBackground(oldColor);
            
            hexCodeLabel.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
            newColorPreview.setBackground(newColor);
            
            float[] hsb = Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), null);
            
            if (source != colorWheel) {
                colorWheel.setHSB(hsb[0], hsb[1], hsb[2]);
            }

            if (source != hueSlider && source != saturationSlider && source != brightnessSlider) {
                hueSlider.setValue(Math.round(hsb[0] * 360));
                saturationSlider.setValue(Math.round(hsb[1] * 100));
                brightnessSlider.setValue(Math.round(hsb[2] * 100));
            }

            if (source != redSlider && source != greenSlider && source != blueSlider) {
                redSlider.setValue(newColor.getRed());
                greenSlider.setValue(newColor.getGreen());
                blueSlider.setValue(newColor.getBlue());
            }
            
            if (saturationSlider.getUI() instanceof CustomSaturationSliderUI) {
                ((CustomSaturationSliderUI) saturationSlider.getUI()).setCurrentHue(hsb[0]);
            }
            
            if (redSlider.getUI() instanceof CustomRGBSliderUI) {
                ((CustomRGBSliderUI) redSlider.getUI()).setOtherColorComponents(newColor.getGreen(), newColor.getBlue());
                ((CustomRGBSliderUI) greenSlider.getUI()).setOtherColorComponents(newColor.getRed(), newColor.getBlue());
                ((CustomRGBSliderUI) blueSlider.getUI()).setOtherColorComponents(newColor.getRed(), newColor.getGreen());
            }

        } finally {
            isUpdating = false;
        }
    }
    
    /**
     * Alt panel (propertiesPanel) için fırça önizlemesi ve fırça listesi sekmelerini kurar.
     */
    private void setupPropertiesPanel() {
        propertiesPanel.removeAll(); // Temizle

        // 1. Üst Panelcik: Seçili Fırça Önizlemesi
        JPanel brushPreviewPanel = createBrushPreviewPanel();
        
        // 2. Alt Panelcik: Sekmeli Fırça Listesi (Scroll View ile)
        brushTabsPanel = createBrushTabs(); // Sekmeli paneli oluştur

        // Ayarlar/Liste geçişini yönetecek konteyner oluştur
        brushSettingsContainer = new JPanel(new BorderLayout());
        brushSettingsContainer.setBackground(Color.decode("#3A3A3A"));
        
        // Başlangıçta, konteynere fırça sekmelerini ekle
        brushSettingsContainer.add(brushTabsPanel, BorderLayout.CENTER);
        
        // Alt panelin yerleşimi: Önizleme üstte, Konteyner altta
        propertiesPanel.add(brushPreviewPanel, BorderLayout.NORTH);
        propertiesPanel.add(brushSettingsContainer, BorderLayout.CENTER); // BURADA KONTEYNER KULLANILIYOR

        propertiesPanel.revalidate();
        propertiesPanel.repaint();
    }
    
    /**
     * Seçili fırçanın büyük önizlemesini gösteren paneli oluşturur.
     */
    private JPanel createBrushPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#454545"));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100)); // Yüksekliği ayarla
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel previewLabel = new JLabel("SEÇİLİ SİLGİ ÖN İZLEMESİ BURADA", SwingConstants.CENTER);
        previewLabel.setForeground(Color.WHITE);
        panel.add(previewLabel, BorderLayout.CENTER);
        
        // Başlık/İsim (Örn: Dijital kalem) için küçük bir alan
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.decode("#3A3A3A"));
        JLabel titleLabel = new JLabel("Dijital kalem");
        titleLabel.setForeground(Color.decode("#B0B0B0"));
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.SOUTH);

        return panel;
    }
    
    /**
     * Temel ve Özel fırça listesi sekmelerini oluşturur (scroll view içerir).
     */
    private JTabbedPane createBrushTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.decode("#3A3A3A"));
        tabbedPane.setForeground(Color.decode("#B0B0B0"));

        // --- TEMEL SEKME ---
        // Gerçek fırça listesi için bir ScrollPane kullanıyoruz
        JPanel basicBrushList = createSampleBrushList(); 
        JScrollPane basicScrollPane = new JScrollPane(basicBrushList);
        basicScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        basicScrollPane.setBorder(null); // Çirkin kenarlıkları kaldır
        basicScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Hızlı kaydırma için
        
        tabbedPane.addTab("Temel", basicScrollPane);

        // --- ÖZEL SEKME ---
        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(Color.decode("#3A3A3A"));
        customPanel.add(new JLabel("Özel Fırçalar Buraya Gelecek", SwingConstants.CENTER), BorderLayout.CENTER);
        
        tabbedPane.addTab("Özel", customPanel);
        
        // Çevrim İçi sekmesi eklenebilir
        // tabbedPane.addTab("Çevrim İçi", createOnlinePanel());

        return tabbedPane;
    }

    /**
     * Örnek fırça listesi öğelerini oluşturan bir panel.
     * Daha fazla kaydırılabilir içerik sağlamak için listeyi uzatırız.
     */
    private JPanel createSampleBrushList() {
        JPanel listPanel = new JPanel();
        // Alt alta yerleşim için BoxLayout
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); 
        listPanel.setBackground(Color.decode("#3A3A3A")); 
        
        // Örnek Fırça Öğeleri ekleme (A, B, C, ...)
        String[] brushNames = {
            "Keçeli kalem (Yumuşak)", "Keçeli kalem (Sert)", "Kalem (Soluk)", 
            "Dijital kalem", "Boya tabancası (Normal)", "Boya tabancası (Üçgen)"
        };
        
        // Fırça listesini uzatmak için 5 kez döngü yapalım
        for (int i = 0; i < 5; i++) {
            for (String name : brushNames) {
                // Fırça boyutunu indeksle biraz değiştirelim
                double size = 50.0 + (i * 5) + Math.random() * 10;
                size = Math.round(size * 10.0) / 10.0; // Bir ondalık basamağa yuvarla
                
                listPanel.add(createBrushListItem(name + " (" + (i + 1) + ")", size));
            }
        }
        
        // Artık VerticalGlue'a gerek yok, çünkü listPanel ScrollPane içinde olduğu için
        // dikeyde kapladığı alan kadar büyür.

        return listPanel;
    }

    /**
     * Tek bir fırça listesi öğesini (önizleme, isim, ayar butonu) oluşturur.
     */
    private JPanel createBrushListItem(String name, double size) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Sabit yükseklik
        itemPanel.setBackground(Color.decode("#3A3A3A"));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Sol: Artı İkonu (Yer Tutucu)
        JLabel plusIcon = new JLabel("+");
        plusIcon.setForeground(Color.decode("#B0B0B0"));
        itemPanel.add(plusIcon, BorderLayout.WEST);

        // Orta: Fırça Adı ve Önizlemesi (Basit bir fırça önizlemesi çizmek için özel bir Component gerekir)
        JLabel nameLabel = new JLabel(name); 
        nameLabel.setForeground(Color.decode("#B0B0B0"));
        itemPanel.add(nameLabel, BorderLayout.CENTER);
        
        // Sağ: Ayar Bilgisi (Örn: Boyut) ve Ayar Butonu
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        
        JLabel sizeLabel = new JLabel(String.valueOf(size));
        sizeLabel.setForeground(Color.decode("#B0B0B0"));
        
        // ≡ İkonu (Şimdilik > ile temsil edelim)
        JLabel settingsIcon = new JLabel(">"); 
        settingsIcon.setForeground(Color.decode("#B0B0B0"));
        
        rightPanel.add(sizeLabel);
        rightPanel.add(settingsIcon);
        
        itemPanel.add(rightPanel, BorderLayout.EAST);

        // Tıklanma efekti veya seçilme durumu için MouseListener eklenebilir.
        // Örn: itemPanel.addMouseListener(...)

        return itemPanel;
    }
}

