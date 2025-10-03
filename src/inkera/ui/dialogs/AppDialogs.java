package inkera.ui.dialogs;

import inkera.main.DrawingWindow;
import inkera.main.MainMenuFrame;
import inkera.ui.placeholders.PlaceholderText;
import inkera.ui.titlebars.DialogTitleBar;
import inkera.languages.Languages;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class AppDialogs {

    private AppDialogs() {}

    public static class NewImageDialog extends JDialog {
        private JTextField nameField; // DÜZELTME: PlaceholderText yerine standart JTextField
        private JTextField widthField;
        private JTextField heightField;
        private JFrame ownerFrame;
        private boolean imageCreated = false;
        private Languages languageManager;
        
        // DÜZELTME: Gerekli olmayan referans kaldırıldı.
        // private MainMenuFrame mainMenuFrameInstance;

        // --- Dil Anahtarları (Bunları Languages.java'ya eklemelisiniz) ---
        // public static final String KEY_NEW_IMAGE_TITLE = "newImageTitle";
        // public static final String KEY_IMAGE_NAME = "imageNameLabel";
        // public static final String KEY_IMAGE_WIDTH = "imageWidthLabel";
        // public static final String KEY_IMAGE_HEIGHT = "imageHeightLabel";
        // public static final String KEY_CREATE_BUTTON = "createButton";
        // public static final String KEY_CANCEL_BUTTON = "cancelButton";


        public NewImageDialog(JFrame parentFrame, Languages languageManager) {
            super(parentFrame, "", true); // Başlık updateTexts ile ayarlanacak
            this.ownerFrame = parentFrame;
            this.languageManager = languageManager;
            initDialog();
            updateTexts(); // Metinleri yükle
        }

        private void initDialog() 
        {
        	setSize(352, 30 + 302); // Yüksekliği içeriğe göre ayarlıyoruz
            setUndecorated(true);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            setResizable(false);

            // DÜZELTME: SignInDialog ile aynı yapıyı kuruyoruz.
            JPanel rootPanel = new JPanel(new BorderLayout());
            rootPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));
            
            DialogTitleBar titleBar = new DialogTitleBar(this, "InkEra - New Image");
            rootPanel.add(titleBar, BorderLayout.NORTH);
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(null); // SignInDialog gibi null layout
            contentPanel.setBackground(Color.decode("#121212"));
            
            Color labelColor = Color.decode("#B0B0B0");
            Color fieldBgColor = Color.decode("#424242");
            Color fieldFgColor = Color.decode("#FFFFFF");
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

            nameField = new PlaceholderText("Image Name");
            nameField.setBounds(50, 50, 250, 30);
            nameField.setBackground(fieldBgColor); 
            nameField.setForeground(fieldFgColor);
            nameField.setCaretColor(Color.decode("#FFFFFF"));
            nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contentPanel.add(nameField);

            widthField = new PlaceholderText("Width");
            widthField.setBounds(50, 100, 250, 30);
            widthField.setBackground(fieldBgColor); 
            widthField.setForeground(fieldFgColor);
            widthField.setCaretColor(Color.decode("#FFFFFF"));
            widthField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contentPanel.add(widthField);
            
            heightField = new PlaceholderText("Height");
            heightField.setBounds(50, 150, 250, 30);
            heightField.setBackground(fieldBgColor); 
            heightField.setForeground(fieldFgColor);
            heightField.setCaretColor(Color.decode("#FFFFFF"));
            heightField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contentPanel.add(heightField);
            
            JButton createButton = new JButton("Create");
            createButton.setBounds(50, 220, 110, 30);
            createButton.setBackground(Color.decode("#388E3C")); 
            createButton.setForeground(Color.WHITE);
            createButton.addActionListener(e -> 
            {
            	createNewImage();
            });
            contentPanel.add(createButton);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(190, 220, 110, 30);
            cancelButton.setBackground(Color.decode("#C94C4C")); 
            cancelButton.setForeground(Color.WHITE);
            cancelButton.addActionListener(e -> 
            {
            	imageCreated = false; 
            	dispose();	
            });
            contentPanel.add(cancelButton);

            rootPanel.add(contentPanel, BorderLayout.CENTER);
            add(rootPanel, BorderLayout.CENTER);
        }

        // DÜZELTME: Dil güncelleme metodu eklendi.
        public void updateTexts() {
            if (languageManager == null) return;
            // Not: Bu anahtarları ("newImageTitle", "imageNameLabel" vb.)
            // Languages.java sınıfınıza eklemeniz gerekmektedir.
            
            // titleBar.setTitle(languageManager.getString("newImageTitle"));
            // nameLabel.setText(languageManager.getString("imageNameLabel"));
            // widthLabel.setText(languageManager.getString("imageWidthLabel"));
            // heightLabel.setText(languageManager.getString("imageHeightLabel"));
            // createButton.setText(languageManager.getString("createButton"));
            // cancelButton.setText(languageManager.getString("cancelButton"));
        }

        private void createNewImage() {
            try {
                String name = getProjectName();
                int width = getImageWidth();
                int height = getImageHeight();

                if (name.trim().isEmpty() || width <= 0 || height <= 0) {
                    JOptionPane.showMessageDialog(this, "Geçersiz resim adı veya boyutları.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                imageCreated = true;
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Genişlik veya yükseklik için geçersiz sayı formatı.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isImageCreated() { return imageCreated; }

        public String getProjectName() {
            return nameField.getText();
        }

        public int getImageWidth() {
            return Integer.parseInt(widthField.getText());
        }

        public int getImageHeight() {
            return Integer.parseInt(heightField.getText());
        }
    }
    
    // Diğer diyalog sınıfları buraya eklenebilir...
}
