package inkera.ui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import inkera.ui.account.AccountManager; // DÜZELTME: AccountManager import edildi.
import inkera.languages.Languages;
import inkera.ui.placeholders.PlaceholderText;
import inkera.ui.titlebars.DialogTitleBar;

public class VerificationDialog extends JDialog
{
	private PlaceholderText verField;
	
	public JButton nextButton, closeButton;
	public JLabel timeLabel;
	public JPanel rootPanel, verPanel;
	
	public Languages languagesManager;
	public DialogTitleBar titleBar;
    
    private Timer countdownTimer;
    private int timeLeftInSeconds = 180;
    
    // DÜZELTME: Önceki diyalogdan gelen bilgileri tutacak alanlar.
    private final String userEmail;
    private final String purpose; // "SIGN_UP" veya "PASSWORD_RESET" gibi bir amaç

	/**
	 * Bu constructor, bir amaç ve e-posta ile çağrılmalıdır.
	 * @param languagesManager Dil yöneticisi
	 * @param email İşlem yapılacak e-posta adresi
	 * @param purpose Bu diyaloğun açılma amacı (örn: "SIGN_UP_VERIFICATION")
	 */
	public VerificationDialog(Languages languagesManager, String email, String purpose)
	{
		this.languagesManager = (languagesManager != null) ? languagesManager : new Languages();
        this.userEmail = email;
        this.purpose = purpose;
		createDialog();
		updateTexts();
        startCountdown();
	}
	
	private void createDialog()
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
        
		verPanel = new JPanel();
		verPanel.setLayout(null);
		verPanel.setBackground(Color.decode("#121212"));
		
		verField = new PlaceholderText("");
		verField.setBounds(50, 50, 250, 30);
		verField.setBackground(Color.decode("#424242"));
		verField.setForeground(Color.decode("#FFFFFF"));
		verField.setCaretColor(Color.decode("#FFFFFF"));
        verField.setHorizontalAlignment(JTextField.CENTER);
        verField.setFont(new Font("Segoe UI", Font.BOLD, 14));
		verPanel.add(verField);
		
		timeLabel = new JLabel("3:00", SwingConstants.CENTER);
		timeLabel.setBounds(50, 95, 250, 30);
		timeLabel.setForeground(Color.decode("#FFFFFF"));
		verPanel.add(timeLabel);
		
		JLabel resendCodeLabel = new JLabel("Send Code Again", SwingConstants.CENTER);
		resendCodeLabel.setBounds(50, 120, 250, 30);
		resendCodeLabel.setForeground(Color.decode("#AAAAAA"));
		verPanel.add(resendCodeLabel);
		
		nextButton = new JButton();
		nextButton.setBounds(50, 170, 110, 30);
		nextButton.setBackground(Color.decode("#388E3C"));
		nextButton.setForeground(Color.decode("#FFFFFF"));
		// DÜZELTME: ActionListener, handleNextClick metodunu çağırıyor.
		nextButton.addActionListener(e -> handleNextClick());
		verPanel.add(nextButton);
		
		closeButton = new JButton();
		closeButton.setBounds(190, 170, 110, 30);
		closeButton.setBackground(Color.decode("#C94C4C"));
		closeButton.setForeground(Color.decode("#FFFFFF"));
		closeButton.addActionListener(e ->
		{
            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }
			dispose();
		});
		verPanel.add(closeButton);
		
		rootPanel.add(verPanel);
		add(rootPanel);
	}
    
    private void handleNextClick() {
        String code = verField.getText();
        if (code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen doğrulama kodunu girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AccountManager accountManager = AccountManager.getInstance();

        // Diyalogun hangi amaçla açıldığına göre farklı işlem yap
        if ("PASSWORD_RESET".equals(purpose)) {
            // Bu diyalogun görevi sadece kodu toplamak. Asıl kontrol bir sonraki adımda.
            // Bu yüzden kodu bir sonraki diyaloga aktarıyoruz.
            dispose();
            ChangePasswordDialog cpd = new ChangePasswordDialog(languagesManager, userEmail, code);
            cpd.setVisible(true);

        } else if ("SIGN_UP_VERIFICATION".equals(purpose)) {
            boolean success = accountManager.verifyAccount(userEmail, code);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hesabınız başarıyla doğrulandı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // TODO: Kullanıcıyı SignInDialog'a yönlendir.
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz doğrulama kodu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            timeLeftInSeconds--;

            int minutes = timeLeftInSeconds / 60;
            int seconds = timeLeftInSeconds % 60;
            String timeString = String.format("%d:%02d", minutes, seconds);
            
            timeLabel.setText(timeString);

            if (timeLeftInSeconds <= 0) {
                countdownTimer.stop();
                timeLabel.setText("Süre Doldu!");
                nextButton.setEnabled(false);
                verField.setEnabled(false);
            }
        });
        countdownTimer.start();
    }
	
	public void updateTexts()
	{
		if(languagesManager == null) return;
		
		// Not: Bu anahtarları Languages.java sınıfınıza eklemeniz gerekecek.
		// titleBar.setTitle("InkEra - " + languagesManager.getString(Languages.KEY_VERIFICATION_TITLE));
		// verField.setPlaceholder(languagesManager.getString(Languages.KEY_VERIFICATION_CODE));
		// nextButton.setText(languagesManager.getString(Languages.KEY_NEXT));
		
        titleBar.setTitle("InkEra - Doğrulama");
        verField.setPlaceholder("Doğrulama Kodu");
        nextButton.setText("İleri");
		closeButton.setText(languagesManager.getString(Languages.KEY_CLOSE));
	}
}
