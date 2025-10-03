package inkera.ui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import inkera.languages.Languages;
import inkera.ui.placeholders.PasswordEye;
import inkera.ui.placeholders.PlaceholderPassword;
import inkera.ui.placeholders.PlaceholderText;
import inkera.ui.titlebars.DialogTitleBar;

public class SignInDialog extends JDialog {
    private PlaceholderText usernameField;
    private PlaceholderText emailField;
    private PlaceholderPassword passwordField;
    
    public JButton signInButton, closeButton;
    public JPanel rootPanel, signInPanel;
    
    public Languages languagesManager;
    public DialogTitleBar titleBar;

    // DÜZELTME: Girişin başarılı olup olmadığını tutacak bir bayrak (flag).
    private boolean loginSuccessful = false;

    public SignInDialog() {
        this(new Languages());
    }

    public SignInDialog(Languages languagesManager) { 
        this.languagesManager = (languagesManager != null) ? languagesManager : new Languages();
        createWindow();
        updateTexts();
    }
    
    /**
     * DÜZELTME: MainMenuFrame'in diyalog sonucunu kontrol edebilmesi için yeni metot.
     * @return Kullanıcı girişi başarılı olduysa true, aksi takdirde false döner.
     */
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    private void createWindow() {
        setSize(352, 30 + 302);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // DÜZELTME: Diyalogun, kapanana kadar ana pencereyi bekletmesi için.
        setModal(true); 
        
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));

        titleBar = new DialogTitleBar(this, "");
        rootPanel.add(titleBar, BorderLayout.NORTH);
        
        signInPanel = new JPanel();
        signInPanel.setLayout(null);
        signInPanel.setBackground(Color.decode("#121212"));

        usernameField = new PlaceholderText("");
        usernameField.setBounds(50, 50, 250, 30);
        usernameField.setBackground(Color.decode("#424242"));
        usernameField.setForeground(Color.decode("#FFFFFF"));
        usernameField.setCaretColor(Color.decode("#FFFFFF"));
        signInPanel.add(usernameField);

        emailField = new PlaceholderText("");
        emailField.setBounds(50, 100, 250, 30);
        emailField.setBackground(Color.decode("#424242"));
        emailField.setForeground(Color.decode("#FFFFFF"));
        emailField.setCaretColor(Color.decode("#FFFFFF"));
        signInPanel.add(emailField);

        passwordField = new PlaceholderPassword("");
        passwordField.setBounds(50, 150, 250, 30);
        passwordField.setBackground(Color.decode("#424242"));
        passwordField.setForeground(Color.decode("#FFFFFF"));
        passwordField.setCaretColor(Color.decode("#FFFFFF"));
        signInPanel.add(passwordField);
        
        PasswordEye.attachTo(passwordField, "/images/eye-open.png", "/images/eye-closed.png");

        signInButton = new JButton();
        signInButton.setBounds(50, 220, 110, 30);
        signInButton.setBackground(Color.decode("#388E3C"));
        signInButton.setForeground(Color.decode("#FFFFFF"));
        signInButton.addActionListener(e -> {
            AccountManager accountManager = AccountManager.getInstance();
            String loginIdentifier = usernameField.getText();
            if (loginIdentifier.trim().isEmpty()) {
                loginIdentifier = emailField.getText();
            }
            String password = new String(passwordField.getPassword());

            if (loginIdentifier.trim().isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(signInPanel, 
                    "Lütfen kullanıcı adı/e-posta ve parola alanlarını doldurun.", 
                    "Giriş Hatası", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = accountManager.signIn(loginIdentifier, password);

            if (success) {
                // DÜZELTME: Başarı mesajı yerine, bayrağı ayarla ve pencereyi kapat.
                this.loginSuccessful = true;
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(signInPanel, 
                    "Kullanıcı adı/e-posta veya parola yanlış ya da hesap doğrulanmamış.", 
                    "Giriş Hatası", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        signInPanel.add(signInButton);
        
        closeButton = new JButton();
        closeButton.setBounds(190, 220, 110, 30);
        closeButton.setBackground(Color.decode("#C94C4C"));
        closeButton.setForeground(Color.decode("#FFFFFF"));
        closeButton.addActionListener(e -> dispose());
        signInPanel.add(closeButton);

        rootPanel.add(signInPanel);
        add(rootPanel);
    }

    public void updateTexts() {
        if (languagesManager == null) return;
        titleBar.setTitle("InkEra - " + languagesManager.getString(Languages.KEY_SIGN_IN_TITLE));
        usernameField.setPlaceholder(languagesManager.getString(Languages.KEY_USERNAME));
        emailField.setPlaceholder(languagesManager.getString(Languages.KEY_EMAIL));
        passwordField.setPlaceholder(languagesManager.getString(Languages.KEY_PASSWORD));
        closeButton.setText(languagesManager.getString(Languages.KEY_CLOSE));
        signInButton.setText(languagesManager.getString(Languages.KEY_SIGN_IN_BUTTON));
    }
}
