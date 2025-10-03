package inkera.ui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import inkera.ui.account.AccountManager; // DÜZELTME: AccountManager import edildi.
import inkera.languages.Languages;
import inkera.ui.placeholders.PasswordEye;
import inkera.ui.placeholders.PlaceholderPassword;
import inkera.ui.titlebars.DialogTitleBar;

public class ChangePasswordDialog extends JDialog
{
	private PlaceholderPassword newPasswordField, newPasswordField2;
	
	public JButton changePasswordButton,  closeButton;
	public JPanel rootPanel, changePasswordPanel;
	
	public Languages languagesManager;
	public DialogTitleBar titleBar;
    
    // DÜZELTME: Önceki adımlardan gelen bilgileri tutmak için alanlar eklendi.
    private final String userEmail;
    private final String verificationCode;

	public ChangePasswordDialog()
	{
		// Test için varsayılan değerler. Gerçek kullanımda diğer constructor çağrılmalı.
		this(new Languages(), "test@example.com", "123456");
	}

    // DÜZELTME: Constructor artık e-posta ve doğrulama kodunu alıyor.
	public ChangePasswordDialog(Languages languages, String email, String code) 
	{
		this.languagesManager = (languages != null) ? languages : new Languages();
        this.userEmail = email;
        this.verificationCode = code;
		createDialog();
		updateTexts();
	}
	
	public static void main(String[] args) 
	{
        SwingUtilities.invokeLater(() -> 
        {
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.setVisible(true);
        });
    }
	
	public void createDialog() 
	{
		setSize(352, 30 + 252);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));
		
		titleBar = new DialogTitleBar(this, "");
		rootPanel.add(titleBar, BorderLayout.NORTH);
		
		changePasswordPanel = new JPanel();
		changePasswordPanel.setLayout(null);
		changePasswordPanel.setBackground(Color.decode("#121212"));
		
		newPasswordField = new PlaceholderPassword("");
		newPasswordField.setBounds(50, 50, 250, 30);
		newPasswordField.setBackground(Color.decode("#424242"));
		newPasswordField.setForeground(Color.decode("#FFFFFF"));
		newPasswordField.setCaretColor(Color.decode("#FFFFFF"));
		changePasswordPanel.add(newPasswordField);
		
		newPasswordField2 = new PlaceholderPassword("");
		newPasswordField2.setBounds(50, 100, 250, 30);
		newPasswordField2.setBackground(Color.decode("#424242"));
		newPasswordField2.setForeground(Color.decode("#FFFFFF"));
		newPasswordField2.setCaretColor(Color.decode("#FFFFFF"));
		changePasswordPanel.add(newPasswordField2);
		
		PasswordEye.attachTo(newPasswordField, "/images/eye-open.png", "/images/eye-closed.png");
		PasswordEye.attachTo(newPasswordField2, "/images/eye-open.png", "/images/eye-closed.png");

		changePasswordButton = new JButton();
		changePasswordButton.setBounds(50, 170, 110, 30);
		changePasswordButton.setBackground(Color.decode("#388E3C"));
		changePasswordButton.setForeground(Color.decode("#FFFFFF"));
        
        // DÜZELTME: Butonun ActionListener'ı dolduruldu.
		changePasswordButton.addActionListener(e -> {
			handleChangePassword();
		});
		changePasswordPanel.add(changePasswordButton);

		closeButton = new JButton();
		closeButton.setBounds(190, 170, 110, 30);
		closeButton.setBackground(Color.decode("#C94C4C"));
		closeButton.setForeground(Color.decode("#FFFFFF"));
		closeButton.addActionListener(e -> dispose());
		changePasswordPanel.add(closeButton);
		
		rootPanel.add(changePasswordPanel);
		add(rootPanel);
	}
    
    /**
     * "Şifreyi Değiştir" butonuna basıldığında çalışan mantık.
     */
    private void handleChangePassword() {
        String newPassword1 = new String(newPasswordField.getPassword());
        String newPassword2 = new String(newPasswordField2.getPassword());

        // 1. Alanların boş olup olmadığını kontrol et.
        if (newPassword1.isEmpty() || newPassword2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen her iki şifre alanını da doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Girilen iki şifrenin eşleşip eşleşmediğini kontrol et.
        if (!newPassword1.equals(newPassword2)) {
            JOptionPane.showMessageDialog(this, "Girilen şifreler eşleşmiyor.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. (İsteğe bağlı) Yeni şifrenin belirli bir uzunlukta olmasını kontrol et.
        if (newPassword1.length() < 6) {
            JOptionPane.showMessageDialog(this, "Şifre en az 6 karakter uzunluğunda olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. AccountManager ile şifreyi sıfırla.
        AccountManager accountManager = AccountManager.getInstance();
        boolean success = accountManager.resetPassword(this.userEmail, this.verificationCode, newPassword1);

        // 5. Sonuca göre kullanıcıyı bilgilendir.
        if (success) {
            JOptionPane.showMessageDialog(this, "Şifreniz başarıyla değiştirildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Pencereyi kapat
        } else {
            // Bu hata normalde verificationCode yanlışsa oluşur, ama bu diyaloga zaten
            // doğru kodla gelindiği için beklenmedik bir durumdur.
            JOptionPane.showMessageDialog(this, "Şifre sıfırlanırken beklenmedik bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
	
	public void updateTexts() 
	{
		if (languagesManager == null) return;
		
		titleBar.setTitle("InkEra - " + languagesManager.getString(Languages.KEY_CHANGE_PASSWORD_TITLE));
		newPasswordField.setPlaceholder(languagesManager.getString(Languages.KEY_NEW_PASSWORD));
		newPasswordField2.setPlaceholder(languagesManager.getString(Languages.KEY_NEW_PASSWORD) + " (Tekrar)"); // Kullanıcıya ipucu
        
        // DÜZELTME: Buton metinleri de güncellenmeli.
        changePasswordButton.setText(languagesManager.getString(Languages.KEY_CHANGE_PASSWORD_BUTTON));
        closeButton.setText(languagesManager.getString(Languages.KEY_CLOSE));
	}
}
