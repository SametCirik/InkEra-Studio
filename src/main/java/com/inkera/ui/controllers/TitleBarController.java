package com.inkera.ui.controllers; // GÜNCELLENDİ

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TitleBarController {

    @FXML private HBox titleBar;
    @FXML private Button minimizeBtn;
    @FXML private Button maximizeBtn;
    @FXML private Button closeBtn;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // İkonların başlangıç durumu
        maximizeBtn.setText("□"); 
    }

    // --- Pencere Hareketi ---
    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        }
    }

    @FXML
    private void handleMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            maximize();
        }
    }

    // --- Buton İşlevleri ---

    @FXML
    private void minimize() {
        ((Stage) titleBar.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void maximize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        
        // Durumu tersine çevir
        boolean currentlyMaximized = stage.isMaximized();
        stage.setMaximized(!currentlyMaximized);

        // İkonu güncelle
        // Eğer şu an tam ekransa (currentlyMaximized=true), küçülecek -> Kare İkonu
        // Eğer şu an küçükse (currentlyMaximized=false), büyüyecek -> İç İçe Kare İkonu
        if (currentlyMaximized) {
            maximizeBtn.setText("□"); // Tek kare (Büyüt)
        } else {
            maximizeBtn.setText("❐"); // İç içe kare (Geri Yükle / Küçült)
        }
    }

    @FXML
    private void close() {
        System.exit(0);
    }
}