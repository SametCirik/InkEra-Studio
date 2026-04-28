package com.inkera.ui.controllers; // GÜNCELLENDİ

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.inkera.App;
import com.inkera.core.models.ProjectModel;
import com.inkera.services.LocaleManager;
import com.inkera.services.ProjectService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HomeController implements Initializable {

    @FXML private BorderPane mainPane;
    private static BorderPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (mainPane != null) {
            rootPane = mainPane;

            // --- YENİ: KWin/Native Başlık Çubuğu Kontrolü ---
            boolean useCustomTitleBar = com.inkera.core.config.ConfigService.getInstance().isCustomTitleBar();
            if (!useCustomTitleBar) {
                // Custom başlık çubuğunu BorderPane'in tepesinden tamamen sil
                mainPane.setTop(null); 
                
                // Pencere yüksekliğini tam olarak TitleBar'ın yüksekliği (32px) kadar daralt
                // Böylece ortadaki çalışma alanının (center) boyutu hiç değişmemiş olur!
                mainPane.setPrefHeight(mainPane.getPrefHeight() - 32); 
            }
            // ------------------------------------------------

            try {
                showDashboard();
            } catch (Exception e) {
                System.err.println("UYARI: Başlangıç ekranı (Dashboard) yüklenemedi. Dosya eksik olabilir.");
            }
        }  
    }

    public static void setCenterView(Parent view) {
        if (rootPane != null) {
            rootPane.setCenter(view);
        } else {
            System.err.println("HATA: Ana Panel (rootPane) bulunamadı! Program akışında hata var.");
        }
    }

    public static void setSidebarVisible(boolean visible) {
        if (rootPane != null && rootPane.getLeft() != null) {
            rootPane.getLeft().setVisible(visible);
            rootPane.getLeft().setManaged(visible); // Managed = false olursa, kapladığı alanı da geri verir!
        }
    }

    private void loadView(String fxmlPath) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("HATA: FXML dosyası bulunamadı! Yol: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(LocaleManager.getInstance().getBundle());
            Parent view = loader.load();
            setCenterView(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("HATA: Dosya yüklenirken G/Ç hatası oluştu: " + fxmlPath);
        }
    }

    // --- MENÜ BUTONLARI ---

    @FXML
    private void showDashboard() {
        loadView("/com/inkera/fxml/dashboard.fxml"); // GÜNCELLENDİ
    }

    @FXML
    private void showGallery() {
        loadView("/com/inkera/fxml/gallery.fxml"); // GÜNCELLENDİ
    }

    @FXML
    private void showMangaGallery() {
        loadView("/com/inkera/fxml/manga_gallery.fxml"); // GÜNCELLENDİ
    }

    @FXML
    private void showSettings() {
        loadView("/com/inkera/fxml/settings.fxml"); // GÜNCELLENDİ
    }

    // --- MANGA İŞLEMLERİ ---

    @FXML
    private void openMangaDetail() {
        loadView("/com/inkera/fxml/manga_detail.fxml"); // GÜNCELLENDİ
    }

    @FXML
    private void backToMangaGallery() {
        showMangaGallery();
    }

    // --- DİĞER İŞLEMLER ---

    @FXML
    private void openLastProject() {
        App.showWorkspace("Cyberpunk_City", 1920, 1080);
    }

    @FXML
    private void handleNewProject(ActionEvent event) {
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/new_project.fxml"); // GÜNCELLENDİ
            if (resourceUrl == null) {
                System.err.println("HATA: 'new_project.fxml' dosyası bulunamadı!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showFullSummary() {
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/summary_view.fxml"); // GÜNCELLENDİ
            if (resourceUrl == null) {
                System.err.println("HATA: 'summary_view.fxml' dosyası bulunamadı!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeSummaryDialog(ActionEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showLoginDialog() {
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/login.fxml"); // GÜNCELLENDİ
            if (resourceUrl == null) {
                System.err.println("HATA: 'login.fxml' dosyası bulunamadı!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}