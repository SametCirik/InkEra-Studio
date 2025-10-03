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
import inkera.ui.placeholders.PlaceholderText;
import inkera.ui.titlebars.DialogTitleBar;

public class SignUpDialog extends JDialog
{
	private PlaceholderText nameField;
	private PlaceholderText surnameField;
	private PlaceholderText usernameField;
	private PlaceholderText emailField;
	private PlaceholderPassword passwordField;
	
	public JButton signUpButton, closeButton;
	public JPanel rootPanel, signUpPanel;
	
	public Languages languagesManager;
	public DialogTitleBar titleBar;
	
	public SignUpDialog()
	{
		this(new Languages());
	}
	
	public SignUpDialog(Languages languagesManager)
	{
		this.languagesManager = (languagesManager != null) ? languagesManager : new Languages();
		createWindow();
		updateTexts();
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() ->
		{
			SignUpDialog dialog = new SignUpDialog();
			dialog.setVisible(true);
		});
	}
	
	private void createWindow() 
	{
		setSize(352, 30 + 402);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));
		
		titleBar = new DialogTitleBar(this, ""); // Metin updateTexts ile ayarlanacak
		rootPanel.add(titleBar, BorderLayout.NORTH);
		
		signUpPanel = new JPanel();
		signUpPanel.setLayout(null);
        signUpPanel.setBackground(Color.decode("#121212"));
		
		nameField = new PlaceholderText("");
		nameField.setBounds(50, 50, 250, 30);
		nameField.setBackground(Color.decode("#424242"));
		nameField.setForeground(Color.decode("#FFFFFF"));
		nameField.setCaretColor(Color.decode("#FFFFFF"));
		signUpPanel.add(nameField);
		
		surnameField = new PlaceholderText("");
		surnameField.setBounds(50, 100, 250, 30);
		surnameField.setBackground(Color.decode("#424242"));
		surnameField.setForeground(Color.decode("#FFFFFF"));
		surnameField.setCaretColor(Color.decode("#FFFFFF"));
		signUpPanel.add(surnameField);
		
		usernameField = new PlaceholderText("");
		usernameField.setBounds(50, 150, 250, 30);
		usernameField.setBackground(Color.decode("#424242"));
		usernameField.setForeground(Color.decode("#FFFFFF"));
		usernameField.setCaretColor(Color.decode("#FFFFFF"));
		signUpPanel.add(usernameField);
		
		emailField = new PlaceholderText("");
		emailField.setBounds(50, 200, 250, 30);
		emailField.setBackground(Color.decode("#424242"));
		emailField.setForeground(Color.decode("#FFFFFF"));
		emailField.setCaretColor(Color.decode("#FFFFFF"));
		signUpPanel.add(emailField);
		
		passwordField = new PlaceholderPassword("");
		passwordField.setBounds(50, 250, 250, 30);
		passwordField.setBackground(Color.decode("#424242"));
		passwordField.setForeground(Color.decode("#FFFFFF"));
		passwordField.setCaretColor(Color.decode("#FFFFFF"));
		signUpPanel.add(passwordField);
		
		PasswordEye.attachTo(passwordField, "/images/eye-open.png", "/images/eye-closed.png");
		
		signUpButton = new JButton();
		signUpButton.setBounds(50, 320, 110, 30);
		signUpButton.setBackground(Color.decode("#388E3C"));
        signUpButton.setForeground(Color.decode("#FFFFFF"));
		signUpButton.addActionListener(e -> handleSignUp());
		signUpPanel.add(signUpButton);
		
		closeButton = new JButton(); 
		closeButton.setBounds(190, 320, 110, 30);
		closeButton.setBackground(Color.decode("#C94C4C"));
        closeButton.setForeground(Color.decode("#FFFFFF"));
		closeButton.addActionListener(e -> dispose());
		signUpPanel.add(closeButton);
		
		rootPanel.add(signUpPanel, BorderLayout.CENTER);
		add(rootPanel);	
	}
	
	// DÜZELTME: Bu metot artık doğrulama akışını başlatıyor.
	private void handleSignUp() 
	{
        // 1. Alanlardaki metinleri al.
        String name = nameField.getText();
        String surname = surnameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // 2. Alanların boş olup olmadığını kontrol et.
        if (name.trim().isEmpty() || surname.trim().isEmpty() || username.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, 
                "Lütfen tüm alanları doldurun.", 
                "Kayıt Hatası", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. AccountManager ile yeni kullanıcıyı kaydetmeyi dene.
        // Bu metot, kullanıcıyı "UNVERIFIED" olarak kaydeder ve bir kod üretir.
        AccountManager accountManager = AccountManager.getInstance();
        boolean success = accountManager.signUp(username, email, password, name, surname);

        // 4. Sonuca göre kullanıcıyı bilgilendir ve doğrulama ekranına yönlendir.
        if (success) 
        {
            // Simülasyon: Kodun konsola yazdırıldığını kullanıcıya bildir.
            JOptionPane.showMessageDialog(this, 
                "Kayıt başarılı! Lütfen e-postanıza gönderilen doğrulama kodunu girin.\n(Simülasyon: Kod konsola yazdırıldı)", 
                "Doğrulama Gerekli", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Mevcut diyalogu kapat.
            dispose();
            
            // VerificationDialog'u aç ve gerekli bilgileri aktar.
            VerificationDialog verDialog = new VerificationDialog(languagesManager, email, "SIGN_UP_VERIFICATION");
            verDialog.setVisible(true);

        } else 
        {
            JOptionPane.showMessageDialog(this, 
                "Bu kullanıcı adı veya e-posta zaten kullanımda.", 
                "Kayıt Hatası", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
	
    public void updateTexts() 
    {
        if (languagesManager == null) 
        {
        	return;
        }

        titleBar.setTitle("InkEra - " + languagesManager.getString(Languages.KEY_SIGN_UP_TITLE));
        nameField.setPlaceholder(languagesManager.getString(Languages.KEY_NAME));
        surnameField.setPlaceholder(languagesManager.getString(Languages.KEY_SURNAME));
        usernameField.setPlaceholder(languagesManager.getString(Languages.KEY_USERNAME));
        emailField.setPlaceholder(languagesManager.getString(Languages.KEY_EMAIL));
        passwordField.setPlaceholder(languagesManager.getString(Languages.KEY_PASSWORD));
        closeButton.setText(languagesManager.getString(Languages.KEY_CLOSE));
        signUpButton.setText(languagesManager.getString(Languages.KEY_SIGN_UP_BUTTON));
    }
}
