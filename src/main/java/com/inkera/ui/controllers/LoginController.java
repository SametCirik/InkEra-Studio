package com.inkera.ui.controllers;

import com.inkera.services.AuthService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        statusLabel.setText("Giriş yapılıyor...");
        boolean success = authService.login(emailField.getText(), passwordField.getText());
        
        if (success) {
            statusLabel.setText("Giriş Başarılı!");
            statusLabel.setStyle("-fx-text-fill: #4caf50;");
            // Burada pencereyi kapatıp ana ekrandaki kullanıcı bilgisini güncelleyebiliriz
             closeWindow();
        } else {
            statusLabel.setText("Giriş Başarısız! Bilgileri kontrol et.");
            statusLabel.setStyle("-fx-text-fill: #e57373;");
        }
    }

    @FXML
    private void handleRegister() {
        statusLabel.setText("Kayıt olunuyor...");
        boolean success = authService.register(emailField.getText(), passwordField.getText());
        
        if (success) {
            statusLabel.setText("Kayıt Başarılı! Şimdi giriş yapabilirsin.");
            statusLabel.setStyle("-fx-text-fill: #4caf50;");
        } else {
            statusLabel.setText("Kayıt Başarısız! (Şifre en az 6 hane olmalı)");
            statusLabel.setStyle("-fx-text-fill: #e57373;");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}