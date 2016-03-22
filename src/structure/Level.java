package structure;

import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {
    private String title;   //Заголовок
    private String detailedInfo;// Опис
    private String imagePath;   //Шлях до картинки
    private ArrayList<Question> questions;//Набір питань

    public Level() {
        questions = new ArrayList<>();
    }

    public Level(String title, String detailedInfo, ArrayList<Question> questions, String image) {
        this.questions = new ArrayList<>();
        this.title = title;
        this.detailedInfo = detailedInfo;
        this.questions = questions;
        this.imagePath = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(String detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}