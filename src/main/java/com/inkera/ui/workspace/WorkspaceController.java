package com.inkera.ui.workspace;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkspaceController implements Initializable {

    @FXML private Button leftMenuBtn;
    @FXML private Button rightMenuBtn;
    
    @FXML private VBox leftFloatingPanel;
    @FXML private VBox rightFloatingPanel;
    
    @FXML private Pane infiniteWorkspace;
    @FXML private Canvas drawingCanvas;
    @FXML private StackPane paperStack;

    private boolean isLeftOpen = false;
    private boolean isRightOpen = false;
    private boolean initialFitDone = false; // YENİ: Başlangıç ortalamasını kontrol eder
    
    private Scale scaleTransform = new Scale(1, 1);
    private Translate translateTransform = new Translate(0, 0);
    
    private final double ZOOM_FACTOR = 1.15;
    private double lastMouseX;
    private double lastMouseY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        leftFloatingPanel.setTranslateX(-280);
        leftFloatingPanel.setVisible(false);
        rightFloatingPanel.setTranslateX(280);
        rightFloatingPanel.setVisible(false);

        leftMenuBtn.setOnAction(e -> {
            isLeftOpen = !isLeftOpen;
            togglePanel(leftFloatingPanel, isLeftOpen, -280);
        });

        rightMenuBtn.setOnAction(e -> {
            isRightOpen = !isRightOpen;
            togglePanel(rightFloatingPanel, isRightOpen, 280);
        });

        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(infiniteWorkspace.widthProperty());
        clipRect.heightProperty().bind(infiniteWorkspace.heightProperty());
        infiniteWorkspace.setClip(clipRect);

        paperStack.getTransforms().addAll(translateTransform, scaleTransform);

        setupZoomAndPanEngine();
        fillCanvasWithWhite();
    }

    private void setupZoomAndPanEngine() {
        infiniteWorkspace.setOnScroll(event -> {
            event.consume();
            if (event.getDeltaY() == 0) return;

            double scaleFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            double currentScale = scaleTransform.getX();
            
            // GÜNCELLENDİ: Maksimum 15.0 (%1500) limit uygulandı. GPU texture sınırını korur.
            double newScale = Math.max(0.05, Math.min(currentScale * scaleFactor, 15.0));

            double mouseX = event.getX();
            double mouseY = event.getY();

            double f = (newScale / currentScale) - 1;
            
            translateTransform.setX(translateTransform.getX() - (mouseX - translateTransform.getX()) * f);
            translateTransform.setY(translateTransform.getY() - (mouseY - translateTransform.getY()) * f);
            
            scaleTransform.setX(newScale);
            scaleTransform.setY(newScale);
        });

        infiniteWorkspace.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                infiniteWorkspace.setStyle("-fx-background-color: #2e2e2e; -fx-cursor: closed_hand;");
                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        });

        infiniteWorkspace.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;
                
                translateTransform.setX(translateTransform.getX() + deltaX);
                translateTransform.setY(translateTransform.getY() + deltaY);
                
                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        });

        infiniteWorkspace.setOnMouseReleased(event -> {
            infiniteWorkspace.setStyle("-fx-background-color: #2e2e2e; -fx-cursor: open_hand;");
        });
    }

    private void togglePanel(VBox panel, boolean open, double offScreenX) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(250), panel);
        if (open) {
            panel.setVisible(true);
            transition.setToX(0);
        } else {
            transition.setToX(offScreenX);
            transition.setOnFinished(e -> panel.setVisible(false));
        }
        transition.play();
    }

    public void setCanvasSize(int width, int height) {
        if (drawingCanvas != null) {
            drawingCanvas.setWidth(width);
            drawingCanvas.setHeight(height);
            
            if (paperStack != null) {
                paperStack.setPrefWidth(width);
                paperStack.setPrefHeight(height);
                paperStack.setMinWidth(width);
                paperStack.setMinHeight(height);
                paperStack.setMaxWidth(width);
                paperStack.setMaxHeight(height);
            }
            
            fillCanvasWithWhite();
            
            // YENİ: Ekranın layout işleminin tamamlanmasını bekleyen zeki dinleyici (Listener)
            infiniteWorkspace.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (!initialFitDone && newVal.doubleValue() > 0) {
                    Platform.runLater(() -> fitCanvasToScreen(width, height));
                    initialFitDone = true;
                }
            });
            
            // Eğer sayfa zaten kuruluysa direkt çalıştır (Yedekleme)
            if (infiniteWorkspace.getWidth() > 0) {
                fitCanvasToScreen(width, height);
                initialFitDone = true;
            }
        }
    }
    
    private void fitCanvasToScreen(int canvasWidth, int canvasHeight) {
        double viewWidth = infiniteWorkspace.getWidth();
        double viewHeight = infiniteWorkspace.getHeight();
        
        if (viewWidth <= 0 || viewHeight <= 0) return;
        
        double scaleX = (viewWidth - 100) / canvasWidth;
        double scaleY = (viewHeight - 100) / canvasHeight;
        
        double finalScale = Math.min(scaleX, scaleY);
        if (finalScale > 1.0) { finalScale = 1.0; }
        
        scaleTransform.setX(finalScale);
        scaleTransform.setY(finalScale);
        
        double scaledWidth = canvasWidth * finalScale;
        double scaledHeight = canvasHeight * finalScale;
        
        translateTransform.setX((viewWidth - scaledWidth) / 2);
        translateTransform.setY((viewHeight - scaledHeight) / 2);
    }

    private void fillCanvasWithWhite() {
        if (drawingCanvas != null) {
            GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        }
    }
}
