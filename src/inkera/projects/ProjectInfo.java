package inkera.projects;

import java.io.File; // Dosya yolu için
import java.nio.file.Path;

public class ProjectInfo {
	private String projectName;
	private Path projectFilePath; // Proje dosyasının tam yolu (katmanları vb. içeren dosya)
	private Path thumbnailPath;   // Önizleme resminin yolu
	private long lastModified;    // Son düzenlenme tarihi (isteğe bağlı)

	// Basit constructor
	public ProjectInfo(String projectName, Path projectFilePath, Path thumbnailPath) {
		this.projectName = projectName;
		this.projectFilePath = projectFilePath;
		this.thumbnailPath = thumbnailPath;
		if (projectFilePath != null) {
		    File file = projectFilePath.toFile();
		    if(file.exists()) {
		        this.lastModified = file.lastModified();
		    }
		}
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Path getProjectFilePath() {
		return projectFilePath;
	}

	public void setProjectFilePath(Path projectFilePath) {
		this.projectFilePath = projectFilePath;
	}

	public Path getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(Path thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return projectName; // JList veya ComboBox'ta görünecek varsayılan metin
	}
}