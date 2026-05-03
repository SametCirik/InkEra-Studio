#ifndef LAYER_HPP
#define LAYER_HPP

#include <stdint.h>
#include <string>
#include <vector>
#include <algorithm>

class Layer {
private:
    int width;
    int height;
    // Pikselleri C++'ın kendi RAM'inde (Heap) güvenle tutuyoruz
    std::vector<uint32_t> pixels; 
    bool visible;
    float opacity; // 0.0 (Tam Şeffaf) ile 1.0 (Tam Opak) arası
    std::string name;

public:
    // Kurucu: Katman ilk oluştuğunda tamamen ŞEFFAF (0x00000000) olur
    Layer(int w, int h, std::string layerName) 
        : width(w), height(h), name(layerName), visible(true), opacity(1.0f) {
        pixels.resize(w * h, 0x00000000); 
    }

    // Katmanı tamamen temizler (Şeffaf yapar)
    void clear() {
        std::fill(pixels.begin(), pixels.end(), 0x00000000);
    }

    // Katmana özgü güvenli piksel boyama
    void setPixel(int x, int y, uint32_t colorArgb) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y * width + x] = colorArgb;
        }
    }

    // İlgili pikselin rengini okuma (Blend/Karıştırma işlemleri için lazım olacak)
    uint32_t getPixel(int x, int y) const {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return pixels[y * width + x];
        }
        return 0; // Sınır dışıysa şeffaf döndür
    }

    // Getter ve Setter Metotları
    bool isVisible() const { return visible; }
    void setVisible(bool v) { visible = v; }
    
    float getOpacity() const { return opacity; }
    void setOpacity(float o) { opacity = o; }
    
    std::string getName() const { return name; }
    void setName(std::string n) { name = n; }
    
    int getWidth() const { return width; }
    int getHeight() const { return height; }
};

#endif // LAYER_HPP
