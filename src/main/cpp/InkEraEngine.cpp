#include "InkEraEngine.h"
#include "LayerManager.hpp"
#include "Pencil.hpp"

static LayerManager* layerManager = nullptr;
static Pencil pencilTool;

extern "C" {
    
    void clearCanvas(uint32_t* pixelBuffer, int width, int height, uint32_t colorArgb) {
        if (layerManager != nullptr) delete layerManager;
        layerManager = new LayerManager(width, height);
        layerManager->render(pixelBuffer);
    }

    void addNewLayer(uint32_t* pixelBuffer, int width, int height) {
        if (layerManager != nullptr) {
            layerManager->addLayer("Yeni Katman");
            layerManager->render(pixelBuffer);
        }
    }

    void setActiveLayer(int index) {
        if (layerManager != nullptr) layerManager->setActiveLayer(index);
    }

    void usePencil(uint32_t* pixelBuffer, int width, int height, int x0, int y0, int x1, int y1, uint32_t colorArgb) {
        if (layerManager == nullptr) return;
        Layer* activeLayer = layerManager->getActiveLayer();
        if (activeLayer != nullptr) {
            pencilTool.setColor(colorArgb);
            if (x0 == x1 && y0 == y1) {
                pencilTool.apply(*activeLayer, x1, y1);
            } else {
                pencilTool.applyLine(*activeLayer, x0, y0, x1, y1);
            }
            int padding = 5;
            layerManager->renderRegion(pixelBuffer, std::min(x0, x1) - padding, std::min(y0, y1) - padding, std::max(x0, x1) + padding, std::max(y0, y1) + padding);
        }
    }

    void setLayerVisibility(uint32_t* pixelBuffer, int width, int height, int index, bool isVisible) {
        if (layerManager != nullptr) {
            layerManager->setLayerVisibility(index, isVisible);
            layerManager->render(pixelBuffer); // Görünürlük değişince tüm ekranı tazele
        }
    }

    void deleteLayer(uint32_t* pixelBuffer, int width, int height, int index) {
        if (layerManager != nullptr) {
            layerManager->removeLayer(index);
            layerManager->render(pixelBuffer); // Silinince tüm ekranı tazele
        }
    }

    void moveLayer(uint32_t* pixelBuffer, int width, int height, int fromIndex, int toIndex) {
        if (layerManager != nullptr) {
            layerManager->moveLayer(fromIndex, toIndex);
            layerManager->render(pixelBuffer); // Hiyerarşi değişince tüm ekranı tazele
        }
    }
}
