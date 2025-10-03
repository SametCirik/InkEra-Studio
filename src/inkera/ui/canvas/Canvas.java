package inkera.ui.canvas;

import inkera.layers.LayerKit;

import javax.swing.*;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.ImageIcon; 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public class Canvas extends JPanel {
    private int logicalWidth;
    private int logicalHeight;
    private double zoomLevel = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point panStartPoint;

    private LayerKit.LayerManager layerManager;
    
    private final Class<?> resourceContext;
    
    // Canvas.java sınıf alanları arasına ekleyin:
    // ... (diğer değişkenler: logicalWidth, layerManager, resourceContext vb.)
    private String currentToolName = "None"; // Seçili aracın adını tutar (Örn: "Brush", "Eraser")
    private String currentCursorShape = "Circle";
    private int brushSize = 30; // Çember/Artı imlecinin boyutu
 // ...

    public Canvas(int logicalWidth, int logicalHeight, Class<?> resourceContext) {
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.resourceContext = resourceContext;
        this.layerManager = new LayerKit.LayerManager(logicalWidth, logicalHeight);
        
        // Başlangıçta tuvalin arka planını beyaz yapalım
        setBackground(Color.WHITE);
        setOpaque(true);

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() 
        {
        	// mousePressed metodu
        	@Override
        	public void mousePressed(MouseEvent e) {
        	    // Orta tuş ile kaydırma (panning)
        	    if (SwingUtilities.isMiddleMouseButton(e)) {
        	        panStartPoint = e.getPoint();
        	        
                    // Panning (Kaydırma) için KAPALI el (tutuyor) imlecini kullan
                    setCursor(createCustomToolCursor(
                        // Kapalı El imleci yolu
                        "/images/CursorIcon/Cursor_HandGrabbingClosed.png", 
                        "HandGrabbingClosed"
                    ));
        	    } 
                // Sol tuş ile sürükleme (Hand aracı seçiliyken)
                else if (SwingUtilities.isLeftMouseButton(e) && "Move".equals(currentToolName)) {
                    // Taşıma (Move) aracı seçiliyken sol tık basılırsa kapalı el imlecini göster
                    setHandCursorForDragging(true);
                }
        	}   

        	@Override
            public void mouseDragged(MouseEvent e) {
                if (panStartPoint != null) {
                    // Kaydırma mantığı...
                } 
                // Sol tuş ile sürükleme (Hand aracı seçiliyken)
                else if (SwingUtilities.isLeftMouseButton(e) && "Move".equals(currentToolName)) {
                     // Sürükleme devam ederken imleç kapalı kalmalı
                } else {
                    currentMousePosition = e.getPoint(); // Çizim yaparken de konumu takip et
                    // TODO: Çizim araçları ile sürükleme mantığı buraya eklenecek
                    repaint();
                }
            }

        	@Override
            public void mouseReleased(MouseEvent e) {
                if (panStartPoint != null) {
                    panStartPoint = null;
                    
                    // Kaydırma bittiğinde: Normal imlece geri dön.
                    setCursorShape(currentToolName, currentCursorShape, brushSize);
                }
                // Sol tuş bırakıldığında (Hand aracı seçiliyken)
                else if (SwingUtilities.isLeftMouseButton(e) && "Move".equals(currentToolName)) {
                    // Açık el imlecine geri dön
                    setHandCursorForDragging(false);
                }
                // ... (TODO: Çizim işlemini sonlandırma mantığı buraya eklenecek)
            }
            
        	@Override
            public void mouseMoved(MouseEvent e) {
                if (!isPanning()) { // Kaydırma yapmıyorken imleci takip et
                    currentMousePosition = e.getPoint();
                    repaint();
                }
            }

        	@Override
            public void mouseExited(MouseEvent e) {
                currentMousePosition = new Point(-1, -1); // Canvas dışına çıkınca imleci gizle
                repaint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Arka planı temizler (beyaz yapar)
        Graphics2D g2d = (Graphics2D) g.create();

        // Daha pürüzsüz çizimler için Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Kaydırma ve zoom'u uygula
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoomLevel, zoomLevel);

        // Tüm katmanları çiz
        if (layerManager != null) {
            for (Layer layer : layerManager.getLayers()) {
                if (layer.isVisible() && layer.getImage() != null) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layer.getOpacity()));
                    g2d.drawImage(layer.getImage(), 0, 0, null);
                }
            }
        }
        
        g2d.dispose();
        
     // --- ÖZEL İMLEÇ ÇİZİMİ ---
        if (currentMousePosition.x != -1 && currentCursorShape != null) {
            Graphics2D rawG2d = (Graphics2D) g.create();
            rawG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int x = currentMousePosition.x;
            int y = currentMousePosition.y;
            int halfSize = brushSize / 2;
            int iconSize = 24; 

            // 1. Çizim Şeklini Çiz (SADECE Circle veya Crosshair için)
            // Yeni şekil adlarını burada kontrol edin. Eğer bu bir custom ikon ise (HandGrabbing gibi),
            // Çizim şeklini atlarız çünkü bu bir çizim aracı değil, taşıma alanını gösteren bir şekil.
            if ("Circle".equals(currentCursorShape) || "Crosshair".equals(currentCursorShape)) { 
                rawG2d.setColor(Color.BLACK);
                rawG2d.setStroke(new BasicStroke(1.5f));

                if ("Circle".equals(currentCursorShape)) {
                    rawG2d.drawOval(x - halfSize, y - halfSize, brushSize, brushSize);
                } else if ("Crosshair".equals(currentCursorShape)) {
                    rawG2d.drawLine(x - halfSize, y, x + halfSize, y); 
                    rawG2d.drawLine(x, y - halfSize, x, y + halfSize); 
                }
            }

            // 2. Araç İkonunu Çiz (TÜM araçlar için çalışır)
            String iconPath = getToolIconPath(); 
            try {
                URL iconUrl = resourceContext.getResource(iconPath);
                if (iconUrl != null) {
                    Image iconImage = new ImageIcon(iconUrl).getImage();
                    
                    int drawX, drawY;

                    // Şekil Çizimi Olanlar için (Circle/Crosshair): İkonu sağa ofsetle
                    if ("Circle".equals(currentCursorShape) || "Crosshair".equals(currentCursorShape)) { 
                         int offsetFromCenter = halfSize; 
                         drawX = x + offsetFromCenter + 5; 
                         drawY = y - iconSize/2;
                    } 
                    // İkon Çizimi Olanlar (Move/Text/Eyedropper/vb.): İmlecin sağında, dikeyde ortalanmış.
                    else { 
                         drawX = x + 16; 
                         drawY = y - iconSize/2; 
                    }
                    
                    rawG2d.drawImage(iconImage, drawX, drawY, iconSize, iconSize, null);

                } else {
                    // Eğer ikon yolu boşsa (getToolIconPath'den gelen) hata vermeyi atla.
                    if (!iconPath.isEmpty()) {
                        System.err.println("UYARI: Özel imleç simgesi bulunamadı: " + iconPath);
                    }
                }
            } catch (Exception e) {
                System.err.println("HATA: Özel imleç simgesi çizilemedi: " + iconPath);
                e.printStackTrace();
            }

            rawG2d.dispose();
        }
    }
    
    // --- Getter ve Setter'lar ---
    public LayerKit.LayerManager getLayerManager() { return layerManager; }
    public boolean isPanning() { return panStartPoint != null; }
    
    
 // --- CURSOR YÖNETİMİ METOTLARI ---

    /**
     * Bir çizim aracı ikonu için özel bir fare imleci oluşturur.
     * Not: Ikon dosyalarının projenizin kaynak yolunda (classpath) olduğundan emin olun.
     *
     * @param imagePath İmleç için kullanılacak ikon dosyasının yolu (Örn: "/icons/brush.png")
     * @param name İmleç için açıklayıcı bir isim (Örn: "BrushCursor")
     * @return Yeni oluşturulan özel Cursor nesnesi.
     */
    
    /**
     * Seçili aracın adını kullanarak siyah ikon dosyasının kaynak yolunu döndürür.
     * İkon adları artık '...Black.png' formatındadır.
     */
    private String getToolIconPath() {
        // currentToolName'i kullanmaya devam et, shape'ten bağımsız
        return switch (currentToolName) {
            case "Brush" -> "/images/ToolsIcon/Icon_PenBlack.png";
            case "Eraser" -> "/images/ToolsIcon/Icon_EraserBlack.png";
            case "PaintBucket" -> "/images/ToolsIcon/Icon_PaintBucketBlack.png";
            case "Move" -> "/images/ToolsIcon/Icon_MoveBlack.png"; // Taşıma ikonunu göster
            case "Eyedropper" -> "/images/ToolsIcon/Icon_EyedropperBlack.png";   
            case "Text" -> "/images/ToolsIcon/Icon_TextBlack.png";
            case "Lasso" -> "/images/ToolsIcon/Icon_LassoBlack.png";
            case "MagicWand" -> "/images/ToolsIcon/Icon_MacicWandBlack.png";
            case "AI" -> "/images/ToolsIcon/Icon_AIBlack.png";
            
            // Eğer "HandGrabbing" (Kapalı El) imleci çizilmek isteniyorsa, 
            // setToolCursor ile atanan gerçek imleç kullanılır, 
            // buradaki küçük ikon çizimi atlanır. 
            // Bu kısım, imleç etrafına bir işaret çizilmesini isterseniz değişebilir.
            case "None" -> "/images/ToolsIcon/Icon_QuestionBlack.png";
            
            default -> ""; 
        };
    }
    
    private Cursor createCustomToolCursor(String imagePath, String name) {
        try {
            java.net.URL imageUrl = resourceContext.getResource(imagePath);
            
            if (imageUrl == null) {
                System.err.println("HATA: İkon dosyası kaynaklarda bulunamadı: " + imagePath);
                return Cursor.getDefaultCursor(); 
            }
            
            Image image = new ImageIcon(imageUrl).getImage(); 
            
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension bestSize = toolkit.getBestCursorSize(image.getWidth(null), image.getHeight(null));
            Image scaledImage = image.getScaledInstance(bestSize.width, bestSize.height, Image.SCALE_SMOOTH);
            Point hotSpot = new Point(bestSize.width / 2, bestSize.height / 2); // İmleci ortaya hizala
            
            // Hand/Text gibi ikonların hotSpot'ını sol üst köşe yapmak daha doğru olabilir:
            if (name.contains("Hand") || name.contains("Text")) {
                 hotSpot = new Point(0, 0); 
            }
            
            return toolkit.createCustomCursor(scaledImage, hotSpot, name);
        } catch (Exception e) {
            System.err.println("Özel imleç oluşturulamadı: " + imagePath);
            e.printStackTrace();
            return Cursor.getDefaultCursor(); 
        }
    }

    /**
     * Seçili çizim aracına göre Canvas'ın imlecini değiştirir.
     * Bu metot, DrawingWindow'dan çağrılacaktır.
     * @param toolName Seçili aracın adı (Örn: "Brush", "Pencil", "Eraser")
     */
    public void setToolCursor(String toolName) {
        if (isPanning()) {
            return;
        }
        
        Cursor newCursor;
        
        switch (toolName) 
        {
            case "Move": // Taşıma Aracı için Açık El
                newCursor = createCustomToolCursor(
                    "/images/CursorIcon/Cursor_HandOpen.png", 
                    "HandCursor"
                );
                break;
                
            case "Text": 
                newCursor = createCustomToolCursor(
                    "/images/CursorIcon/Icon_CursorText.png", 
                    "TextCursor"
                );
                break;

            case "PaintBucket": 
                newCursor = createCustomToolCursor(
                    "/images/CursorIcon/Icon_CursorPaintBucket.png", 
                    "PaintBucketCursor"
                );
                break;
                
            case "Eyedropper": 
                newCursor = createCustomToolCursor(
                    "/images/CursorIcon/Icon_CursorEyedropper.png", 
                    "EyedropperCursor"
                );
                break;
                
            // Brush, Eraser, Lasso, MagicWand, AI için görünmez imleç kullan
            default:
                newCursor = this.getToolkit().createCustomCursor(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "InvisibleCursor"
                );
                break;
        }

        setCursor(newCursor);
    }
    
 // Canvas.java sınıf alanları arasına ekleyin:
    private Point currentMousePosition = new Point(-1, -1); // Fare konumunu takip etmek için
    
    public void setCursorShape(String toolName, String shape, int size) 
    {
        this.currentToolName = toolName; 
        this.currentCursorShape = shape; 
        this.brushSize = size;
        
        // Şekil yoksa ve araç seçiliyse IconOnly varsay
        if(shape == null && !toolName.equals("None")) {
            this.currentCursorShape = "IconOnly"; 
        }
        
        // Gerçek Java imlecini Görünmez Yap.
        // setToolCursor ile atanan Hand, Text gibi özel imleçler hariç.
        // Taşıma aracı (Move) için özel imleç atanmasını korumak gerekiyor.
        
        // Bu metot çağrıldığında araca göre atanmış özel imleçleri tekrar görünmez yapma
        // bu yüzden setToolCursor'ı her zaman setCursorShape'ten önce çağırmalısınız!
        
        // ÖZEL İMLEÇ KONTROLÜ: Move (Hand) aracı seçiliyken görünmez imleç ataması YAPMA.
        // Move aracı, setToolCursor ile zaten atanmış olan Hand imlecini kullanmaya devam etmelidir.
        if (!"Move".equals(toolName) && !"Text".equals(toolName) && !"PaintBucket".equals(toolName) && !"Eyedropper".equals(toolName)) {
             super.setCursor(this.getToolkit().createCustomCursor(
                 new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "InvisibleCursor"));
        }
        
        repaint(); 
    }

    /**
     * Sol tık ile tuvali sürükleme (Hand aracı seçiliyken) sırasında imleci ayarlar.
     * @param isGrabbing True ise kapalı el (tutuyor), False ise açık el (normal) imlecini ayarlar.
     */
    public void setHandCursorForDragging(boolean isGrabbing) {
        if (isGrabbing) {
            setCursor(createCustomToolCursor(
                "/images/CursorIcon/Cursor_HandGrabbingClosed.png", 
                "HandGrabbingClosedCursor"
            ));
        } else {
            // Normal Açık El imlecine geri dön.
            setToolCursor("Move");
        }
    }
    
}
