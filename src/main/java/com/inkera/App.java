package com.inkera;

import java.io.IOException;

import com.inkera.core.config.ConfigService;
import com.inkera.services.LocaleManager;
import com.inkera.ui.controllers.WorkspaceController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
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
        forceCenterOnScreen(stage);
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

                if (useCustomTitleBar) {
                    homeStage.initStyle(StageStyle.UNDECORATED);
                } else {
                    homeStage.initStyle(StageStyle.DECORATED);
                }

                Scene scene = new Scene(root, 1150, 800);
                homeStage.setTitle("InkEra Studio - Home");
                homeStage.setScene(scene);

                homeStage.setMinWidth(1150);
                homeStage.setMaxWidth(1150);
                homeStage.setMinHeight(800);
                homeStage.setMaxHeight(800);

                homeStage.show();
                forceCenterOnScreen(homeStage);

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

                if (useCustomTitleBar) {
                    workspaceStage.initStyle(StageStyle.UNDECORATED);
                } else {
                    workspaceStage.initStyle(StageStyle.DECORATED);
                }
                
                Scene scene = new Scene(root, 1150, 800);
                workspaceStage.setTitle("InkEra Studio - " + name);  
                workspaceStage.setScene(scene);
                workspaceStage.setResizable(false);

                workspaceStage.show();
                forceCenterOnScreen(workspaceStage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // YENİ METOT: KWin'i atlayıp pencereyi matematiksel olarak ekranın tam ortasına zorla kilitler
    private static void forceCenterOnScreen(Stage stage) {
        Platform.runLater(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
