package inkera.languages; // Yeni bir paket önerisi: inkera.ui.components

import inkera.languages.Languages;
import inkera.util.ColorConsole; // Konsol çıktısı için

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LanguageSelectionPanel extends JPanel {

    private JButton languageToggleButton;
    private JComboBox<String> languageComboBox;
    private Languages languageManager;
    private Runnable onLanguageChangeCallback; // Dil değiştiğinde çalışacak metot

    public LanguageSelectionPanel(Languages langManager, Runnable onLanguageChangeCallback) {
        this.languageManager = langManager;
        this.onLanguageChangeCallback = onLanguageChangeCallback;
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

        languageToggleButton = new JButton("Dil");
        languageToggleButton.setFocusable(false);
        languageToggleButton.setMargin(new Insets(2, 5, 2, 5));
        languageToggleButton.setBackground(Color.decode("#3E3E3E"));
        languageToggleButton.setForeground(Color.decode("#B0B0B0"));
        languageToggleButton.setBounds(langPanelPadding, langPanelPadding, langButtonWidth, langComponentHeight);
        add(languageToggleButton);

        // JComboBox için gösterilecek dil adları. Bunların Languages enum'ı ile tam olarak eşleşmesi gerekiyor.
        String[] displayLanguages = { "English", "العربية", "日本語", "Русский", "Türkçe" };
        languageComboBox = new JComboBox<>(displayLanguages);
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
                switch (selectedDisplayLanguage) {
                    case "English": newLang = Languages.LanguageEnum.ENGLISH; break;
                    case "العربية": newLang = Languages.LanguageEnum.ARABIC; break;
                    case "日本語": newLang = Languages.LanguageEnum.JAPANESE; break;
                    case "Русский": newLang = Languages.LanguageEnum.RUSSIAN; break;
                    case "Türkçe": newLang = Languages.LanguageEnum.TURKISH; break;
                }
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
    public void updateLanguageDisplay() {
        // Dil seçici butonun metnini mevcut dile göre güncelle (iki harfli kod)
        if (languageToggleButton != null) {
            switch (languageManager.getCurrentLanguage()) {
                case ARABIC: languageToggleButton.setText("AR"); break;
                case ENGLISH: languageToggleButton.setText("EN"); break;
                case JAPANESE: languageToggleButton.setText("JP"); break;
                case RUSSIAN: languageToggleButton.setText("RU"); break;
                case TURKISH: languageToggleButton.setText("TR"); break;
                default: languageToggleButton.setText("Dil");
            }
        }
        // JComboBox'ta mevcut dili seçili olarak ayarla
        if (languageComboBox != null) {
            switch (languageManager.getCurrentLanguage()) {
                case ARABIC: languageComboBox.setSelectedItem("العربية"); break;
                case ENGLISH: languageComboBox.setSelectedItem("English"); break;
                case JAPANESE: languageComboBox.setSelectedItem("日本語"); break;
                case RUSSIAN: languageComboBox.setSelectedItem("Русский"); break;
                case TURKISH: languageComboBox.setSelectedItem("Türkçe"); break;
            }
        }
    }
}