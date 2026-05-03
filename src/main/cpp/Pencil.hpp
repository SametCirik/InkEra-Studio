#ifndef PENCIL_HPP
#define PENCIL_HPP

#include "Tool.hpp"

class Pencil : public Tool {
public:
    void apply(Layer& layer, int x, int y) override {
        // Hedeflenen katmandaki o minik pikseli boya!
        layer.setPixel(x, y, color);
    }
};

#endif // PENCIL_HPP
