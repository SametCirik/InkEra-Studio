package com.inkera.ui.controllers;

import com.inkera.core.models.ProjectModel;
import com.inkera.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChapterDetailController {

    @FXML private Label headerTitleLabel;
    @FXML private Label chapterTitleLabel;
    @FXML private Label layoutInfoLabel;
    @FXML private VBox pagesVBox;

    private ProjectModel project;
    private ProjectModel.Chapter chapter;
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ProjectService projectService = new ProjectService();

    public void setChapterContext(ProjectModel project, ProjectModel.Chapter chapter) {
        this.project = project;
        this.chapter = chapter;

        headerTitleLabel.setText(project.getTitle() + " - " + chapter.getTitle());
        chapterTitleLabel.setText(chapter.getTitle());
        
        boolean isRTL = "RTL".equals(chapter.getPageLayout());
        layoutInfoLabel.setText(isRTL ? "Okuma Yönü: Sağdan Sola" : "Okuma Yönü: Soldan Sağa");

        // Bölümün yerel meta.json dosyasını oku (varsa)
        loadLocalChapterMeta();
        
        renderPages(isRTL);
    }

    private void loadLocalChapterMeta() {
        Path metaPath = Paths.get(project.getProjectPath(), "Episodes", chapter.getTitle(), ".inkepisode", "meta.json");
        if (Files.exists(metaPath)) {
            try (Reader reader = Files.newBufferedReader(metaPath)) {
                ProjectModel.Chapter localData = gson.fromJson(reader, ProjectModel.Chapter.class);
                if (localData != null) {
                    // Yerel verileri geçici objemize aktarıyoruz
                    this.chapter.setStartsOnLeft(localData.getStartsOnLeft());
                    this.chapter.setPages(localData.getPages() != null ? localData.getPages() : new ArrayList<>());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLocalChapterMeta() {
        Path metaPath = Paths.get(project.getProjectPath(), "Episodes", chapter.getTitle(), ".inkepisode", "meta.json");
        try {
            Files.createDirectories(metaPath.getParent());
            try (Writer writer = Files.newBufferedWriter(metaPath)) {
                gson.toJson(this.chapter, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Ana proje JSON'unu da (chapter sayfa sayısı için) güncelleyelim
        chapter.setPageCount(chapter.getPages() != null ? chapter.getPages().size() : 0);
        projectService.saveProject(project);
    }

    private void renderPages(boolean isRTL) {
        pagesVBox.getChildren().clear();

        // DURUM 1: Bölüm yeni açılmış, hiçbir sayfa yok. (Kullanıcıdan hizalama isteği)
        if (chapter.getStartsOnLeft() == null || chapter.getPages() == null || chapter.getPages().isEmpty()) {
            HBox initialSpread = new HBox(20);
            initialSpread.setStyle("-fx-alignment: center;");

            StackPane leftBtn = createAddPageButton("Buradan Başla\n(Sol Sayfa)");
            StackPane rightBtn = createAddPageButton("Buradan Başla\n(Sağ Sayfa)");

            leftBtn.setOnMouseClicked(e -> handleInitialPagePlacement(true));
            rightBtn.setOnMouseClicked(e -> handleInitialPagePlacement(false));

            if (isRTL) {
                initialSpread.getChildren().addAll(rightBtn, leftBtn); // Manga: Sağ kapak önce gelir
            } else {
                initialSpread.getChildren().addAll(leftBtn, rightBtn); // Comic: Sol kapak önce gelir
            }
            
            pagesVBox.getChildren().add(initialSpread);
            return;
        }

        // DURUM 2: Hizalama belli, sayfalar var. Matbaa dizilimi yap.
        boolean startsLeft = chapter.getStartsOnLeft();
        int totalPages = chapter.getPages().size();
        
        // Offset hesabı: Eğer kapak sağdan başlıyorsa, sol tarafı "boş" (null) sayarak dizilime dahil etmeliyiz.
        // Veya tam tersi. Bu matematik formayı oluşturur.
        int currentIdx = 0;
        
        // İlk satır (Spread)
        HBox firstSpread = new HBox(20);
        firstSpread.setStyle("-fx-alignment: center;");
        
        StackPane firstLeft = null;
        StackPane firstRight = null;

        if (startsLeft) {
            firstLeft = createPageCard(1); // Sayfa 1 solda
            currentIdx++;
            if (currentIdx < totalPages) {
                firstRight = createPageCard(2); // Varsa Sayfa 2 sağda
                currentIdx++;
            } else {
                firstRight = createAddPageButton("+"); // Yoksa sağa ekleme butonu
            }
        } else {
            // Sayfa 1 sağda başlayacak. Sol taraf önceki bölüme aittir (veya boştur).
            firstLeft = createEmptyPlaceholder("Önceki Bölüm");
            firstRight = createPageCard(1);
            currentIdx++;
        }

        if (isRTL) firstSpread.getChildren().addAll(firstRight, firstLeft);
        else firstSpread.getChildren().addAll(firstLeft, firstRight);
        
        pagesVBox.getChildren().add(firstSpread);

        // Kalan sayfaları ikişer ikişer (spread olarak) ekle
        while (currentIdx < totalPages) {
            HBox spread = new HBox(20);
            spread.setStyle("-fx-alignment: center;");
            
            StackPane leftPage = createPageCard(currentIdx + 1);
            currentIdx++;
            
            StackPane rightPage = null;
            if (currentIdx < totalPages) {
                rightPage = createPageCard(currentIdx + 1);
                currentIdx++;
            } else {
                rightPage = createAddPageButton("+");
            }

            if (isRTL) spread.getChildren().addAll(rightPage, leftPage);
            else spread.getChildren().addAll(leftPage, rightPage);

            pagesVBox.getChildren().add(spread);
        }

        // Eğer tüm sayfalar tam bir forma (çift) oluşturduysa, en alta yeni bir "+" spreadi açmamız lazım
        // Örneğin kapak solda (startsLeft=true) ve 2 sayfamız var. Sonraki (3. sayfa) yeni bir satırın soluna gelmeli.
        if (currentIdx == totalPages) {
            // Son eklenen kart bir "+" butonu DEĞİLSE, yeni satır aç
            boolean lastRowIsFull = false;
            
            if (startsLeft && totalPages % 2 == 0) lastRowIsFull = true;
            if (!startsLeft && totalPages % 2 != 0) lastRowIsFull = true;

            if (lastRowIsFull) {
                HBox addSpread = new HBox(20);
                addSpread.setStyle("-fx-alignment: center;");
                StackPane addBtn = createAddPageButton("+");
                StackPane empty = createEmptyPlaceholder("");
                
                if (isRTL) addSpread.getChildren().addAll(empty, addBtn);
                else addSpread.getChildren().addAll(addBtn, empty);
                
                pagesVBox.getChildren().add(addSpread);
            }
        }
    }

    private void handleInitialPagePlacement(boolean isLeft) {
        if (chapter.getPages() == null) chapter.setPages(new ArrayList<>());
        
        chapter.setStartsOnLeft(isLeft);
        chapter.getPages().add(1); // 1. sayfayı ekle
        
        // Fiziksel klasörü oluştur (Episodes/BolumAdi/1)
        File pageDir = new File(project.getProjectPath() + "/Episodes/" + chapter.getTitle() + "/1");
        pageDir.mkdirs();

        saveLocalChapterMeta();
        renderPages("RTL".equals(chapter.getPageLayout()));
    }

    // SONRAKİ SAYFALARI EKLEMEK İÇİN TETİKLENECEK METOT
    private void addNewPage() {
        int newPageNum = chapter.getPages().size() + 1;
        chapter.getPages().add(newPageNum);
        
        File pageDir = new File(project.getProjectPath() + "/Episodes/" + chapter.getTitle() + "/" + newPageNum);
        pageDir.mkdirs();

        saveLocalChapterMeta();
        renderPages("RTL".equals(chapter.getPageLayout()));
    }

    private StackPane createPageCard(int pageNum) {
        StackPane pane = new StackPane();
        pane.setPrefSize(180, 250);
        pane.setStyle("-fx-background-color: #333; -fx-background-radius: 5;");
        
        Label label = new Label("Sayfa " + pageNum);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        pane.getChildren().add(label);
        return pane;
    }

    private StackPane createAddPageButton(String text) {
        StackPane pane = new StackPane();
        pane.setPrefSize(180, 250);
        pane.setStyle("-fx-background-color: transparent; -fx-border-color: #4caf50; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 5;");
        pane.setCursor(Cursor.HAND);
        
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        pane.getChildren().add(label);
        
        // Tıklanınca yeni sayfa ekle (Sadece metin "+" ise, "Buradan Başla" mantığı farklı)
        if ("+".equals(text)) {
            pane.setOnMouseClicked(e -> addNewPage());
        }
        
        return pane;
    }

    private StackPane createEmptyPlaceholder(String text) {
        StackPane pane = new StackPane();
        pane.setPrefSize(180, 250);
        pane.setStyle("-fx-background-color: transparent;");
        
        if (!text.isEmpty()) {
            Label label = new Label(text);
            label.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");
            pane.getChildren().add(label);
        }
        return pane;
    }

    @FXML
    private void backToMangaDetail() {
        try {
            URL resourceUrl = getClass().getResource("/com/inkera/fxml/manga_detail.fxml");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(com.inkera.services.LocaleManager.getInstance().getBundle());
            Parent detailView = loader.load();
            
            MangaDetailController controller = loader.getController();
            controller.setProject(project);
            
            HomeController.setCenterView(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
