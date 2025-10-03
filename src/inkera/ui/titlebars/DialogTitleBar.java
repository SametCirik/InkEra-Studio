package inkera.ui.titlebars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton; // JButton importu titleBar için kullanılıyordu, DialogTitleBar için gerekmeyebilir
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
// ColorConsole ve Canvas ile ilgili importlar bu sınıfta doğrudan kullanılmıyor, kaldırılabilir.
// import inkera.util.ColorConsole;
// import inkera.ui.canvas.Canvas;
// import inkera.ui.canvas.CanvasSaveUtil;
// JPopupMenu ve JMenuItem da bu sınıfta doğrudan kullanılmıyor.

public class DialogTitleBar extends JPanel {
	private Point initialClick;
	private JDialog dialog; // Sahip olan JDialog
	private JLabel titleLabel; // Başlık etiketini sınıf seviyesinde tutalım
	private final int PREFERRED_HEIGHT = 30;

	public DialogTitleBar(JDialog parentDialog, String title) {
		this.dialog = parentDialog;
		setLayout(new BorderLayout());
		setBackground(Color.decode("#1E1E1E"));
//		setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.decode("#FFFFFF")));
		setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));

		URL iconUrl = getClass().getResource("/images/AppLogo_.png");
		JLabel appIconLabel; // JButton yerine JLabel
		if (iconUrl != null) {
			Image iconImage = new ImageIcon(iconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			appIconLabel = new JLabel(new ImageIcon(iconImage));
			appIconLabel.setPreferredSize(new Dimension(30, PREFERRED_HEIGHT)); 
		} else {
			// System.err.println("DialogTitleBar: AppLogo_.png not found in /images/ path!");
			appIconLabel = new JLabel(" "); // Hata durumunda boşluk veya minimal bir ikon
			appIconLabel.setPreferredSize(new Dimension(30,PREFERRED_HEIGHT)); // Boyutunu ayarla
		}
		// appIconLabel.setPreferredSize(new Dimension(30, PREFERRED_HEIGHT)); // İkon ve padding için
		JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // İkonun soluna 5px boşluk
		iconPanel.setOpaque(false);
		iconPanel.add(appIconLabel);
		add(iconPanel, BorderLayout.WEST);
		
		URL emptyIconUrl = getClass().getResource("/images/emptyIcon.png.png");
		JLabel emptyAppIconLabel; // JButton yerine JLabel
		if (emptyIconUrl != null) {
			Image emptyIconImage = new ImageIcon(emptyIconUrl).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			emptyAppIconLabel = new JLabel(new ImageIcon(emptyIconImage));
			emptyAppIconLabel.setPreferredSize(new Dimension(30, PREFERRED_HEIGHT)); 
		} else {
			// System.err.println("DialogTitleBar: AppLogo_.png not found in /images/ path!");
			emptyAppIconLabel = new JLabel(" "); // Hata durumunda boşluk veya minimal bir ikon
			emptyAppIconLabel.setPreferredSize(new Dimension(30, PREFERRED_HEIGHT)); // Boyutunu ayarla
		}
		// appIconLabel.setPreferredSize(new Dimension(30, PREFERRED_HEIGHT)); // İkon ve padding için
		JPanel emptyIconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // İkonun soluna 5px boşluk
		emptyIconPanel.setOpaque(false);
		emptyIconPanel.add(emptyAppIconLabel); 
		add(emptyIconPanel, BorderLayout.EAST);

		titleLabel = new JLabel(title, SwingConstants.CENTER); // Sınıf alanına ata
		titleLabel.setForeground(Color.decode("#B0B0B0"));
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		add(titleLabel, BorderLayout.CENTER);

		MouseAdapter dragListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					initialClick = e.getPoint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (initialClick == null || (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
					return;
				}
				// JDialog'un ekran üzerindeki konumuyla mouse hareketini birleştirerek yeni konumu bul
				int thisX = dialog.getLocationOnScreen().x;
				int thisY = dialog.getLocationOnScreen().y;
				
				// e.getX() ve e.getY() başlık çubuğuna göre, initialClick de öyle.
				// Mouse'un ekran üzerindeki yeni konumu:
				Point newScreenPoint = e.getLocationOnScreen();
				// Başlangıçtaki tıklamanın ekran üzerindeki konumu:
				Point initialScreenPoint = dialog.getLocationOnScreen();
				initialScreenPoint.translate(initialClick.x, initialClick.y);

				int xMoved = newScreenPoint.x - initialScreenPoint.x;
				int yMoved = newScreenPoint.y - initialScreenPoint.y;

				dialog.setLocation(dialog.getX() + xMoved, dialog.getY() + yMoved);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1) {
			        initialClick = null;
			    }
			}
		};
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
	}

	/**
	 * Diyalog başlık çubuğunun metnini günceller.
	 * @param newTitle Gösterilecek yeni başlık.
	 */
	public void setTitle(String newTitle) {
		if (this.titleLabel != null) {
			this.titleLabel.setText(newTitle);
		}
	}
}