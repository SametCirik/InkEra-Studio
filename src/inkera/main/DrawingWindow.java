package inkera.main;

import inkera.languages.Languages;
import inkera.layers.LayerKit;
import inkera.ui.SidePanelComponentSettings.SidePanelBrush; 
import inkera.ui.SidePanelComponentSettings.SidePanelEraser;
import inkera.ui.SidePanelComponentSettings.SidePanelMove;
import inkera.ui.SidePanelComponentSettings.SidePanelPaintBucket;
import inkera.ui.canvas.Canvas;
import inkera.ui.canvas.CanvasPanel;
import inkera.ui.titlebars.DrawingWindowTitleBar;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import javax.swing.*;

/**
 * Ana çizim penceresi. Yeni modüler bileşenleri kullanır.
 */
public class DrawingWindow extends JFrame 
{
	private MainMenuFrame mainMenuFrameInstance;

    private final double aspectRatio;
    private final Languages languageManager;
    
    // DÜZELTME: Alanlar artık gerçek, dış sınıflara referans veriyor.
    private final Canvas actualCanvas;
    private final CanvasPanel canvasHolderPanel;
    private final LayerKit.LayersPanelUI actualLayersPanel;

    private final ToolbarPanel toolbarPanel;
    
    // Placeholder sınıflar
    private final DrawingTool brushTool = new BrushTool();
    private final DrawingTool eraserTool = new EraserTool();
    private final LassoTool lassoTool = new LassoTool();
    private final Canvas canvas; 

    public DrawingWindow(MainMenuFrame mainMenu, String name, int width, int height, Languages languageManager) 
    {
    	this.mainMenuFrameInstance = mainMenu;
    	this.aspectRatio = (double) width / height;
        this.languageManager = languageManager;
    //  this.canvas = new Canvas(width, height);

        // Ana bileşenleri oluştur
        DrawingWindowTitleBar titleBar = new DrawingWindowTitleBar(this, name + " - InkEra");
        this.toolbarPanel = new ToolbarPanel();
        
        // DÜZELTME: Gerçek Canvas ve CanvasPanel doğru sırada ve doğru şekilde oluşturuluyor.
        this.actualCanvas = new Canvas(width, height, this.getClass()); // YENİ
        this.canvas = this.actualCanvas; // YENİ
        this.canvasHolderPanel = new CanvasPanel(this.actualCanvas); 
        this.actualLayersPanel = new LayerKit.LayersPanelUI(this.actualCanvas);
        
        SidePanel sidePanel = new SidePanel(this.actualLayersPanel);

        // Pencereyi yapılandır ve bileşenleri ekle
        configureWindow();
        add(titleBar, BorderLayout.NORTH);
        add(this.toolbarPanel, BorderLayout.WEST);
        // DÜZELTME: Artık merkezde canvasHolderPanel var.
        add(canvasHolderPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        new CustomFrameResizer(this);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // DÜZELTME: Yeniden boyutlandırma mantığı aktif edildi.
                SwingUtilities.invokeLater(() -> handleWindowResize());
            }
        });
        
     // --- YENİ EKLENEN KOD ---
        // Pencere ilk kez görünür hale geldikten ve tüm bileşenler yerleştikten sonra
        // kanvas boyutunu bir kereliğine mahsus doğru şekilde ayarla.
        SwingUtilities.invokeLater(() -> {
            handleWindowResize();
        });
        // --- YENİ EKLENEN KOD SONU ---

        setTitle(name + " - InkEra");
    }

    private void configureWindow() {
        URL iconUrl = getClass().getResource("/images/AppLogo.png");
        if (iconUrl != null) setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        this.setBounds(bounds);
        
        setMinimumSize(new Dimension(600, 400));
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
    }
    
    private void handleWindowResize() {
        if (actualCanvas == null || canvasHolderPanel == null) return;
        
        int availableWidth = canvasHolderPanel.getWidth();
        int availableHeight = canvasHolderPanel.getHeight();
        if (availableWidth <= 0 || availableHeight <= 0) return;

        int newCanvasWidth;
        int newCanvasHeight;

        if (availableWidth / aspectRatio <= availableHeight) {
            newCanvasWidth = availableWidth;
            newCanvasHeight = (int) (availableWidth / aspectRatio);
        } else {
            newCanvasHeight = availableHeight;
            newCanvasWidth = (int) (availableHeight * aspectRatio);
        }
        
        canvasHolderPanel.updateCanvasSize(newCanvasWidth, newCanvasHeight);
    }
    
    // =================================================================================
    // Placeholder Sınıflar
    // =================================================================================

    
    public static class CustomFrameResizer {
        public CustomFrameResizer(JFrame frame) { /* TODO */ }
    }
    
    public interface DrawingTool {}
    public static class BrushTool implements DrawingTool {}
    public static class EraserTool implements DrawingTool {}
    public static class LassoTool implements DrawingTool {}


    // =================================================================================
    // İç Sınıflar: Pencerenin modüler parçaları
    // =================================================================================

    private class ToolbarPanel extends JPanel 
    {
        private final int TOOLBAR_WIDTH = 45;
        private final int COMPONENTS_PANEL_WIDTH = 300;
        private final JPanel componentsPanel;
        private Component currentSettingsPanel = null;

        ToolbarPanel() {
            setLayout(new BorderLayout()); 
            
            JPanel buttonContainer = new JPanel();
            buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
            buttonContainer.setBackground(Color.decode("#4A4A4A"));
            buttonContainer.setPreferredSize(new Dimension(TOOLBAR_WIDTH, 0));
            buttonContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            componentsPanel = new JPanel();
            componentsPanel.setLayout(new BorderLayout());
            componentsPanel.setPreferredSize(new Dimension(COMPONENTS_PANEL_WIDTH, 0));
            componentsPanel.setBackground(Color.decode("#3A3A3A"));
            componentsPanel.setVisible(false);

            addToolButtons(buttonContainer);
            
            add(buttonContainer, BorderLayout.WEST);
            add(componentsPanel, BorderLayout.CENTER);
        }
        
        // Side Panel içeriğini değiştirmek ve görünürlüğü kontrol etmek için bu metot kullanılır.
        public void switchSettingsPanel(JPanel newSettingsPanel)
        {
        	if(currentSettingsPanel != null)
        	{
        		componentsPanel.remove(currentSettingsPanel);
        	}
        	
        	currentSettingsPanel = newSettingsPanel;
        	if(currentSettingsPanel != null)
        	{
        		componentsPanel.add(currentSettingsPanel, BorderLayout.CENTER);
        		componentsPanel.setVisible(true); // Ayar paneli varken ComponentsPanel görünür olmalı
        	} 
        	else
        	{
        		componentsPanel.setVisible(false); // Ayar paneli yoksa gizle
        	}
        	
        	DrawingWindow.this.revalidate();
        	DrawingWindow.this.repaint();
        	SwingUtilities.invokeLater(() -> handleWindowResize());
        }
        
        // Yan Ayar Panelleri için Kayma animasyonu (DÜZELTEMEZSEM KULLANMAYACAĞIM)
        private void toggleDrawer(JPanel drawer, boolean openLeft) {
            int startX = drawer.getX();
            int targetX = openLeft 
                ? (drawer.isVisible() ? -drawer.getWidth() : 0) 
                : (drawer.isVisible() ? getWidth() : getWidth() - drawer.getWidth());

            drawer.setVisible(true);

            Timer timer = new Timer(5, null);
            timer.addActionListener(e -> {
                int currentX = drawer.getX();
                int step = (targetX - currentX) / 5;

                if (Math.abs(step) < 1) step = (targetX > currentX ? 1 : -1);

                drawer.setLocation(currentX + step, drawer.getY());

                if ((step > 0 && drawer.getX() >= targetX) || (step < 0 && drawer.getX() <= targetX)) {
                    drawer.setLocation(targetX, drawer.getY());
                    drawer.setVisible(targetX != -drawer.getWidth() && targetX != getWidth());
                    ((Timer) e.getSource()).stop();
                }
            });
            timer.start();
        }


        private void addToolButtons(JPanel container) {
        	JButton toggleComponentsButton = createIconButton("Icon_TogglePanel.png", "Komponent Ayarlarını Göster/Gizle");
            toggleComponentsButton.addActionListener(e -> 
            {
                // Panelin görünürlüğünü değiştir
            	componentsPanel.setVisible(!componentsPanel.isVisible());
//              toggleDrawer(componentsPanel,true);   // Kayma animasyonu için kullanılabilir (DÜZELTİLMESİ GEREKİYOR) 
            	
                // Ana pencerenin yerleşimini yeniden doğrula ve çiz
                DrawingWindow.this.revalidate();
                DrawingWindow.this.repaint();

                // YENİ: Yerleşim güncellendikten sonra kanvas boyutunu yeniden hesapla
                SwingUtilities.invokeLater(() -> handleWindowResize());
            });
            container.add(toggleComponentsButton);
            container.add(Box.createVerticalStrut(250));
            JButton brushButton = createIconButton("Icon_Pen.png", languageManager.getString(Languages.KEY_TOOL_BRUSH));
            brushButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Fırça seçildi. Ayar paneli açılıyor.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelBrush()); // Yakında
                canvas.setToolCursor("Brush");
                canvas.setCursorShape("Brush","Circle", 25);
            });
            container.add(brushButton);
            container.add(Box.createVerticalStrut(5));
            JButton eraserButton = createIconButton("Icon_Eraser.png", languageManager.getString(Languages.KEY_TOOL_ERASER));
            eraserButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Silgi seçildi. Ayar paneli açılıyor.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelEraser()); // Yakında
                canvas.setToolCursor("Eraser"); 
                canvas.setCursorShape("Eraser","Circle", 40);
            });
            container.add(eraserButton);
            container.add(Box.createVerticalStrut(5));
            JButton paintBucketButton = createIconButton("Icon_PaintBucket.png", languageManager.getString(Languages.KEY_TOOL_ERASER));
            paintBucketButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Boya Kovası seçildi. Ayar paneli açılıyor.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelPaintBucket()); // Yakında
                canvas.setToolCursor("PaintBucket");
                canvas.setCursorShape("PaintBucket","Crosshair", 25); 
            });
            container.add(paintBucketButton);
            container.add(Box.createVerticalStrut(5));
            JButton moveButton = createIconButton("Icon_Move.png", languageManager.getString(Languages.KEY_TOOL_ERASER));
            moveButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Taşıma seçildi. Ayar paneli açılıyor.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelMove()); // Yakında
                canvas.setToolCursor("Move");
                canvas.setCursorShape("Move", "HandGribbing", 0); 
            });
            container.add(moveButton);
            container.add(Box.createVerticalStrut(5));
            JButton eyedropperButton = createIconButton("Icon_Eyedropper.png", languageManager.getString(Languages.KEY_TOOL_ERASER));
            eyedropperButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Damlalık seçildi.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelPaintBucket()); // Yakında
                canvas.setCursorShape("Eyedropper", "IconOnly", 25);
            });
            container.add(eyedropperButton);
            container.add(Box.createVerticalStrut(5));
            JButton TextButton = createIconButton("Icon_Text.png", languageManager.getString(Languages.KEY_TOOL_ERASER));
            TextButton.addActionListener(e -> {
                // Örnek: Fırça aracını seç ve ayar panelini aç
                // ÖNEMLİ: Burada, fırça ayar panelini oluşturup switchSettingsPanel'a göndereceğiz.
                // Şimdilik null yollayalım (daha sonra oluşturulacak).
                System.out.println("Metin Aracı seçildi.");
                DrawingWindow.this.toolbarPanel.switchSettingsPanel(new SidePanelPaintBucket()); // Yakında
                canvas.setCursorShape("Text", "IconOnly", 25);
            });
            container.add(TextButton);
            container.add(Box.createVerticalStrut(5));
           
//          container.add(createIconButton("Icon_Pen.png", languageManager.getString(Languages.KEY_TOOL_BRUSH)));
//          container.add(Box.createVerticalStrut(5));
//          container.add(createIconButton("Icon_Eraser.png", languageManager.getString(Languages.KEY_TOOL_ERASER)));
//          container.add(Box.createVerticalStrut(5));
//          container.add(createIconButton("Icon_PaintBucket.png", languageManager.getString(Languages.KEY_TOOL_BUCKET)));
//          container.add(Box.createVerticalStrut(5));
//          container.add(createIconButton("Icon_Move.png", languageManager.getString(Languages.KEY_TOOL_TRANSFORM)));
//          container.add(Box.createVerticalStrut(5));
//          container.add(createIconButton("Icon_Eyedropper.png", languageManager.getString(Languages.KEY_TOOL_EYEDROPPER)));
//          container.add(Box.createVerticalStrut(5));
//          container.add(createIconButton("Icon_Text.png", languageManager.getString(Languages.KEY_TOOL_TEXT)));
//          container.add(Box.createVerticalStrut(5));
            container.add(createIconButton("Icon_Lasso.png", languageManager.getString(Languages.KEY_TOOL_LASSO)));
            container.add(Box.createVerticalStrut(5));
            container.add(createIconButton("Icon_MagicWand.png", languageManager.getString(Languages.KEY_TOOL_WAND)));
            container.add(Box.createVerticalGlue());
        }
    }

    private class SidePanel extends JPanel {
        SidePanel(LayerKit.LayersPanelUI layersPanel) { // Constructor güncellendi
            setLayout(new BorderLayout());
            
            JPanel rightToolbar = new JPanel();
            rightToolbar.setLayout(new BoxLayout(rightToolbar, BoxLayout.Y_AXIS));
            rightToolbar.setBackground(Color.decode("#4A4A4A"));
            rightToolbar.setPreferredSize(new Dimension(45, 0));
            rightToolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            layersPanel.setVisible(false);
            
            JButton toggleLayersButton = createIconButton("Icon_TogglePanel.png", "Katmanlar Panelini Göster/Gizle");
            toggleLayersButton.addActionListener(e -> 
            {
                // Panelin görünürlüğünü değiştir
                layersPanel.setVisible(!layersPanel.isVisible());
                
                // Ana pencerenin yerleşimini yeniden doğrula ve çiz
                DrawingWindow.this.revalidate();
                DrawingWindow.this.repaint();

                // YENİ: Yerleşim güncellendikten sonra kanvas boyutunu yeniden hesapla
                SwingUtilities.invokeLater(() -> handleWindowResize());
            });
            rightToolbar.add(toggleLayersButton);

            rightToolbar.add(Box.createVerticalStrut(250));
            rightToolbar.add(createIconButton("Icon_C.png", "Katmanı Temizle"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_V.png", "Dikey Çevir"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_H.png", "Yatay Çevir"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_M.png", "Aşağıdaki Katmanla Birleştir"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_Cam.png", "Kameradan Aktar"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_D.png", "Katmanı Sil"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_E.png", "Filtreler"));
            rightToolbar.add(Box.createVerticalStrut(5));
            rightToolbar.add(createIconButton("Icon_AI.png", "Yapay Zeka"));
            rightToolbar.add(Box.createVerticalGlue());

            add(layersPanel, BorderLayout.CENTER);
            add(rightToolbar, BorderLayout.EAST);
        }
    }
    
    private void configureButton(JButton button, String tooltip) {
        button.setToolTipText(tooltip);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMargin(new Insets(2, 2, 2, 2));
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(45, 35));
        button.setMaximumSize(new Dimension(45, 35));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
    }

    private JButton createIconButton(String iconFileName, String tooltip) {
        JButton button = new JButton();
        configureButton(button, tooltip);

        try {
            String folder = iconFileName.startsWith("RightIcon") ? "LayerIcon" : "ToolsIcon";
            URL iconUrl = getClass().getResource("/images/" + folder + "/" + iconFileName);
            
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } else {
                button.setText("?"); 
                System.err.println("İkon bulunamadı: /images/" + folder + "/" + iconFileName);
            }
        } catch (Exception e) {
            button.setText("!");
            e.printStackTrace();
        }

        button.addActionListener(e -> System.out.println(tooltip + " tıklandı."));
        return button;
    }

    public void closeWindowAndOpenMainMenu() {
        // Önce ana menüyü görünür yap
        if (mainMenuFrameInstance != null) {
            mainMenuFrameInstance.setVisible(true);
        }
        
        // Sonra bu çizim penceresini kapat
        this.dispose(); 
    }
}
