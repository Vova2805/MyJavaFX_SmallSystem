package UI.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import structure.Level;
import structure.User;

import java.util.Map;

/**
 * Created by Volodymyr Dudas on 16.02.2016.
 */
public class statisticController {
    public void ShowNoviceStat(ActionEvent actionEvent) {
        Show("#anchor","Novice","#mark","#summaryAssesment");
    }

    public void ShowAdvBegStat(ActionEvent actionEvent) {
        Show("#anchor","Advanced beginner","#mark","#summaryAssesment");
    }

    public void ShowCompStat(ActionEvent actionEvent) {
        Show("#anchor","Competent","#mark","#summaryAssesment");
    }

    public void ShowProfStat(ActionEvent actionEvent) {
        Show("#anchor","Proficient","#mark","#summaryAssesment");
    }

    public void ShowExpStat(ActionEvent actionEvent) {
        Show("#anchor","Expert","#mark","#summaryAssesment");
    }

    static boolean firstTime = true;
    public static void Show(String id,String level,String mark,String summary)
    {
        AnchorPane anchorPane = (AnchorPane)quizeWindowController.statScene.lookup(id);
        anchorPane.getChildren().clear();
        Chart chart = quizeWindowController.generateChart(level);
        chart.setMinWidth(anchorPane.getPrefWidth());
        chart.setMinHeight(anchorPane.getPrefHeight());
        chart.setMaxWidth( chart.getMinWidth());
        chart.setMaxHeight(chart.getMinHeight());
        chart.setTitle(User.quize.getProgrammerLevels().get(level).getTitle());
        anchorPane.getChildren().addAll(chart);
        Text text = (Text)quizeWindowController.statScene.lookup(mark);
        text.setText(" "+quizeWindowController.getSummary()+" ");
        Text text1 = (Text)quizeWindowController.statScene.lookup(summary);
        text1.setText("Сумарна оцінка ");
        if(firstTime)
        {
            quizeWindowController.statScene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    AnchorPane anchorPane = (AnchorPane)quizeWindowController.statScene.lookup("#imageAnchor");
                    ImageView imageView = (ImageView)quizeWindowController.statScene.lookup("#imageView");
                    imageView.setFitHeight(anchorPane.getHeight());
                    imageView.setFitWidth(anchorPane.getWidth());
                }
            });
            quizeWindowController.statScene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    AnchorPane anchorPane = (AnchorPane)quizeWindowController.statScene.lookup("#resultImageAnchor");
                    ImageView imageView = (ImageView)quizeWindowController.statScene.lookup("#imageView");
                    imageView.setFitHeight(anchorPane.getHeight());
                    imageView.setFitWidth(anchorPane.getWidth());
                }
            });

            AnchorPane anchorPane1 = (AnchorPane) quizeWindowController.statScene.lookup("#imageAnchor");
            ImageView imageView = new ImageView();
            imageView.setId("imageView");
            imageView.setFitHeight(anchorPane.getHeight());
            imageView.setFitWidth(anchorPane.getWidth());
            anchorPane1.getChildren().clear();
            anchorPane1.getChildren().add(imageView);


            firstTime = false;
        }
        Image image = new Image(User.quize.getProgrammerLevels().get(level).getImagePath());
        ImageView imageView = (ImageView)quizeWindowController.statScene.lookup("#imageView");
        imageView.setImage(image);
    }
    private static String winnerLevel = "Novice";
    public void ShowCommonStat(ActionEvent actionEvent) {
        AnchorPane anchorPane = (AnchorPane)quizeWindowController.statScene.lookup("#anchor");
        anchorPane.getChildren().clear();

        Chart chart = generateStackedChart();
        chart.setMinWidth(anchorPane.getWidth());
        chart.setMinHeight(anchorPane.getHeight());
        chart.setMaxWidth(anchorPane.getWidth());
        chart.setMaxHeight(anchorPane.getHeight());
        chart.setTitle("Зведений графік");
        anchorPane.getChildren().addAll(chart);

        Text text1 = (Text)quizeWindowController.statScene.lookup("#summaryAssesment");
        text1.setText("Вітаємо! Ваш рівень - "+User.quize.getProgrammerLevels().get(winnerLevel).getTitle().replace('\n',' ')+". ");
        Text text = (Text)quizeWindowController.statScene.lookup("#mark");
        text.setText(" Ви отримали :"+max+" б.");

        Image image = new Image(User.quize.getProgrammerLevels().get(winnerLevel).getImagePath());
        ImageView imageView = (ImageView)quizeWindowController.statScene.lookup("#imageView");
        imageView.setImage(image);
    }
    private static int max = 0;
    public static StackedBarChart generateStackedChart()
    {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Тип програміста");
        String []array = new String[User.quize.getProgrammerLevels().size()];
        int i=0;
        for(Map.Entry<String, Level> item:User.quize.getProgrammerLevels().entrySet())
        {
            array[i] = item.getKey();
            i++;
        }
        yAxis.setLabel("Сумарна оцінка");

        final StackedBarChart<String,Number> stackedBarChart = new StackedBarChart<String,Number>(xAxis,yAxis);

        int sum;
        boolean first = true;

        for(Map.Entry<String, Level> item:User.quize.getProgrammerLevels().entrySet())
        {
            sum=0;
            for (int j = 0; j < item.getValue().getQuestions().size(); j++) {
                sum+=item.getValue().getQuestions().get(j).getUserAnswer();
            }
            if(sum>max){
                winnerLevel = item.getKey();
                max = sum;
            }
        }

        int j = 0;

        while (true) {
            boolean existQuestion  = false;
            XYChart.Series<String,Number> series1 = new XYChart.Series();
            series1.setName("Питання № "+(j+1));
            i=0;//по рівнях
            for(Map.Entry<String, Level> item:User.quize.getProgrammerLevels().entrySet())
            {
                if(item.getValue().getQuestions().size()>j){
                    series1.getData().add(new XYChart.Data<String, Number>(array[i],item.getValue().getQuestions().get(j).getUserAnswer()));
                    existQuestion = true;
                }
                i++;
            }
            if(existQuestion)
            {
                stackedBarChart.getData().addAll(series1);
                j++;//по питаннях
            }else{
                break;
            }
        }
        return stackedBarChart;

    }

}
