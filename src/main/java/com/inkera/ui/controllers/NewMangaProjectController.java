package com.inkera.ui.controllers;

import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;

public class NewMangaProjectController {

    @FXML private TextField projectNameField;
    @FXML private TextField authorField;
    @FXML private TextField pathField;

    private File selectedDirectory;
    private final ProjectService projectService = new ProjectService();

    @FXML
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Proje Ana Klasörünü Seçin");
        selectedDirectory = directoryChooser.showDialog(pathField.getScene().getWindow());
        
        if (selectedDirectory != null) {
            pathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleCreate() {
        String name = projectNameField.getText();
        String author = authorField.getText();

        if (name.isEmpty() || selectedDirectory == null) {
            System.err.println("Eksik bilgi!");
            return;
        }

        // Fiziksel klasörleri oluştur (ProjectService içindeki motorumuz)
        projectService.createMangaProjectScaffold(name, author, selectedDirectory);
        
        // Pencereyi kapat
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) projectNameField.getScene().getWindow();
        stage.close();
    }
}
