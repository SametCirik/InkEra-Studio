package com.inkera.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inkera.core.models.ProjectModel;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectService {
    // Pretty printing: JSON dosyalarını yan yana değil, alt alta ve girintili (okunabilir) kaydeder
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    private final Path dataPath;

    public ProjectService() {
        // Geliştirme Ortamı İçin: Kök dizindeki "user-data/projects" klasörü
        String projectRoot = System.getProperty("user.dir");
        this.dataPath = Paths.get(projectRoot, "user-data", "projects");
        
        try {
            Files.createDirectories(dataPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ProjectModel> loadAllProjects() {
        List<ProjectModel> projects = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataPath, "*.json")) {
            for (Path entry : stream) {
                try (Reader reader = Files.newBufferedReader(entry)) {
                    projects.add(gson.fromJson(reader, ProjectModel.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public void saveProject(ProjectModel project) {
        // 1. Ana Merkeze (Hub) Kaydet
        Path hubFilePath = dataPath.resolve(project.getProjectId() + ".json");
        try (Writer writer = Files.newBufferedWriter(hubFilePath)) {
            gson.toJson(project, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Projenin yerel klasöründeki (.inkera/meta.json) dosyayı da güncelle
        if (project.getProjectPath() != null) {
            Path localMetaFile = Paths.get(project.getProjectPath(), ".inkera", "meta.json");
            if (Files.exists(localMetaFile.getParent())) {
                try (Writer writer = Files.newBufferedWriter(localMetaFile)) {
                    gson.toJson(project, writer);
                } catch (IOException e) {
                    System.err.println("Yerel meta.json güncellenemedi!");
                    e.printStackTrace();
                }
            }
        }
    }

    // --- YENİ EKLENEN: MANGA PROJESİ İNŞA MOTORU ---
    public void createMangaProjectScaffold(String projectName, String author, File selectedDirectory) {
        try {
            // 1. Kullanıcının seçtiği yere ana proje klasörünü aç
            Path projectRoot = Paths.get(selectedDirectory.getAbsolutePath(), projectName);
            Files.createDirectories(projectRoot);

            // 2. Gizli .inkera klasörünü ve bölümler klasörünü oluştur
            Path inkeraFolder = projectRoot.resolve(".inkera");
            Path episodesFolder = projectRoot.resolve("Episodes");
            Files.createDirectories(inkeraFolder);
            Files.createDirectories(episodesFolder);

            // 3. Proje verisini (Modelini) oluştur
            ProjectModel newProject = new ProjectModel();
            newProject.setProjectId(UUID.randomUUID().toString()); // Çakışmaları önlemek için eşsiz ID
            newProject.setTitle(projectName);
            newProject.setAuthor(author);
            newProject.setStatus("ONGOING");
            newProject.setTags(new ArrayList<>());
            newProject.setChapters(new ArrayList<>());
            newProject.setLastEdited(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            newProject.setProjectPath(projectRoot.toString());

            // 4. .inkera içine projenin kendi yerel meta dosyasını bırak
            Path localMetaFile = inkeraFolder.resolve("meta.json");
            try (Writer writer = Files.newBufferedWriter(localMetaFile)) {
                gson.toJson(newProject, writer);
            }

            // 5. InkEra'nın kendi merkezine (user-data/projects) bu projeyi kaydet
            saveProject(newProject);

            System.out.println("Manga proje klasörleri başarıyla oluşturuldu: " + projectRoot.toString());

        } catch (Exception e) {
            System.err.println("HATA: Proje klasörleri oluşturulamadı!");
            e.printStackTrace();
        }
    }
}
