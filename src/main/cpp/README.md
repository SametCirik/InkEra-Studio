# InkEra Native Engine (C++)

InkEra Studio'nun yüksek performanslı çizim motorudur. Java tarafıyla **Project Panama (FFM API)** üzerinden konuşur.

## Mimari Yapı

1. **InkEraEngine.cpp (Dispatcher):** Java'dan gelen `extern "C"` çağrılarını karşılayan ana kapıdır. Burası mantık yürütmez, sadece gelen verileri ilgili sınıflara (Tool, Canvas) yönlendirir.

2. **Canvas Sınıfı:** Java'dan gelen bellek adresini (MemorySegment) yönetir. Piksellerin ARGB formatında manipüle edilmesinden sorumludur.

3. **Tool Sistemi:**
   - `BaseTool`: Tüm çizim araçlarının (fırça, silgi, dolgu) temel özelliklerini (renk, boyut, opaklık) içerir.
   - `Pencil`: Antialiasing (yumuşatma) yapmadan doğrudan pikselleri boyayan keskin kalem aracıdır.

## Veri Akışı
1. JavaFX farenin ekran koordinatlarını yakalar.
2. `WorkspaceController` bu koordinatları matris üzerinden tuval koordinatına (0,0 ile Width,Height arası) çevirir.
3. Panama aracılığıyla `draw_pixel(x, y, color)` komutu C++'a iletilir.
4. C++ doğrudan paylaşımlı belleğe (Shared Memory) yazar.
5. JavaFX `pixelBuffer.updateBuffer()` çağrısı ile GPU'yu uyarır ve resim güncellenir.

## Derleme (Build)
Derleme işlemi CMake ile yapılır:
\`\`\`bash
mkdir build && cd build
cmake ..
make
\`\`\`
