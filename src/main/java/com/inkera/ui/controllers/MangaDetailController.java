package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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
        // Artık dinleyiciye (listener) ihtiyacımız yok, her şeyi Enter tuşu ile çözeceğiz.
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

    // Etiketleri normal görünümde (Label olarak) çizer
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

    // Düzenleme moduna geçiş (Kutucukları oluşturur)
    @FXML
    private void handleEditTags(MouseEvent event) {
        // Eğer zaten TextField (Edit modu) açıksa sıfırlamayı engelle
        if (!tagsFlowPane.getChildren().isEmpty() && tagsFlowPane.getChildren().get(0) instanceof TextField) {
            return; 
        }

        tagsFlowPane.getChildren().clear();
        
        // Mevcut etiketleri TextField'a dönüştür
        if (currentProject.getTags() != null) {
            for (String tag : currentProject.getTags()) {
                tagsFlowPane.getChildren().add(createMiniTagInput(tag));
            }
        }
        
        // Sona her zaman boş bir kutu ekle
        TextField emptyInput = createMiniTagInput("");
        tagsFlowPane.getChildren().add(emptyInput);
        emptyInput.requestFocus();
    }

    // Mini TextField üreten özel metodumuz
    private TextField createMiniTagInput(String text) {
        TextField field = new TextField(text);
        field.getStyleClass().add("mini-tag-input");
        
        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                // Boşluğa basılınca o kutuyu onaylar, yanına yeni bir boş kutu açar
                e.consume(); 
                if (!field.getText().trim().isEmpty()) {
                    TextField newInput = createMiniTagInput("");
                    int currentIndex = tagsFlowPane.getChildren().indexOf(field);
                    tagsFlowPane.getChildren().add(currentIndex + 1, newInput);
                    newInput.requestFocus();
                }
            } else if (e.getCode() == KeyCode.ENTER) {
                // Enter'a basınca her şeyi kaydet ve normal görünüme dön
                handleSaveTags();
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                // Kutunun içi boşken silme tuşuna basılırsa kutuyu sil ve bir öncekine odaklan
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

    // Tüm kutulardaki yazıları toplayıp projeyi kaydeder
    private void handleSaveTags() {
        List<String> newTags = new ArrayList<>();
        for (javafx.scene.Node node : tagsFlowPane.getChildren()) {
            if (node instanceof TextField) {
                String val = ((TextField) node).getText().trim();
                // Sadece içi dolu olan kutuları etiket olarak kabul et
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
                Image coverImage = new Image(imageUri);
                coverImageView.setImage(coverImage);
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
        return hbox;
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
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/summary_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            SummaryDialogController controller = loader.getController();
            controller.setProject(currentProject);

            dialogStage.setTitle("Hikaye Özeti (Sinopsis)"); 
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddChapter() {
        if (currentProject == null) return;
        try {
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/new_chapter_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent root = loader.load();

            NewChapterDialogController controller = loader.getController();
            controller.setProject(currentProject);

            // Native OS Penceresi
            dialogStage.setTitle("Yeni Bölüm Ekle"); 
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.centerOnScreen();
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Pencere kapanana kadar bekle
            dialogStage.showAndWait();

            // Pencere kapandıktan sonra (yeni bölüm eklenmiş olabilir) UI'ı tazele!
            setProject(currentProject); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
