package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class NewChapterDialogController {

    @FXML private TextField chapterTitleField;

    private ProjectModel currentProject;
    private final ProjectService projectService = new ProjectService();

    public void setProject(ProjectModel project) {
        this.currentProject = project;
    }

    @FXML
    private void handleCreate() {
        String title = chapterTitleField.getText().trim();
        if (title.isEmpty() || currentProject == null || currentProject.getProjectPath() == null) {
            return;
        }

        // 1. Fiziksel Klasörü Oluştur (ProjeYolu/Episodes/BolumAdi)
        Path episodeFolder = Paths.get(currentProject.getProjectPath(), "Episodes", title);
        try {
            if (!Files.exists(episodeFolder)) {
                Files.createDirectories(episodeFolder);
            }
        } catch (IOException e) {
            System.err.println("Bölüm klasörü oluşturulamadı: " + e.getMessage());
            return;
        }

        // 2. JSON Verisini Güncelle
        if (currentProject.getChapters() == null) {
            currentProject.setChapters(new ArrayList<>());
        }

        ProjectModel.Chapter newChapter = new ProjectModel.Chapter();
        // ID'yi mevcut bölüm sayısının 1 fazlası yapalım
        newChapter.setId(String.valueOf(currentProject.getChapters().size() + 1));
        newChapter.setTitle(title);
        newChapter.setPageCount(0); 

        currentProject.getChapters().add(newChapter);

        // 3. Hem Hub'a hem yerel meta.json'a kaydet
        projectService.saveProject(currentProject);

        // 4. Pencereyi Kapat
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) chapterTitleField.getScene().getWindow();
        stage.close();
    }
}
