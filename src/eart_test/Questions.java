package eart_test;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Created by Sina Rezaeizadeh on 2017-11-18.
 *
 */
public class Questions {
    private Stage stage;

    private ArrayList<File> listOfQuestions = new ArrayList<>();
    private Map<File, Integer> answersToNum = new HashMap<>();
    private int tokens = 3;
    private File answer;
    private Double replayPoint;

    private boolean isRightAns = false;

    private MediaPlayer mp;
    private MediaView mv;
r
    Questions(ArrayList<File> files) {
        if (files != null && !files.isEmpty()) {
            listOfQuestions.addAll(files);
            for (File listOfQuestion : listOfQuestions) {
                answersToNum.put(listOfQuestion, 0);
            }
            stage = new Stage();
            stage.setTitle("Test");
            stage.initModality(Modality.APPLICATION_MODAL);
            startTest();
        }

    }

    private void startTest() {


        // Scroll pane is awesome!
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(575, 400);


        // GridPane used for the Vbox.
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        gridPane.setVgap(10);
        // gridPane.setGridLinesVisible(true);

        // A Text info and the a tex for tokens.
        HBox chooseBox = new HBox(10);
        chooseBox.setAlignment(Pos.CENTER);
        HBox tokenBox = new HBox(10);
        tokenBox.setAlignment(Pos.TOP_RIGHT);
        Text chooseText = new Text("Choose your answer:");
        chooseBox.getChildren().add(chooseText);
        Text tokenText = new Text("# of Tokens: " + Integer.toString(tokens));
        tokenBox.getChildren().add(tokenText);
        gridPane.add(chooseBox, 0, 0);
        gridPane.add(tokenBox, 1, 0);

        tokenText.setId("tokenText");
        chooseText.setId("chooseText");

        // The vertical box used for the RadioButtons.
        VBox radioBox = new VBox(10);
        radioBox.setAlignment(Pos.CENTER_LEFT);
        scrollPane.setContent(radioBox);
        gridPane.add(scrollPane, 0, 1, 2, 1);

        // The HBox for two buttons.
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);
        Button submitBtn = new Button("Submit");
        Button replayBtn = new Button("Replay");
        buttonBox.getChildren().add(submitBtn);
        buttonBox.getChildren().add(replayBtn);
        gridPane.add(buttonBox, 0, 4);

        // The HBox for one button: next:
        GridPane buttonBox2 = new GridPane();
        buttonBox2.setAlignment(Pos.BOTTOM_RIGHT);
        Button nextBtn = new Button("Next");
        buttonBox2.getChildren().add(nextBtn);
        gridPane.add(buttonBox2, 1, 4);

        // Group of RadioButtons:
        ToggleGroup group = new ToggleGroup();

        for (File f: listOfQuestions) {
            RadioButton rb = new RadioButton(f.getName());
            rb.setToggleGroup(group);
            radioBox.getChildren().add(rb);
        }

        shuffleQuestions();

        Random random = new Random();
        random.nextInt();
        int randFileind = random.nextInt(listOfQuestions.size());

        answer = listOfQuestions.get(randFileind);


        // Let's See how this goes:
        playMedia();

        // Lable for showing the right or wrong answer:
        Label showAnswer = new Label();
        ScrollPane answerScroll = new ScrollPane();
        answerScroll.setContent(showAnswer);
        answerScroll.setPrefSize(200, 100);
        gridPane.add(answerScroll, 0, 7, 2, 1);
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                if (selected != null) {
                    if (answer.getName().equals(selected.getText())) {
                        showAnswer.setText("Correct: \n" + answer.getName());
                        showAnswer.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
                        showAnswer.setStyle("-fx-text-fill: darkgreen");
                        isRightAns = true;
                    } else {
                        showAnswer.setText("Try again!");
                        showAnswer.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
                        showAnswer.setStyle("-fx-text-fill: orangered");
                        isRightAns = false;

                        tokens --;
                        if (tokens >= 0) {
                            tokenText.setText("# of Tokens: " + tokens);
                        } else {
                            showAnswer.setText("Correct Ans. is: \n" + answer.getName());
                            showAnswer.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
                            showAnswer.setStyle("-fx-text-fill: orange");
                        }

                    }
                } else {
                    chooseText.setText("CHOOSE ONE!!!!");
                }

            }
        });

        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isRightAns) {
                    tokens = 3;
                    tokenText.setText("# of Tokens: " + tokens);


                    mp.dispose();
                    showAnswer.setText("");

                    random.nextInt();
                    int randFileind = random.nextInt(listOfQuestions.size());
                    answer = listOfQuestions.get(randFileind);
                    playMedia();
                    isRightAns = false;
                } else {
                    showAnswer.setText("You should answer this question first!");
                    showAnswer.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
                    showAnswer.setStyle("-fx-text-fill: orangered");
                }

            }
        });

        replayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tokens > 0) {
                    mp.seek(Duration.seconds(replayPoint));
                    tokens --;
                    tokenText.setText("# of Tokens: " + tokens);
                }

            }
        });


        mv = new MediaView(mp);
        gridPane.add(mv, 0, 3);
        Scene scene = new Scene(gridPane, 600, 600);
        scene.getStylesheets().add(
                Questions.class.getResource("Questions.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mp.dispose();
            }
        });
    }
    private void playMedia() {
        Media media = new Media(answer.toURI().toString());

        mp = new MediaPlayer(media);
        mp.setAutoPlay(true);


        mp.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                Double sec = mp.getMedia().getDuration().toSeconds();
                Double interval = floor(sec / 25);

                Random random = new Random();
                random.nextInt();
                int randomPoint = random.nextInt(25);

                Double result = interval * randomPoint;
                replayPoint = result;

                if (Duration.seconds(result).add(Duration.seconds(25)).toSeconds() < sec) {
                    mp.setStopTime(Duration.seconds(result).add(Duration.seconds(25)));
                }


                mp.seek(Duration.seconds(result));
            }
        });
    }
    private void replayMedia() {

    }

    private void shuffleQuestions() {
        Random random = new Random();
        random.nextInt();

        int n = listOfQuestions.size();
        for (int i = 0; i < n; i ++) {
            int j = i + random.nextInt(n - i);
            File f_j = listOfQuestions.get(j);
            listOfQuestions.set(j, listOfQuestions.get(i));
            listOfQuestions.set(i, f_j);
        }
    }


}
