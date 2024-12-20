package com.javengers.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The main application class that extends JavaFX Application.
 * This class initializes and starts the JavaFX application.
 */
public class MainApp extends Application {

    /**
     * The entry point for the JavaFX application.
     * 
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Create an instance of ControllerPane which handles all the controls and chart
        // updates
        Viewer controllerPane = new Viewer();

        // Create the root layout using a StackPane to hold the main layout from
        // ControllerPane
        StackPane root = new StackPane(controllerPane.getMainLayout());

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Maven App");

        primaryStage.show();
    }

    /**
     * The main method that serves as the entry point for the application.
     * 
     * @param args Command-line arguments for the application (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
