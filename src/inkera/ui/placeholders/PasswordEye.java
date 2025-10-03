package inkera.ui.placeholders; // Paket adınızın doğru olduğundan emin olun

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPasswordField;

public class PasswordEye 
{
    public static void attachTo(JPasswordField passwordField, String openIconPath, String closedIconPath) 
    {
        boolean useTextFallback = (openIconPath == null || openIconPath.isEmpty() ||
                                   closedIconPath == null || closedIconPath.isEmpty());

        final Icon eyeOpenIcon = !useTextFallback ? resizeIcon(openIconPath, 30, 22) : null;
        final Icon eyeClosedIcon = !useTextFallback ? resizeIcon(closedIconPath, 30, 22) : null;

        // Eğer ikon yüklenemezse (resizeIcon null dönerse), metin moduna geç
        if (!useTextFallback && (eyeOpenIcon == null || eyeClosedIcon == null))
        {
            useTextFallback = true;
        }

        final JButton eyeButton = new JButton();

        if (useTextFallback) 
        {
            eyeButton.setText("∅");
        } else 
        {
            eyeButton.setIcon(eyeClosedIcon);
        }

        eyeButton.setBorder(null);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeButton.setToolTipText("Şifreyi Göster");

        final boolean[] isVisible = {false};
        final boolean finalUseTextFallback = useTextFallback; // Lambda için final kopya

        eyeButton.addActionListener(e -> {
            isVisible[0] = !isVisible[0];
            passwordField.setEchoChar(isVisible[0] ? (char) 0 : '•');

            if (finalUseTextFallback) 
            {
                eyeButton.setText(isVisible[0] ? "o" : "∅");
            } else 
            {
                eyeButton.setIcon(isVisible[0] ? eyeOpenIcon : eyeClosedIcon);
            }

            eyeButton.setToolTipText(isVisible[0] ? "Şifreyi Gizle" : "Şifreyi Göster");
        });

        passwordField.setLayout(new BorderLayout());
        passwordField.add(eyeButton, BorderLayout.EAST);
    }

    private static Icon resizeIcon(String path, int width, int height) 
    {
        try 
        {
            java.net.URL imgUrl = PasswordEye.class.getResource(path);
            if (imgUrl == null) 
            {
                System.err.println("HATA: İkon kaynağı bulunamadı! Lütfen yolu kontrol edin -> " + path);
                return null;
            }
            BufferedImage img = ImageIO.read(imgUrl);
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
}