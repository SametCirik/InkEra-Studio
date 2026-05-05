# 🎨 InkEra Studio - Masaüstü Mimari Dokümanı

**Oluşturulma:** 2026-05-05  
**Proje Durumu:** Geliştirme Aşamasında (Pro-Layer Sistemi Tamamlandı)  
**Etiketler:** #java #javafx #cpp #project-panama #arch-linux #manga-studio

## 📌 Proje Özeti
InkEra Studio, manga ve çizgi roman sanatçıları için özel olarak tasarlanmış, yan yana iki sayfa (wide panel) çalışılmasına olanak tanıyan, sıfır gecikmeli (zero-copy) bir masaüstü çizim uygulamasıdır. Electron gibi RAM canavarı web sarmalayıcıları (wrappers) yerine, saf performans için JavaFX ve C++ birleşimiyle inşa edilmiştir.

## 🛠 Teknoloji Yığını
*   **Arayüz (UI):** JavaFX 21 (Modern, karanlık tema ağırlıklı UI)
*   **Çizim Motoru (Backend):** C++ (Native bellek yönetimi ve ışık hızında piksel manipülasyonu)
*   **Köprü (Bridge):** Java FFM API (Project Panama) - JNI'ın hantal yapısı yerine doğrudan bellek erişimi sağlar.

## ⚙️ Çekirdek Sistemler ve Optimizasyonlar
1.  **Bölgesel Render (Dirty Rectangles) & 60 FPS:**
    *   Tüm tuvali her fare hareketinde yenilemek yerine, yalnızca farenin etkileşime girdiği *Kirli Dikdörtgen* (Dirty Region) alanı C++ tarafında hesaplanır ve JavaFX'e iletilir. Bu, veri transferini 34 MB'den birkaç KB'a düşürerek sistemi 60 FPS'e sabitler.
2.  **Linux / Wayland Donanım İvmelendirme Zorlaması:**
    *   Arch Linux ve Mesa sürücülerinde yaşanan JavaFX GPU reddi, `-Dprism.forceGPU=true` ve `-Dprism.order=es2` JVM bayraklarıyla ezilmiş, sistem zorla OpenGL kullanmaya programlanmıştır.
3.  **Katman (Layer) Sistemi:**
    *   Her katman kendi `vector<uint32_t>` bellek havuzuna sahiptir. Görünürlük (visibility), hiyerarşi (z-index) ve silme işlemleri doğrudan C++ motorunda işlenip JavaFX arayüzündeki (Drag & Drop destekli) Custom ListCell'ler ile senkronize edilir.
4.  **Matematiksel Fırça ve Tablet Entegrasyonu:**
    *   Huion 420 ve OpenTabletDriver desteği için Kuadratik Bezier eğrileriyle hesaplanan dinamik basınç hassasiyeti sistemi.

## 🚀 Gelecek Planları (Geliştirici Notları)
*   **Web Marketplace Entegrasyonu:** Web sitesindeki fırça ve eklentiler, uygulama içindeki bir REST API istemcisi ile doğrudan `~/.local/share/InkEra/` dizinine indirilecek (Webview/Electron kullanılmadan).
*   **Hibrit ThumbHash Üretimi:** Kullanıcı projesini dışa aktarırken veya buluta yüklerken, düşük çözünürlüklü ThumbHash görsel string'i C++ motoru tarafından hesaplanıp web sunucusuna yollanacak. Bu sunucu maliyetini devasa oranda düşürecektir.