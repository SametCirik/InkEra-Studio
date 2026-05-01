package com.inkera.ui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.scene.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GalleryController {

    @FXML private FlowPane projectsFlowPane;
    @FXML private VBox newProjectCard;

    @FXML
    public void initialize() {
        if (newProjectCard != null) {
            newProjectCard.setOnMouseClicked(this::handleNewProjectClick);
        }
        loadProjectsToUI();
    }

    private void handleNewProjectClick(MouseEvent event) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Yeni Çizim Projesi");
        dialog.setHeaderText("Yeni şaheserinin detaylarını gir:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b2b;");
        
        ButtonType createButtonType = new ButtonType("Oluştur", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        Label nameLabel = new Label("Proje Adı:");
        nameLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label authorLabel = new Label("Yazar:");
        authorLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-weight: bold; -fx-font-size: 14px;");

        TextField nameField = new TextField("Adsız_Çizim");
        nameField.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 4;");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Örn: Samet Cırık");
        authorField.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 4; -fx-prompt-text-fill: #888;");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(authorLabel, 0, 1);
        grid.add(authorField, 1, 1);

        dialogPane.setContent(grid);

        Node headerText = dialogPane.lookup(".header-panel .label");
        if (headerText != null) {
            headerText.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");
        }
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1e1e1e;");

        Node createButton = dialogPane.lookupButton(createButtonType);
        createButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        Node cancelButton = dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-cursor: hand;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new String[]{nameField.getText(), authorField.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        if (result.isPresent()) {
            String projectName = result.get()[0].trim();
            String authorName = result.get()[1].trim();
            if (authorName.isEmpty()) authorName = "Bilinmeyen Yazar";
            
            if (!projectName.isEmpty()) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Projenin Kaydedileceği Konumu Seçin");
                File selectedDirectory = directoryChooser.showDialog(projectsFlowPane.getScene().getWindow());

                if (selectedDirectory != null) {
                    createProjectScaffolding(projectName, authorName, selectedDirectory);
                }
            }
        }
    }

    private void createProjectScaffolding(String projectName, String authorName, File parentDir) {
        try {
            File projectDir = new File(parentDir, projectName);
            File inkeraDir = new File(projectDir, ".inkera");
            
            if (!projectDir.exists()) projectDir.mkdirs();
            if (!inkeraDir.exists()) inkeraDir.mkdirs();

            File userDataImagesDir = new File(System.getProperty("user.dir"), "user-data/images");
            if (!userDataImagesDir.exists()) userDataImagesDir.mkdirs();

            String projectId = UUID.randomUUID().toString();
            long currentTime = System.currentTimeMillis();

            File registryJson = new File(userDataImagesDir, projectName + ".json");
            try (FileWriter writer = new FileWriter(registryJson)) {
                writer.write("{\n");
                writer.write("  \"id\": \"" + projectId + "\",\n");
                writer.write("  \"title\": \"" + projectName + "\",\n");
                writer.write("  \"author\": \"" + authorName + "\",\n");
                writer.write("  \"path\": \"" + projectDir.getAbsolutePath().replace("\\", "/") + "\",\n");
                writer.write("  \"createdAt\": " + currentTime + ",\n");
                writer.write("  \"lastModified\": " + currentTime + "\n");
                writer.write("}");
            }

            File localMetaJson = new File(inkeraDir, "meta.json");
            try (FileWriter writer = new FileWriter(localMetaJson)) {
                writer.write("{\n");
                writer.write("  \"id\": \"" + projectId + "\",\n");
                writer.write("  \"author\": \"" + authorName + "\",\n");
                writer.write("  \"type\": \"IMAGE\",\n");
                writer.write("  \"resolution\": \"1920x1080\"\n"); 
                writer.write("}");
            }

            loadProjectsToUI();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProjectsToUI() {
        projectsFlowPane.getChildren().clear();
        
        if (newProjectCard != null) {
            projectsFlowPane.getChildren().add(newProjectCard);
        }

        File userDataImagesDir = new File(System.getProperty("user.dir"), "user-data/images");
        if (userDataImagesDir.exists() && userDataImagesDir.isDirectory()) {
            File[] jsonFiles = userDataImagesDir.listFiles((dir, name) -> name.endsWith(".json"));
            
            if (jsonFiles != null) {
                for (File file : jsonFiles) {
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        
                        String title = extractJsonString(content, "title");
                        String path = extractJsonString(content, "path");
                        String author = extractJsonString(content, "author");
                        if (author.isEmpty()) author = "Bilinmeyen Yazar";

                        if (!title.isEmpty() && !path.isEmpty()) {
                            VBox card = createDrawingCard(title, author, path);
                            projectsFlowPane.getChildren().add(card);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private VBox createDrawingCard(String title, String author, String projectPath) {
        VBox card = new VBox(12);
        card.getStyleClass().add("manga-card");
        card.setStyle("-fx-alignment: top-center;");

        StackPane cover = new StackPane();
        cover.getStyleClass().add("manga-card-cover");
        cover.setPrefSize(230, 230);
        cover.setMinSize(230, 230);
        cover.setMaxSize(230, 230);
        cover.setStyle("-fx-min-height: 230px; -fx-max-height: 230px;");

        File posterFile = new File(projectPath + "/.inkera/poster.png");
        if (posterFile.exists()) {
            String imageUri = posterFile.toURI().toString() + "?time=" + System.currentTimeMillis();
            Image hqImage = new Image(imageUri, 230, 230, false, true);
            
            Rectangle clip = new Rectangle(230, 230);
            clip.setArcWidth(12);
            clip.setArcHeight(12);
            clip.setFill(new ImagePattern(hqImage));
            cover.getChildren().add(clip);
        } else {
            Label noCoverLabel = new Label("Resim Yok");
            noCoverLabel.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");
            cover.getChildren().add(noCoverLabel);
        }

        VBox infoBox = new VBox(4);
        infoBox.setStyle("-fx-alignment: center;");

        // YENİ: Başlık hizalandı ve taşma engellendi
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("manga-title");
        titleLabel.setMaxWidth(200);
        titleLabel.setStyle("-fx-alignment: center;");
        titleLabel.setWrapText(false);
        titleLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

        // YENİ: Tarih çıkarıldı, Yazar eklendi
        Label authorLabel = new Label(author);
        authorLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px; -fx-alignment: center;");
        authorLabel.setMaxWidth(200);
        authorLabel.setWrapText(false);
        authorLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

        infoBox.getChildren().addAll(titleLabel, authorLabel);
        card.getChildren().addAll(cover, infoBox);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Resmi Sil");
        deleteItem.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
        deleteItem.setOnAction(e -> deleteProject(title, projectPath));
        contextMenu.getItems().add(deleteItem);

        card.setOnContextMenuRequested(e -> {
            contextMenu.show(card, e.getScreenX(), e.getScreenY());
        });

        card.setOnMouseClicked(e -> {
            System.out.println("Çizime Tıklandı: " + title + " | ID Bulunuyor...");
        });

        return card;
    }

    private void deleteProject(String projectName, String projectPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Projeyi Sil");
        alert.setHeaderText("'" + projectName + "' kalıcı olarak silinecek!");
        alert.setContentText("Bu işlem geri alınamaz. Proje klasörü ve içindeki tüm dosyalar silinecektir. Onaylıyor musunuz?");

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
            
            File projectFolder = new File(projectPath);
            if (projectFolder.exists()) {
                deleteDirectoryRecursively(projectFolder);
            }

            File userDataImagesDir = new File(System.getProperty("user.dir"), "user-data/images");
            File registryJson = new File(userDataImagesDir, projectName + ".json");
            if (registryJson.exists()) {
                registryJson.delete();
            }

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

    private String extractJsonString(String content, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"").matcher(content);
        if (m.find()) return m.group(1);
        return "";
    }
}
