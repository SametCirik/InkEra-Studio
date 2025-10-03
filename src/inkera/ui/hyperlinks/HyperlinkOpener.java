package inkera.ui.hyperlinks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import inkera.languages.Languages; // Languages sınıfını import ediyoruz

public class HyperlinkOpener extends JLabel 
{

    // --- DEĞİŞEN KISIM BAŞLANGICI ---
    private Class<? extends Window> windowClassToOpen;
    private Languages languagesManager; // Languages yöneticisini tutmak için
    private String textKey;             // Metnin anahtar kelimesini tutmak için
    // --- DEĞİŞEN KISIM SONU ---

    /**
     * Tıklandığında yeni bir pencere açan ve dil değişimine duyarlı bir hyperlink oluşturur.
     * @param languagesManager Dil yöneticisi nesnesi.
     * @param textKey Gösterilecek metnin Languages sınıfındaki anahtarı.
     * @param windowClassToOpen Açılacak pencerenin sınıfı.
     */
    public HyperlinkOpener(Languages languagesManager, String textKey, Class<? extends Window> windowClassToOpen) 
    {
        // --- DEĞİŞEN KISIM BAŞLANGICI ---
        this.languagesManager = languagesManager;
        this.textKey = textKey;
        this.windowClassToOpen = windowClassToOpen;
        // --- DEĞİŞEN KISIM SONU ---

        setForeground(Color.decode("#388E3C"));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        updateLanguage(); // Metni ilk kez ayarlamak için yeni metodu çağırıyoruz.

        addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                try 
                {
                    // Verilen sınıftan yeni bir pencere nesnesi oluştur.
                    Window window = windowClassToOpen.getDeclaredConstructor().newInstance();

                    // Eğer açılacak pencere bir JDialog ise, onu ana pencereye göre ortala.
                    if (window instanceof JDialog) 
                    {
                        JDialog dialog = (JDialog) window;
                        Frame owner = (Frame) SwingUtilities.getWindowAncestor(HyperlinkOpener.this);
                        dialog.setModal(true);
                        dialog.setLocationRelativeTo(owner);
                    } 
                    // Eğer bir JFrame ise, ekranın ortasında başlat.
                    else 
                    {
                        window.setLocationRelativeTo(null);
                    }
                    window.setVisible(true);

                } catch (Exception ex) 
                {
                    System.err.println("Pencere oluşturulurken hata oluştu: " + windowClassToOpen.getName());
                    ex.printStackTrace();
                }
            }
        });
    }

    // --- YENİ METOT BAŞLANGICI ---
    /**
     * Bu metot, dil değiştirildiğinde çağrılarak linkin metnini günceller.
     */
    public void updateLanguage() 
    {
        String translatedText = languagesManager.getString(textKey);
        // Kodunuzdaki yorumda belirttiğiniz gibi altı çizgili yapmak için HTML kullandım.
        super.setText(translatedText);  // Linki altı çizgili şekilde yazmak için burayı ("<html><u>" + translatedText + "</u></html>") şeklinde yazmalıyız.
        super.setToolTipText(translatedText);
    }
    // --- YENİ METOT SONU ---
}