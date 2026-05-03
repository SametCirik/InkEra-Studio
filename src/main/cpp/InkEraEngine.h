#ifndef INKERA_ENGINE_H
#define INKERA_ENGINE_H

#include <stdint.h>

extern "C" {
    void clearCanvas(uint32_t* pixelBuffer, int width, int height, uint32_t colorArgb);
    void addNewLayer(uint32_t* pixelBuffer, int width, int height);
    void setActiveLayer(int index);
    void usePencil(uint32_t* pixelBuffer, int width, int height, int x0, int y0, int x1, int y1, uint32_t colorArgb);
    
    // YENİ PRO YETENEKLER KÖPRÜSÜ
    void setLayerVisibility(uint32_t* pixelBuffer, int width, int height, int index, bool isVisible);
    void deleteLayer(uint32_t* pixelBuffer, int width, int height, int index);
    void moveLayer(uint32_t* pixelBuffer, int width, int height, int fromIndex, int toIndex);
}

#endif // INKERA_ENGINE_H
