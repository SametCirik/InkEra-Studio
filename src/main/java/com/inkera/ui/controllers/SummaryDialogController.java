package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class SummaryDialogController {

    @FXML private TextArea summaryArea;
    @FXML private Button editButton;
    @FXML private Button saveButton;

    private ProjectModel currentProject;
    private final ProjectService projectService = new ProjectService();

    public void setProject(ProjectModel project) {
        this.currentProject = project;
        if (project.getSynopsis() != null) {
            summaryArea.setText(project.getSynopsis());
        }
    }

    @FXML
    private void toggleEdit() {
        boolean isEditable = summaryArea.isEditable();
        summaryArea.setEditable(!isEditable);
        
        if (!isEditable) { // Düzenleme moduna geçildi
            summaryArea.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-color: transparent; -fx-border-color: #4caf50; -fx-border-radius: 5;");
            editButton.setVisible(false);
            saveButton.setVisible(true);
            saveButton.setManaged(true);
        }
    }

    @FXML
    private void handleSave() {
        if (currentProject != null) {
            currentProject.setSynopsis(summaryArea.getText());
            // ProjectService, bu metotla veriyi hem yerel klasöre hem Hub'a kaydedecek!
            projectService.saveProject(currentProject); 
        }
        
        // Arayüzü tekrar "Sadece Okunur" moduna al
        summaryArea.setEditable(false);
        summaryArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-color: transparent; -fx-border-color: #333; -fx-border-radius: 5;");
        editButton.setVisible(true);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) summaryArea.getScene().getWindow();
        stage.close();
    }
}
