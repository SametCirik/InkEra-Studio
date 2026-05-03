# CHANGELOG

---

**Oluşturulma Tarihi:** 2026-02-12  
**Son Değiştirilme Tarihi:** 2026-05-03 
**Düzenleyen:** Samet Cırık & Google Gemini

---

## 2026-05-03

- **Eklenenler**
    - **C++ Çizim Motoru:** Project Panama kullanılarak JavaFX ile sıfır gecikmeli (zero-copy) iletişim kuran, yüksek performanslı C++ render motoru entegre edildi.
    - **Çoklu Katman (Layer) Mimarisi:** `LayerManager` sınıfı ile desteklenen, her biri kendi piksel belleğine (vector<uint32_t>) sahip bağımsız katman sistemi oluşturuldu.
    - **Gelişmiş Katman Paneli (UI):** Arayüze katmanları görünür/görünmez yapma, silme ve sürükle-bırak (Drag & Drop) ile hiyerarşi değiştirme yeteneklerine sahip özel hücre (Custom ListCell) yapısı eklendi.
    - **Bresenham Çizgi Algoritması:** Yüksek hızlı fare/kalem hareketlerinde (polling rate kaynaklı) oluşan piksel boşluklarını matematiksel olarak kusursuzca dolduran algoritma motor seviyesinde uygulandı.

- **Düzeltilenler / Optimizasyonlar**
    - **Dinamik Zoom Koruması:** Culling sorununu ve GPU doku sınırını (8192x8192) aşmayı önlemek için, kanvas boyutuna göre kendini otomatik limitleyen matematiksel zoom koruması getirildi.
    - **Bölgesel Render (Regional Rendering):** JavaFX tarafındaki bellek darboğazını ve yarım saniyelik kasmaları çözmek için, `AnimationTimer` destekli 60 FPS sabitleyici ve C++ tarafında sadece "Kirli Dikdörtgen" (Dirty Region) alanını işleyen üst düzey optimizasyon kodlandı.
    - **Linux/Wayland Donanım İvmelendirmesi:** Arch Linux ve Mesa sürücülerinde yaşanan `NullPointerException`, ekranda yırtılma (tearing) ve donanımsal reddedilme sorunları; JavaFX'in `es2` (OpenGL) motoru ve `forceGPU` bayrakları zorlanarak kalıcı olarak çözüldü.

---

## 2026-05-01

- **Eklenenler**
    - **Grafik Tablet Ayarları:** Ayarlar menüsüne donanım yapılandırması için özel bir sekme eklendi.
    - **İnteraktif Basınç Eğrisi:** Kalem basınç hassasiyetini görsel olarak ayarlamak için sürüklenebilir Kuadratik Bezier eğrisi (1:1 doğrusal referans çizgisi ve sıfırlama işlevi ile birlikte) eklendi.
    - **Dinamik Fırça Önizlemesi:** Basınç eğrisindeki ayarlamaları dalgalı bir fırça darbesi olarak anlık gösteren gerçek zamanlı matematiksel bezier çözücü sisteme entegre edildi.
    - **Donanım Tarayıcı:** Gelecekteki Huion 420 / OpenTabletDriver entegrasyonları için "USB Portlarını Tara" simülasyonu ve dinamik durum göstergeleri (Taranıyor -> Bağlandı) eklendi.
    - **Yerelleştirme (i18n):** Rusça (`ru`) çeviriler tamamlandı ve manga endüstrisi terminolojisine uygun (örn. 連載中 - Devam Ediyor) Japonca (`ja`) dil desteği eklendi.

- **Düzeltilenler**
    - **JavaFX Odak (Focus) Hatası:** Dinamik donanım taraması bittiğinde `ScrollPane`'in istemsizce aşağı kayma sorunu, `Platform.runLater` kullanılarak ve odak zorlanarak çözüldü.
    - **Arayüz Renk Tutarlılığı:** Tablet ayarlarındaki renk paleti agresif kırmızılardan stüdyo standartlarına uygun koyu gri (`#444`) ve kırık beyaz (`#e0e0e0`) tonlarına revize edildi.

---

## 2026-04-30

-  **Eklenenler**
    - **Proje Silme Motoru:** Hem tekli Resim hem de Manga projeleri için iç içe klasör silme (recursive deletion) ve UUID/Yol eşleşmeli JSON kayıt temizleme sistemi uygulandı.
    - **Sağ Tık Menüleri:** Galeri kartlarına, karanlık temaya uygun onay diyalogları içeren "Projeyi Sil" içerik (context) menüleri eklendi.
    - **Yazar Üstverisi (Metadata):** Projeler artık ilgili üstveri ve JSON kayıtlarında "Yazar" alanını kabul ediyor ve saklıyor.

- **Değiştirilenler**
    - **Yeni Proje Diyaloğu:** Standart `TextInputDialog`, stüdyonun karanlık temasına sıkı sıkıya bağlı kalan, çoklu girdiye sahip özel bir `GridPane` diyaloğu ile değiştirildi.
    - **Galeri Arayüzü:** Galeri kartları; başlıkları ve yazar adlarını ortalayacak, uzun metinleri üç nokta (`...`) ile kırpacak şekilde yeniden düzenlendi ve kart görünümünden tarih etiketi kaldırıldı.

---

## 2026-04-29

- **Bölüm & Sayfa Motoru (Print Simulation):**
    - **Matbaa/Forma Mantığı:** Bölümlerin basit bir liste yerine, basılı materyal (çift sayfa/spread) mantığıyla dizilmesini sağlayan dinamik sayfa motoru kodlandı.
    - **Okuma Yönü & Offset:** Yeni bölüm oluşturulurken Manga (Sağdan Sola) veya Çizgi Roman (Soldan Sağa) düzeni seçme özelliği eklendi. İlk sayfanın sağda veya solda başlama durumuna (offset) göre tüm sayfaların dizilimini hesaplayan algoritma kuruldu.
    - **Bölüm İçi Veri Yönetimi:** Bölüme ait okuma yönü ve sayfa sıralaması gibi verilerin, ana JSON'u şişirmemesi için proje içindeki `Episodes/BolumAdi/.inkepisode/meta.json` dosyasına yazılması (Payload/Registry ayrımı) sağlandı.
    - **Recursive Deletion:** Manga detay ekranındaki bölüm kartlarına sağ tık (Context Menu) menüsü eklendi. Özel uyarı penceresiyle birlikte içi dolu bölüm klasörlerini ve JSON kayıtlarını eş zamanlı silen motor yazıldı.

- **UI/UX & Pencere Yönetimi:**
    - **Studio Stabilization:** Uygulama penceresinin yeniden boyutlandırılması (resize/fullscreen) kapatılarak, arayüzün her ekranda stabil kalmasını sağlayan sabit "Premium Studio" (1150x800) çözünürlüğüne geçildi.
    - **KWin Override (Centering Hook):** Linux pencere yöneticilerinin (KWin) JavaFX yaşam döngüsüyle çakışmasını engellemek için, ekran boyutlarını matematiksel olarak hesaplayıp pencereyi zorla merkeze sabitleyen `forceCenterOnScreen` metodu yazıldı.
    - **Karanlık Tema İyileştirmeleri:** Açılır menüler (ComboBox) ve sağ tık (Context Menu) baloncuları Windows/Linux varsayılan görünümünden çıkartılarak stüdyo karanlık temasına entegre edildi.
    - **Bölüm Detay Görünümü:** Manga okuma yönüne göre şekillenen, bölüm kapağını doğru oranda (1:1.41) gölge efektiyle (drop shadow) sabitleyen yeni bir sayfa yönetim arayüzü tasarlandı.

- **Görsel Motor & Layout Optimizasyonu:**
    - **High-Res Rendering:** Resimlerin piksellenme sorununu çözen donanımsal yumuşatma (`smooth=true`) ve önbellekleme sistemi Manga Galeri ve Detay ekranlarına entegre edildi. CSS sınırlamaları kaldırılarak HD kapaklara (230x324 ve 250x352) geçildi.
    - **Dynamic Grid (3-Column):** Manga Galerisi arayüzü, kapakların yazıları ezmesini engelleyecek şekilde ferahlatılarak (40px boşluk) 3'lü matris yapısına oturtuldu.
    - **Object-Fit Cover Simülasyonu:** Resim Galerisi için, farklı en-boy oranlarındaki resimleri esnetmeden kusursuz 230x230 karelere kırpan `Rectangle` + `ImagePattern` mimarisi hazırlandı.

- **Açık Kaynak & GitHub:**
    - Projenin vizyonunu, özelliklerini ve derleme talimatlarını içeren profesyonel bir `README.md` yazıldı ve global GitHub vitrinine uygun hale getirildi.

---

## 2026-04-28 

- **Manga Detay Paneli & Dinamik Veri Yönetimi:**
    - **Gelişmiş Etiket Sistemi (Token Input):** Tıklanabilir, boşluk (Space) tuşuyla yeni kutucuk açan, Enter ile kaydedilen ve odak kaybında otomatik senkronizasyon sağlayan modern etiket düzenleyici implemente edildi. 
    - **Görsel Stil Güncellemesi:** Etiket metinleri ve vurgular için stüdyo temasına uygun yeşil (#4caf50) renk şemasına geçildi. 
    - **Kapak Resmi Motoru:** `.inkera/poster.png` üzerinden kapak yükleme, değiştirme ve JavaFX image cache sorunlarını aşan (timestamping) dinamik önizleme sistemi kuruldu. 
    - **Native Pencere Mimarisi:** Diyalog pencereleri (Yeni Proje, Sinopsis) işletim sistemi (KDE/KWin) pencere yöneticisine devredilerek görsel artefaktlar ve beyaz köşe hataları (glitch) giderildi. 
    - **Custom Scrollbar:** Sinopsis alanı için stüdyo karanlık temasıyla uyumlu, hover efektli yeşil kaydırma çubukları tasarlandı. 

- **Bug Fixes & Refactoring:**
    - **Persistence Layer:** Verilerin hem merkezi `user-data` registry klasörüne hem de yerel proje dizinindeki `.inkera/meta.json` dosyasına yazılmasını sağlayan çift yönlü kayıt mekanizması kuruldu. 
    - **Layout Stabilization:** Manga galerisinde uzun proje isimlerinin grid yapısını bozması, metin sınırlama (ellipsis) ve hizalama güncellemeleriyle çözüldü. 

--- 
## 2026-04-27 

- **Mimari Devrim & Proje Yapılandırması:** 
    - **Modern Studio Architecture:** Proje tamamen modüler JavaFX 21 yapısına taşındı. 
    - **Proje İnşası (Scaffolding):** Yeni seri başlatıldığında kullanıcının seçtiği dizinde otomatik olarak `.inkera/` (gizli meta) ve `Episodes/` klasör hiyerarşisinin kurulması sağlandı. 
    - **Dinamik Galeri:** JSON dosyalarını tarayarak otomatik kart üreten ve detay ekranına veri paslayan galeri motoru geliştirildi. 

- **GitHub & Versiyon Kontrolü:** 
    - **Repository Migration:** Proje GitHub'a (`master` branch) taşındı. 
    - **Lisanslama:** Projenin tüm haklarını koruyan ancak kodun incelenmesine izin veren GPLv3 lisans yapısı kuruldu. 
    - **Obsidian Entegrasyonu:** Proje kök dizini Obsidian Vault olarak yapılandırıldı; `.obsidian` klasörü `.gitignore` kapsamına alındı. 

---

### 2026-02-18

* **Core Development (Çizim Modülü):**
    * Workspace (Çizim Penceresi) arayüzü temel yapısı kuruldu.
    * Ekranın sağ ve sol kısımlarına dikey şerit (strip) şeklinde buton panelleri eklendi.
    * `WorkspaceController` tarafında panellerin dinamik olarak açılıp kapanmasını sağlayan (Toggle) mekanizma geliştirildi.

* **UI/UX & Assets:**
    * Araç çubuklarına (fırça, silgi, katman işlemleri vb.) PNG formatında ikon entegrasyonu denendi.
    * İkonların görüntülenmesindeki teknik aksaklıklar ve görsel uyumsuzluklar nedeniyle **süreç askıya alındı**.
    * **Gelecek Planı:** Tüm ikon kütüphanesinin SVG formatına dönüştürülmesi ve sistematiği basitleştirmek adına "A-B-C-D" şeklinde yeniden isimlendirilmesi kararlaştırıldı.

---

### 2026-02-13

* **Proje Yapısı ve Refactoring:**
    * Kodun okunabilirliğini ve bakımını kolaylaştırmak amacıyla, FXML ve denetleyici (controller) sınıfları kendi paketleri altına taşındı (`com.inkera.view`, `com.inkera.controllers`, `com.inkera.titlebar` vb.).
    * Bu yeniden yapılandırma sonrası ortaya çıkan paket yolu hataları giderildi.

* **Bug Fixes (Hata Düzeltmeleri):**
    * **Uygulama Başlatma Hatası (Critical):** `ClassNotFoundException` hatası, FXML dosyalarındaki `fx:controller` yollarının yeni paket yapılarına göre güncellenmesiyle çözüldü.
    * **FXML Dahil Etme Hatası (Critical):** FXML dosyaları arasındaki iç içe yüklemelerde (`fx:include`) oluşan `FileNotFoundException`, dosya yollarının düzeltilmesiyle giderildi.
    * **Stil Dosyası Bulunamama Sorunu:** Arayüz (`.fxml`) dosyalarının `style.css` dosyasını bulamaması sorunu, göreceli yol (relative path) güncellemeleriyle çözüldü.

---

### 2026-02-12

* **Migration (Teknoloji Göçü):**
    * Proje altyapısı **Java Swing**'den **JavaFX 21**'e taşındı.
    * **Arch Linux / VS Code OSS** ortamı için Maven yapılandırması (`pom.xml`) ve modüler sistem ayarları (`module-info.java`) yapıldı.

* **Backend & Cloud (Bulut Entegrasyonu):**
    * **Firebase Auth (Deneysel):** Google Firebase projesi oluşturuldu ve REST API üzerinden Email/Şifre giriş sistemi kodlandı.
    * *Not: Temel bağlantı sağlandı ancak tam kararlılık testleri henüz tamamlanmadı (Work in Progress).*

* **UI/UX Design (Arayüz Tasarımı):**
    * Uygulama genelinde **Modern Dark Theme** (Koyu Tema) yapısına geçildi.
    * **Layout Stabilization:** Menü butonlarındaki hover efekti kaynaklı titreme (layout shift) sorunu giderildi.
    * Standart pencere kenarlıkları kaldırılarak (`StageStyle.UNDECORATED`), özel tasarım bir **TitleBar** entegre edildi.
    * **Frame/Content Mimarisi:** Ana pencere sabit bir çerçeveye dönüştürülerek sayfa geçişleri optimize edildi.
    * **Manga Yönetim Modülü:** Galeri görünümü, detay ekranı (Split-View) ve bağımsız özet penceresi geliştirildi.