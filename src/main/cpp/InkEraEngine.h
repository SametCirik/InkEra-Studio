#ifndef INKERA_ENGINE_H
#define INKERA_ENGINE_H

#include <stdint.h>

extern "C" {
    // Tuvali dışarıdan (Java'dan) gönderilen spesifik bir renkle doldurur
    void clearCanvas(uint32_t* pixelBuffer, int width, int height, uint32_t colorArgb);
}

#endif // INKERA_ENGINE_H
