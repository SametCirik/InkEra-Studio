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
            
            // MUHTEŞEM HIZ OPTİMİZASYONU:
            // Çizilen çizginin etrafına 5 piksellik bir güvenlik payı bırakıp sadece o kutuyu renderla
            int padding = 5;
            int minX = std::min(x0, x1) - padding;
            int minY = std::min(y0, y1) - padding;
            int maxX = std::max(x0, x1) + padding;
            int maxY = std::max(y0, y1) + padding;
            
            layerManager->renderRegion(pixelBuffer, minX, minY, maxX, maxY);
        }
    }
}
