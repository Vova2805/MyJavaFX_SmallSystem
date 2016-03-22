package UI;

import UI.controllers.loginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/login.fxml"));
        primaryStage.setTitle("Авторизація");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        double width = gd.getDisplayMode().getWidth();
        double height = gd.getDisplayMode().getHeight()-60;
        Scene scene = new Scene(root,width,height);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        loginController.currentStage = primaryStage;
        loginController.currentScene = scene;
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
