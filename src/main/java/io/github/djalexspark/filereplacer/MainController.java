package io.github.djalexspark.filereplacer;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    Stage stage;
    File from, into;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TextField fromTextField;
    @FXML
    private CheckBox innerFoldersCheckBox;
    @FXML
    private Button buttonStart;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField whereToField;
    @FXML
    void buttonDirectoryFromChooserOnAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку для сохранения");
        File defaultDirectory = new File("C:\\");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            if (selectedDirectory.exists()) {
                fromTextField.setText(selectedDirectory.getAbsolutePath());
                fromTextField.setStyle("");
            }
        }
    }

    @FXML
    void buttonDirectoryIntoChooserOnAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку, куда сравнивать");
        File defaultDirectory = new File("C:\\");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            if (selectedDirectory.exists()) {
                whereToField.setText(selectedDirectory.getAbsolutePath());
                whereToField.setStyle("");
            }
        }
    }

    @FXML
    void onButtonClick(ActionEvent event) {

    }


    public void setSettings(Stage stage) {
        this.stage = stage;
        fromTextField.setText("");
        whereToField.setText("");
        innerFoldersCheckBox.setSelected(true);
        buttonStart.setOnAction(actionEvent -> {
            if (!(whereToField.getText().trim().isEmpty() && fromTextField.getText().trim().isEmpty())) {
                try {
                    buttonStart.setDisable(true);
                    from = new File(fromTextField.getText());
                    into = new File(whereToField.getText());
                    List<Path> listOfFilesToTakeFrom;
                    if (innerFoldersCheckBox.isSelected()) {
                        listOfFilesToTakeFrom = Files.walk(from.toPath()).filter(s -> s.toFile().isFile()).collect(Collectors.toList());
                    } else {
                        listOfFilesToTakeFrom = Files.list(from.toPath()).filter(s -> s.toFile().isFile()).collect(Collectors.toList());
                    }
                    listOfFilesToTakeFrom.forEach(System.out::println);
                    List<Path> listOfFilesToTakeInto = Files.walk(into.toPath()).filter(s -> s.toFile().isFile()).collect(Collectors.toList());
                    listOfFilesToTakeInto.forEach(System.out::println);
                    statusLabel.setText("процесс пошел");

                    for (Path file : listOfFilesToTakeFrom) {
                        listOfFilesToTakeInto.forEach(f -> {
                            if (file.getFileName().equals(f.getFileName())) {
                                try {
                                    Files.copy(file, f, StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    buttonStart.setDisable(false);
                    Task<Void> sleeper = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                statusLabel.setText("процесс закончен");
                                Thread.sleep(3500);
                            } catch (InterruptedException e) {
                            }
                            return null;
                        }
                    };
                    sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            statusLabel.setText("");
                        }
                    });
                    Thread thread = new Thread(sleeper);
                    thread.setDaemon(true);
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void textFieldTakeFileFromOnKeyTyped(KeyEvent event) {
        try {
            ((TextField) event.getSource()).getStyle();
            if (Files.notExists(Path.of(((TextField) event.getSource()).getText()))) {
                ((TextField) event.getSource()).setStyle("-fx-text-fill:#ff0000");
            } else {
                ((TextField) event.getSource()).setStyle("");
            }
            ((TextField) event.getSource()).getText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}


