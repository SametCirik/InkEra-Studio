package inkera.splashscreen;

import inkera.main.MainMenuFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 * Projedeki tüm splash screen pencerelerini barındıran bir yardımcı (utility) sınıftır.
 */
public final class SplashScreenKit {
	
	private SplashScreenKit() {}
	
	// =================================================================================
	// LoaderTask Sınıfı
	// =================================================================================
	public static class LoaderTask extends SwingWorker<Void, Void> 
	{
		private final SplashScreenWithProgress splash;

		public LoaderTask(SplashScreenWithProgress splash) 
		{
			this.splash = splash;
		}
		
		@Override
		protected Void doInBackground() 
				throws Exception 
		{
			for (int i = 0; i <= 100; i++) 
			{
				Thread.sleep(5); // Biraz daha görünür olması için süreyi artırdım
				splash.updateProgress(i, 
						"Loading component " + i + "..."); // Mesaj eklendi
			}
			return null;
		}

		@Override
		protected void done() 
		{
			splash.close();
			// MainMenuFrame'i EDT üzerinde oluşturup göstermek daha güvenli olabilir
			// SwingWorker.done() zaten EDT'de çalışır.
			JFrame mainFrame = new MainMenuFrame();
			mainFrame.setVisible(true);
		}
	}

	// =================================================================================
	// SplashScreenWithProgress Sınıfı
	// =================================================================================
	public static class SplashScreenWithProgress 
		extends JWindow 
	{
		private JProgressBar progressBar;
		private JLabel statusLabel; // Kullanılacaksa initialize edilmeli

		public SplashScreenWithProgress() 
		{
			JPanel content = new JPanel(new BorderLayout());
			content.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 
															 2)); // Kenarlık rengi değişti
			content.setBackground(Color.BLACK);
			
			// Resim yükleme classpath üzerinden
			URL imageUrl = getClass().getResource("/images/SplashScreen.png");
			JLabel imageLabel;
			if (imageUrl != null) 
			{
				imageLabel = new JLabel(new ImageIcon(imageUrl));
			} 
			else 
			{
				imageLabel = new JLabel("Splash Image Not Found");
				imageLabel.setForeground(Color.WHITE);
				imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			content.add(imageLabel, BorderLayout.CENTER);
			
			JPanel progressPanel = new JPanel(new BorderLayout()); // Status label için ayrı panel
			progressPanel.setOpaque(false);

			progressBar = new JProgressBar(0, 
										   100);
			progressBar.setStringPainted(true);
			progressBar.setBackground(Color.BLACK); // Arka planı siyah olabilir veya temanıza uygun
			progressBar.setForeground(Color.decode("#388E3C")); // İlerleme çubuğu rengi
			progressBar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 
																 1));
			progressPanel.add(progressBar, 
							  BorderLayout.CENTER);

			statusLabel = new JLabel("Loading...");
			statusLabel.setForeground(Color.LIGHT_GRAY);
			statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
			statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 
																  0, 
																  5, 
																  0)); // Biraz boşluk
			progressPanel.add(statusLabel, 
							  BorderLayout.SOUTH); 


			content.add(progressPanel, 
						BorderLayout.SOUTH); 

			setContentPane(content);
			pack();

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - getSize().width) / 2, 
						(screenSize.height - getSize().height) / 2);
		}

		public void showSplash() 
		{
		setVisible(true);
		}
		
		public void updateProgress(int percent, 
								   String message) 
		{
			progressBar.setValue(percent);
			if (message != null) 
			{
				statusLabel.setText(message);
			}
		}

		public void close() 
		{
			setVisible(false); 
			dispose();
		}
	}
}
