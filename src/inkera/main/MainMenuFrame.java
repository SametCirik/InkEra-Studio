package inkera.main;

import inkera.languages.LanguageSelectionPanel;
import inkera.languages.Languages;
import inkera.ui.account.ForgotPasswordDialog;
import inkera.ui.account.SignInDialog;
import inkera.ui.account.SignUpDialog;
import inkera.ui.hyperlinks.HyperlinkOpener;
import inkera.ui.panels.mainmenu.MainMenuPanelsKit;
import inkera.ui.panels.mainmenu.MarketplacePanel;
import inkera.ui.panels.mainmenu.SettingsPanel;
import inkera.ui.titlebars.MainMenuTitleBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainMenuFrame extends JFrame 
{
	private JPanel currentSubPanel = null;
	private JPanel mainContentAreaPanel;
	private JPanel WestPanel;
	private JPanel accountPanel;

	private Languages languageManager;
	private MainMenuTitleBar titleBar;
	private JLabel appTitleLabel;
	private JButton galleryButton, settingsButton, marketplaceButton, aboutButton;
	
	private HyperlinkOpener signInLink, signUpLink, forgotPasswordLink;
	
    private LanguageSelectionPanel languagePanel;

	private MainMenuPanelsKit.IdlePanel idlePanelInstance;
	private MainMenuPanelsKit.GalleryPanel galleryPanelInstance;
	private SettingsPanel settingsPanelInstance;
	private MarketplacePanel marketplacePanelInstance;
	
	public static final int MIN_WIDTH = 400;
	public static final int MIN_HEIGHT = 300;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainMenuFrame frame = new MainMenuFrame();
			frame.setVisible(true);
		});
	}

	public MainMenuFrame() {
		languageManager = new Languages();
		createWindow();
		updateTexts();
	}

	
    public MainMenuFrame(Languages languageManager) {
		this.languageManager = (languageManager != null) ? languageManager : new Languages();
		createWindow();
		updateTexts();
	}

	public void createWindow() {
		URL iconUrl = getClass().getResource("/images/AppLogo.png");
		if (iconUrl != null) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
		}

		setTitle("InkEra");
		setSize(902, 30 + 602);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setLayout(new BorderLayout());
		
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new BorderLayout());
		basePanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#AAAAAA")));
		add(basePanel);

		titleBar = new MainMenuTitleBar(this, languageManager.getString(Languages.KEY_APP_TITLE));
		basePanel.add(titleBar, BorderLayout.NORTH);

		JPanel rootPanel = new JPanel(new BorderLayout());
//		rootPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.decode("#FFFFFF")));
		rootPanel.setBackground(Color.decode("#121212"));
		rootPanel.setLayout(new BorderLayout());
		basePanel.add(rootPanel, BorderLayout.CENTER);

		JPanel menuButtonPanel = new JPanel();
		menuButtonPanel.setLayout(new BorderLayout());
		menuButtonPanel.setBackground(Color.decode("#121212"));
		menuButtonPanel.setPreferredSize(new Dimension(300, 0));
		rootPanel.add(menuButtonPanel, BorderLayout.WEST);

		int langPanelPadding = 10;
		int langComponentHeight = 25;
		int langButtonWidth = 60;
		int langComboWidth = 110;
		int langElementSpacing = 5;

		languagePanel = new LanguageSelectionPanel(languageManager, () -> updateTexts());
		languagePanel.setBounds(langPanelPadding, langPanelPadding, 
		                         langButtonWidth + langElementSpacing + langComboWidth, langComponentHeight);
		menuButtonPanel.add(languagePanel);

		int currentY = langPanelPadding + langComponentHeight + 120;

		appTitleLabel = new JLabel();
		appTitleLabel.setForeground(Color.decode("#388E3C"));
		appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
		appTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		appTitleLabel.setBounds(0, currentY, 300, 30);
		
		// DÜZELTME: appTitleLabel'a MouseListener eklendi.
		appTitleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// IdlePanel'in bir örneği olduğundan emin ol ve paneli değiştir.
				if (idlePanelInstance == null) {
					idlePanelInstance = new MainMenuPanelsKit.IdlePanel(MainMenuFrame.this, languageManager);
				}
				switchPanel(idlePanelInstance);
			}
		});
		
		menuButtonPanel.add(appTitleLabel);
		currentY += 30 + 45;

		galleryButton = createMenuButton("", currentY);
		menuButtonPanel.add(galleryButton);
		currentY += 40;

		settingsButton = createMenuButton("", currentY);
		menuButtonPanel.add(settingsButton);
		currentY += 40;

		marketplaceButton = createMenuButton("", currentY);
		menuButtonPanel.add(marketplaceButton);
		currentY += 40;

		aboutButton = createMenuButton("About", currentY);
		aboutButton.addActionListener(e -> {
		    String appInfo = languageManager.getString(Languages.KEY_APP_TITLE) + "\nVersion 0.0.01 (Pre-Alpha)\nDeveloped by Samet Cırık";
		    String aboutTitle = languageManager.getString(Languages.KEY_ABOUT_DIALOG_TITLE);
		    JOptionPane.showMessageDialog(MainMenuFrame.this, appInfo, aboutTitle, JOptionPane.INFORMATION_MESSAGE);
		});
		menuButtonPanel.add(aboutButton);
		
		/*
		HyperlinkJDialogOpen signInLink = new HyperlinkJDialogOpen(100, 
				 												   410, 
				 												   40, 
				 												   30,
				 												   menuButtonPanel,
				 												   "Sign In",
				 												   SignUpDialog.class,
				 												   new JDialog());
				 												   
		HyperlinkJDialogOpen signUpLink = new HyperlinkJDialogOpen(165, 
														  		   410, 
														  		   45, 
														     	   30,
														  		   menuButtonPanel,
														  		   "Sign Up",
														  		   SignUpDialog.class,
														  		   new JDialog());
		*/
		
		menuButtonPanel.setLayout(new BorderLayout());
		
		accountPanel = new JPanel();
		accountPanel.setLayout(null);
		accountPanel.setBackground(Color.decode("#121212"));
		accountPanel.setPreferredSize(new Dimension(300, 125));
		menuButtonPanel.add(accountPanel, BorderLayout.SOUTH);
		
		signInLink = new HyperlinkOpener(languageManager, Languages.KEY_SIGN_IN_LINK, SignInDialog.class);
		signInLink.setHorizontalAlignment(HyperlinkOpener.CENTER);
		signInLink.setBounds(50, 5, 200, 30); // Kendi konum ayarlarınızı yapın
		accountPanel.add(signInLink);
		
		signUpLink = new HyperlinkOpener(languageManager, Languages.KEY_SIGN_UP_LINK, SignUpDialog.class);
		signUpLink.setHorizontalAlignment(HyperlinkOpener.CENTER);
		signUpLink.setBounds(50, 30, 200, 30); // Kendi konum ayarlarınızı yapın
		accountPanel.add(signUpLink);
		
		forgotPasswordLink = new HyperlinkOpener(languageManager, Languages.KEY_FORGOT_PASSWORD_LINK, ForgotPasswordDialog.class);
		forgotPasswordLink.setHorizontalAlignment(HyperlinkOpener.CENTER);
		forgotPasswordLink.setBounds(50, 55, 200, 30); // Kendi konum ayarlarınızı yapın
		accountPanel.add(forgotPasswordLink);
		
		galleryButton.addActionListener(e -> {
			if (galleryPanelInstance == null) galleryPanelInstance = new MainMenuPanelsKit.GalleryPanel(this, languageManager);
			switchPanel(galleryPanelInstance);
		});
		settingsButton.addActionListener(e -> {
			if (settingsPanelInstance == null) settingsPanelInstance = new SettingsPanel(this, languageManager);
			switchPanel(settingsPanelInstance);
		});
		marketplaceButton.addActionListener(e -> {
			if (marketplacePanelInstance == null) marketplacePanelInstance = new MarketplacePanel(this, languageManager);
			switchPanel(marketplacePanelInstance);
		});

		mainContentAreaPanel = new JPanel();
		mainContentAreaPanel.setLayout(new GridLayout(1, 1));
		mainContentAreaPanel.setBackground(Color.decode("#1E1E1E"));
		rootPanel.add(mainContentAreaPanel, BorderLayout.CENTER);

		idlePanelInstance = new MainMenuPanelsKit.IdlePanel(this, languageManager);
		switchPanel(idlePanelInstance);
	}

	public void switchPanel(JPanel newPanel) {
		if (currentSubPanel != null) {
			mainContentAreaPanel.remove(currentSubPanel);
		}
		currentSubPanel = newPanel;
		mainContentAreaPanel.add(currentSubPanel);
		
		mainContentAreaPanel.revalidate();
		mainContentAreaPanel.repaint();
	}

	public void updateTexts() {
		if (languageManager == null) return;

		// Ana çerçeve bileşenlerini güncelle
		String appTitle = languageManager.getString(Languages.KEY_APP_TITLE);
		if (titleBar != null) titleBar.setTitle(appTitle);
		if (appTitleLabel != null) appTitleLabel.setText(appTitle);
		if (galleryButton != null) galleryButton.setText(languageManager.getString(Languages.KEY_GALLERY_BUTTON));
		if (settingsButton != null) settingsButton.setText(languageManager.getString(Languages.KEY_SETTINGS_BUTTON));
		if (marketplaceButton != null) marketplaceButton.setText(languageManager.getString(Languages.KEY_MARKETPLACE_BUTTON));
		if (aboutButton != null) aboutButton.setText(languageManager.getString(Languages.KEY_ABOUT_BUTTON));
		if (languagePanel != null) languagePanel.updateLanguageDisplay();
		if (signUpLink != null) signUpLink.updateLanguage();
		if (signInLink != null) signInLink.updateLanguage();
		if (forgotPasswordLink != null) forgotPasswordLink.updateLanguage();
		
		// --- DEĞİŞİKLİK BURADA ---
		// Sadece aktif olanı değil, oluşturulmuş olan TÜM panelleri güncelle.
		if (idlePanelInstance != null) {
			idlePanelInstance.updateTexts();
		}
		if (galleryPanelInstance != null) {
			galleryPanelInstance.updateTexts();
		}
		if (settingsPanelInstance != null) {
			// Not: SettingsPanel'in updateTexts metodu farklı bir parametre alıyorsa
			// ona göre çağırın. Önceki kodunuzda böyleydi.
			settingsPanelInstance.updateTexts(languageManager);
		}
		if (marketplacePanelInstance != null) {
			marketplacePanelInstance.updateTexts();
		}
		// --- DEĞİŞİKLİK SONU ---
	}

	private JButton createMenuButton(String text, int yPos) {
		JButton button = new JButton(text);
		button.setFocusable(false);
		button.setBorderPainted(false);
		button.setBackground(Color.decode("#1E1E1E"));
		button.setForeground(Color.decode("#B0B0B0"));
		button.setBounds(50, yPos, 200, 30);
		return button;
	}
}
