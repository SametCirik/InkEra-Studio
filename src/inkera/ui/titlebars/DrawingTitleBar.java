package inkera.ui.titlebars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// DÜZELTME: DrawingWindow import'u kaldırıldı, artık bu sınıfa bağımlı değil.

public class DrawingTitleBar extends JPanel {
	private Point initialClickForDrag;
	private JFrame ownerFrame;
	private Rectangle normalBounds;
	private boolean frameIsMaximized = false;
	private final int PREFERRED_HEIGHT = 30;
	private JLabel titleLabel;
	private boolean isCurrentlyDragging = false;
    
    // DÜZELTME: Minimum boyut sabitleri, DrawingWindow'dan alınmak yerine
    // doğrudan bu sınıfın mantığı için buraya eklendi.
    private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 300;

	public DrawingTitleBar(JFrame frame, String title) {
		this.ownerFrame = frame;
		setLayout(new BorderLayout());
		setBackground(Color.decode("#1E1E1E"));
		setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));

		// Sol Taraf: Uygulama İkonu
		JPanel iconDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, (PREFERRED_HEIGHT - 20) / 2));
		iconDisplayPanel.setOpaque(false);

		URL appIconUrl = getClass().getResource("/images/AppLogo_.png");
		JLabel appIconLabel;
		if (appIconUrl != null) {
			Image iconImage = new ImageIcon(appIconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			appIconLabel = new JLabel(new ImageIcon(iconImage));
		} else {
			appIconLabel = new JLabel();
			System.err.println("MainMenuTitleBar: AppLogo_.png not found in /images/ path!");
		}
		appIconLabel.setPreferredSize(new Dimension(20, 20));
		iconDisplayPanel.add(appIconLabel);
		
		iconDisplayPanel.setPreferredSize(new Dimension(PREFERRED_HEIGHT + 10, PREFERRED_HEIGHT));
		add(iconDisplayPanel, BorderLayout.WEST);

		// Sağ Kontrol Düğmeleri Paneli
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
		
		// Merkezdeki Başlık Etiketi
		titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setForeground(Color.decode("#B0B0B0"));
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		add(titleLabel, BorderLayout.CENTER);

		MouseAdapter dragListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && ownerFrame.getCursor().getType() == Cursor.DEFAULT_CURSOR) {
					initialClickForDrag = e.getPoint();
					isCurrentlyDragging = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isCurrentlyDragging = false;
					initialClickForDrag = null;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isCurrentlyDragging && initialClickForDrag != null && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
					if (frameIsMaximized) return;

                    Point newScreenPoint = e.getLocationOnScreen();
                    ownerFrame.setLocation(newScreenPoint.x - initialClickForDrag.x, newScreenPoint.y - initialClickForDrag.y);
				}
			}
		};
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
	}
	
	public boolean isDragging() {
	    return isCurrentlyDragging;
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
		button.setBackground(new Color(30, 30, 30));
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(45, PREFERRED_HEIGHT));
		return button;
	}
	
	private void toggleMaximize() {
		if (frameIsMaximized) {
			if(normalBounds != null) {
			    ownerFrame.setBounds(normalBounds);
			} else {
			    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    // DÜZELTME: DrawingWindow.MIN_WIDTH yerine bu sınıftaki MIN_WIDTH kullanılıyor.
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

	public boolean isFrameMaximized() {
	    return frameIsMaximized;
	}
}
