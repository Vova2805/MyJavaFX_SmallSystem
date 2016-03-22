package UI.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import structure.User;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;

public class loginController {
    public TextField user_name;

    public  static Stage currentStage;
    public static  Scene currentScene;
    public Label actiontarget;

    public void handleSubmitButtonAction(ActionEvent actionEvent) {

        try {
            if(!user_name.getText().equals("") && user_name.getText().length()<30){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/quize_window.fxml"));
            Parent root = fxmlLoader.load();

                Scene scene = new Scene(root,currentScene.getWidth(),currentScene.getHeight());
                quizeWindowController.currentScene = scene;

                User.setName(user_name.getText(), "#user_name");
                User.setName("Новачок (Novice)", "#level_title");
                User.NextQuestion("#questionText", "#answerBox");
                quizeWindowController.restLevels();

                FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("../fxmls/levelView.fxml"));
                Parent root1 = fxmlLoader1.load();
                Scene scene1 = new Scene(root1,currentScene.getWidth(),currentScene.getHeight());
                currentStage.setScene(scene1);
                currentStage.setMaximized(true);
                currentStage.setTitle("Хід тестування");

                Image image = new Image("images/novice.jpg");
                Text description = (Text)scene1.lookup("#description");
                description.setText(User.quize.getProgrammerLevels().get("Novice").getDetailedInfo());

                Label title = (Label)scene1.lookup("#title");
                title.setText(User.quize.getProgrammerLevels().get("Novice").getTitle());

                AnchorPane anchorPane = (AnchorPane)scene1.lookup("#imagePane");
                ImageView imageView = new ImageView();
                imageView.setId("imageView");
                imageView.setFitHeight(anchorPane.getHeight());
                imageView.setFitWidth(anchorPane.getWidth());
                anchorPane.getChildren().add(imageView);
                imageView.setImage(image);
                levelController.currentScene = scene1;
                currentStage.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                        AnchorPane anchorPane = (AnchorPane)levelController.currentScene.lookup("#imagePane");
                        ImageView imageView = (ImageView)levelController.currentScene.lookup("#imageView");
                        imageView.setFitHeight(anchorPane.getHeight());
                        imageView.setFitWidth(anchorPane.getWidth());
                    }
                });
                currentStage.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                        AnchorPane anchorPane = (AnchorPane)levelController.currentScene.lookup("#imagePane");
                        ImageView imageView = (ImageView)levelController.currentScene.lookup("#imageView");
                        imageView.setFitHeight(anchorPane.getHeight());
                        imageView.setFitWidth(anchorPane.getWidth());
                    }
                });
                currentStage.setMaximized(true);

                AnchorPane anchorPane1 = (AnchorPane)scene1.lookup("#imagePane");
                ImageView imageView1 = (ImageView)scene1.lookup("#imageView");
                imageView1.setFitHeight(anchorPane1.getHeight());
                imageView1.setFitWidth(anchorPane1.getWidth());
        }
            else{
                actiontarget.setText("Не правильне ім'я. Спробуйте знову \n(ім'я не повинно бути порожнім або більшим ніж 30 символів)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        User.name = user_name.getText();
    }

    public void keyTyped(Event event) {
        actiontarget.setText("");
    }

    public void ShowInfo(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/aboutLevel.fxml"));
        Parent root = null;//reflection
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();//show quize window
        Scene scene = new Scene(root);
        stage.setTitle("Довідка");
        TextArea textArea = (TextArea)scene.lookup("#info");
        textArea.setText(User.getLevelInfo());
        textArea.setWrapText(true);
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/icon.png"));
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public void About(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/aboutProgram.fxml"));
        Parent root = null;//reflection
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();//show quize window
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/icon.png"));
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public void AsAdmin(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/admin_pane.fxml"));
            Parent root = null;//reflection
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();//show quize window
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/icon.png"));
            stage.setMaximized(true);
            stage.setOnCloseRequest(event -> {
            if(adminController.changed)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Підтвердження змін");
                alert.setHeaderText("Обережно! Зміни не збережені");
                alert.setContentText("Бажаєте зберегти усі внесені зміни?");
                ButtonType buttonTypeOne = new ButtonType("Так");
                ButtonType buttonTypeTwo = new ButtonType("Ні");

                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                Optional<ButtonType> result  = alert.showAndWait();
                if(result.get() == buttonTypeOne )
                {
                    adminController.Save();
                }
            }
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
