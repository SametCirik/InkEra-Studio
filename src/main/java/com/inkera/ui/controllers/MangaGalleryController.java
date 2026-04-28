package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MangaGalleryController {

    @FXML private FlowPane projectsFlowPane;
    @FXML private VBox newProjectCard;

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        if (newProjectCard != null) {
            newProjectCard.setOnMouseClicked(this::openNewMangaProjectDialog);
        }
        loadProjectsToUI();
    }

    public void loadProjectsToUI() {
        projectsFlowPane.getChildren().clear();
        
        List<ProjectModel> projects = projectService.loadAllProjects();
        
        for (ProjectModel project : projects) {
            VBox card = createProjectCard(project);
            projectsFlowPane.getChildren().add(card);
        }
        
        if (newProjectCard != null) {
            projectsFlowPane.getChildren().add(newProjectCard);
        }
    }

    private VBox createProjectCard(ProjectModel project) {
        VBox card = new VBox(12);
        card.getStyleClass().add("manga-card");
        
        StackPane cover = new StackPane();
        cover.getStyleClass().add("manga-card-cover");
        
        boolean hasCover = false;
        if (project.getProjectPath() != null) {
            File posterFile = new File(project.getProjectPath() + "/.inkera/poster.png");
            if (posterFile.exists()) {
                String imageUri = posterFile.toURI().toString() + "?time=" + System.currentTimeMillis();
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(new javafx.scene.image.Image(imageUri));
                
                imageView.setFitWidth(158); 
                imageView.setFitHeight(238);
                
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(158, 238);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                imageView.setClip(clip);
                
                cover.getChildren().add(imageView);
                hasCover = true;
            }
        }
        
        if (!hasCover) {
            Label noCoverLabel = new Label("Kapak Yok");
            noCoverLabel.setStyle("-fx-text-fill: #555;");
            cover.getChildren().add(noCoverLabel);
        }
        
        // EKSİK OLAN SATIRLAR BURAYA EKLENDİ!
        VBox infoBox = new VBox(4);
        infoBox.setStyle("-fx-alignment: center;");
        
        Label titleLabel = new Label(project.getTitle());
        titleLabel.getStyleClass().add("manga-title");
        titleLabel.setMaxWidth(150); 
        titleLabel.setWrapText(false); 
        titleLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        titleLabel.setStyle("-fx-alignment: center;"); 
        
        int chapterCount = project.getChapters() != null ? project.getChapters().size() : 0;
        Label subLabel = new Label(chapterCount + " Bölüm • " + project.getStatus());
        subLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        
        infoBox.getChildren().addAll(titleLabel, subLabel);
        card.getChildren().addAll(cover, infoBox);
        
        card.setOnMouseClicked(e -> {
            System.out.println("Projeye Tıklandı: " + project.getTitle() + " | Yol: " + project.getProjectPath());
            openMangaDetail(project);
        });
        
        return card;
    }

    private void openMangaDetail(ProjectModel project) {
        try {
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/manga_detail.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent detailView = loader.load();
            
            MangaDetailController detailController = loader.getController();
            detailController.setProject(project);
            
            HomeController.setCenterView(detailView);
            HomeController.setSidebarVisible(false);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewMangaProjectDialog(MouseEvent event) {
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/new_manga_project.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            // GÜNCELLENDİ: Native başlık çubuğuna isim verdik
            dialogStage.setTitle("Yeni Manga Projesi Başlat"); 
            // initStyle(StageStyle.UNDECORATED) SATIRI SİLİNDİ!

            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            dialogStage.showAndWait();
            
            loadProjectsToUI();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
