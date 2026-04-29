package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NewChapterDialogController {

    @FXML private TextField chapterTitleField;
    @FXML private ComboBox<String> layoutComboBox;

    private ProjectModel currentProject;
    private final ProjectService projectService = new ProjectService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @FXML
    public void initialize() {
        layoutComboBox.getSelectionModel().selectFirst();
    }

    public void setProject(ProjectModel project) {
        this.currentProject = project;
    }

    @FXML
    private void handleCreate() {
        String title = chapterTitleField.getText().trim();
        if (title.isEmpty() || currentProject == null || currentProject.getProjectPath() == null) return;

        // 1. Fiziksel Klasörleri Oluştur (ProjeYolu/Episodes/BolumAdi/.inkepisode)
        Path episodeFolder = Paths.get(currentProject.getProjectPath(), "Episodes", title);
        Path inkEpisodeFolder = episodeFolder.resolve(".inkepisode");
        try {
            if (!Files.exists(episodeFolder)) Files.createDirectories(episodeFolder);
            if (!Files.exists(inkEpisodeFolder)) Files.createDirectories(inkEpisodeFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 2. Okuma Yönünü Belirle
        String layout = layoutComboBox.getSelectionModel().getSelectedIndex() == 0 ? "RTL" : "LTR";

        // 3. Bölüm objesini oluştur ve ana JSON'a ekle
        if (currentProject.getChapters() == null) currentProject.setChapters(new ArrayList<>());
        
        ProjectModel.Chapter newChapter = new ProjectModel.Chapter();
        newChapter.setId(String.valueOf(currentProject.getChapters().size() + 1));
        newChapter.setTitle(title);
        newChapter.setPageCount(0);
        newChapter.setPageLayout(layout);

        currentProject.getChapters().add(newChapter);
        projectService.saveProject(currentProject);

        // 4. .inkepisode içine bölümün yerel JSON dosyasını oluştur
        try (Writer writer = Files.newBufferedWriter(inkEpisodeFolder.resolve("meta.json"))) {
            gson.toJson(newChapter, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        closeWindow();
    }

    @FXML
    private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        Stage stage = (Stage) chapterTitleField.getScene().getWindow();
        stage.close();
    }
}
