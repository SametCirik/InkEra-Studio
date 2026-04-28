package com.inkera;

import java.io.IOException;

import com.inkera.core.config.ConfigService;
import com.inkera.services.LocaleManager;
import com.inkera.ui.controllers.WorkspaceController;
import com.inkera.util.ResizeHelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static Stage splashStage;  
    private static Stage currentStage;  
    private static boolean useCustomTitleBar;

    @Override
    public void start(Stage stage) throws IOException {
        useCustomTitleBar = ConfigService.getInstance().isCustomTitleBar();

        splashStage = stage;
         
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inkera/fxml/splash.fxml"));
        Parent root = loader.load();
         
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED); 
        stage.setScene(scene);
        stage.show();
    }

    public static void showHome() {
        Platform.runLater(() -> {
            try {
                if (splashStage != null) splashStage.close();
                if (currentStage != null) currentStage.close();

                Stage homeStage = new Stage();
                currentStage = homeStage;  

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/inkera/fxml/home.fxml"));
                loader.setResources(LocaleManager.getInstance().getBundle());
                Parent root = loader.load();

                // 1. Önce Pencere Stilini Ayarla
                if (useCustomTitleBar) {
                    homeStage.initStyle(StageStyle.UNDECORATED);
                } else {
                    homeStage.initStyle(StageStyle.DECORATED);
                }

                // 2. Scene'i oluştur ve Stage'e bağla (Kritik Adım)
                Scene scene = new Scene(root);
                homeStage.setTitle("InkEra Studio - Home");
                homeStage.setScene(scene);

                // 3. Resize Listener'ı EKLE (Scene bağlandıktan SONRA çağrılmalı!)
                if (useCustomTitleBar) {
                    ResizeHelper.addResizeListener(homeStage);
                }

                homeStage.centerOnScreen();
                homeStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void reloadUI() {
        showHome();
    }

    public static void showWorkspace(String name, int width, int height) {
        Platform.runLater(() -> {
            try {
                if (currentStage != null) currentStage.close();
                if (splashStage != null) splashStage.close();

                Stage workspaceStage = new Stage();
                currentStage = workspaceStage;

                FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/inkera/fxml/workspace.fxml"));
                loader.setResources(LocaleManager.getInstance().getBundle());
                Parent root = loader.load();

                WorkspaceController controller = loader.getController();
                controller.setCanvasSize(width, height);

                // 1. Önce Pencere Stilini Ayarla
                if (useCustomTitleBar) {
                    workspaceStage.initStyle(StageStyle.UNDECORATED);
                } else {
                    workspaceStage.initStyle(StageStyle.DECORATED);
                }
                
                // 2. Scene'i oluştur ve Stage'e bağla (Kritik Adım)
                Scene scene = new Scene(root);
                workspaceStage.setTitle("InkEra Studio - " + name);  
                workspaceStage.setScene(scene);

                // 3. Resize Listener'ı EKLE (Scene bağlandıktan SONRA çağrılmalı!)
                if (useCustomTitleBar) {
                    ResizeHelper.addResizeListener(workspaceStage);  
                }

                workspaceStage.centerOnScreen();
                workspaceStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}