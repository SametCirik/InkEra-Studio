package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

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
        cover.setPrefSize(230, 324);
        cover.setMinSize(230, 324);
        cover.setMaxSize(230, 324);
        
        boolean hasCover = false;
        if (project.getProjectPath() != null) {
            File posterFile = new File(project.getProjectPath() + "/.inkera/poster.png");
            if (posterFile.exists()) {
                String imageUri = posterFile.toURI().toString() + "?time=" + System.currentTimeMillis();
                javafx.scene.image.Image hqImage = new javafx.scene.image.Image(imageUri, 230, 324, false, true);
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(hqImage);
                
                imageView.setSmooth(true);
                imageView.setCache(true);
                
                imageView.setFitWidth(230); 
                imageView.setFitHeight(324);
                
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(230, 324);
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
        
        VBox infoBox = new VBox(4);
        infoBox.setStyle("-fx-alignment: center;");
        
        Label titleLabel = new Label(project.getTitle());
        titleLabel.getStyleClass().add("manga-title");
        titleLabel.setMaxWidth(220); 
        titleLabel.setWrapText(false); 
        titleLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        titleLabel.setStyle("-fx-alignment: center;"); 
        
        int chapterCount = project.getChapters() != null ? project.getChapters().size() : 0;
        Label subLabel = new Label(chapterCount + " Bölüm • " + (project.getStatus() != null ? project.getStatus() : "ONGOING"));
        subLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        
        infoBox.getChildren().addAll(titleLabel, subLabel);
        card.getChildren().addAll(cover, infoBox);
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Projeyi Sil");
        deleteItem.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
        deleteItem.setOnAction(e -> deleteMangaProject(project));
        contextMenu.getItems().add(deleteItem);

        card.setOnContextMenuRequested(e -> {
            contextMenu.show(card, e.getScreenX(), e.getScreenY());
        });

        card.setOnMouseClicked(e -> {
            System.out.println("Projeye Tıklandı: " + project.getTitle() + " | Yol: " + project.getProjectPath());
            openMangaDetail(project);
        });
        
        return card;
    }

    private void deleteMangaProject(ProjectModel project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Manga Projesini Sil");
        alert.setHeaderText("'" + project.getTitle() + "' kalıcı olarak silinecek!");
        alert.setContentText("Bu işlem geri alınamaz. Proje klasörü, bölümler ve sayfalar tamamen silinecektir. Onaylıyor musunuz?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        Node contentLabel = dialogPane.lookup(".content.label");
        if (contentLabel != null) contentLabel.setStyle("-fx-text-fill: #e0e0e0;");
        Node headerPanel = dialogPane.lookup(".header-panel");
        if (headerPanel != null) headerPanel.setStyle("-fx-background-color: #1e1e1e;");
        Node headerText = dialogPane.lookup(".header-panel .label");
        if (headerText != null) headerText.setStyle("-fx-text-fill: #e0e0e0;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // 1. Fiziksel Dizin Siliniyor
            if (project.getProjectPath() != null) {
                File projectFolder = new File(project.getProjectPath());
                if (projectFolder.exists()) {
                    deleteDirectoryRecursively(projectFolder);
                }
            }

            // 2. GÜNCELLENDİ: PATH Eşleşmesi ile Kurşun Geçirmez JSON Avcısı
            if (project.getProjectPath() != null) {
                File userDataProjectsDir = new File(System.getProperty("user.dir"), "user-data/projects");
                if (userDataProjectsDir.exists() && userDataProjectsDir.isDirectory()) {
                    File[] jsonFiles = userDataProjectsDir.listFiles((dir, name) -> name.endsWith(".json"));
                    if (jsonFiles != null) {
                        String targetPath = project.getProjectPath().replace("\\", "/");
                        
                        for (File file : jsonFiles) {
                            try {
                                String content = new String(Files.readAllBytes(file.toPath()));
                                // Eğer dosyanın içindeki path, sildiğimiz projenin path'ini içeriyorsa kesin odur!
                                if (content.contains(targetPath)) {
                                    file.delete();
                                    break;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            // 3. Arayüzü Tazele
            loadProjectsToUI();
        }
    }

    private void deleteDirectoryRecursively(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        directoryToBeDeleted.delete();
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

            dialogStage.setTitle("Yeni Manga Projesi Başlat"); 

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
