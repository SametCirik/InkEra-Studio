package com.inkera.ui.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.inkera.services.LocaleManager;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;

public class SettingsController implements Initializable {

    @FXML private ComboBox<String> languageComboBox;
    
    // Cihaz Bağlantısı Elemanları
    @FXML private ScrollPane tabletScrollPane;
    @FXML private VBox tabletSettingsContainer;
    @FXML private ComboBox<String> deviceComboBox;
    @FXML private Button scanDevicesBtn;
    @FXML private Circle deviceStatusDot;
    @FXML private Label deviceStatusLabel;

    @FXML private Pane pressureGraphPane;
    @FXML private Pane brushPreviewPane;
    @FXML private Button resetCurveBtn;
    
    @FXML private Slider smoothingSlider;
    @FXML private Label smoothingValLabel;
    @FXML private ProgressBar pressureTestBar;

    private final Map<String, String> languageMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLanguageSection(resources);
        setupTabletSection();
        setupDeviceScanner();
    }

    private void setupLanguageSection(ResourceBundle resources) {
        languageMap.put(resources.getString("language.turkish"), "tr");
        languageMap.put(resources.getString("language.english"), "en");
        languageMap.put(resources.getString("language.russian"), "ru");

        languageComboBox.getItems().addAll(languageMap.keySet());

        String currentLangCode = LocaleManager.getInstance().getLocale().getLanguage();
        for (Map.Entry<String, String> entry : languageMap.entrySet()) {
            if (entry.getValue().equals(currentLangCode)) {
                languageComboBox.getSelectionModel().select(entry.getKey());
                break;
            }
        }
        
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null || newValue.equals(oldValue)) return;
            String langCode = languageMap.get(newValue);
            if(langCode != null){
                LocaleManager.getInstance().setLanguage(langCode);
            }
        });
    }

    private void setupDeviceScanner() {
        scanDevicesBtn.setOnAction(e -> {
            
            // İŞTE BÜYÜ BURADA: Buton devre dışı kalmadan önce JavaFX'in odağını 
            // zorla sayfanın en üstündeki görünmez çerçeveye hapsediyoruz!
            tabletSettingsContainer.requestFocus();
            
            deviceStatusLabel.setText("USB Portları Taranıyor...");
            deviceStatusLabel.setStyle("-fx-text-fill: #ffa500; -fx-font-weight: bold;"); 
            deviceStatusDot.setFill(Color.web("#ffa500"));
            
            scanDevicesBtn.setDisable(true); // Artık aşağı atlamayacak
            deviceComboBox.getItems().clear();
            deviceComboBox.setPromptText("Taranıyor...");

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                deviceComboBox.getItems().add("Huion 420 (OpenTabletDriver)");
                deviceComboBox.getItems().add("Huion 420 (Raw HID - Yakında)");
                
                scanDevicesBtn.setDisable(false);
                deviceComboBox.getSelectionModel().selectFirst();
                
                deviceStatusLabel.setText("Bağlandı");
                deviceStatusLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;"); 
                deviceStatusDot.setFill(Color.web("#4caf50"));
                
                // İşlem bittikten sonra da ekranın kaymasını %100 önlemek için çifte güvenlik:
                Platform.runLater(() -> {
                    tabletSettingsContainer.requestFocus();
                    tabletScrollPane.setVvalue(0.0);
                });
            });
            pause.play();
        });
    }

    private void setupTabletSection() {
        smoothingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            smoothingValLabel.setText(String.valueOf(newVal.intValue()));
        });
        pressureTestBar.setProgress(0.0);

        double width = 300.0;
        double height = 200.0;

        for (int i = 1; i < 4; i++) {
            Line vLine = new Line(i * (width / 4), 0, i * (width / 4), height);
            vLine.setStroke(Color.web("#333333"));
            vLine.setStrokeWidth(1);
            
            Line hLine = new Line(0, i * (height / 4), width, i * (height / 4));
            hLine.setStroke(Color.web("#333333"));
            hLine.setStrokeWidth(1);
            
            pressureGraphPane.getChildren().addAll(vLine, hLine);
        }

        Line defaultRefLine = new Line(0, height, width, 0);
        defaultRefLine.setStroke(Color.web("#666666"));
        defaultRefLine.setStrokeWidth(2);
        defaultRefLine.getStrokeDashArray().addAll(8d, 8d);
        defaultRefLine.setVisible(false); 

        QuadCurve curve = new QuadCurve();
        curve.setStartX(0);
        curve.setStartY(height);
        curve.setEndX(width);
        curve.setEndY(0);
        curve.setControlX(width / 2);
        curve.setControlY(height / 2);
        curve.setStroke(Color.web("#4caf50"));
        curve.setStrokeWidth(3);
        curve.setFill(null);

        Circle controlPoint = new Circle(width / 2, height / 2, 7);
        controlPoint.setFill(Color.WHITE);
        controlPoint.setStroke(Color.web("#2b2b2b"));
        controlPoint.setStrokeWidth(2);
        controlPoint.setCursor(javafx.scene.Cursor.HAND);

        pressureGraphPane.getChildren().addAll(defaultRefLine, curve, controlPoint);

        Runnable updateUI = () -> {
            boolean isModified = (curve.getControlX() != width / 2) || (curve.getControlY() != height / 2);
            defaultRefLine.setVisible(isModified);
            resetCurveBtn.setVisible(isModified);
            
            drawBrushPreview(curve.getControlX(), curve.getControlY(), width, height);
        };

        controlPoint.setOnMouseDragged(e -> {
            double newX = Math.max(0, Math.min(width, e.getX()));
            double newY = Math.max(0, Math.min(height, e.getY()));
            
            controlPoint.setCenterX(newX);
            controlPoint.setCenterY(newY);
            
            curve.setControlX(newX);
            curve.setControlY(newY);
            
            updateUI.run();
        });

        resetCurveBtn.setOnAction(e -> {
            controlPoint.setCenterX(width / 2);
            controlPoint.setCenterY(height / 2);
            curve.setControlX(width / 2);
            curve.setControlY(height / 2);
            updateUI.run();
        });

        updateUI.run();
    }

    private void drawBrushPreview(double controlX, double controlY, double graphWidth, double graphHeight) {
        brushPreviewPane.getChildren().clear();
        double pWidth = 250;
        double pHeight = 200;

        double cx = controlX / graphWidth;
        double cy = (graphHeight - controlY) / graphHeight;

        for(double x = 10; x < pWidth - 10; x += 1.5) {
            
            double inputPressure = (x - 10) / (pWidth - 20); 
            
            double a = 1.0 - 2.0 * cx;
            double b = 2.0 * cx;
            double c_val = -inputPressure;
            double t;

            if (Math.abs(a) < 1e-6) {
                t = inputPressure; 
            } else {
                double delta = b * b - 4.0 * a * c_val;
                t = (-b + Math.sqrt(Math.max(0, delta))) / (2.0 * a);
            }

            double outputPressure = 2.0 * (1.0 - t) * t * cy + t * t;
            outputPressure = Math.max(0, Math.min(1, outputPressure)); 
            
            double radius = outputPressure * 25 + 1;
            double strokeY = (pHeight / 2) + Math.sin(inputPressure * Math.PI * 1.5) * 40;
            
            Circle c = new Circle(x, strokeY, radius);
            c.setFill(Color.web("#e0e0e0"));
            brushPreviewPane.getChildren().add(c);
        }
    }
}
