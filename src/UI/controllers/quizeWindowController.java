package UI.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import structure.Level;
import structure.Question;
import structure.User;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Volodymyr Dudas on 16.02.2016.
 */
public class quizeWindowController{

    public AnchorPane anchor;
    public Button nextQuestion;
    public Button previousQuestion;
    public Button startAgain;
    public MenuItem nextQuestion1;
    public MenuItem previousQuestion1;
    public MenuItem startAgain1;

    public void ChangeQuestion(ActionEvent actionEvent) {//перехід між питаннями
        if(actionEvent==null) User.NextQuestion("#questionText","#answerBox");
        else
        if( actionEvent.getSource()==nextQuestion || actionEvent.getSource()==nextQuestion1)
        {
            User.NextQuestion("#questionText","#answerBox");
        }else
        if(actionEvent.getSource()==previousQuestion || actionEvent.getSource()==previousQuestion1)
        {
            User.PreviousQuestion("#questionText","#answerBox");
            restoreNextButton();
            ((Button)currentScene.lookup("#next")).setOnAction(event -> {ChangeQuestion(null);});
        }
        else {
            User.ResetLevel("#questionText", "#answerBox");
            ((Button)currentScene.lookup("#previous")).setDisable(false);
            ((Button)currentScene.lookup("#startAgain")).setDisable(false);
        }
    }

    public static void restoreNextButton()//відновлення стану кнопки
    {
        ((Button)currentScene.lookup("#next")).setText("Наступне питання");
    }
    public void ResetQuize(ActionEvent actionEvent) {
        restoreNextButton();
        User.ResetQuize("#questionText","#answerBox");
        ((Button)currentScene.lookup("#next")).setOnAction(event -> {ChangeQuestion(null);});
        ((Button)currentScene.lookup("#previous")).setDisable(false);
        ((Button)currentScene.lookup("#startAgain")).setDisable(false);
    }

    public static  Scene currentScene;
    public static  Scene statScene;
    static int summary = 0;
    public static int getSummary()
    {
        return summary;
    }//результат
    public static BarChart generateChart(String levelTitle)//генерація діаграми
    {
        summary = 0;
        Level level = User.quize.getProgrammerLevels().get(levelTitle);
        String []array = new String[level.getQuestions().size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = String.valueOf(i+1);
        }
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis("Отримані оцінки", 0.0d, 5000.0d, 1000.0d);
        ObservableList barChartData = FXCollections.observableArrayList();
        for (int i = 0; i < array.length; i++) {
            int answ = level.getQuestions().get(i).getUserAnswer();
            summary+=answ;
            barChartData.add(
                    new BarChart.Series("Питання № "+array[i], FXCollections.observableArrayList(
                            new BarChart.Data(array[0],answ*1000.0)
                    )));
        }
        BarChart chart = new BarChart(xAxis, yAxis, barChartData, 25.0d);
        chart.setTitle(levelTitle);
        return chart;
    }

    public void finishQuize(ActionEvent actionEvent) {//вивід результатів
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI/fxmls/statistics.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();//show quize window
        Scene scene = new Scene(root,loginController.currentScene.getWidth(),loginController.currentScene.getHeight());
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/icon.png"));
        stage.sizeToScene();
        statScene = scene;

        AnchorPane anchorPane = (AnchorPane)scene.lookup("#anchor");
        anchorPane.getChildren().clear();
        Chart chart = generateChart("Novice");
        chart.setMinWidth(anchorPane.getWidth());
        chart.setMinHeight(anchorPane.getHeight());
        chart.setMaxWidth(anchorPane.getWidth());
        chart.setMaxHeight(anchorPane.getHeight());
        chart.setTitle(User.quize.getProgrammerLevels().get("Novice").getTitle());
        chart.setMinSize(anchorPane.getWidth(),anchorPane.getHeight());
        anchorPane.getChildren().addAll(chart);
        Text text = (Text)scene.lookup("#mark");
        text.setText(" "+summary+" ");
        statisticController.Show("#anchor","Novice","#mark","#summaryAssesment");
        stage.setMaximized(true);
        stage.show();

    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    void startAgain(String level){//розпочати тестування заново
        restoreNextButton();
        ((Button)currentScene.lookup("#next")).setOnAction(event -> {ChangeQuestion(null);});
        User.currentLevel = level;
        User.currentQuestion = 0;
        ((Button)quizeWindowController.currentScene.lookup("#next")).fire();
    }
    //проходження заново окремих рівнів
    public void noviceAction(ActionEvent actionEvent) {
        startAgain("Novice");
    }

    public void beginnerAction(ActionEvent actionEvent) {
        startAgain("Advanced beginner");
    }

    public void competentAction(ActionEvent actionEvent) {
        startAgain("Competent");
    }

    public void proficientAction(ActionEvent actionEvent) {
        startAgain("Proficient");
    }

    public void expertAction(ActionEvent actionEvent) {
        startAgain("Expert");
    }
    public static void restLevels()//попередження про не повне проходження опитування
    {
        Label text = (Label) currentScene.lookup("#restLevels");
        String t = "Вам залишилось завершити рівні : ";
        boolean exist = false;
        for (Map.Entry<String,Level> level:User.quize.getProgrammerLevels().entrySet())
        {
            for(Question question: level.getValue().getQuestions()) {
                if (question.getUserAnswer() == 0) {
                    t += level.getKey() + ", ";
                    exist = true;
                    break;
                }
            }
        }
        t = t.substring(0,t.length()-2);
        if(!exist){
            t = "Вітаємо! Ви відповіли на всі питання! Натисніть Управління->Завершити тестування";
            ((Label)currentScene.lookup("#restLevels")).setStyle("-fx-fill: BLACK;-fx-font-size: 25px;");
        }
        text.setText(t);
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

    public void About(ActionEvent actionEvent) {//довідка
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

    public void AsAdmin(ActionEvent actionEvent) {//зайти як адмін для редагування вмісту опитування
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
        stage.show();
    }

    public void changeUser(ActionEvent actionEvent) {//на початкове вікно
        loginController.currentStage.setScene(loginController.currentScene);
        loginController.currentStage.setTitle("Авторизація");
    }
}


