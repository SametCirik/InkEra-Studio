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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import inkera.main.DrawingWindow;

public class DrawingWindowTitleBar extends JPanel {
	private Point initialClickForDrag;
	private final DrawingWindow ownerFrame;
	private Rectangle normalBounds;
	private boolean frameIsMaximized = false;
	private final int PREFERRED_HEIGHT = 30;
	private final JLabel titleLabel;
	private boolean isCurrentlyDragging = false;
    
    private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 300;

	public DrawingWindowTitleBar(DrawingWindow frame, String title) {
		this.ownerFrame = frame;
		setLayout(new BorderLayout());
		setBackground(Color.decode("#1E1E1E"));
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
		closeButton.addActionListener(e -> 
		{
//			handleCloseRequest();
			ownerFrame.closeWindowAndOpenMainMenu();
		});
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.EAST);
        
        // Sağ panelin genişliğini al
        Dimension buttonPanelSize = buttonPanel.getPreferredSize();

		// --- Sol Taraf: Uygulama İkonu ve Menü ---
        int iconHeight = 20;
		JPanel iconDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		iconDisplayPanel.setOpaque(false);
        // DÜZELTME: Sol panelin genişliğini sağ panele eşitliyoruz.
        iconDisplayPanel.setPreferredSize(buttonPanelSize);

		URL appIconUrl = getClass().getResource("/images/AppLogo_.png");
		JButton iconButton;
		if (appIconUrl != null) {
			Image iconImage = new ImageIcon(appIconUrl).getImage().getScaledInstance(iconHeight, iconHeight, Image.SCALE_SMOOTH);
			iconButton = new JButton(new ImageIcon(iconImage));
		} else {
			iconButton = new JButton("F"); // İkon bulunamazsa
		}
		
		configureTitleBarButton(iconButton);
		iconButton.setPreferredSize(new Dimension(PREFERRED_HEIGHT, PREFERRED_HEIGHT));

		JPopupMenu fileMenu = createFileMenu();
		iconButton.addActionListener(e -> fileMenu.show(iconButton, 0, iconButton.getHeight()));
		
		iconDisplayPanel.add(iconButton);
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
    
    private JPopupMenu createFileMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.setBackground(Color.decode("#252525"));
		
		menu.add(createMenuItem("Yeni Proje", () -> System.out.println("Yeni Proje tıklandı.")));
		menu.add(createMenuItem("Proje Aç...", () -> System.out.println("Proje Aç tıklandı.")));
		menu.addSeparator();
		menu.add(createMenuItem("Kaydet", () -> System.out.println("Kaydet tıklandı.")));
		menu.add(createMenuItem("Farklı Kaydet...", () -> System.out.println("Farklı Kaydet tıklandı.")));
		menu.addSeparator();
		menu.add(createMenuItem("Resmi Kaydet...", () -> System.out.println("Resmi Kaydet tıklandı.")));
		menu.addSeparator();
		menu.add(createMenuItem("Proje Ayarları", () -> System.out.println("Proje Ayarları tıklandı.")));
		menu.add(createMenuItem("Uygulama Hakkında", () -> System.out.println("Uygulama Hakkında tıklandı.")));
		menu.add(createMenuItem("Ana Menüye Dön", () -> ownerFrame.closeWindowAndOpenMainMenu()));
		return menu;
	}

	private JMenuItem createMenuItem(String text, Runnable action) {
		JMenuItem item = new JMenuItem(text);
		item.setBackground(Color.decode("#252525"));
		item.setForeground(Color.decode("#B0B0B0"));
		item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		if (action != null) {
			item.addActionListener(e -> action.run());
		}
		return item;
	}

    private void configureTitleBarButton(JButton button) {
		button.setFocusable(false);
		button.setBorderPainted(false);
		button.setOpaque(true);
		button.setBackground(this.getBackground());
		button.setForeground(Color.WHITE);
	}

	private JButton createTitleBarButton(String text) {
		JButton button = new JButton(text);
		configureTitleBarButton(button);
		button.setPreferredSize(new Dimension(45, PREFERRED_HEIGHT));
        
        button.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				if (!"X".equals(button.getText())) button.setBackground(Color.decode("#4F4F4F"));
			}
			public void mouseExited(MouseEvent evt) {
				if (!"X".equals(button.getText())) button.setBackground(DrawingWindowTitleBar.this.getBackground());
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

    private void handleCloseRequest() {
		int choice = JOptionPane.showConfirmDialog(
			ownerFrame,
			"Ana menüye dönmek istediğinize emin misiniz? Kaydedilmemiş değişiklikler kaybolacaktır.",
			"Çıkışı Onayla",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE
		);

		if (choice == JOptionPane.YES_OPTION) {
			ownerFrame.closeWindowAndOpenMainMenu();
		}
	}
}
