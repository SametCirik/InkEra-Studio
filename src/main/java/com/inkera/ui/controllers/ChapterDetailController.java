package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ChapterDetailController {

    @FXML private Label headerTitleLabel;
    @FXML private Label chapterTitleLabel;
    @FXML private Label layoutInfoLabel;
    @FXML private VBox pagesVBox;

    private ProjectModel project;
    private ProjectModel.Chapter chapter;

    public void setChapterContext(ProjectModel project, ProjectModel.Chapter chapter) {
        this.project = project;
        this.chapter = chapter;

        headerTitleLabel.setText(project.getTitle() + " - " + chapter.getTitle());
        chapterTitleLabel.setText(chapter.getTitle());
        
        boolean isRTL = "RTL".equals(chapter.getPageLayout());
        layoutInfoLabel.setText(isRTL ? "Okuma Yönü: Sağdan Sola" : "Okuma Yönü: Soldan Sağa");

        loadPages(isRTL);
    }

    private void loadPages(boolean isRTL) {
        pagesVBox.getChildren().clear();
        
        // Şimdilik test amaçlı sahte sayfalar üretiyoruz (Sayfa 2, 3, 4, 5...)
        // (Gerçek sistemde Files.list ile klasörleri sayacağız)
        int totalMockPages = 5; 

        // Sayfa 1 soldaki panelde (kapak). O yüzden 2. sayfadan başlıyoruz.
        for (int i = 2; i <= totalMockPages; i += 2) {
            HBox spread = new HBox(10);
            spread.setStyle("-fx-alignment: center;");
            
            StackPane leftPage = createPageMock(isRTL ? i + 1 : i, totalMockPages);
            StackPane rightPage = createPageMock(isRTL ? i : i + 1, totalMockPages);
            
            spread.getChildren().addAll(leftPage, rightPage);
            pagesVBox.getChildren().add(spread);
        }
    }

    private StackPane createPageMock(int pageNum, int max) {
        StackPane pane = new StackPane();
        pane.setPrefSize(180, 250);
        if (pageNum > max) {
            pane.setStyle("-fx-background-color: transparent;"); // Çift numara yoksa boşluk bırak
        } else {
            pane.setStyle("-fx-background-color: #333; -fx-background-radius: 5;");
            Label label = new Label("Sayfa " + pageNum);
            label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            pane.getChildren().add(label);
        }
        return pane;
    }

    @FXML
    private void backToMangaDetail() {
        try {
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/manga_detail.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent detailView = loader.load();
            
            MangaDetailController controller = loader.getController();
            controller.setProject(project);
            
            HomeController.setCenterView(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // YENİ: Bölüm Silme İşlemi
    private void deleteChapter(ProjectModel.Chapter chapter) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bölümü Sil");
        alert.setHeaderText("'" + chapter.getTitle() + "' silinecek!");
        alert.setContentText("Bu işlem geri alınamaz. Bölüm klasörü ve içindeki tüm çizimler kalıcı olarak silinecektir. Onaylıyor musunuz?");

        // Uyarı penceresinin temasını da karanlık yapalım ki sırıtmasın
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1e1e1e;");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            
            // 1. Fiziksel Klasörü Sil (İçindekilerle birlikte)
            File chapterFolder = new File(currentProject.getProjectPath() + "/Episodes/" + chapter.getTitle());
            if (chapterFolder.exists()) {
                deleteDirectoryRecursively(chapterFolder);
            }

            // 2. JSON'dan Çıkar ve Kaydet
            currentProject.getChapters().remove(chapter);
            
            // Not: İleride user-data'dan bölüm listesini kaldıracağız, 
            // ama şimdilik mevcut servisimizle her iki tarafı da eşitliyoruz.
            projectService.saveProject(currentProject); 

            // 3. UI'ı Tazele
            setProject(currentProject);
        }
    }

    // YENİ: İçi dolu klasörleri silmek için rekürsif (kendi kendini çağıran) silici
    private void deleteDirectoryRecursively(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
