package inkera.ui.panels.mainmenu;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import inkera.languages.Languages;
import inkera.main.MainMenuFrame;

// JPanel'den türetildi
public class SettingsPanel extends JPanel {
	private JLabel comingSoonLabel;
	private Languages languageManager;

    // Constructor artık createPanel görevini üstleniyor
	public SettingsPanel(MainMenuFrame parentFrame, Languages langManager) { // MainMenuFrame parametresi artık isteğe bağlı, kaldırabilirsiniz eğer kullanılmıyorsa
		this.languageManager = langManager;

        // Panelin kendi layout'unu ayarla
		setLayout(new GridLayout(1,1));
		setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#1E1E1E")));
		setBackground(Color.decode("#121212"));
		
		comingSoonLabel = new JLabel("");
		// Fontu güncelleyin
		comingSoonLabel.setFont(new Font("SansSerif", Font.ITALIC, 24));
		comingSoonLabel.setForeground(Color.decode("#707070"));
		comingSoonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(comingSoonLabel); // Bu panelin kendisine ekle
		
		updateTexts(languageManager); // İlk metni ayarla
	}

	public void updateTexts(Languages languageManager) {
		if (comingSoonLabel != null && languageManager != null) {
			comingSoonLabel.setText(languageManager.getString(Languages.KEY_SETTINGS_COMING_SOON));
		}
	}
}