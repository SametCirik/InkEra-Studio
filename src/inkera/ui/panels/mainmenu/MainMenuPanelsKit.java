package inkera.ui.panels.mainmenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import inkera.languages.Languages;
import inkera.main.DrawingWindow;
import inkera.main.MainMenuFrame;
import inkera.projects.ProjectInfo;
import inkera.ui.account.AccountManager;
import inkera.ui.dialogs.AppDialogs;

/**
 * Bu sınıf, Idle ve Gallery panellerini barındıran bir yardımcı (utility) sınıftır.
 */
public final class MainMenuPanelsKit {

	private MainMenuPanelsKit() {}
	
	
	// =================================================================================
	// IdlePanel Sınıfı
	// =================================================================================
	public static class IdlePanel extends JPanel 
	{
		private final JLabel idleTitleLabel;
		private final Languages languageManager;

		public IdlePanel(MainMenuFrame parentFrame, Languages langManager) 
		{
			this.languageManager = langManager;

			setLayout(new GridLayout(1,1));
			setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#1E1E1E")));
			setBackground(Color.decode("#121212"));

			idleTitleLabel = new JLabel("", SwingConstants.CENTER); // Başlangıçta boş oluştur
			idleTitleLabel.setVerticalAlignment(SwingConstants.CENTER);
			idleTitleLabel.setForeground(Color.decode("#B0B0B0"));
			idleTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

			add(idleTitleLabel);
            
            // DÜZELTME: Panel ilk oluşturulduğunda metni güncelleyelim.
            updateTexts();
		}

	public final void updateTexts() {
			if (idleTitleLabel != null && languageManager != null) 
			{
                // DÜZELTME: AccountManager'dan giriş yapan kullanıcıyı kontrol et.
                AccountManager am = AccountManager.getInstance();
                String currentUserName = am.getCurrentUserName();
                String greeting = languageManager.getString(Languages.KEY_IDLE_GREETING);

                if (currentUserName != null) 
                {
                    // Kullanıcı giriş yapmışsa, ismini ekle.
                    idleTitleLabel.setText(greeting + ", " + currentUserName + "!");
                } else 
                {
                    // Kimse giriş yapmamışsa, sadece "Hello!" yaz.
                    idleTitleLabel.setText(greeting + "!");
                }
			}
		}
	}
	
	
	// =================================================================================
	// GalleryPanel Sınıfı
	// =================================================================================
	public static class GalleryPanel extends JPanel { 
		private final JPanel projectPreviewsContainer;
		private final Languages languageManager;
		private final JButton newImageButton;
		private final MainMenuFrame parentFrame;

		private static final String PROJECTS_DIRECTORY = System.getProperty("user.home") + File.separator + "InkEraProjects";
		private static final String THUMBNAIL_SUFFIX = "_thumb.png";

	    public GalleryPanel(MainMenuFrame parentFrame, Languages langManager) {
			this.parentFrame = parentFrame;
			this.languageManager = langManager;

			setLayout(new BorderLayout(0, 10));
			setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#1E1E1E")));
			setBackground(Color.decode("#121212"));

			// Üst Panel: "Yeni Resim" butonu
			JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
			topPanel.setOpaque(false);
//			topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.decode("#1E1E1E")));
			newImageButton = new JButton();
			newImageButton.setFocusable(false);
			newImageButton.setBorderPainted(false);
			newImageButton.setBackground(Color.decode("#2E7D32"));
			newImageButton.setForeground(Color.WHITE);
			newImageButton.addActionListener((ActionEvent e) -> {
			    // 1. Bilgi toplamak için diyaloğu aç ve kapanmasını bekle.
			    AppDialogs.NewImageDialog newImageDialog = new AppDialogs.NewImageDialog(this.parentFrame, this.languageManager);
			    newImageDialog.setVisible(true);

			    // 2. Diyalog "Create" ile kapatıldıysa devam et.
			    if (newImageDialog.isImageCreated()) {
			        // 3. Diyalogdan alınan bilgilerle DrawingWindow'u oluştur.
			        String projectName = newImageDialog.getProjectName();
			        int width = newImageDialog.getImageWidth();
			        int height = newImageDialog.getImageHeight();

			        DrawingWindow drawingWindow = new DrawingWindow(
			            this.parentFrame, 
			            projectName, 
			            width, 
			            height, 
			            this.languageManager
			        );
			        drawingWindow.setVisible(true);

			        // 4. Ana menüyü gizle.
			        this.parentFrame.setVisible(false);
			    }
			});
			topPanel.add(newImageButton);
			add(topPanel, BorderLayout.NORTH);

			// Merkez Panel: Proje Önizlemeleri için Kaydırılabilir Alan
			projectPreviewsContainer = new JPanel(); // Layout'u dinamik olarak belirlenecek
			projectPreviewsContainer.setBackground(Color.decode("#121212"));
			projectPreviewsContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

			JScrollPane scrollPane = new JScrollPane(projectPreviewsContainer);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Color.decode("#1E1E1E")));
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			add(scrollPane, BorderLayout.CENTER);

			updateTexts(); // Buton metnini ve boş galeri mesajını ilk oluşturmada ayarla
			refreshGallery(); // Galeriyi mevcut projelerle doldur
		}

		// printBorder removed — unused helper

	public final void refreshGallery() {
			if (projectPreviewsContainer == null) return;
			projectPreviewsContainer.removeAll(); // Önceki önizlemeleri temizle

			List<ProjectInfo> projects = loadProjects(); // Mevcut projeleri yükle

			if (projects.isEmpty()) {
				// Proje yoksa, boş mesajı göster
				projectPreviewsContainer.setLayout(new BorderLayout()); // Mesajı ortalamak için BorderLayout
				JLabel emptyLabel = new JLabel(languageManager.getString(Languages.KEY_GALLERY_EMPTY));
				emptyLabel.setForeground(Color.GRAY);
				emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
	            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 20)); 
				projectPreviewsContainer.add(emptyLabel, BorderLayout.CENTER);
			} else {
				// Projeler varsa, thumbnail'ları göstermek için GridLayout'a geri dön
				projectPreviewsContainer.setLayout(new GridLayout(0, 3, 15, 15)); 
				for (ProjectInfo proj : projects) {
					ProjectPreviewComponent previewComp = new ProjectPreviewComponent(proj, parentFrame, languageManager);
					previewComp.addMouseListener(new MouseAdapter() {
					    @Override
					    public void mouseClicked(MouseEvent e) {
					        System.out.println("Opening project: " + proj.getProjectName() + " from path: " + proj.getProjectFilePath());
					        DrawingWindow drawingWindow = new DrawingWindow(
					            parentFrame,
					            proj.getProjectName(),
					            800, // Varsayılan genişlik - proje dosyasından yüklenecek
					            600, // Varsayılan yükseklik - proje dosyasından yüklenecek
					            languageManager
					        );
					        drawingWindow.setVisible(true);
					        parentFrame.setVisible(false);
					    }
					});
					projectPreviewsContainer.add(previewComp);
				}
			}
			projectPreviewsContainer.revalidate(); // Bileşenleri yeniden doğrula
			projectPreviewsContainer.repaint(); // Yeniden çiz
		}

	private List<ProjectInfo> loadProjects() {
			List<ProjectInfo> projectList = new ArrayList<>();
			File projectsDir = new File(PROJECTS_DIRECTORY);

			if (!projectsDir.exists()) {
				projectsDir.mkdirs(); // Dizini oluştur
			}

			if (projectsDir.exists() && projectsDir.isDirectory()) {
				File[] files = projectsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".inkera")); // Varsayılan proje uzantısı
				if (files != null) {
					for (File projectFile : files) {
						String projectName = projectFile.getName().substring(0, projectFile.getName().lastIndexOf('.'));
						Path thumbnailPath = Paths.get(projectsDir.getAbsolutePath(), projectName + THUMBNAIL_SUFFIX);
						// Yalnızca thumbnail dosyası varsa projeyi ekle
						if (thumbnailPath.toFile().exists()) { // Thumbnail var mı kontrolü
							projectList.add(new ProjectInfo(projectName, projectFile.toPath(), thumbnailPath));
						} else {
							// Thumbnail yoksa uyarı ver, projeyi yine de ekleyebilirsin veya görmezden gelebilirsin
							// Şu anki mantık, thumbnail yoksa ProjectInfo'ya null geçiyor veya eklemiyor.
							System.out.println("Warning: Thumbnail not found for project: " + projectName + ". Project will not be shown.");
							// Eğer yine de projeyi göstermek isterseniz (thumbnail olmadan):
							// projectList.add(new ProjectInfo(projectName + " (No Thumb)", projectFile.toPath(), null));
						}
					}
				}
			}
			
			// !!! Örnek resimlerin eklenmesini sağlayan bloğu kaldırdık/yorumladık.
			// Bu sayede GalleryPanel, gerçekten diskte olan projelere odaklanacak.
			/*
			if (projectList.isEmpty()) { 
			    addProjectInfoIfThumbnailExists(projectList, "SampleImage1");
			    addProjectInfoIfThumbnailExists(projectList, "SampleImage2");
			    addProjectInfoIfThumbnailExists(projectList, "SampleImage3");
			}
			*/

			return projectList;
		}
		
	    // addProjectInfoIfThumbnailExists metodu artık kullanılmadığı için kaldırıldı.
	    /*
		private void addProjectInfoIfThumbnailExists(List<ProjectInfo> list, String baseName) {
		    URL thumbUrl = getClass().getResource("/images/" + baseName + THUMBNAIL_SUFFIX);
		    if (thumbUrl != null) {
		        try {
		            Path thumbPath = Paths.get(thumbUrl.toURI());
		            Path projectPath = Paths.get(PROJECTS_DIRECTORY, baseName + ".inkera");
		            list.add(new ProjectInfo(baseName, projectPath, thumbPath));
		        } catch (Exception e) {
		            System.err.println("Error creating path for sample: " + baseName + " - " + e.getMessage());
		        }
		    } else {
		        System.err.println("Sample thumbnail not found: /images/" + baseName + THUMBNAIL_SUFFIX + " (This is normal if you don't have sample images.)");
		        Path projectPath = Paths.get(PROJECTS_DIRECTORY, baseName + ".inkera");
		        list.add(new ProjectInfo(baseName + " (No Thumb)", projectPath, null));
		    }
		}
	    */

	public final void updateTexts() {
		    if (languageManager == null) return;
		    if (newImageButton != null) {
		        newImageButton.setText(languageManager.getString(Languages.KEY_NEW_IMAGE_BUTTON));
		    }
		    // Eğer boş galeri mesajı aktifse metnini güncelle
		    // Not: Bu kontrol, emptyLabel'in gerçekten JPanel'in bir bileşeni olup olmadığını kontrol eder.
		    // Eğer boş değilse, ilk bileşen bir JLabel olmayabilir.
		    if (projectPreviewsContainer.getComponentCount() == 1 &&
		        projectPreviewsContainer.getComponent(0) instanceof JLabel) {
		        JLabel emptyLabel = (JLabel) projectPreviewsContainer.getComponent(0);
		        emptyLabel.setText(languageManager.getString(Languages.KEY_GALLERY_EMPTY));
		    }
		}
	}
	
	
	// =================================================================================
	// ProjectPreviewComponent Sınıfı
	// =================================================================================
	public static class ProjectPreviewComponent extends JPanel {
		private final ProjectInfo projectInfo;
		private final JLabel imageLabel;
		private final JLabel nameLabel;
		private final MainMenuFrame parentFrame;
		private final Languages languageManager;

		private static final int THUMBNAIL_WIDTH = 160;
		private static final int THUMBNAIL_HEIGHT = 120;
		private static final Dimension COMPONENT_SIZE = new Dimension(THUMBNAIL_WIDTH + 10, THUMBNAIL_HEIGHT + 30);

		public ProjectPreviewComponent(ProjectInfo projectInfo, MainMenuFrame parentFrame, Languages langManager) {
			this.projectInfo = projectInfo;
			this.parentFrame = parentFrame;
			this.languageManager = langManager;
			setLayout(new BorderLayout(5, 5)); // Resim ve isim arasında boşluk
			setPreferredSize(COMPONENT_SIZE);
			setMaximumSize(COMPONENT_SIZE); // GridLayout'ta boyutları korumak için
			setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#1E1E1E")));
			setBackground(Color.decode("121212")); // Biraz daha açık bir arka plan

			imageLabel = new JLabel();
			imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			imageLabel.setPreferredSize(new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT));
			loadThumbnail();
			add(imageLabel, BorderLayout.CENTER);

			nameLabel = new JLabel(projectInfo.getProjectName(), SwingConstants.CENTER);
			nameLabel.setForeground(Color.WHITE);
			add(nameLabel, BorderLayout.SOUTH);

			// Tıklama dinleyicisi (şimdilik sadece konsola yazar)
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Project clicked: " + projectInfo.getProjectName());
					if (e.getButton() == MouseEvent.BUTTON3) { // Sağ tıklama - kontextual menü
						JPopupMenu optionsMenu = new JPopupMenu();
						
						JMenuItem openItem = new JMenuItem("Open");
						openItem.addActionListener(ae -> {
							// Projeyi aç
							DrawingWindow drawingWindow = new DrawingWindow(
								parentFrame,
								projectInfo.getProjectName(),
								800, // Varsayılan genişlik - proje dosyasından yüklenecek
								600, // Varsayılan yükseklik - proje dosyasından yüklenecek
								languageManager
							);
							drawingWindow.setVisible(true);
							parentFrame.setVisible(false);
						});
						
						JMenuItem renameItem = new JMenuItem("Rename");
						renameItem.addActionListener(ae -> {
							String newName = AppDialogs.showRenameDialog(
								parentFrame,
								projectInfo.getProjectName()
							);
							if (newName != null && !newName.trim().isEmpty()) {
								// TODO: Implement project renaming
								System.out.println("Project will be renamed to: " + newName);
							}
						});
						
						JMenuItem deleteItem = new JMenuItem("Delete");
						deleteItem.addActionListener(ae -> {
							boolean confirmed = AppDialogs.showDeleteConfirmDialog(
								parentFrame,
								projectInfo.getProjectName()
							);
							if (confirmed) {
								// TODO: Implement project deletion
								System.out.println("Project will be deleted: " + projectInfo.getProjectName());
							}
						});
						
						optionsMenu.add(openItem);
						optionsMenu.add(renameItem);
						optionsMenu.addSeparator();
						optionsMenu.add(deleteItem);
						
						optionsMenu.show(e.getComponent(), e.getX(), e.getY());
					} else { // Sol tıklama - direkt aç
						DrawingWindow drawingWindow = new DrawingWindow(
							parentFrame,
							projectInfo.getProjectName(),
							800, // Varsayılan genişlik - proje dosyasından yüklenecek
							600, // Varsayılan yükseklik - proje dosyasından yüklenecek
							languageManager
						);
						drawingWindow.setVisible(true);
						parentFrame.setVisible(false);
					}
				}
			});
		}

		private void loadThumbnail() {
			if (projectInfo.getThumbnailPath() != null && projectInfo.getThumbnailPath().toFile().exists()) {
				try {
					ImageIcon icon = new ImageIcon(projectInfo.getThumbnailPath().toUri().toURL());
					Image scaledImg = icon.getImage().getScaledInstance(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, Image.SCALE_SMOOTH);
					imageLabel.setIcon(new ImageIcon(scaledImg));
				} catch (MalformedURLException e) {
					imageLabel.setText("Error loading");
					System.err.println("Error creating URL for thumbnail: " + projectInfo.getThumbnailPath() + " - " + e.getMessage());
				} catch (Exception e) {
					imageLabel.setText("Preview N/A");
					System.err.println("Error loading thumbnail: " + projectInfo.getThumbnailPath() + " - " + e.getMessage());
				}
			} else {
				imageLabel.setText("No Preview");
				imageLabel.setForeground(Color.GRAY);
			}
		}

		public ProjectInfo getProjectInfo() {
			return projectInfo;
		}
	}
}
