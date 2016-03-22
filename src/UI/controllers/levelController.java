package UI.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import structure.User;

/**
 * Created by Volodymyr Dudas on 23.02.2016.
 */
public class levelController {
    public  static Scene currentScene;
    public void Previous(ActionEvent actionEvent) {
        if(User.currentLevel.equals("Novice"))
        {
            loginController.currentStage.setScene(loginController.currentScene);
            loginController.currentStage.setTitle("Авторизація");
        }
        else{
            ((Button)quizeWindowController.currentScene.lookup("#previous")).fire();
            loginController.currentStage.setScene(quizeWindowController.currentScene);
        }

    }

    public void Next(ActionEvent actionEvent) {
        loginController.currentStage.setScene(quizeWindowController.currentScene);
    }
}
