#include "InkEraEngine.h"

extern "C" {

    void renderTestGradient(uint32_t* pixelBuffer, int width, int height) {
        // Her bir pikseli tek tek dolaş
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                // Basit bir Cyberpunk renk geçişi (Gradient) matematiği
                uint8_t r = (uint8_t)((x * 255) / width);     // Kırmızı kanalı X eksenine göre artar
                uint8_t g = 30;                               // Yeşil sabit (koyu)
                uint8_t b = (uint8_t)((y * 255) / height);    // Mavi kanalı Y eksenine göre artar
                uint8_t a = 255;                              // Tamamen opak (Alpha)

                // JavaFX PixelFormat.getIntArgbPreInstance() için A-R-G-B formatında bit kaydırma
                uint32_t color = (a << 24) | (r << 16) | (g << 8) | b;
                
                // Pikseli belleğe yaz
                pixelBuffer[y * width + x] = color;
            }
        }
    }

}
