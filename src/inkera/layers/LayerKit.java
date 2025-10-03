package inkera.layers;

import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.Layer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LayerKit {

    private LayerKit() {}

    /**
     * Tüm katmanları yöneten sınıf. Katman ekleme, silme,
     * aktif katmanı belirleme gibi işlemleri yapar.
     */
    public static class LayerManager {
        private final List<Layer> layers = new ArrayList<>();
        private int activeLayerIndex = -1;

        public LayerManager(int width, int height) {
            // Başlangıç için bir arka plan ve bir çizim katmanı oluşturalım
            Layer background = new Layer(width, height, "Background");
            Graphics2D g = background.getImage().createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.dispose();
            
            Layer layer1 = new Layer(width, height, "Layer 1");

            layers.add(background);
            layers.add(layer1);
            activeLayerIndex = 1; // "Layer 1" aktif olsun
        }

        public List<Layer> getLayers() {
            return Collections.unmodifiableList(layers); 
        }
    }

    /**
     * Sağ tarafta görünecek olan katmanlar paneli.
     */
    public static class LayersPanelUI extends JPanel {
        public LayersPanelUI(Canvas canvas) {
            setPreferredSize(new Dimension(200, 0));
            setBackground(Color.decode("#3A3A3A"));
            
            JLabel title = new JLabel("Katmanlar Paneli");
            title.setForeground(Color.WHITE);
            add(title);
            
            // TODO: Burası JList ile doldurulacak
        }
    }
}
