package eart_test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sina Rezaeizadeh on 2017-11-17.
 *
 */
public class CanYouGuessIt extends Application{

    private ArrayList<File> audFiles = new ArrayList<>();
    private ObservableList<String> audFilesName = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) throws Exception {

        // mainGrid contains a button testButton and the a list...
        GridPane mainGrid = new GridPane();
        //mainGrid.setGridLinesVisible(true); // TODO: Remove this
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(10);
        mainGrid.setVgap(40);
        mainGrid.setPadding(new Insets(25, 25, 25, 25));


        // Buttons: open, remove, help, start
        Button openDirBtn = new Button("Open Directory");
        Button openFileBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        Button helpBtn = new Button("Help");
        Button startBtn = new Button("Start");

        //Grid and HBox for the buttons open, remove, and help in the mainGrid.
        HBox hboxButtons = new HBox(40);
        hboxButtons.setAlignment(Pos.BOTTOM_CENTER);
        hboxButtons.getChildren().addAll(openDirBtn, openFileBtn, removeBtn, helpBtn);
        mainGrid.add(hboxButtons, 0, 1);

        //  testGrid contains buttons for adding/removing, and getting help.
        GridPane testGrid = new GridPane();
        //testGrid.setGridLinesVisible(true); // TODO: Remove this
        testGrid.setAlignment(Pos.CENTER);
        testGrid.setHgap(10);
        testGrid.setVgap(10);
        testGrid.setPadding(new Insets(10 ,10, 10, 10));

        // Label for the testGrid:
        HBox hboxLabel = new HBox(10);
        hboxLabel.setAlignment(Pos.CENTER);
        Label questionListTxt = new Label("List of Questions");
        hboxLabel.getChildren().add(questionListTxt);
        testGrid.add(hboxLabel, 0, 0);

        // Hbox for start button
        HBox hboxStartBtn = new HBox(10);
        hboxStartBtn.setAlignment(Pos.BOTTOM_CENTER);
        hboxStartBtn.getChildren().add(startBtn);
        testGrid.add(hboxStartBtn, 0, 2);
        hboxStartBtn.setId("start-button");

        // ListView for the files to choose from:
        ListView<String> list = new ListView<>();
        list.setPrefWidth(450);
        list.setPrefHeight(500);
        testGrid.add(list, 0, 1);


        mainGrid.add(testGrid, 0, 0);

        Scene scene = new Scene(mainGrid);
        // Adding CSS
        scene.getStylesheets().add(
                CanYouGuessIt.class.getResource("CanYouGuessIt.css").toExternalForm());
        primaryStage.setTitle("Can You Guess It!");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();


        openDirBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Open Test Folder");
                File selectedDirectory = chooser.showDialog(primaryStage);
                if (selectedDirectory != null) {
                    File[] files = selectedDirectory.listFiles();
                    assert files != null;
                    for (File file : files) {
                        if (file.isFile() && (getExtension(file).equalsIgnoreCase("mp3") ||
                                getExtension(file).equalsIgnoreCase("mp4") ||
                                getExtension(file).equalsIgnoreCase("wav"))) {
                            if (!audFiles.contains(file)) {
                                audFiles.add(file);
                                audFilesName.add(file.getName());
                            }
                        }
                    }
                    list.setItems(audFilesName);
                }
            }
        });
        openFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Music File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.mp4"),
                        new FileChooser.ExtensionFilter("MP4", "*.mp4"));
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null && selectedFile.isFile()) {
                    if (!audFiles.contains(selectedFile)) {
                        audFiles.add(selectedFile);
                        audFilesName.add(selectedFile.getName());
                        list.setItems(audFilesName);
                    }

                }

            }
        });

        removeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = list.getSelectionModel().getSelectedIndex();
                if (!audFiles.isEmpty() && index >= 0) {
                    audFiles.remove(index);
                    audFilesName.remove(index);
                    list.refresh();
                }

                }
        });

        list.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().getName().equalsIgnoreCase("DELETE") ||
                            event.getCode().getName().equalsIgnoreCase("Backspace")) {
                    if (!audFilesName.isEmpty()) {
                        int index = list.getSelectionModel().getSelectedIndex();
                        audFiles.remove(index);
                        audFilesName.remove(index);
                        list.refresh();
                    }

                }
            }
        });

        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (audFiles.isEmpty()) {
                    questionListTxt.setText("Please choose at least one audio file");
                    questionListTxt.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
                    questionListTxt.setStyle("-fx-text-fill: red");

                } else {
                    Questions questions = new Questions(audFiles);
                }

            }
        });

        helpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                helpBtn.setDisable(true);
                Stage helpStage = new Stage();
                helpStage.setTitle("Help");
                ScrollPane scrollPane = new ScrollPane();
                Text info = new Text(
                        "Hello, سلام \n" +
                                 "اول از همه شما راست می گویید!\n" +
                                "- Click on Open Folder to open bunch of music files at once\n" +
                                "- Click add to add an individual file, you can remove it by \n " +
                                "  pressing remove button or (DELETE key)\n" +
                                "- Click Start to start the quiz/test/bs\n" +
                                "- Jiggle your ass, and get the lube\n" +
                                "- HELLO, my name JEFF\n" +
                                "- Mary Anne. Parker is watching you\n" +
                                "- you have 3 tokens for each quiz...\n" +
                                "  if you answer wrong or click on replay you will user your tokens.\n" +
                                "- Click next when you answered the question correctly\n" +
                                "- Tavalodet ham mobarak...ummm dige kheili dare awkard mishe"

                );
                scrollPane.setContent(info);
                helpStage.setScene(new Scene(scrollPane, 400, 300));
                helpStage.show();

                helpStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        helpBtn.setDisable(false);
                    }
                });
            }
        });
        primaryStage.setMaxWidth(primaryStage.getWidth() + 1);
        primaryStage.setMaxHeight(primaryStage.getHeight() + 1);

    }

    private String getExtension(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != 0 && lastIndex != -1){
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
