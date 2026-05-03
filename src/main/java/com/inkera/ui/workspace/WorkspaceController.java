package com.inkera.ui.workspace;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    @FXML private ListView<String> layerListView;
    @FXML private Button addLayerBtn;
    
    private boolean isLeftOpen = false, isRightOpen = false, userInteracted = false; 
    private Affine affineTransform = new Affine();
    private double currentScale = 1.0, translateX = 0.0, translateY = 0.0;
    private final double ZOOM_FACTOR = 1.15;
    private double dynamicMaxZoom = 1.0; 
    private double lastPanX, lastPanY;

    private double lastDrawX = -1;
    private double lastDrawY = -1;

    // Sadece bu basit bayrak GPU'yu kontrol edecek
    private boolean needsRender = false;
    private AnimationTimer renderTimer;

    private Arena arena;
    private MemorySegment pixelSegment;
    private PixelBuffer<IntBuffer> pixelBuffer;
    
    private MethodHandle clearCanvasMH, usePencilMH, addNewLayerMH, setActiveLayerMH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        paperStack.getTransforms().add(affineTransform);
        setupZoomAndPanEngine();
        loadCEngine();
        setupDrawingEngine();
        setupLayerUI();
        setupRenderEngine();

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

    // Mesa sürücü hatasını ezen saf 60 FPS motor
    private void setupRenderEngine() {
        renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (needsRender && pixelBuffer != null) {
                    // b -> null demek: "Tüm belleği güvenle GPU'ya aktar". Hatalı bölgesel güncellemeleri çözer.
                    pixelBuffer.updateBuffer(b -> null);
                    needsRender = false;
                }
            }
        };
        renderTimer.start();
    }

    private void setupLayerUI() {
        layerListView.getItems().add("Katman 1");
        layerListView.getSelectionModel().selectFirst();
        layerListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.intValue() >= 0) {
                try { if (setActiveLayerMH != null) setActiveLayerMH.invokeExact(newV.intValue()); } catch (Throwable t) {}
            }
        });

        addLayerBtn.setOnAction(e -> {
            int newIndex = layerListView.getItems().size() + 1;
            layerListView.getItems().add("Katman " + newIndex);
            layerListView.getSelectionModel().selectLast();
            try {
                int w = Math.max((int) paperStack.getPrefWidth(), 64);
                int h = Math.max((int) paperStack.getPrefHeight(), 64);
                if (addNewLayerMH != null && pixelSegment != null) {
                    addNewLayerMH.invokeExact(pixelSegment, w, h);
                    needsRender = true; 
                }
            } catch (Throwable t) {}
        });
    }

    private void loadCEngine() {
        try {
            System.load(System.getProperty("user.dir") + "/src/main/cpp/build/libinkera_engine.so");
            Linker linker = Linker.nativeLinker();
            SymbolLookup lookup = SymbolLookup.loaderLookup();
            
            clearCanvasMH = linker.downcallHandle(lookup.find("clearCanvas").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            addNewLayerMH = linker.downcallHandle(lookup.find("addNewLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            setActiveLayerMH = linker.downcallHandle(lookup.find("setActiveLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT));
            usePencilMH = linker.downcallHandle(lookup.find("usePencil").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
                
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupDrawingEngine() {
        canvasImageView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                lastDrawX = event.getX();
                lastDrawY = event.getY();
                drawAtPixel(lastDrawX, lastDrawY, event.getX(), event.getY());
            }
        });

        canvasImageView.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && lastDrawX != -1 && lastDrawY != -1) {
                drawAtPixel(lastDrawX, lastDrawY, event.getX(), event.getY());
                lastDrawX = event.getX();
                lastDrawY = event.getY();
            }
        });

        canvasImageView.setOnMouseReleased(event -> {
            lastDrawX = -1;
            lastDrawY = -1;
        });
    }

    private void drawAtPixel(double startX, double startY, double endX, double endY) {
        if (usePencilMH == null || pixelSegment == null) return;
        
        int x0 = (int) startX; int y0 = (int) startY;
        int x1 = (int) endX;   int y1 = (int) endY;
        
        int bufferWidth = Math.max((int) paperStack.getPrefWidth(), 64);
        int bufferHeight = Math.max((int) paperStack.getPrefHeight(), 64);
        int colorArgb = 0xFF000000;

        try {
            // C++ sadece o minik kutucuğu renderlayacak, ışık hızında bitecek!
            usePencilMH.invokeExact(pixelSegment, bufferWidth, bufferHeight, x0, y0, x1, y1, colorArgb);
            needsRender = true; // Timer'a "Ekrana bas" sinyalini yolla
        } catch (Throwable t) { t.printStackTrace(); }
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
                lastPanX = event.getX(); lastPanY = event.getY();
            }
        });

        infiniteWorkspace.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {
                translateX += event.getX() - lastPanX;
                translateY += event.getY() - lastPanY;
                lastPanX = event.getX(); lastPanY = event.getY();
                updateMatrix();
            }
        });
        
        infiniteWorkspace.setOnMouseReleased(event -> infiniteWorkspace.setStyle("-fx-background-color: #2e2e2e; -fx-cursor: open_hand;"));
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
        
        double maxDimension = Math.max(width, height);
        dynamicMaxZoom = Math.max(1.0, 8192.0 / maxDimension);
        
        clearViaCPP(bufferWidth, bufferHeight, 0xFFFFFFFF);
        Platform.runLater(this::autoCenterCanvas);
    }

    private void clearViaCPP(int w, int h, int color) {
        if (clearCanvasMH == null || pixelSegment == null) return;
        try { 
            clearCanvasMH.invokeExact(pixelSegment, w, h, color); 
            needsRender = true;
        } catch (Throwable t) {}
    }

    private void autoCenterCanvas() {
        if (userInteracted || paperStack.getPrefWidth() <= 0) return;
        double vw = infiniteWorkspace.getWidth(), vh = infiniteWorkspace.getHeight();
        if (vw <= 0 || vh <= 0) return;
        double cw = paperStack.getPrefWidth(), ch = paperStack.getPrefHeight();
        currentScale = Math.min((vw - 100) / cw, (vh - 100) / ch);
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
