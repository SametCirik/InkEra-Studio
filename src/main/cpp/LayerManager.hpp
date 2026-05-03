#ifndef LAYER_MANAGER_HPP
#define LAYER_MANAGER_HPP

#include "Layer.hpp"
#include <vector>
#include <string>
#include <algorithm>

class LayerManager {
private:
    int width;
    int height;
    std::vector<Layer*> layers;
    int activeLayerIndex;

public:
    LayerManager(int w, int h) : width(w), height(h), activeLayerIndex(-1) {
        addLayer("Katman 1");
    }

    ~LayerManager() {
        for (Layer* l : layers) delete l;
        layers.clear();
    }

    void addLayer(std::string name) {
        layers.push_back(new Layer(width, height, name));
        activeLayerIndex = layers.size() - 1;
    }

    void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) activeLayerIndex = index;
    }

    Layer* getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) return layers[activeLayerIndex];
        return nullptr;
    }

    // Tam ekran render (Sadece ekranı temizlerken veya yeni katman açarken kullanılır)
    void render(uint32_t* finalBuffer) {
        renderRegion(finalBuffer, 0, 0, width - 1, height - 1);
    }

    // YENİ OPTİMİZASYON: Sadece farenin gezdiği "Kirli Dikdörtgeni" render eden ışık hızlı metot
    void renderRegion(uint32_t* finalBuffer, int minX, int minY, int maxX, int maxY) {
        // Sınırların dışına çıkmasını engelle
        minX = std::max(0, minX);
        minY = std::max(0, minY);
        maxX = std::min(width - 1, maxX);
        maxY = std::min(height - 1, maxY);

        if (minX > maxX || minY > maxY) return;

        // 1. Sadece ilgili minik kutuyu beyaza boya
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                finalBuffer[y * width + x] = 0xFFFFFFFF;
            }
        }

        // 2. Sadece ilgili minik kutudaki katman piksellerini birleştir
        for (Layer* layer : layers) {
            if (!layer->isVisible()) continue;
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    uint32_t color = layer->getPixel(x, y);
                    uint8_t alpha = (color >> 24) & 0xFF;
                    if (alpha > 0) {
                        finalBuffer[y * width + x] = color;
                    }
                }
            }
        }
    }
};

#endif // LAYER_MANAGER_HPP
