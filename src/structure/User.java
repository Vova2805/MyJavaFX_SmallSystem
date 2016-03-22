package structure;

import UI.Login;
import UI.controllers.levelController;
import UI.controllers.loginController;
import UI.controllers.quizeWindowController;
import UI.controllers.statisticController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Created by Volodymyr Dudas on 16.02.2016.
 */
public  class User {
    public static String name;
    public static Quize quize;
    public static String currentLevel;
    public static int currentQuestion;
    private static Question question;
    public static String fileNameBinary = "quize.out";
    private static ListIterator<Map.Entry<String, Level>> righReverseIterator; //двонапрямлений ітератор
    static {
        quize = Deserialize(fileNameBinary,false);

        currentQuestion = 0;
        righReverseIterator = new ArrayList<>(quize.getProgrammerLevels().entrySet()).
                listIterator();
        if(righReverseIterator.hasNext())
        {
            Map.Entry<String,Level> level = righReverseIterator.next();
            currentLevel = level.getKey();
        }
    }

    public static void setName(String Name,String id)
    {
        name = Name;
        Label name = (Label) quizeWindowController.currentScene.lookup(id);
        name.setText(Name);
    }

    public static void PreviousQuestion(String idTitle, String idHbox) // перехід до попереднього питання і вивід
    {
        if(liter.hasPrevious() && currentQuestion!=1) //якщо є попереднє питання і рівень не початковий
        {
            question = liter.previous();
            if(currentQuestion == question.getNumber())question = liter.previous();
            Fill(idTitle,idHbox,question);
            currentQuestion = question.getNumber();
        }
        else
        {
            if(righReverseIterator.hasPrevious() && !currentLevel.equals("Novice"))
            {
                Map.Entry<String,Level> level = righReverseIterator.previous();
                if(level.getKey().equals(currentLevel))level = righReverseIterator.previous();
                currentQuestion=level.getValue().getQuestions().size();
                currentLevel = level.getKey();
                liter = quize.getProgrammerLevels().get(currentLevel).getQuestions().listIterator(currentQuestion);
                Level level1 = quize.getProgrammerLevels().get(currentLevel);
                loginController.currentStage.setTitle(level1.getTitle());
                ((Label)quizeWindowController.currentScene.lookup("#level_title")).setText(level1.getTitle());
                currentQuestion++;
                PreviousQuestion(idTitle, idHbox);
            }
        }
        if(currentQuestion==1 && currentLevel.equals("Novice")){
            ((Button)quizeWindowController.currentScene.lookup("#previous")).setDisable(true);
        }
    }
    private static void ResetMarks(String level)//скинути усі відповіді юзера
    {
        Iterator<Question> iterator = quize.getProgrammerLevels().get(level).getQuestions().iterator();
        while (iterator.hasNext())
        {
            Question question = iterator.next();
            question.setAnswered(false);
            question.setUserAnswer(0);
        }

    }
    public static void ResetLevel(String idTitle, String idHbox)//скинути відповіді у поточному рівні
    {
        ResetMarks(currentLevel);
        for (Question question: quize.getProgrammerLevels().get(currentLevel).getQuestions())
        {
            Fill(idTitle,idHbox,question);
            question.setUserAnswer(0);
            question.setAnswered(false);
        }
        liter = quize.getProgrammerLevels().get(currentLevel).getQuestions().listIterator(0);
        Question question = liter.next();
        Fill(idTitle,idHbox,question);
        quizeWindowController.restLevels();
        currentQuestion = question.getNumber();
    }
    public static void ResetQuize(String idTitle, String idHbox)//розпочати опитування заново
    {
        for(Map.Entry<String,Level> item: quize.getProgrammerLevels().entrySet())
        {
            ResetMarks(item.getKey());
        }
        righReverseIterator = new ArrayList<>(quize.getProgrammerLevels().entrySet()).listIterator(0);
        currentLevel = righReverseIterator.next().getKey();
        loginController.currentStage.setTitle(quize.getProgrammerLevels().get(currentLevel).getTitle());
        ResetLevel(idTitle,idHbox);
    }
    private static ListIterator<Question> liter;
    public static void NextQuestion(String idTitle, String idHbox)//перехід до наступного питання
    {
        ((Button)quizeWindowController.currentScene.lookup("#previous")).setDisable(false);
        if(currentQuestion==0)
        {
            liter = quize.getProgrammerLevels().get(currentLevel).getQuestions().listIterator();
            Level level = quize.getProgrammerLevels().get(currentLevel);
            loginController.currentStage.setTitle(level.getTitle().replace('\n',' '));
            ((Label)quizeWindowController.currentScene.lookup("#level_title")).setText(level.getTitle().replace('\n',' '));
        }
        if(liter.hasNext() && currentQuestion!=quize.getProgrammerLevels().get(currentLevel).getQuestions().size())
        {
            question = liter.next();
            if(currentQuestion == question.getNumber()) question = liter.next();
            Fill(idTitle,idHbox,question);
            currentQuestion = question.getNumber();
        }
        else
        {
            currentQuestion = 0;
            if(righReverseIterator.hasNext() && !currentLevel.equals("Expert"))
            {
                Map.Entry<String,Level> level = righReverseIterator.next();
                if(currentLevel.equals(level.getKey()))level = righReverseIterator.next();
                currentLevel = level.getKey();
                NextQuestion(idTitle, idHbox);

                Image image = new Image(quize.getProgrammerLevels().get(currentLevel).getImagePath());
                ImageView imageView = (ImageView) levelController.currentScene.lookup("#imageView");
                imageView.setImage(image);

                Text description = (Text)levelController.currentScene.lookup("#description");
                description.setText(User.quize.getProgrammerLevels().get(currentLevel).getDetailedInfo());

                Label title = (Label)levelController.currentScene.lookup("#title");
                String t = User.quize.getProgrammerLevels().get(currentLevel).getTitle();
                if(t.length()<20)t = t.replace('\n',' ');
                title.setText(t);
                ((Label)quizeWindowController.currentScene.lookup("#level_title")).setText(t);
                loginController.currentStage.setScene(levelController.currentScene);
            }

        }
        if(!liter.hasNext() && currentLevel.equals("Expert")){

            ((Button)quizeWindowController.currentScene.lookup("#next")).setText("Завершити тестування");
            ((Label)quizeWindowController.currentScene.lookup("#restLevels")).setStyle("" +
                    "-fx-fill: FIREBRICK;-fx-font-size: 25px;" +
                    "  -fx-font-weight: bold;\n" +
                    "  -fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
            ((Button)quizeWindowController.currentScene.lookup("#next")).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("/UI/fxmls/statistics.fxml"));
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
                    quizeWindowController.statScene = scene;
                    ((Button)quizeWindowController.currentScene.lookup("#next")).setText("Вихід");
                    ((Button)quizeWindowController.currentScene.lookup("#next")).setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.exit(0);
                        }
                    });
                    statisticController.Show("#anchor","Novice","#mark","#summaryAssesment");
                    stage.setMaximized(true);
                    stage.show();

                }
            });
        }
        else{
            ((Label)quizeWindowController.currentScene.lookup("#restLevels")).setStyle("" +
                    "-fx-fill: BLACK;-fx-font-size: 25px;");
        }
    }

    private static void Fill(String idTitle, String idHbox,Question question)//вивід питання із варіантами відповідей
    {
        Label qTitle = (Label) quizeWindowController.currentScene.lookup(idTitle);
        qTitle.setText(question.getNumber()+". "+question.getTitle());
        HBox box = (HBox)quizeWindowController.currentScene.lookup(idHbox);
        box.getChildren().clear();
        ToggleGroup toggleGroup = new ToggleGroup();
        for (Map.Entry<String, Integer> item:question.getAnswers().entrySet())
        {
            RadioButton radioButton = new RadioButton();
            radioButton.setText(item.getKey());
            if(question.getUserAnswer() == item.getValue() && question.isAnswered()) {
                radioButton.requestFocus();
                radioButton.setSelected(true);
            }
            else radioButton.setSelected(false);
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setOnAction(event -> {
                question.setUserAnswer(item.getValue()) ;
                question.setAnswered(true);
                quizeWindowController.restLevels();
            });
            box.getChildren().addAll(radioButton);
        }
    }
    public static String getLevelInfo()
    {
        String levelInfo = "";
        for(Map.Entry<String,Level> level:quize.getProgrammerLevels().entrySet())
        {
            levelInfo+="\t\t"+ level.getValue().getTitle();
            levelInfo+=" \n "+level.getValue().getDetailedInfo()+" \n\n\n";
        }
        return levelInfo;
    }


    public static  void Serialize(Quize quizeLocal, String filePath)//сереалізація у бінарний формат
    {
        try{
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(quizeLocal);
            oos.flush();
            oos.close();
        }catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Відбулася помилка при серіалізації. Зміни не збережено! Спробуйте знову");
            alert.showAndWait();
        }
    }

    public static Quize Deserialize(String filePath,boolean again)
    {
        try {
            File file = new File(filePath);
            if(!file.exists())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Помилки при читанні даних із файлу "+filePath+". Тестування буде відновлено до початково версії!");
                alert.showAndWait();
                restoreOriginal(filePath);
            }
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fis);
            return (Quize) oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Помилки при читанні даних із файлу "+filePath+". Тестування буде відновлено до початково версії!");
            alert.showAndWait();
            restoreOriginal(filePath);
            if(!again)
            Deserialize(filePath,true);
        }
        return null;
    }
    public static void restoreOriginal(String filePath)//Відновлення опитування
    {
        quize = new Quize();
        //Novice
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question(1,"Переживаєте за успіх в роботі?", new LinkedHashMap<String, Integer>(){{put("сильно", 5);put("не дуже", 3);put("спокійний", 2);}}));
        questions.add(new Question(2,"Прагнете досягти швидко\n результату ?", new LinkedHashMap<String, Integer>(){{put("поступово", 2);put("якомога швидше", 3);put("дуже", 5);}}));
        questions.add(new Question(3,"Легко попадаєте в тупик \nпри проблемах в роботі ?", new LinkedHashMap<String, Integer>(){{put("неодмінно", 5);put("поступово", 3);put("зрідка", 2);}}));
        questions.add(new Question(4,"Чи потрібен чіткий алгоритм\n для вирішення задач?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("в окремих випадках", 3);put("не потрібен", 2);}}));
        quize.getProgrammerLevels().put("Novice",new Level("Новачок (Novice)",
                "Новачки дуже переживають за свою успішність; їх досвіду замало, щоб повести їх у правильному напрямку і вони не знають чи їх вчинки будуть правильними. Новачки зазвичай не хочуть вчитися, зате хочуть досягти миттєвого результату. Вони не знають як реагувати на помилки і тому легко збиваються з пантелику, коли щось іде “не так”. Зате вони можуть бути досить ефективними, коли їм дати набір контекстно незалежних правил у формі “у випадку ХХХ, роби УУУ”. Іншими словами їм необхідний рецепт або алгоритм.",
                questions, "images/novice.jpg"));
        //Advanced beginner
        questions = new ArrayList<>();
        questions.add(new Question(1,"Чи використовуєте власний \nдосвід при вирішенні задач?", new LinkedHashMap<String, Integer>(){{put("зрідка", 5);put("частково", 3);put("ні", 2);}}));
        questions.add(new Question(2,"Чи користуєтесь фіксованими\n правилами  для вирішення задач?", new LinkedHashMap<String, Integer>(){{put("так", 2);put("в окремих випадках", 3);put("не потрібні", 5);}}));
        questions.add(new Question(3,"Чи відчуваєте ви загальний\n контекст вирішення задачі?", new LinkedHashMap<String, Integer>(){{put("так", 2);put("частково", 3);put("в окремих випадках", 5);}}));
        quize.getProgrammerLevels().put("Advanced beginner",new Level("Твердий початківець\n(Advanced beginner)",
                "Тверді початківці починають вже потроху відступати від фіксованих правил. Вони можуть спробувати якісь задачі самостійно, але у них все ще є труднощі із усуненням проблем, які виникають. Початківці можуть скористатись порадами в правильному контексті, врахувавши свій досвід подібних ситуацій, але ледь-ледь. І хоч вони вже починають формулювати якісь загальні принципи, вони все ще не бачать “всієї картини”. Якщо спробувати надати їм ширший контекст – вони відмахнуться від нього як від недоречного. ",
                questions, "images/advancedBeginner.jpg"));
        //Competent
        questions = new ArrayList<>();
        questions.add(new Question(1,"Чи можете ви побудувати\n модель вирішуваної задачі?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("не повністю", 3);put("в окремих випадках", 2);}}));
        questions.add(new Question(2,"Чи вистачає вам ініціативи\n при вирішенні задач?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("зрідка", 3);put("потрібне натхнення", 2);}}));
        questions.add(new Question(3,"Чи можете вирішувати проблеми,\n з якими ще не стикались?", new LinkedHashMap<String, Integer>(){{put("так", 2);put("в окремих випадках", 3);put("ні", 5);}}));
        quize.getProgrammerLevels().put("Competent",new Level("Компетентний \n(Competent)",
                "Компетентні будують правильні моделі проблемної області та ефективно нею користуються. Здатні усувати проблеми з якими раніше не стикались. Про людей на цьому рівні часто кажуть, що вони “мають ініціативу”. Вони можуть вчити новачків і не задовбують експертів. Щоправда їм ще бракує досвіду аби вдало розставити пріоритети при рішенні задач. Власне кажучи, саме з цього рівня людину можна вже назвати інженером – компетентні вирішують задачі, а не працюють за алгоритмом. ",
                questions, "images/competent.jpg"));
        //Proficient
        questions = new ArrayList<>();
        questions.add(new Question(1,"Чи  необхідний вам весь\n контекст задачі ?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("в окремих деталях", 3);put("в загальному", 2);}}));
        questions.add(new Question(2,"Чи переглядаєте ви свої\n наміри до вирішення задачі?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("зрідка", 3);put("коли є потреба", 2);}}));
        questions.add(new Question(3,"Чи здатні  ви  навчатись у інших ?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("зрідка", 3);put("коли є потреба", 2);}}));
        quize.getProgrammerLevels().put("Proficient",new Level("Досвідчений\n (Proficient)",
                " Досвідченим необхідна “повна картина” проблемної області, адже вони хочуть розуміти весь концепт. Вони роблять значний прорив в рамках моделі братів Дрейфус, адже постійно оцінюють виконану роботу і переглядають свої підходи, аби наступного разу бути ще ефективнішими. Вони також можуть навчатись використовуючи чужий досвід. І найголовніше – вони завжди беруть до уваги контекст задачі. Якщо повернутись до програмування, то чудовий приклад ілюстрації – це використання патернів проектування. Лише досвідчені використовують їх виключно там де треба, а не бездумно і повсюдно, бо це круто і модно. ",
                questions, "images/proficient.jpg"));
        //Expert
        questions = new ArrayList<>();
        questions.add(new Question(1,"Чи обираєте ви нові\n методи своєї роботи?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("вибірково", 3);put("вистачає досвіду", 2);}}));
        questions.add(new Question(2,"Чи допомагає власна\n інтуїція при вирішенні задач?", new LinkedHashMap<String, Integer>(){{put("так", 5);put("частково", 3);put("при емоційному напруженні", 2);}}));
        questions.add(new Question(3,"Чи застовуєте рішення\n задач за аналогією", new LinkedHashMap<String, Integer>(){{put("часто", 5);put("зрідка", 3);put("тільки власний варіант", 2);}}));
        quize.getProgrammerLevels().put("Expert",new Level("Експерт (Expert)",
                "Експерти – основне джерело знань та інформації в будь-якій сфері. Вони безперестану шукають все кращі і кращі методи роботи. Вони завжди застосовують весь свій велетенський багаж знань у правильному контексті. Вони пишуть книжки, статті та проводять семінари. Це сучасні чаклуни. Експерти керуються інтуїцією . Доктор Хаус, який з одного погляду на пацієнта (або взагалі його медичну картку) міг поставити діагноз – типовий приклад експерта. Експерти працюють за допомогою несвідомого “порівняння з взірцем” (“pattern matching”) по базі свого досвіду. От тільки проблема в тому, що функція “порівняння з взірцем” асинхронна і знаходиться в частині мозку, яка не підконтрольна свідомості. ",
                questions, "images/expert.jpg"));
        Serialize(quize,filePath);
    }

    public static void Streamline(Level current)//упорядкувати усі питання даного рівня
    {
        int i = 1;
        for(Question question: current.getQuestions())
        {
            question.setNumber(i++);
        }
    }
    public static Question getQuestionI(int level,int question,Quize quize1)//повертає питання за його номером у рівні
    {

        for(Map.Entry<String,Level> item: quize1.getProgrammerLevels().entrySet())
        {
            if(--level==0)
            {
                return item.getValue().getQuestions().get(question-1);
            }
        }
        return null;
    }
    public static Level getLevelI(int level,Quize quize1)//повертає номер рівня
    {

        for(Map.Entry<String,Level> item: quize1.getProgrammerLevels().entrySet())
        {
            if(--level==0)
            {
                return item.getValue();
            }
        }
        return null;
    }
}
