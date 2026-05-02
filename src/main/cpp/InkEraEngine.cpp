#include "InkEraEngine.h"
#include "Canvas.hpp" // Yeni sınıfımızı dahil ediyoruz

extern "C" {
    
    void clearCanvas(uint32_t* pixelBuffer, int width, int height, uint32_t colorArgb) {
        // Java'dan gelen ham verilerle anında akıllı bir Canvas nesnesi yarat
        Canvas canvas(pixelBuffer, width, height);
        
        // Temizleme işini Canvas nesnesine devret
        canvas.clear(colorArgb);
    }
    
}
