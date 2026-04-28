module com.inkera {
    requires javafx.controls;
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http; 
    requires com.google.gson; 

    // Ana paketi ve yeni controller paketini FXML'e aç
    opens com.inkera to javafx.fxml;
    opens com.inkera.ui.controllers to javafx.fxml; // GÜNCELLENDİ
    opens com.inkera.core.models to com.google.gson;
    
    // Dış dünyaya aç
    exports com.inkera;
}