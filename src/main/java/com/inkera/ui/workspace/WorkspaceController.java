package com.inkera.ui.workspace;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ResourceBundle;

public class WorkspaceController implements Initializable {

    @FXML private Button leftMenuBtn, rightMenuBtn;
    @FXML private VBox leftFloatingPanel, rightFloatingPanel;
    @FXML private Pane infiniteWorkspace;
    @FXML private StackPane paperStack;
    @FXML private ImageView canvasImageView;
    
    private boolean isLeftOpen = false, isRightOpen = false, userInteracted = false; 
    private Affine affineTransform = new Affine();
    private double currentScale = 1.0, translateX = 0.0, translateY = 0.0;
    
    private final double ZOOM_FACTOR = 1.15;
    
    // GÜNCELLENDİ: Sabit zoom limiti kaldırıldı. Artık GPU'yu korumak için dinamik hesaplanacak.
    private double dynamicMaxZoom = 1.0; 
    private double lastMouseX, lastMouseY;

    // PROJECT PANAMA
    private Arena arena;
    private MemorySegment pixelSegment;
    private PixelBuffer<IntBuffer> pixelBuffer;
    private MethodHandle clearCanvasMH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        
        paperStack.getTransforms().add(affineTransform);
        setupZoomAndPanEngine();
        loadCEngine();

        infiniteWorkspace.widthProperty().addListener((o, oldV, newV) -> autoCenterCanvas());
        infiniteWorkspace.heightProperty().addListener((o, oldV, newV) -> autoCenterCanvas());
    }

    private void setupUI() {
        leftFloatingPanel.setTranslateX(-280); leftFloatingPanel.setVisible(false);
        rightFloatingPanel.setTranslateX(280); rightFloatingPanel.setVisible(false);
        leftMenuBtn.setOnAction(e -> togglePanel(leftFloatingPanel, isLeftOpen = !isLeftOpen, -280));
        rightMenuBtn.setOnAction(e -> togglePanel(rightFloatingPanel, isRightOpen = !isRightOpen, 280));
        
        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(infiniteWorkspace.widthProperty());
        clipRect.heightProperty().bind(infiniteWorkspace.heightProperty());
        infiniteWorkspace.setClip(clipRect);
    }

    private void updateMatrix() {
        affineTransform.setToIdentity();
        affineTransform.appendTranslation(translateX, translateY);
        affineTransform.appendScale(currentScale, currentScale);
    }

    private void setupZoomAndPanEngine() {
        infiniteWorkspace.setOnScroll(event -> {
            event.consume();
            if (event.getDeltaY() == 0) return;
            userInteracted = true; 
            
            double scaleMultiplier = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            
            // GÜNCELLENDİ: Dinamik maksimum zoom koruması devrede!
            double newScale = Math.max(0.05, Math.min(currentScale * scaleMultiplier, dynamicMaxZoom));
            
            double mouseX = event.getX(), mouseY = event.getY();
            double localX = (mouseX - translateX) / currentScale;
            double localY = (mouseY - translateY) / currentScale;
            
            translateX = mouseX - (localX * newScale);
            translateY = mouseY - (localY * newScale);
            currentScale = newScale;
            
            updateMatrix();
        });

        infiniteWorkspace.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                userInteracted = true;
                infiniteWorkspace.setStyle("-fx-background-color: #2e2e2e; -fx-cursor: closed_hand;");
                lastMouseX = event.getX(); lastMouseY = event.getY();
            }
        });

        infiniteWorkspace.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {
                translateX += event.getX() - lastMouseX;
                translateY += event.getY() - lastMouseY;
                lastMouseX = event.getX(); lastMouseY = event.getY();
                updateMatrix();
            }
        });
        
        infiniteWorkspace.setOnMouseReleased(event -> {
            infiniteWorkspace.setStyle("-fx-background-color: #2e2e2e; -fx-cursor: open_hand;");
        });
    }

    public void setCanvasSize(int width, int height) {
        if (arena != null) arena.close();
        arena = Arena.ofShared();
        
        int bufferWidth = Math.max(width, 64);
        int bufferHeight = Math.max(height, 64);
        
        pixelSegment = arena.allocate((long) bufferWidth * bufferHeight * 4, 4);
        IntBuffer intBuf = pixelSegment.asByteBuffer().order(ByteOrder.nativeOrder()).asIntBuffer();
        pixelBuffer = new PixelBuffer<>(bufferWidth, bufferHeight, intBuf, PixelFormat.getIntArgbPreInstance());
        
        canvasImageView.setImage(new WritableImage(pixelBuffer));
        canvasImageView.setViewport(new javafx.geometry.Rectangle2D(0, 0, width, height));

        paperStack.setPrefSize(width, height);
        paperStack.setMinSize(width, height);
        paperStack.setMaxSize(width, height);
        
        // --- KUSURSUZ GPU KORUMA MATEMATİĞİ ---
        // GPU'nun çökmeyeceği maksimum güvenli sınır 8192 pikseldir.
        double maxDimension = Math.max(width, height);
        // Tuval boyutuna göre asla 8192'yi aşmayacak maksimum zoom katsayısını hesapla:
        dynamicMaxZoom = 8192.0 / maxDimension;
        // Çok büyük tuvallerde zoom 1.0'ın altına düşmesin diye alt sınır koyuyoruz
        dynamicMaxZoom = Math.max(1.0, dynamicMaxZoom);
        
        clearViaCPP(bufferWidth, bufferHeight, 0xFFFFFFFF);
        Platform.runLater(this::autoCenterCanvas);
    }

    private void loadCEngine() {
        try {
            System.load(System.getProperty("user.dir") + "/src/main/cpp/build/libinkera_engine.so");
            Linker linker = Linker.nativeLinker();
            SymbolLookup lookup = SymbolLookup.loaderLookup();
            clearCanvasMH = linker.downcallHandle(lookup.find("clearCanvas").get(), 
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearViaCPP(int w, int h, int color) {
        try { clearCanvasMH.invokeExact(pixelSegment, w, h, color); pixelBuffer.updateBuffer(b -> null); } catch (Throwable t) {}
    }

    private void autoCenterCanvas() {
        if (userInteracted || paperStack.getPrefWidth() <= 0) return;
        double vw = infiniteWorkspace.getWidth(), vh = infiniteWorkspace.getHeight();
        if (vw <= 0 || vh <= 0) return;
        double cw = paperStack.getPrefWidth(), ch = paperStack.getPrefHeight();
        
        currentScale = Math.min((vw - 100) / cw, (vh - 100) / ch);
        // Otomatik merkezlemede de bu dinamik sınırı kullanıyoruz
        currentScale = Math.min(currentScale, dynamicMaxZoom); 
        
        translateX = (vw - (cw * currentScale)) / 2.0;
        translateY = (vh - (ch * currentScale)) / 2.0;
        updateMatrix();
    }

    private void togglePanel(VBox p, boolean open, double off) {
        TranslateTransition t = new TranslateTransition(Duration.millis(250), p);
        if (open) { p.setVisible(true); t.setToX(0); } else { t.setToX(off); t.setOnFinished(e -> p.setVisible(false)); }
        t.play();
    }
}
