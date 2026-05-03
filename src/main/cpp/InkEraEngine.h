#ifndef INKERA_ENGINE_H
#define INKERA_ENGINE_H

#include <stdint.h>

extern "C" {
    void clearCanvas(uint32_t* pixelBuffer, int width, int height, uint32_t colorArgb);
    void addNewLayer(uint32_t* pixelBuffer, int width, int height);
    void setActiveLayer(int index);
    
    // GÜNCELLENDİ: Artık A ve B noktalarını alıyor (x0, y0 -> x1, y1)
    void usePencil(uint32_t* pixelBuffer, int width, int height, int x0, int y0, int x1, int y1, uint32_t colorArgb);
}

#endif // INKERA_ENGINE_H
