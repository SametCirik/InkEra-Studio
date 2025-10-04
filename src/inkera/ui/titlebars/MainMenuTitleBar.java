package inkera.ui.titlebars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenuTitleBar extends JPanel {
	private Point initialClickForDrag;
	private final JFrame ownerFrame;
	private Rectangle normalBounds;
	private boolean frameIsMaximized = false;
	private final int PREFERRED_HEIGHT = 30;
	private final JLabel titleLabel;
	private boolean isCurrentlyDragging = false;
    
    private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 300;

	public MainMenuTitleBar(JFrame frame, String title) {
		this.ownerFrame = frame;
		setLayout(new BorderLayout());
		setBackground(Color.decode("#1E1E1E"));
//		setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.decode("#FFFFFF")));
		setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));

		// --- Sağ Kontrol Düğmeleri Paneli (Genişliği belirlemek için önce oluşturuluyor) ---
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonPanel.setOpaque(false);

		JButton hideButton = createTitleBarButton("_");
		hideButton.addActionListener(e -> ownerFrame.setState(Frame.ICONIFIED));
		buttonPanel.add(hideButton);

		JButton maximizeButton = createTitleBarButton("\u25A2"); // Kare ikonu
		maximizeButton.addActionListener(e -> toggleMaximize());
		buttonPanel.add(maximizeButton);

		JButton closeButton = createTitleBarButton("X");
		closeButton.setBackground(Color.decode("#C94C4C"));
		closeButton.addActionListener(e -> System.exit(0));
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.EAST);
        
        // Sağ panelin genişliğini al
        Dimension buttonPanelSize = buttonPanel.getPreferredSize();

		// --- Sol Taraf: Uygulama İkonu ---
        int iconHeight = 20;
		JPanel iconDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, (PREFERRED_HEIGHT - iconHeight) / 2));
		iconDisplayPanel.setOpaque(false);
        // DÜZELTME: Sol panelin genişliğini sağ panele eşitliyoruz.
        iconDisplayPanel.setPreferredSize(buttonPanelSize);

		URL appIconUrl = getClass().getResource("/images/AppLogo_.png");
		JLabel appIconLabel;
		if (appIconUrl != null) {
			Image iconImage = new ImageIcon(appIconUrl).getImage().getScaledInstance(iconHeight, iconHeight, Image.SCALE_SMOOTH);
			appIconLabel = new JLabel(new ImageIcon(iconImage));
		} else {
			appIconLabel = new JLabel();
			System.err.println("MainMenuTitleBar: AppLogo_.png not found in /images/ path!");
		}
		appIconLabel.setPreferredSize(new Dimension(iconHeight, iconHeight));
		iconDisplayPanel.add(appIconLabel);
		
		add(iconDisplayPanel, BorderLayout.WEST);
		
		// --- Merkezdeki Başlık Etiketi ---
		titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setForeground(Color.decode("#B0B0B0"));
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		add(titleLabel, BorderLayout.CENTER);

		// --- Sürükleme Dinleyicisi ---
		MouseAdapter dragListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					initialClickForDrag = e.getPoint();
					isCurrentlyDragging = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isCurrentlyDragging = false;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isCurrentlyDragging && !frameIsMaximized) {
                    Point newScreenPoint = e.getLocationOnScreen();
                    ownerFrame.setLocation(newScreenPoint.x - initialClickForDrag.x, newScreenPoint.y - initialClickForDrag.y);
				}
			}
		};
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
	}
	
	public void setTitle(String newTitle) {
	    if (titleLabel != null) {
	        titleLabel.setText(newTitle);
	    }
	}

	private JButton createTitleBarButton(String text) {
		JButton button = new JButton(text);
		button.setFocusable(false);
		button.setBorderPainted(false);
		button.setOpaque(true);
		button.setBackground(this.getBackground()); // Ana başlık çubuğu rengini al
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(45, PREFERRED_HEIGHT));
        
        // DÜZELTME: Hover efekti eklendi
        button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				if (!"X".equals(button.getText())) button.setBackground(Color.decode("#4F4F4F"));
			}
			@Override
			public void mouseExited(MouseEvent evt) {
				if (!"X".equals(button.getText())) button.setBackground(MainMenuTitleBar.this.getBackground());
			}
		});
		return button;
	}
	
	private void toggleMaximize() {
		if (frameIsMaximized) {
			if(normalBounds != null) {
			    ownerFrame.setBounds(normalBounds);
			} else {
			    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    int w = Math.max(MIN_WIDTH, screenSize.width / 2);
			    int h = Math.max(MIN_HEIGHT, screenSize.height / 2);
			    ownerFrame.setSize(w,h);
			    ownerFrame.setLocationRelativeTo(null);
			}
		} else {
			if ((ownerFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
				normalBounds = ownerFrame.getBounds();
			}
            Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			ownerFrame.setBounds(screenBounds);
		}
		frameIsMaximized = !frameIsMaximized;
	}
}
