package inkera.languages; // Yeni bir paket önerisi: inkera.ui.components

import inkera.util.ColorConsole; // Konsol çıktısı için

import javax.swing.*;
import java.awt.*;

public class LanguageSelectionPanel extends JPanel {

    private final JButton languageToggleButton;
    private final JComboBox<String> languageComboBox;
    private final Languages languageManager;
    private final Runnable onLanguageChangeCallback; // Dil değiştiğinde çalışacak metot

    public LanguageSelectionPanel(Languages langManager, Runnable onLanguageChangeCallback) {
        this.languageManager = langManager;
        this.onLanguageChangeCallback = onLanguageChangeCallback;
        
        // Initialize UI components
        languageToggleButton = new JButton("Dil");
        languageComboBox = new JComboBox<>(new String[] { "English", "العربية", "日本語", "Русский", "Türkçe" });
        
        initializePanel();
        updateLanguageDisplay(); // Başlangıçta doğru dilin görünmesini sağla
    }

    private void initializePanel() {
        // LayoutManager'ı null olarak ayarlıyoruz çünkü MainMenuFrame içinde setBounds ile konumlandırılacak.
        // Eğer bu paneli kendi içinde düzenlemek isterseniz, burada bir LayoutManager (örn. FlowLayout) kullanmalısınız.
        setLayout(null); // veya FlowLayout, GridLayout gibi bir layout seçebilirsiniz.
        setOpaque(false); // Arka planını şeffaf yap, böylece ana panelin rengini alır.

        int langButtonWidth = 60;
        int langComboWidth = 110;
        int langComponentHeight = 25;
        int langPanelPadding = 0; // Bu panelin kendi içindeki konumlandırma için padding (dışarıdan verilecek)
        int langElementSpacing = 5;

        // Button was already initialized in constructor
        languageToggleButton.setFocusable(false);
        languageToggleButton.setMargin(new Insets(2, 5, 2, 5));
        languageToggleButton.setBackground(Color.decode("#3E3E3E"));
        languageToggleButton.setForeground(Color.decode("#B0B0B0"));
        languageToggleButton.setBounds(langPanelPadding, langPanelPadding, langButtonWidth, langComponentHeight);
        add(languageToggleButton);

        // JComboBox was already initialized in constructor
        languageComboBox.setVisible(false);
        languageComboBox.setFocusable(false);
        languageComboBox.setBounds(langPanelPadding + langButtonWidth + langElementSpacing,
                                   langPanelPadding,
                                   langComboWidth,
                                   langComponentHeight);
        add(languageComboBox);

        languageToggleButton.addActionListener(e -> {
            languageComboBox.setVisible(!languageComboBox.isVisible());
            // JComboBox'ın görünürlüğü değiştiğinde, panelin kendisini ve ebeveynini yeniden doğrula ve boya
            revalidate();
            repaint();
        });

        languageComboBox.addActionListener(e -> {
            String selectedDisplayLanguage = (String) languageComboBox.getSelectedItem();
            Languages.LanguageEnum newLang = languageManager.getCurrentLanguage(); // Mevcut dil varsayılan
            if (selectedDisplayLanguage != null) {
                newLang = switch (selectedDisplayLanguage) {
                    case "English" -> Languages.LanguageEnum.ENGLISH;
                    case "العربية" -> Languages.LanguageEnum.ARABIC;
                    case "日本語" -> Languages.LanguageEnum.JAPANESE;
                    case "Русский" -> Languages.LanguageEnum.RUSSIAN;
                    case "Türkçe" -> Languages.LanguageEnum.TURKISH;
                    default -> languageManager.getCurrentLanguage();
                };
                if (newLang != languageManager.getCurrentLanguage()) {
                    languageManager.setLanguage(newLang);
                    System.out.println(ColorConsole.ANSI_GREEN + "UI Language changed to: " + newLang.name() + ColorConsole.ANSI_RESET);
                    if (onLanguageChangeCallback != null) {
                        onLanguageChangeCallback.run(); // MainMenuFrame'deki updateTexts'i çağır
                    }
                }
                languageComboBox.setVisible(false); // Dil seçildikten sonra ComboBox'ı gizle
            }
        });
    }

    /**
     * Dil değiştirme butonu ve JComboBox'taki dil gösterimini günceller.
     * Bu metot, dil değiştiğinde dışarıdan çağrılmalıdır.
     */
    public final void updateLanguageDisplay() {
        // Dil seçici butonun metnini sabit "Dil" olarak ayarla
        if (languageToggleButton != null) {
            languageToggleButton.setText("Dil");
        }
        
        // JComboBox'ta mevcut dili seçili olarak ayarla
        if (languageComboBox != null) {
            String selected = switch (languageManager.getCurrentLanguage()) {
                case ARABIC: yield "العربية";
                case ENGLISH: yield "English";
                case JAPANESE: yield "日本語";
                case RUSSIAN: yield "Русский";
                case TURKISH: yield "Türkçe";
            };
            languageComboBox.setSelectedItem(selected);
        }
    }
}