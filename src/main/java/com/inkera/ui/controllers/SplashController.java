package com.inkera.ui.controllers;

import com.inkera.App;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class SplashController {

    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Arka planda çalışacak görev (UI'ı dondurmamak için Task kullanıyoruz)
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simülasyon: Modüller yükleniyor gibi yapıyoruz
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(20); // Yükleme hızı (toplamda ~2 saniye sürer)
                    updateProgress(i, 100);
                    updateMessage("InkEra Studio Başlatılıyor... %" + i);
                }
                return null;
            }
        };

        // Task'in ilerlemesini ve mesajını FXML bileşenlerine bağlıyoruz
        if (progressBar != null) {
            progressBar.progressProperty().bind(task.progressProperty());
        }
        
        if (statusLabel != null) {
            statusLabel.textProperty().bind(task.messageProperty());
        }

        // Görev başarıyla bittiğinde çalışacak kısım
        task.setOnSucceeded(event -> {
            // ARTIK BURASI HOME EKRANINI AÇIYOR:
            App.showHome();
        });

        // Görevi yeni bir thread içinde başlat
        new Thread(task).start();
    }
}