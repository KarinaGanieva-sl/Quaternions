package ControllerPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Main class to launch the application
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class Main extends Application {

    /**
     * This method is used to initialize stage
     * @param primaryStage stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("stage.fxml"));

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Scene scene = new Scene(root, 1024, 600, true);

        primaryStage.setTitle("Quaternions");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(1024);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }

    /**
     * This method is used to launch the application
     * @param args stage
     */
    public static void main(String[] args) {
        launch(args);
    }
}
