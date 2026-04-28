package com.inkera.ui.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.inkera.services.LocaleManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class SettingsController implements Initializable {

    @FXML
    private ComboBox<String> languageComboBox;

    private final Map<String, String> languageMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Dil adlarını ve kodlarını eşle
        languageMap.put(resources.getString("language.turkish"), "tr");
        languageMap.put(resources.getString("language.english"), "en");
        languageMap.put(resources.getString("language.russian"), "ru");

        // Dil seçiciyi doldur
        languageComboBox.getItems().addAll(languageMap.keySet());

        // Mevcut dili seç
        String currentLangCode = LocaleManager.getInstance().getLocale().getLanguage();
        for (Map.Entry<String, String> entry : languageMap.entrySet()) {
            if (entry.getValue().equals(currentLangCode)) {
                languageComboBox.getSelectionModel().select(entry.getKey());
                break;
            }
        }
        
        // Dil seçildiğinde metinleri güncelle ve ayarı kaydet
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null || newValue.equals(oldValue)) return;
            
            String langCode = languageMap.get(newValue);
            if(langCode != null){
                LocaleManager.getInstance().setLanguage(langCode);
            }
        });
    }
}
