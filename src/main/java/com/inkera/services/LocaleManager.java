package com.inkera.services;

import java.util.Locale;
import java.util.ResourceBundle;

import com.inkera.App;
import com.inkera.core.config.ConfigService; // GÜNCELLENDİ: Yeni yeri import edildi

public class LocaleManager {

    private static LocaleManager instance;
    private final ConfigService configService;
    private ResourceBundle bundle;
    private Locale locale;

    private LocaleManager() {
        configService = ConfigService.getInstance(); // GÜNCELLENDİ: Singleton yapısına göre çağrıldı
        loadLocale(configService.getLanguage());
    }

    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    private void loadLocale(String language) {
        locale = new Locale(language);
        bundle = ResourceBundle.getBundle("com.inkera.i18n.messages", locale);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLanguage(String language) {
        configService.setLanguage(language);
        loadLocale(language); 
        App.reloadUI();
    }
}
