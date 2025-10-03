package inkera.ui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import inkera.ui.account.AccountManager;
import inkera.languages.Languages;
import inkera.ui.placeholders.PlaceholderText;
import inkera.ui.titlebars.DialogTitleBar;

public class ForgotPasswordDialog extends JDialog 
{
    private PlaceholderText emailField;
    
    public JButton nextPasswordButton, closeButton;
    public JPanel rootPanel, forgotPasswordPanel;
    
    public Languages languagesManager;
    public DialogTitleBar titleBar;

    public ForgotPasswordDialog() 
    {
        this(new Languages());
    }

    public ForgotPasswordDialog(Languages languagesManager) 
    { 
        this.languagesManager = (languagesManager != null) ? languagesManager : new Languages();
        createWindow();
        updateTexts();
    }
    
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog();
            dialog.setVisible(true);
        });
    }

    private void createWindow() 
    {
        setSize(350, 30 + 200);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));

        titleBar = new DialogTitleBar(this, "");
        rootPanel.add(titleBar, BorderLayout.NORTH);
        
        forgotPasswordPanel = new JPanel();
        forgotPasswordPanel.setLayout(null);
        forgotPasswordPanel.setBackground(Color.decode("#121212"));

        emailField = new PlaceholderText("");
        emailField.setBounds(50, 50, 250, 30);
        emailField.setBackground(Color.decode("#424242"));
        emailField.setForeground(Color.decode("#FFFFFF"));
        emailField.setCaretColor(Color.decode("#FFFFFF"));
        forgotPasswordPanel.add(emailField);

        nextPasswordButton = new JButton();
        nextPasswordButton.setBounds(50, 120, 110, 30);
        nextPasswordButton.setBackground(Color.decode("#388E3C"));
        nextPasswordButton.setForeground(Color.decode("#FFFFFF"));
        
        // DÜZELTME: Butonun ActionListener'ı AccountManager'ı kullanacak şekilde güncellendi.
        nextPasswordButton.addActionListener(e -> {
            handlePasswordResetRequest();
        });
        forgotPasswordPanel.add(nextPasswordButton);
        
        closeButton = new JButton();
        closeButton.setBounds(190, 120, 110, 30);
        closeButton.setBackground(Color.decode("#C94C4C"));
        closeButton.setForeground(Color.decode("#FFFFFF"));
        closeButton.addActionListener(e -> dispose());
        forgotPasswordPanel.add(closeButton);

        rootPanel.add(forgotPasswordPanel);
        add(rootPanel);
    }

    /**
     * "İleri" butonuna tıklandığında çalışan mantık.
     */
    private void handlePasswordResetRequest() {
        String email = emailField.getText();

        // 1. E-posta alanının boş olup olmadığını kontrol et.
        if (email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Lütfen e-posta adresinizi girin.", 
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. AccountManager'dan bir doğrulama kodu talep et.
        AccountManager accountManager = AccountManager.getInstance();
        boolean emailExists = accountManager.requestPasswordReset(email);

        // 3. Sonuca göre davran.
        if (emailExists) {
            // E-posta bulunduysa, bu pencereyi kapat ve VerificationDialog'u aç.
            dispose();
            // VerificationDialog'a e-posta adresini ve dil yöneticisini aktar.
            VerificationDialog verDialog = new VerificationDialog(languagesManager, email, "PASSWORD_RESET");
            verDialog.setVisible(true);
        } else {
            // E-posta bulunamadıysa kullanıcıyı bilgilendir.
            JOptionPane.showMessageDialog(this,
                "Bu e-posta adresiyle kayıtlı bir hesap bulunamadı.", 
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTexts() 
    {
        if (languagesManager == null) return;

        titleBar.setTitle("InkEra - " + languagesManager.getString(Languages.KEY_FORGOT_PASSWORD_LINK));
        emailField.setPlaceholder(languagesManager.getString(Languages.KEY_EMAIL));
        closeButton.setText(languagesManager.getString(Languages.KEY_CLOSE));
        nextPasswordButton.setText(languagesManager.getString(Languages.KEY_NEXT));
    }
}
