#ifndef CANVAS_HPP
#define CANVAS_HPP

#include <stdint.h>
#include <algorithm> // std::fill için

class Canvas {
private:
    uint32_t* buffer;
    int width;
    int height;

public:
    // Kurucu (Constructor): Java'dan gelen bellek adresini ve boyutları alır
    Canvas(uint32_t* pixelBuffer, int w, int h) 
        : buffer(pixelBuffer), width(w), height(h) {}

    // Tuvali tek renge boyar (Çok daha optimize edilmiş yöntem)
    void clear(uint32_t colorArgb) {
        int totalPixels = width * height;
        std::fill(buffer, buffer + totalPixels, colorArgb);
    }

    // GÜVENLİ PİKSEL BOYAMA: Belirtilen X ve Y koordinatını boyar
    // Eğer koordinatlar tuvalin dışındaysa, işlemi iptal edip çökmeyi önler!
    void setPixel(int x, int y, uint32_t colorArgb) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            buffer[y * width + x] = colorArgb;
        }
    }

    // Getter Metotları (İleride araçlar tuval boyutunu öğrenmek isterse diye)
    int getWidth() const { return width; }
    int getHeight() const { return height; }
    uint32_t* getBuffer() const { return buffer; }
};

#endif // CANVAS_HPP
