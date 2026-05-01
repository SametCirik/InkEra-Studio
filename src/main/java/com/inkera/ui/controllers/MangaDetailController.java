package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MangaDetailController {

    @FXML private ImageView coverImageView;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private FlowPane tagsFlowPane;
    @FXML private Label totalChaptersLabel;
    @FXML private VBox chaptersVBox;

    private ProjectModel currentProject;
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
    }

    public void setProject(ProjectModel project) {
        this.currentProject = project;

        titleLabel.setText(project.getTitle());
        authorLabel.setText(project.getAuthor() != null ? project.getAuthor() : "Bilinmeyen Yazar");

        loadCoverImage();
        refreshTagsUI();

        chaptersVBox.getChildren().clear();
        int total = project.getChapters() != null ? project.getChapters().size() : 0;
        totalChaptersLabel.setText("Total " + total);

        if (total > 0) {
            for (ProjectModel.Chapter chapter : project.getChapters()) {
                chaptersVBox.getChildren().add(createChapterItem(chapter));
            }
        } else {
            Label noChapLabel = new Label("Henüz bölüm eklenmemiş. Yeni bölüm başlatarak serüvene katıl!");
            noChapLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
            chaptersVBox.getChildren().add(noChapLabel);
        }
    }

    private void refreshTagsUI() {
        tagsFlowPane.getChildren().clear();
        if (currentProject.getTags() != null && !currentProject.getTags().isEmpty()) {
            for (String tag : currentProject.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("meta-tag");
                tagsFlowPane.getChildren().add(tagLabel);
            }
        } else {
            Label noTagLabel = new Label("Etiket Ekle +");
            noTagLabel.getStyleClass().add("meta-tag");
            noTagLabel.setStyle("-fx-border-style: dashed; -fx-text-fill: #888;");
            tagsFlowPane.getChildren().add(noTagLabel);
        }
    }

    @FXML
    private void handleEditTags(MouseEvent event) {
        if (!tagsFlowPane.getChildren().isEmpty() && tagsFlowPane.getChildren().get(0) instanceof TextField) {
            return; 
        }

        tagsFlowPane.getChildren().clear();
        
        if (currentProject.getTags() != null) {
            for (String tag : currentProject.getTags()) {
                tagsFlowPane.getChildren().add(createMiniTagInput(tag));
            }
        }
        
        TextField emptyInput = createMiniTagInput("");
        tagsFlowPane.getChildren().add(emptyInput);
        emptyInput.requestFocus();
    }

    private TextField createMiniTagInput(String text) {
        TextField field = new TextField(text);
        field.getStyleClass().add("mini-tag-input");
        
        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                e.consume(); 
                if (!field.getText().trim().isEmpty()) {
                    TextField newInput = createMiniTagInput("");
                    int currentIndex = tagsFlowPane.getChildren().indexOf(field);
                    tagsFlowPane.getChildren().add(currentIndex + 1, newInput);
                    newInput.requestFocus();
                }
            } else if (e.getCode() == KeyCode.ENTER) {
                handleSaveTags();
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                if (field.getText().isEmpty() && tagsFlowPane.getChildren().size() > 1) {
                    e.consume();
                    int currentIndex = tagsFlowPane.getChildren().indexOf(field);
                    tagsFlowPane.getChildren().remove(field);
                    if (currentIndex > 0) {
                        tagsFlowPane.getChildren().get(currentIndex - 1).requestFocus();
                    } else {
                        tagsFlowPane.getChildren().get(0).requestFocus();
                    }
                }
            }
        });
        return field;
    }

    private void handleSaveTags() {
        List<String> newTags = new ArrayList<>();
        for (javafx.scene.Node node : tagsFlowPane.getChildren()) {
            if (node instanceof TextField) {
                String val = ((TextField) node).getText().trim();
                if (!val.isEmpty()) {
                    newTags.add(val);
                }
            }
        }
        currentProject.setTags(newTags);
        projectService.saveProject(currentProject); 
        refreshTagsUI();
    }

    private void loadCoverImage() {
        if (currentProject != null && currentProject.getProjectPath() != null) {
            File posterFile = new File(currentProject.getProjectPath() + "/.inkera/poster.png");
            if (posterFile.exists()) {
                String imageUri = posterFile.toURI().toString() + "?time=" + System.currentTimeMillis();
                
                // GÜNCELLENDİ: HD Çözünürlük ve Yumuşatma parametreleri eklendi (250x352)
                Image coverImage = new Image(imageUri, 250, 352, false, true);
                coverImageView.setImage(coverImage);
                coverImageView.setSmooth(true);
                coverImageView.setCache(true);
                
                // GÜNCELLENDİ: Poster hissiyatı için köşeleri ovalleştiriyoruz
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(250, 352);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                coverImageView.setClip(clip);
            } else {
                coverImageView.setImage(null);
            }
        }
    }

    @FXML
    private void handleChangeCover() {
        if (currentProject == null || currentProject.getProjectPath() == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Kapak Resmi Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(coverImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Path targetPath = Paths.get(currentProject.getProjectPath(), ".inkera", "poster.png");
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                loadCoverImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HBox createChapterItem(ProjectModel.Chapter chapter) {
        HBox hbox = new HBox(15);
        hbox.getStyleClass().add("chapter-item");
        hbox.setStyle("-fx-alignment: center-left;");

        Label idLabel = new Label("#" + chapter.getId());
        idLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold; -fx-font-size: 16px;");

        VBox infoBox = new VBox(2);
        Label chapterTitleLabel = new Label(chapter.getTitle());
        chapterTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        Label pageLabel = new Label(chapter.getPageCount() + " Sayfa");
        pageLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(chapterTitleLabel, pageLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button editBtn = new Button("Düzenle");
        editBtn.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-cursor: hand;");
        
        hbox.getChildren().addAll(idLabel, infoBox, spacer, editBtn);
        
        hbox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                openChapterDetail(chapter);
            }
        });
        editBtn.setOnAction(e -> openChapterDetail(chapter));
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Bölümü Sil");
        deleteItem.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
        deleteItem.setOnAction(e -> deleteChapter(chapter));
        contextMenu.getItems().add(deleteItem);

        hbox.setOnContextMenuRequested(e -> {
            contextMenu.show(hbox, e.getScreenX(), e.getScreenY());
        });
        
        return hbox;
    }

    private void openChapterDetail(ProjectModel.Chapter chapter) {
        try {
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/chapter_detail.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent detailView = loader.load();
            
            ChapterDetailController controller = loader.getController();
            controller.setChapterContext(currentProject, chapter);
            
            HomeController.setCenterView(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteChapter(ProjectModel.Chapter chapter) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bölümü Sil");
        alert.setHeaderText("'" + chapter.getTitle() + "' silinecek!");
        alert.setContentText("Bu işlem geri alınamaz. Bölüm klasörü ve içindeki tüm çizimler kalıcı olarak silinecektir. Onaylıyor musunuz?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1e1e1e;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            File chapterFolder = new File(currentProject.getProjectPath() + "/Episodes/" + chapter.getTitle());
            if (chapterFolder.exists()) {
                deleteDirectoryRecursively(chapterFolder);
            }

            currentProject.getChapters().remove(chapter);
            projectService.saveProject(currentProject); 
            setProject(currentProject);
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

    @FXML
    private void backToMangaGallery() {
        try {
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/manga_gallery.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent galleryView = loader.load();
            
            HomeController.setCenterView(galleryView);
            HomeController.setSidebarVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showFullSummary() {
        if (currentProject == null) return;
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/summary_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            SummaryDialogController controller = loader.getController();
            controller.setProject(currentProject);

            dialogStage.setTitle("Hikaye Özeti (Sinopsis)"); 
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddChapter() {
        if (currentProject == null) return;
        try {
            Stage dialogStage = new Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/new_chapter_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            NewChapterDialogController controller = loader.getController();
            controller.setProject(currentProject);

            dialogStage.setTitle("Yeni Bölüm Ekle"); 
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            setProject(currentProject); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
