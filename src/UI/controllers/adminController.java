package UI.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import structure.Level;
import structure.Question;
import structure.Quize;
import structure.User;

import javax.swing.*;
import java.util.*;

/**
 * Created by Volodymyr Dudas on 01.03.2016.
 */
public class adminController {
    public PasswordField passwordField;
    public static TreeTableView treeViewTable;
    public AnchorPane toolBoxBottom;
    public AnchorPane right;
    public AnchorPane verification;
    public TextArea titleTextArea;
    public static TextArea titleTextArea1;
    public GridPane answersGridPane;
    public static GridPane answersGridPane1;
    public TextField answerNumb;
    public BorderPane treePane;
    public AnchorPane treeAnchorPane;
    public static AnchorPane treeAnchorPane1;
    private String password = "Vova";
    public Label error;
    private static Question currentQuestion;
    private static Level  currentLevel;
    private static Quize quizeCopy;
    public static boolean changed = false;
    private static int questionN = 1;
    public static void Save(){
        SaveAnswers();
    changed = false;
    User.quize = quizeCopy;
    User.Serialize(User.quize,User.fileNameBinary);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText("Зміни успішно збережно!");
        alert.showAndWait();
    }

    public void SaveAllChanges(ActionEvent actionEvent) {
        currentQuestion.setTitle(titleTextArea.getText());
        Save();
    }

    public void removeQuestion(ActionEvent actionEvent) {
        rmQ();
    }

    public void addQuestion(ActionEvent actionEvent) {
        addQ();
    }

    private static void addQ()//додаємо питання
    {
        currentLevel.getQuestions().add(questionN,new Question());
        updateTreeView();
        User.Streamline(currentLevel);//впорядкування нумерації
    }
    private static void rmQ()//видалити питання
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Видалення");
        alert.setHeaderText(null);
        alert.setContentText("Ви дійсно бажаєте видалити це питання?");
        ButtonType buttonTypeOne = new ButtonType("Так");
        ButtonType buttonTypeTwo = new ButtonType("Ні");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Optional<ButtonType> result  = alert.showAndWait();
        if(result.get() == buttonTypeOne )
        {
            currentLevel.getQuestions().remove(questionN-1);
            User.Streamline(currentLevel);
            updateTreeView();
        }
    }

    public static class uiElements{
        public TextField textField;
        public Slider slider;

        public uiElements(TextField textField, Slider slider) {
            this.textField = textField;
            this.slider = slider;
        }
    }

    private static List<uiElements> elementsList = new ArrayList<>();

    public void Reset(ActionEvent actionEvent) {
        User.restoreOriginal(User.fileNameBinary);
        quizeCopy = new Quize(User.quize);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вітаємо");
        alert.setHeaderText(null);
        alert.setContentText("Відновлення пройшло успішно!");
        alert.showAndWait();

        updateTreeView();
        currentQuestion = User.getQuestionI(1,1,quizeCopy);
        currentLevel = User.getLevelI(1,quizeCopy);
        updateGrid(currentQuestion);
        changed = false;
    }
    private static ContextMenu addMenu = new ContextMenu();
    static{
        MenuItem addMenuItem = new MenuItem("Додати питання");
        MenuItem addMenuItem1 = new MenuItem("Видалити питання");
        addMenu.getItems().addAll(addMenuItem,addMenuItem1);
        addMenuItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                addQ();
            }
        });
        addMenuItem1.setOnAction(new EventHandler() {
            public void handle(Event t) {
                rmQ();
            }
        });
    }

   public void resetError(Event event) {
        error.setText("");
    }

    public void removeAnswer(ActionEvent actionEvent) {
        try {
            changed = true;
            currentQuestion.setTitle(titleTextArea.getText());
            SaveAnswers();
            int answerN = Integer.parseInt(answerNumb.getText().toString());
            if(answerN>0 && answerN<=currentQuestion.getAnswers().size())
            {
                Iterator<Map.Entry<String,Integer>> iter = currentQuestion.getAnswers().entrySet().iterator();
                Map.Entry<String,Integer> item = iter.next();//перший елемент
                int i = 1;
                while (i!=answerN)
                {
                    item = iter.next();i++;
                }

                currentQuestion.getAnswers().remove(item.getKey(),item.getValue());//видалили
                updateGrid(currentQuestion);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Індекс знаходиться за допустимими межами. Він повинен бути від 1 до "+currentQuestion.getAnswers().size());
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Не правильні дані у полі із номером! Повинно бути ціле число від 1 до "+currentQuestion.getAnswers().size());
            alert.showAndWait();
        }
    }

    public void addNewAnswer(ActionEvent actionEvent) {//додаємо в кінець
        changed = true;
        currentQuestion.setTitle(titleTextArea.getText());
        SaveAnswers();
        if(currentQuestion.getAnswers().size()<5)
        {
            currentQuestion.getAnswers().put("",1);
            updateGrid(currentQuestion);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Максимальна кількість відповідей - 5");
            alert.showAndWait();
        }
    }

    public void enterNumberIntoTextField(Event event) {
    }

    public void checkPassword(ActionEvent actionEvent) {
        treeAnchorPane1 = treeAnchorPane;
        titleTextArea1 = titleTextArea;
        answersGridPane1 = answersGridPane;
        if(!passwordField.textProperty().getValue().equals(password))
        {
            error.setText("Не правильний пароль. Знайдіть адміна або власника.");
            error.setStyle("-fx-text-fill:red;");
            right.setDisable(true);
            toolBoxBottom.setDisable(true);
            treePane.setDisable(true);
            verification.setDisable(false);
            answersGridPane.setVisible(false);
        }
        else{
            error.setText("Все вірно! Приємної роботи");
            error.setStyle("-fx-text-fill:black;");
            right.setDisable(false);
            toolBoxBottom.setDisable(false);
            verification.setDisable(true);
            treePane.setDisable(false);
            answersGridPane.setVisible(true);
            this.quizeCopy = new Quize(User.quize);
            updateTreeView();
            currentQuestion = User.getQuestionI(1,1,quizeCopy);
            currentLevel = User.getLevelI(1,quizeCopy);
            questionN = 1;
            updateGrid(currentQuestion);
        }

    }
    private static void updateTreeView()
    {
        treeViewTable = new TreeTableView();
        TreeTableColumn<String,String> treeTableColumn = new TreeTableColumn<>();
        treeTableColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<String, String> p)->{return new ReadOnlyStringWrapper(p.getValue().getValue());});

        treeViewTable.setRoot(getInfo());
        treeViewTable.setEditable(true);
        treeViewTable.setContextMenu(addMenu);
        treeViewTable.getColumns().add(treeTableColumn);
        treeViewTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeViewTable.setShowRoot(true);
        treeViewTable.getSelectionModel().selectedItemProperty()
                .addListener((v,oldValue,newValue)->{
                    currentQuestion.setTitle(titleTextArea1.getText());
                    SaveAnswers();
                    if(newValue!=null)
                    {
                        TreeItem<String> temp = ((TreeItem<String>)newValue);
                        if(temp.isLeaf())//Question
                        {
                            int x = 1,y=1;
                            TreeItem<String> temp1 = temp;
                            while(temp1.previousSibling()!=null)
                            {
                                temp1 = temp1.previousSibling();
                                x++;
                            }
                            temp1 = temp.getParent();
                            while(temp1.previousSibling()!=null)
                            {
                                temp1 = temp1.previousSibling();
                                y++;
                            }
                            SaveAnswers();
                            currentQuestion = User.getQuestionI(y,x,quizeCopy);
                            currentLevel = User.getLevelI(y,quizeCopy);
                            questionN = x;
                            if(currentQuestion!=null)
                            {
                                updateGrid(currentQuestion);
                            }
                        }
                    }
                });
        treeAnchorPane1.getChildren().clear();
        treeViewTable.setMaxSize(treeAnchorPane1.getWidth(),treeAnchorPane1.getHeight());
        treeViewTable.setMinSize(treeAnchorPane1.getWidth(),treeAnchorPane1.getHeight());

        treeViewTable.setVisible(true);
        treeAnchorPane1.getChildren().add(treeViewTable);
    }

    private static void SaveAnswers()
    {
        currentQuestion.getAnswers().clear();
        for(uiElements elem:elementsList)
        {
        currentQuestion.getAnswers().put(elem.textField.getText(),(int)elem.slider.getValue());
        }
    }
    private static void updateGrid(Question question)
    {
        elementsList.clear();
        titleTextArea1.setText(currentQuestion.getTitle());
        answersGridPane1.getChildren().clear();
        int i = 0;
        for(Map.Entry<String,Integer> answer:question.getAnswers().entrySet())
        {
            Label numb = new Label();
            numb.setText(String.valueOf(i+1));
            TextField answerText = new TextField();

            Slider slider =  new Slider();
            slider.setMin(1);
            slider.setMax(5);
            Label label = new Label();
            label.setText("1");
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov,
                                    Number old_val, Number new_val) {
                    changed = true;
                    label.setText(String.valueOf(new_val.intValue()));
                }
            });
            slider.setValue(answer.getValue());

            slider.setId("slider"+i);
            label.setId("label"+i);
            answerText.setId("answer"+i);
            answerText.setOnKeyTyped(event -> {
                changed = true;
            });

            GridPane.setConstraints(numb, 0, i,1,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,Priority.NEVER, Insets.EMPTY);
            GridPane.setConstraints(answerText, 1, i,1,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,Priority.NEVER, Insets.EMPTY);
            GridPane.setConstraints(slider, 2, i,1,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,Priority.NEVER, Insets.EMPTY);
            GridPane.setConstraints(label, 3, i,1,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS,Priority.NEVER, Insets.EMPTY);
            answerText.setText(answer.getKey());

            answerText.setAlignment(Pos.CENTER);
            answersGridPane1.getChildren().addAll(numb,answerText,slider,label);
            elementsList.add(new uiElements(answerText,slider));
            i++;
        }
    }
    private static TreeItem<String> getInfo()
    {
        ImageView rootIcon = new ImageView(
                new Image("UI/fxmls/survey.png")
        );
        rootIcon.setFitHeight(20);
        rootIcon.setFitWidth(20);
        final TreeItem<String> root = new TreeItem<>("Опитування",rootIcon);
        root.setExpanded(true);
        for(Map.Entry<String, Level> instance:quizeCopy.getProgrammerLevels().entrySet())
        {
            ImageView progIcon = new ImageView(
                    new Image("UI/fxmls/programmer.png")
            );
            progIcon.setFitHeight(20);
            progIcon.setFitWidth(20);
            TreeItem<String> children = new TreeItem<>(instance.getValue().getTitle(),progIcon);
            children.setExpanded(true);
            for(Question question: instance.getValue().getQuestions())
            {
                ImageView qIcon = new ImageView(
                        new Image("UI/fxmls/question.png")
                );
                qIcon.setFitHeight(20);
                qIcon.setFitWidth(20);
                TreeItem<String> q =  new TreeItem<>(question.getTitle(),qIcon);
                children.getChildren().addAll(q);
            }
            root.getChildren().addAll(children);
        }
        return root;
    }



}


