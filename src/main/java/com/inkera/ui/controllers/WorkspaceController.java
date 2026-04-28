package com.inkera.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox; // HBox import edildi
import javafx.scene.layout.Pane; // Pane import edildi
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class WorkspaceController {

    @FXML private Canvas mainCanvas;
    @FXML private Pane canvasContainer; 
    @FXML private VBox leftSidebar; // Sol taraf VBox (Dikey)
    @FXML private HBox rightSidebar; // DÜZELTME: Sağ taraf artık HBox (Yatay)
    @FXML private Slider sizeSlider;
    @FXML private Label projectTitleLabel;

    private GraphicsContext gc;
    private double currentSize = 5.0;

    @FXML
    public void initialize() {
        gc = mainCanvas.getGraphicsContext2D();

        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentSize = newVal.doubleValue();
        });

        initDrawEvents();
    }

    public void setCanvasSize(int width, int height) {
        mainCanvas.setWidth(width);
        mainCanvas.setHeight(height);
        
        canvasContainer.setPrefSize(width, height);
        canvasContainer.setMaxSize(width, height);
        
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        
        gc.setStroke(Color.BLACK);
    }
    
    private void initDrawEvents() {
        mainCanvas.setOnMousePressed(e -> {
            gc.setLineWidth(currentSize);
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke(); 
        });

        mainCanvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
    }

    @FXML
    private void selectPen() {
        gc.setStroke(Color.BLACK);
    }

    @FXML
    private void selectEraser() {
        gc.setStroke(Color.WHITE); 
    }

    @FXML
    private void toggleLeftSidebar() {
        toggleSidebar(leftSidebar);
    }

    @FXML
    private void toggleRightSidebar() {
        toggleSidebar(rightSidebar);
    }

    // DÜZELTME: Parametre tipi 'VBox' yerine 'Pane' yapıldı.
    // Böylece hem VBox (Sol) hem HBox (Sağ) kabul edebilir.
    private void toggleSidebar(Pane sidebar) {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible); 
    }
}