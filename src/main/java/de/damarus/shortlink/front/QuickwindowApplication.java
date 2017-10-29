package de.damarus.shortlink.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuickwindowApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/quickwindow.fxml"));

        primaryStage.setTitle("Shortlink");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
