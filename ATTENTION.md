# Fountext - Linux (KDE/Wayland) Paketleme ve Dağıtım Rehberi

Bu belge, PyQt6 ile geliştirilmiş Fountext uygulamasının PyInstaller kullanılarak Linux (özellikle Arch Linux / KDE Plasma / Wayland) ortamı için nasıl paketlendiğini, karşılaşılan sorunları ve üretilen çözümleri dökümante eder.

## Karşılaşılan Sorunlar ve Çözümler

### 1. PyInstaller `_internal` Dizini ve CWD (Current Working Directory) Karmaşası
**Sorun:** PyInstaller `--onedir` modunda çalıştırıldığında, uygulamanın çalışması için gereken tüm kütüphaneleri `_internal` adlı bir klasöre atar. Eğer `assets/` (ikonlar, fontlar) ve `locales/` (dil dosyaları) gibi dışa bağımlı klasörler `--add-data` ile verilirse, PyInstaller bunları da `_internal` içine gömer. Uygulama ise bu dosyaları kök dizinde aradığı için fontlar yüklenmez ve diller okunamadığı için UI'da `app_title` gibi ham değişken isimleri görünür.
**Çözüm:** PyInstaller'a sadece Python motorunu ve `.so` kütüphanelerini derlemesi söylendi. `assets`, `locales` ve `user_data` klasörleri derleme işleminden sonra **el yordamıyla (bash betiği üzerinden)** kök dizine, uygulamanın beklediği yerlere (`_internal` dışına) kopyalandı.

### 2. Wayland Görev Çubuğu (Taskbar) İkon Sorunu
**Sorun:** Linux Wayland sunucusunda çalışan uygulamalar, Windows'taki gibi `.exe` içine gömülü ikonları okuyamaz. Bunun yerine jenerik bir "X" veya Wayland logosu gösterir.
**Çözüm:**
1. Python kodu içinde QApplication nesnesine `app.setDesktopFileName("Fountext-Editor")` ataması yapıldı.
2. Oluşturulan `.desktop` başlatıcısı içine `StartupWMClass=Fountext-Editor` eklendi. Bu sayede Wayland, çalışan pencere ile başlatıcı ikonunu başarıyla eşleştirdi.

### 3. Kullanıcı Verisi (`projects.json`) Çakışması
**Sorun:** Geliştirici ortamındaki `projects.json` dosyası, geliştiricinin kendi bilgisayarındaki mutlak (absolute) yolları içerir. Bu dosya paketlenip dağıtıldığında, yeni kullanıcılarda hatalara veya eski projelere dair kalıntılara yol açar.
**Çözüm:** Paketleme betiği içine `echo "{}" > dist/Fountext/user_data/projects.json` komutu eklenerek, dağıtım sürümünde bu dosyanın her zaman "sıfır kilometre" olması sağlandı.

---

## Nihai Paketleme Betiği (Build & Release Script)

Aşağıdaki betik, uygulamayı derler, mükemmel klasör ağacını oluşturur, İngilizce kurulum/kısayol betiğini (`install.sh`) hazırlar ve arşivi kapatır.

```bash
# 1. Asıl proje dizinine geçiyoruz
cd ~/Codes/Fountext

# 2. Temizlik
source venv/bin/activate
rm -rf dist/Fountext Fountext-v1.2-Linux.tar.gz

# 3. PyInstaller'ı çalıştırıyoruz (Sadece Python motoru ve C++ kütüphaneleri)
pyinstaller --noconfirm --onedir --windowed \
  --add-data "src/fountext_engine*.so:src" \
  --name "Fountext" src/main.py

# 4. KLASÖR DİZİLİMİ (Uygulamanın beklediği mimari)
# a) Kılavuzlar ve Logo kök dizine
cp guide_EN.fountain dist/Fountext/
cp guide_TR.fountain dist/Fountext/
cp Fountext_Logo.png dist/Fountext/

# b) assets klasörü KÖK DİZİNE (Font ve ikon okuma sorununu çözer)
cp -r assets dist/Fountext/

# c) locales klasörü KÖK DİZİNE (Dil JSON okuma sorununu çözer)
mkdir -p dist/Fountext/src
cp -r src/locales dist/Fountext/src/

# d) user_data kök dizine (Ayarları alır, projeleri SIFIRLAR)
mkdir -p dist/Fountext/user_data
cp user_data/settings.json dist/Fountext/user_data/
echo "{}" > dist/Fountext/user_data/projects.json

# 5. İNGİLİZCE İSİMLİ KURULUM BETİĞİ (install.sh)
cat << 'EOF' > dist/Fountext/install.sh
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

echo "Integrating Fountext into the system (KDE/Wayland/App Menu)..."

mkdir -p ~/.local/share/applications/
cat << INI > ~/.local/share/applications/Fountext.desktop
[Desktop Entry]
Name=Fountext
Comment=Screenwriting Editor
Exec="$DIR/Fountext"
Path=$DIR
Icon=$DIR/Fountext_Logo.png
Terminal=false
Type=Application
Categories=Office;TextEditor;
StartupWMClass=Fountext-Editor
INI

# Klasör içi taşınabilir kısayolu oluşturalım
cp ~/.local/share/applications/Fountext.desktop "$DIR/Fountext.desktop"
chmod +x "$DIR/Fountext.desktop"

kbuildsycoca5 &> /dev/null || kbuildsycoca6 &> /dev/null || update-desktop-database ~/.local/share/applications/ &> /dev/null || true

echo ""
echo "Installation complete!"
echo "You can now launch Fountext from your App Menu, or use the new 'Fountext' shortcut in this folder."
echo ""
read -p "Press Enter to exit..."
EOF

chmod +x dist/Fountext/install.sh

# 6. README_LINUX.txt OLUŞTURMA
cat << 'EOF' > dist/Fountext/README_LINUX.txt
=========================================
FOUNTEXT - LINUX RELEASE
=========================================

Welcome to Fountext!

HOW TO RUN (PORTABLE MODE):
---------------------------
You can run Fountext immediately without installing anything. 
Just double-click the "Fountext" executable file in this folder.
(Note: In portable mode, your desktop environment might not display the application logo on the taskbar depending on your OS/Wayland settings).

HOW TO INSTALL (SYSTEM INTEGRATION & LOGO):
-------------------------------------------
If you want the Fountext logo to appear correctly on your taskbar (especially on KDE/Wayland) and want to find Fountext in your system's Application/Office Menu:

1. Double-click "install.sh" and run it (or run it via terminal: ./install.sh)
2. A "Fountext" shortcut will be created in this folder.
3. You will also find Fountext in your system's Start/App Menu under Office/TextEditor.

Enjoy writing!
EOF

# 7. Arşivi oluştur ve GitHub'a Push'la
cd dist
tar -czvf ../Fountext-v1.2-Linux.tar.gz Fountext
cd ..

git add .
git commit -m "chore: derleme işlemi"
git push origin master
```

---

Bu aşamadan sonra, tar.gz'yi masaüstüne çıkartıp klasörleri değiştirmem gerekli.

En sonunda aşağıdaki kodu yazarak, çıkartıp düzenlediğimiz klasörü tekrar sıkıştıracağız.

```bash
# 1. Masaüstü dizinine geçelim
cd ~/Masaüstü

# 2. Varsa diye Fountain klasörünü garantiye alalım (yoksa oluşturur)
mkdir -p Fountain

# 3. Fountext klasörünü sıkıştırıp doğrudan Fountain klasörünün içine atalım
tar -czvf Fountain/Fountext-v1.2-Linux.tar.gz Fountext

echo "İşlem tamam! Fountext-v1.2-Linux.tar.gz dosyası ~/Masaüstü/Fountain/ klasöründe seni bekliyor."
```