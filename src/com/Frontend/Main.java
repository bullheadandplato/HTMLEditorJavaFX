package com.Frontend;

import com.backend.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by osama on 4/28/16.
 * Main class used to load the first interface file
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("MainView.fxml"));
        final VBox group = fxmlLoader.load();
        MainViewController controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(group);
        scene.getStylesheets().add(Main.class.getResource("KeywordStyle.css").toExternalForm());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("HTML make easy");
        primaryStage.show();

    }
}
