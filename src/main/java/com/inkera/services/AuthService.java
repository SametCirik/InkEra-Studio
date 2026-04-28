package com.inkera.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AuthService {
    // BURAYA KENDİ WEB API KEY'İNİ YAPIŞTIR
    private static final String API_KEY = "AIzaSyD0I2MaJ0epLBmmh_BdASFZrciInqa-4XM";
    
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private final HttpClient client;

    public AuthService() {
        this.client = HttpClient.newHttpClient();
    }

    // Kayıt Ol (Sign Up)
    public boolean register(String email, String password) {
        return sendAuthRequest(SIGN_UP_URL, email, password);
    }

    // Giriş Yap (Login)
    public boolean login(String email, String password) {
        return sendAuthRequest(SIGN_IN_URL, email, password);
    }

    private boolean sendAuthRequest(String url, String email, String password) {
        try {
            // JSON verisini hazırla
            JsonObject json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("password", password);
            json.addProperty("returnSecureToken", true);

            // İsteği oluştur
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            // İsteği gönder
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Cevabı kontrol et (200 OK ise başarılıdır)
            if (response.statusCode() == 200) {
                JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
                String idToken = responseJson.get("idToken").getAsString();
                String localId = responseJson.get("localId").getAsString();
                
                System.out.println("İşlem Başarılı! User ID: " + localId);
                // İleride bu token'ı kaydedeceğiz.
                return true;
            } else {
                // Hata detayını yazdır
                System.err.println("Hata: " + response.body());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}