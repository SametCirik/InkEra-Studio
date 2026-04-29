package com.inkera.core.models;

import java.util.List;

public class ProjectModel {
    private String projectId;
    private String title;
    private String author;
    private String status;
    private List<String> tags;
    private String lastEdited;
    private String coverImagePath;
    private List<Chapter> chapters;
    private String projectPath;
    private String synopsis; // YENİ: Hikaye özeti

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getLastEdited() { return lastEdited; }
    public void setLastEdited(String lastEdited) { this.lastEdited = lastEdited; }

    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }

    public List<Chapter> getChapters() { return chapters; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }

    public static class Chapter {
        private String id;
        private String title;
        private int pageCount;
        private String pageLayout; // "RTL" veya "LTR"
        
        // YENİ: Bölümün hangi sayfadan başladığı.
        // true = Sol sayfadan başlar, false = Sağ sayfadan başlar.
        // null = Henüz ilk sayfa (kapak) yerleştirilmemiş (İki tane '+' kartı gösterme durumu)
        private Boolean startsOnLeft; 
        
        // YENİ: Sayfaların listesi (1, 2, 3...)
        private java.util.List<Integer> pages;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public int getPageCount() { return pageCount; }
        public void setPageCount(int pageCount) { this.pageCount = pageCount; }

        public String getPageLayout() { return pageLayout; }
        public void setPageLayout(String pageLayout) { this.pageLayout = pageLayout; }

        public Boolean getStartsOnLeft() { return startsOnLeft; }
        public void setStartsOnLeft(Boolean startsOnLeft) { this.startsOnLeft = startsOnLeft; }

        public java.util.List<Integer> getPages() { return pages; }
        public void setPages(java.util.List<Integer> pages) { this.pages = pages; }
    }
}
