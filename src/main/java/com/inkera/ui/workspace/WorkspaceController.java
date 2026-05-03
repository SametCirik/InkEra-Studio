package com.inkera.ui.workspace;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

    // YENİ: Listede tutacağımız veri modeli
    public static class LayerData {
        public String name;
        public boolean visible = true;
        public LayerData(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    @FXML private Button leftMenuBtn, rightMenuBtn;
    @FXML private VBox leftFloatingPanel, rightFloatingPanel;
    @FXML private Pane infiniteWorkspace;
    @FXML private StackPane paperStack;
    @FXML private ImageView canvasImageView;
    @FXML private ListView<LayerData> layerListView; // String yerine Model tutuyor
    @FXML private Button addLayerBtn;
    
    private boolean isLeftOpen = false, isRightOpen = false, userInteracted = false; 
    private Affine affineTransform = new Affine();
    private double currentScale = 1.0, translateX = 0.0, translateY = 0.0;
    private final double ZOOM_FACTOR = 1.15;
    private double dynamicMaxZoom = 1.0; 
    private double lastPanX, lastPanY, lastDrawX = -1, lastDrawY = -1;

    private boolean needsRender = false;
    private AnimationTimer renderTimer;
    private Arena arena;
    private MemorySegment pixelSegment;
    private PixelBuffer<IntBuffer> pixelBuffer;
    
    private MethodHandle clearCanvasMH, usePencilMH, addNewLayerMH, setActiveLayerMH;
    // YENİ PRO KÖPRÜLER
    private MethodHandle setLayerVisibilityMH, deleteLayerMH, moveLayerMH;

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

    private void setupRenderEngine() {
        renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (needsRender && pixelBuffer != null) {
                    pixelBuffer.updateBuffer(b -> null);
                    needsRender = false;
                }
            }
        };
        renderTimer.start();
    }

    // --- YENİ PRO KATMAN ARAYÜZÜ (CUSTOM CELL) ---
    private void setupLayerUI() {
        layerListView.getItems().add(new LayerData("Katman 1"));
        layerListView.getSelectionModel().selectFirst();
        
        layerListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox root = new HBox(10);
            private final Button visibilityBtn = new Button("👁");
            private final Label nameLabel = new Label();
            private final Button deleteBtn = new Button("🗑");
            private final Region spacer = new Region();

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                root.setAlignment(Pos.CENTER_LEFT);
                visibilityBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa; -fx-cursor: hand; -fx-padding: 0 5 0 0;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff5555; -fx-cursor: hand; -fx-padding: 0 0 0 5;");
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
                root.getChildren().addAll(visibilityBtn, nameLabel, spacer, deleteBtn);
                root.setStyle("-fx-padding: 5;");

                visibilityBtn.setOnAction(e -> {
                    LayerData item = getItem();
                    if (item != null) {
                        item.visible = !item.visible;
                        visibilityBtn.setText(item.visible ? "👁" : "🕶");
                        nameLabel.setOpacity(item.visible ? 1.0 : 0.5);
                        triggerCPPAction(setLayerVisibilityMH, getIndex(), item.visible);
                    }
                });

                deleteBtn.setOnAction(e -> {
                    if (getItem() != null && getListView().getItems().size() > 1) {
                        triggerCPPAction(deleteLayerMH, getIndex());
                        getListView().getItems().remove(getItem());
                    }
                });

                // Sürükle Bırak (Drag & Drop) Mimari Bağlantısı
                setOnDragDetected(event -> {
                    if (getItem() == null) return;
                    Dragboard db = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(getIndex()));
                    db.setContent(content);
                    event.consume();
                });

                setOnDragOver(event -> {
                    if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        setStyle("-fx-border-color: #66afeb; -fx-border-width: 0 0 2 0;"); 
                    }
                    event.consume();
                });
                
                setOnDragExited(event -> setStyle("-fx-border-width: 0;"));

                setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    if (db.hasString()) {
                        int fromIdx = Integer.parseInt(db.getString());
                        int toIdx = getIndex();
                        if (fromIdx != toIdx) {
                            LayerData movingItem = getListView().getItems().get(fromIdx);
                            getListView().getItems().remove(fromIdx);
                            getListView().getItems().add(toIdx, movingItem);
                            getListView().getSelectionModel().select(toIdx);
                            triggerCPPMove(fromIdx, toIdx);
                        }
                        event.setDropCompleted(true);
                    } else { event.setDropCompleted(false); }
                    event.consume();
                });
            }

            @Override
            protected void updateItem(LayerData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.name);
                    visibilityBtn.setText(item.visible ? "👁" : "🕶");
                    nameLabel.setOpacity(item.visible ? 1.0 : 0.5);
                    setGraphic(root);
                }
            }
        });

        layerListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.intValue() >= 0) {
                try { if (setActiveLayerMH != null) setActiveLayerMH.invokeExact(newV.intValue()); } catch (Throwable t) {}
            }
        });

        addLayerBtn.setOnAction(e -> {
            int newIndex = layerListView.getItems().size() + 1;
            layerListView.getItems().add(new LayerData("Katman " + newIndex));
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

    // Java'dan C++'a Tek Satırlık Fonksiyon Köprüleri
    private void triggerCPPAction(MethodHandle handle, int index) {
        if (handle == null || pixelSegment == null) return;
        try {
            handle.invokeExact(pixelSegment, Math.max((int) paperStack.getPrefWidth(), 64), Math.max((int) paperStack.getPrefHeight(), 64), index);
            needsRender = true;
        } catch (Throwable t) { t.printStackTrace(); }
    }
    private void triggerCPPAction(MethodHandle handle, int index, boolean flag) {
        if (handle == null || pixelSegment == null) return;
        try {
            handle.invokeExact(pixelSegment, Math.max((int) paperStack.getPrefWidth(), 64), Math.max((int) paperStack.getPrefHeight(), 64), index, flag);
            needsRender = true;
        } catch (Throwable t) { t.printStackTrace(); }
    }
    private void triggerCPPMove(int fromIndex, int toIndex) {
        if (moveLayerMH == null || pixelSegment == null) return;
        try {
            moveLayerMH.invokeExact(pixelSegment, Math.max((int) paperStack.getPrefWidth(), 64), Math.max((int) paperStack.getPrefHeight(), 64), fromIndex, toIndex);
            needsRender = true;
        } catch (Throwable t) { t.printStackTrace(); }
    }
    // ---------------------------------------------

    private void loadCEngine() {
        try {
            System.load(System.getProperty("user.dir") + "/src/main/cpp/build/libinkera_engine.so");
            Linker linker = Linker.nativeLinker();
            SymbolLookup lookup = SymbolLookup.loaderLookup();
            
            clearCanvasMH = linker.downcallHandle(lookup.find("clearCanvas").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            addNewLayerMH = linker.downcallHandle(lookup.find("addNewLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            setActiveLayerMH = linker.downcallHandle(lookup.find("setActiveLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT));
            usePencilMH = linker.downcallHandle(lookup.find("usePencil").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            
            // YENİ BAĞLANTILAR
            setLayerVisibilityMH = linker.downcallHandle(lookup.find("setLayerVisibility").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN));
            deleteLayerMH = linker.downcallHandle(lookup.find("deleteLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            moveLayerMH = linker.downcallHandle(lookup.find("moveLayer").get(), FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
                
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupDrawingEngine() {
        canvasImageView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                lastDrawX = event.getX(); lastDrawY = event.getY();
                drawAtPixel(lastDrawX, lastDrawY, event.getX(), event.getY());
            }
        });

        canvasImageView.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && lastDrawX != -1 && lastDrawY != -1) {
                drawAtPixel(lastDrawX, lastDrawY, event.getX(), event.getY());
                lastDrawX = event.getX(); lastDrawY = event.getY();
            }
        });

        canvasImageView.setOnMouseReleased(event -> { lastDrawX = -1; lastDrawY = -1; });
    }

    private void drawAtPixel(double startX, double startY, double endX, double endY) {
        if (usePencilMH == null || pixelSegment == null) return;
        try {
            usePencilMH.invokeExact(pixelSegment, Math.max((int) paperStack.getPrefWidth(), 64), Math.max((int) paperStack.getPrefHeight(), 64), (int) startX, (int) startY, (int) endX, (int) endY, 0xFF000000);
            needsRender = true;
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
            translateX = event.getX() - ((event.getX() - translateX) / currentScale * newScale);
            translateY = event.getY() - ((event.getY() - translateY) / currentScale * newScale);
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
        int bufferWidth = Math.max(width, 64); int bufferHeight = Math.max(height, 64);
        pixelSegment = arena.allocate((long) bufferWidth * bufferHeight * 4, 4);
        IntBuffer intBuf = pixelSegment.asByteBuffer().order(ByteOrder.nativeOrder()).asIntBuffer();
        pixelBuffer = new PixelBuffer<>(bufferWidth, bufferHeight, intBuf, PixelFormat.getIntArgbPreInstance());
        
        canvasImageView.setImage(new WritableImage(pixelBuffer));
        canvasImageView.setViewport(new javafx.geometry.Rectangle2D(0, 0, width, height));

        paperStack.setPrefSize(width, height); paperStack.setMinSize(width, height); paperStack.setMaxSize(width, height);
        dynamicMaxZoom = Math.max(1.0, 8192.0 / Math.max(width, height));
        
        if (clearCanvasMH != null && pixelSegment != null) {
            try { clearCanvasMH.invokeExact(pixelSegment, bufferWidth, bufferHeight, 0xFFFFFFFF); needsRender = true; } catch (Throwable t) {}
        }
        Platform.runLater(this::autoCenterCanvas);
    }

    private void autoCenterCanvas() {
        if (userInteracted || paperStack.getPrefWidth() <= 0) return;
        double vw = infiniteWorkspace.getWidth(), vh = infiniteWorkspace.getHeight();
        if (vw <= 0 || vh <= 0) return;
        double cw = paperStack.getPrefWidth(), ch = paperStack.getPrefHeight();
        currentScale = Math.min(Math.min((vw - 100) / cw, (vh - 100) / ch), dynamicMaxZoom); 
        translateX = (vw - (cw * currentScale)) / 2.0; translateY = (vh - (ch * currentScale)) / 2.0;
        updateMatrix();
    }

    private void togglePanel(VBox p, boolean open, double off) {
        TranslateTransition t = new TranslateTransition(Duration.millis(250), p);
        if (open) { p.setVisible(true); t.setToX(0); } else { t.setToX(off); t.setOnFinished(e -> p.setVisible(false)); }
        t.play();
    }
}
