package deneme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import inkera.ui.cursor.BrushCursorFactory;

import java.awt.*;

public class DrawingApp {

    public static void main(String[] args) {
        // Swing arayüzünü her zaman Event Dispatch Thread (EDT) üzerinde başlatmalıyız.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Dinamik Cursor Testi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- Çizim Alanı ---
        // Burası bizim çizim yapacağımız panel olacak.
        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setPreferredSize(new Dimension(600, 400));
        
        // --- Kontrol Paneli ---
        JPanel controlPanel = new JPanel();
        JLabel sizeLabel = new JLabel("Fırça Boyutu:");
        JSlider brushSizeSlider = new JSlider(1, 100, 20); // Min:1, Max:100, Başlangıç:20
        
        controlPanel.add(sizeLabel);
        controlPanel.add(brushSizeSlider);

        // --- Slider Değişimini Dinleme ---
        // Slider'ın değeri her değiştiğinde bu blok çalışacak.
        brushSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Slider'dan güncel fırça boyutunu al.
                int newSize = brushSizeSlider.getValue();
                
                // Fabrika sınıfımızı kullanarak yeni cursor'ı üret.
                // Şimdilik rengi sabit kırmızı yapalım.
                Cursor newBrushCursor = BrushCursorFactory.createBrushCursor(newSize, Color.RED);
                
                // Oluşturulan yeni cursor'ı çizim panelimize ata.
                drawingPanel.setCursor(newBrushCursor);
            }
        });

        // Başlangıç cursor'ını ayarla
        // Uygulama ilk açıldığında slider'ın başlangıç değerine göre cursor'ı ayarlıyoruz.
        int initialSize = brushSizeSlider.getValue();
        drawingPanel.setCursor(BrushCursorFactory.createBrushCursor(initialSize, Color.RED));

        // Panelleri ana pencereye ekle
        frame.add(drawingPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Pencereyi ortala
        frame.setVisible(true);
    }
}
