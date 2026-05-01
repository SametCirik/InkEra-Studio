#ifndef INKERA_ENGINE_H
#define INKERA_ENGINE_H

#include <stdint.h>

// Java'nın Project Panama üzerinden bu fonksiyonu bulabilmesi için name-mangling'i kapatıyoruz
extern "C" {
    // Tuval piksellerini dolduracak test fonksiyonumuz
    void renderTestGradient(uint32_t* pixelBuffer, int width, int height);
}

#endif // INKERA_ENGINE_H
