package structure;

        import java.io.Serializable;
        import java.util.LinkedHashMap;

/**
 * Created by Volodymyr Dudas on 16.02.2016.
 */
public class Question implements Serializable {
    private int number;     //номер питання
    private String title;   //саме питання
    private LinkedHashMap<String,Integer> answers; //відповіді
    private int userAnswer;                        //відповідь користувача
    private boolean answered = false;              //чи відповідь було здійснено?

    public Question(Question question) {
     this.number = question.number;
        this.title = question.title;
        this.answers = question.answers;
    }
    public Question()
    {
        answers = new LinkedHashMap<>();
        title = "Нове питання";
        answers.put("Відповідь",1);
    }
    public Question(int number, String title, LinkedHashMap<String, Integer> answers) {
        this.answers = new LinkedHashMap<>();
        this.number = number;
        this.title = title;
        this.answers = answers;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedHashMap<String, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(LinkedHashMap<String, Integer> answers) {
        this.answers = answers;
    }

    public int getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}