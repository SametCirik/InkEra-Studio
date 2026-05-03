#ifndef TOOL_HPP
#define TOOL_HPP

#include "Layer.hpp"
#include <stdint.h>
#include <cmath>

class Tool {
protected:
    uint32_t color;
    int size;

public:
    Tool() : color(0xFF000000), size(1) {}
    virtual ~Tool() = default;

    void setColor(uint32_t c) { color = c; }
    void setSize(int s) { size = s; }

    // Tek bir noktayı boyar
    virtual void apply(Layer& layer, int x, int y) = 0;

    // YENİ: Bresenham Çizgi Algoritması (A noktasından B noktasına pürüzsüz çizgi çeker)
    virtual void applyLine(Layer& layer, int x0, int y0, int x1, int y1) {
        int dx = std::abs(x1 - x0);
        int dy = -std::abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        int e2;

        while (true) {
            apply(layer, x0, y0); // Her adımda o anki pikseli boya
            
            if (x0 == x1 && y0 == y1) break; // Hedefe ulaştık
            
            e2 = 2 * err;
            if (e2 >= dy) { err += dy; x0 += sx; }
            if (e2 <= dx) { err += dx; y0 += sy; }
        }
    }
};

#endif // TOOL_HPP
