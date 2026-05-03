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

    // YENİ: Katman Silme
    void removeLayer(int index) {
        // Tuvalde en az 1 katman kalmasını güvenlik için zorunlu kılıyoruz
        if (index >= 0 && index < layers.size() && layers.size() > 1) {
            delete layers[index];
            layers.erase(layers.begin() + index);
            if (activeLayerIndex >= layers.size()) activeLayerIndex = layers.size() - 1;
        }
    }

    // YENİ: Görünürlük Kapatma/Açma
    void setLayerVisibility(int index, bool visible) {
        if (index >= 0 && index < layers.size()) {
            layers[index]->setVisible(visible);
        }
    }

    // YENİ: Sürükle-Bırak ile Sıra Değiştirme (Hiyerarşi)
    void moveLayer(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;
        if (fromIndex >= 0 && fromIndex < layers.size() && toIndex >= 0 && toIndex < layers.size()) {
            Layer* temp = layers[fromIndex];
            layers.erase(layers.begin() + fromIndex);
            layers.insert(layers.begin() + toIndex, temp);
            activeLayerIndex = toIndex;
        }
    }

    void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) activeLayerIndex = index;
    }

    Layer* getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) return layers[activeLayerIndex];
        return nullptr;
    }

    void render(uint32_t* finalBuffer) {
        renderRegion(finalBuffer, 0, 0, width - 1, height - 1);
    }

    void renderRegion(uint32_t* finalBuffer, int minX, int minY, int maxX, int maxY) {
        minX = std::max(0, minX); minY = std::max(0, minY);
        maxX = std::min(width - 1, maxX); maxY = std::min(height - 1, maxY);
        if (minX > maxX || minY > maxY) return;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                finalBuffer[y * width + x] = 0xFFFFFFFF;
            }
        }

        for (Layer* layer : layers) {
            if (!layer->isVisible()) continue;
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    uint32_t color = layer->getPixel(x, y);
                    uint8_t alpha = (color >> 24) & 0xFF;
                    if (alpha > 0) finalBuffer[y * width + x] = color;
                }
            }
        }
    }
};

#endif // LAYER_MANAGER_HPP
