package deneme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomRadioButton extends JToggleButton {

    // --- Renkler ve Boyutlar ---
    private Color selectedColor = new Color(70, 130, 180);
    private Color unselectedColor = Color.LIGHT_GRAY;
    private Color hoverColor = new Color(135, 206, 250); // LightSkyBlue
    private Color borderColor = Color.DARK_GRAY;
    private Color shadowColor = new Color(0, 0, 0, 50); // Yarı şeffaf gölge

    private int circleRadius = 9;
    private int dotRadius = 5;

    private boolean isHovered = false;

    public CustomRadioButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        
        // --- İYİLEŞTİRME 1: Varsayılan kenarlığı kaldır ---
        setBorderPainted(false);

        // --- İYİLEŞTİRME 2: Fare üzerine gelince el imleci göster ---
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Metin rengini ayarla
        setForeground(new Color(50, 50, 50));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    // --- Setter metodları (değişiklik yok) ---
    public void setSelectedColor(Color selectedColor) { this.selectedColor = selectedColor; repaint(); }
    public void setUnselectedColor(Color unselectedColor) { this.unselectedColor = unselectedColor; repaint(); }
    public void setHoverColor(Color hoverColor) { this.hoverColor = hoverColor; repaint(); }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; repaint(); }
    public void setCircleRadius(int circleRadius) { this.circleRadius = circleRadius; revalidate(); repaint(); }
    public void setDotRadius(int dotRadius) { this.dotRadius = dotRadius; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        // super.paintComponent(g) ÇAĞIRMIYORUZ. Tüm çizimi kendimiz yapacağız.
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = circleRadius * 2;
        int x = 5; // Sol kenardan boşluk
        int y = (getHeight() - diameter) / 2; // Dikey olarak ortala

        // --- İYİLEŞTİRME 3: Hafif bir gölge efekti ekle ---
        g2d.setColor(shadowColor);
        g2d.fillOval(x, y + 1, diameter, diameter);

        // Dış çemberi çiz
        g2d.setColor(borderColor);
        g2d.fillOval(x, y, diameter, diameter);

        // İç çemberi (dolguyu) çiz
        Color fillColor = isSelected() ? selectedColor : unselectedColor;
        if (isHovered) {
            fillColor = isSelected() ? selectedColor.brighter() : hoverColor;
        }
        g2d.setColor(fillColor);
        g2d.fillOval(x + 1, y + 1, diameter - 2, diameter - 2);

        // Seçili ise iç noktacığı çiz
        if (isSelected()) {
            g2d.setColor(Color.WHITE);
            int dotDiameter = dotRadius * 2;
            int dotX = x + (diameter - dotDiameter) / 2;
            int dotY = y + (diameter - dotDiameter) / 2;
            g2d.fillOval(dotX, dotY, dotDiameter, dotDiameter);
        }

        // --- İYİLEŞTİRME 4: Metni manuel olarak çizerek hizala ---
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        // Metnin başlangıç x konumu: çemberin bittiği yer + boşluk
        int textX = x + diameter + 8;
        // Metnin y konumu: Dikey olarak mükemmel hizalama için
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        // --- İYİLEŞTİRME 5: Bileşen boyutunu içeriğe göre hesapla ---
        Dimension size = super.getPreferredSize();
        FontMetrics fm = getFontMetrics(getFont());
        
        // Genişlik: Sol boşluk + Çember çapı + Çember-Metin arası boşluk + Metin genişliği + Sağ boşluk
        int width = 5 + (circleRadius * 2) + 8 + fm.stringWidth(getText()) + 5;
        
        // Yükseklik: Çember çapı ve metin yüksekliğinden büyük olanı al + dikey boşluk
        int height = Math.max(circleRadius * 2, fm.getHeight()) + 8;
        
        return new Dimension(width, height);
    }
    
    // getInsets metodunu kaldırıyoruz çünkü artık gerekli değil.
}
