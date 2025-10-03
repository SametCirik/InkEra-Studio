package inkera.ui.placeholders;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener; // Gerekli import
import javax.swing.JPasswordField;

public class PlaceholderPassword extends JPasswordField {

    public String placeholder;
    private boolean hasFocus; // YENİ: Alanın odakta olup olmadığını takip eder

    public PlaceholderPassword(String placeholder) {
        this.placeholder = placeholder;
        this.hasFocus = false; // Başlangıçta odak yok
        setMargin(new Insets(0, 6, 0, 0));

        // YENİ: FocusListener ekliyoruz
        super.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasFocus = true;
                repaint(); // Odak kazandığında yeniden çiz
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasFocus = false;
                repaint(); // Odak kaybettiğinde yeniden çiz
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // YENİ: Koşul güncellendi. Şifre alanı boşsa VE odakta değilse çiz.
        if (getPassword().length == 0 && !hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 
                          5, 
                          getHeight() / 2 + getFont().getSize() / 2 - 2);
            g2.dispose();
        }
    }

    public void setPlaceholder(String newPlaceholder) {
        this.placeholder = newPlaceholder;
        repaint(); // Değişikliği anında görmek için
    }
}