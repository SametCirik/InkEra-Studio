package deneme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BracketPanel extends JPanel {

    public BracketPanel() {
        setBorder(new EmptyBorder(10, 30, 10, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Component[] components = getComponents();
        
        if (components.length < 2) {
            g2d.dispose();
            return;
        }

        // İlk ve son bileşenlerin dikey merkez konumlarını bul
        int startY = components[0].getY() + components[0].getHeight() / 2;
        int endY = components[components.length - 1].getY() + components[components.length - 1].getHeight() / 2;

        // --- DEĞİŞİKLİK BURADA ---
        
        // Çizgiyi üstten ve alttan ne kadar uzatacağımızı belirleyen değişken
        int extension = 10; // Bu değeri artırarak çizgiyi daha da uzatabilirsiniz

        int lineX = 15;

        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Dikey çizgiyi, başlangıç ve bitiş noktalarına 'extension' ekleyerek çiz
        g2d.drawLine(lineX, startY - extension, lineX, endY + extension);

        g2d.dispose();
    }
}