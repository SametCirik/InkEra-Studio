package com.inkera.core.config; // GÜNCELLENDİ: Yeni paket yolu

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigService {

    private static ConfigService instance; // YENİ: Singleton yapısı (App'in her yerinden kolayca ulaşmak için)
    private final Properties properties = new Properties();
    private final File configDir;
    private final File configFile;

    private ConfigService() {
        // YENİ: Linux XDG Standardına göre config yolunu belirliyoruz (~/.config/inkera)
        String userHome = System.getProperty("user.home");
        String configPath = System.getenv("XDG_CONFIG_HOME");
        
        if (configPath == null || configPath.isEmpty()) {
            configPath = userHome + File.separator + ".config";
        }
        
        configDir = new File(configPath, "inkera");
        configFile = new File(configDir, "config.properties");

        loadOrCreateConfig();
    }

    public static ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }

    private void loadOrCreateConfig() {
        try {
            if (!configDir.exists()) {
                configDir.mkdirs(); // Klasör yoksa oluştur
            }
            if (configFile.exists()) {
                try (FileInputStream in = new FileInputStream(configFile)) {
                    properties.load(in);
                }
            } else {
                // Varsayılan ayarları oluştur
                properties.setProperty("language", "tr");
                properties.setProperty("useCustomTitleBar", "true"); // YENİ: Başlık çubuğu ayarı
                saveProperties();
            }
        } catch (IOException e) {
            e.printStackTrace();
            properties.setProperty("language", "tr");
            properties.setProperty("useCustomTitleBar", "true");
        }
    }

    public void saveProperties() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "InkEra Studio Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLanguage() {
        return properties.getProperty("language", "tr");
    }

    public void setLanguage(String language) {
        properties.setProperty("language", language);
        saveProperties();
    }

    // YENİ: Başlık Çubuğu Getter / Setter
    public boolean isCustomTitleBar() {
        return Boolean.parseBoolean(properties.getProperty("useCustomTitleBar", "true"));
    }

    public void setCustomTitleBar(boolean use) {
        properties.setProperty("useCustomTitleBar", String.valueOf(use));
        saveProperties();
    }
}