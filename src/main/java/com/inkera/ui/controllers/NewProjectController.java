package com.inkera.ui.controllers;

import com.inkera.App;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewProjectController {

    @FXML private TextField nameField;
    @FXML private TextField widthField;
    @FXML private TextField heightField;

    // --- MEVCUT METODLARIN AYNI KALACAK ---
    
    @FXML
    private void handleCreate() {
        try {
            String name = nameField.getText();
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());

            if (width <= 0 || height <= 0) return;
            closeWindow();
            App.showWorkspace(name, width, height);
        } catch (NumberFormatException e) {
            System.out.println("Hatalı sayı girişi");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    // --- YENİ EKLENECEK KISIM: ŞABLON METODLARI ---

    @FXML
    private void presetFHD() {
        widthField.setText("1920");
        heightField.setText("1080");
    }

    @FXML
    private void presetSquare() {
        widthField.setText("1080");
        heightField.setText("1080");
    }

    @FXML
    private void preset4K() {
        widthField.setText("3840");
        heightField.setText("2160");
    }

    @FXML
    private void presetA4() {
        widthField.setText("2480");
        heightField.setText("3508");
    }
}