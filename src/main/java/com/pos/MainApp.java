package com.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/pos/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 480, 420);
        scene.getStylesheets().add(MainApp.class.getResource("/com/pos/css/dark-theme.css").toExternalForm());
        primaryStage.setTitle("POS System — Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showMainScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/pos/fxml/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(MainApp.class.getResource("/com/pos/css/dark-theme.css").toExternalForm());
        primaryStage.setTitle("POS System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
