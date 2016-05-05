package com.backend;

import com.backend.editor.HTMLKeywords;
import com.backend.filehandling.FileHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;

/**
 * Created by osama on 4/28/16.
 * Controller for the fxml file.
 */
public class MainViewController {
    private static final String tempFile = "/tmp/temp.html";

    @FXML
    private WebView webview;
    @FXML
    private CodeArea htmlEditor;
    @FXML
    private CheckBox toggleMode;
    @FXML
    private CheckBox darkBack;
    @FXML
    private Button load;

    private ChangeListener listenerEditor = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    webview.getEngine().loadContent(htmlEditor.getText());
                }
            });
        }
    };
    private ChangeListener listenerCheckBox = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (!toggleMode.isSelected()) {
                htmlEditor.textProperty().removeListener(listenerEditor);
                load.setDisable(false);
                htmlEditor.requestFocus();
            } else {
                htmlEditor.textProperty().addListener(listenerEditor);
                load.setDisable(true);
                htmlEditor.requestFocus();
            }
        }
    };
    private Stage stage;
    private String fileLocation = "/tmp/temp.html";
    private FileHandler fileHandler;
    private boolean autoCompleteText = true;

    @FXML
    public void initialize() {
        htmlEditor.setParagraphGraphicFactory(LineNumberFactory.get(htmlEditor));
        htmlEditor.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> htmlEditor.setStyleSpans(0, HTMLKeywords.computeHighlighting(htmlEditor.getText())));
        autoComplete();

        //create the initial default file
        fileHandler = new FileHandler();
        fileHandler.createFile(fileLocation);
        toggleMode.selectedProperty().addListener(listenerCheckBox);


    }

    @FXML
    public void loadContent() {
        webview.getEngine().loadContent(htmlEditor.getText());

    }

    @FXML
    public void openFile() {
        htmlEditor.replaceText(0, 0, "");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open valid html file");
        File file = fileChooser.showOpenDialog(stage);
        if (fileHandler.verifyFile(file.getAbsolutePath())) {
            htmlEditor.replaceText(0, 0, fileHandler.getFileData(file));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Cannot open file");
            alert.setContentText("File you have selected is not a valid HTML file");
            alert.showAndWait();


        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void close() {
        //be sure to save file before leaving
        fileHandler.saveFile(htmlEditor.getText());
        //close some streams
        fileHandler.cleanUp();
        //finally exit from the ****
        System.exit(0);
    }

    @FXML
    public void createFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(stage);
        if (file.exists()) {
            fileLocation = file.getAbsolutePath();
            //verify file name
            if (!fileHandler.verifyFile(file.getAbsolutePath())) {
                fileLocation += ".html";
            }

            //create and save file in file system.
            fileHandler.createFile(fileLocation);
            fileHandler.saveFile(htmlEditor.getText());
        }

    }

    @FXML
    public void saveFile() {
        boolean temp = false;
        //if it is automatically created file i, user not create file by itself
        //file is default file created by program than give access to user to choose filename
        //and save it.
        File f;
        if (fileLocation.equals(tempFile)) {
            FileChooser fx = new FileChooser();
            try {
                f = fx.showSaveDialog(stage);
                fileLocation = f.getAbsolutePath();
                //verify file name
                if (!fileHandler.verifyFile(fileLocation)) {
                    fileLocation += ".html";
                }
                //create and save file in filesystem.
                fileHandler.createFile(fileLocation);
                temp = fileHandler.saveFile(htmlEditor.getText());


            } catch (Exception e) {
                temp = false;
            }
        } else {
            temp = fileHandler.saveFile(htmlEditor.getText());
        }


        //show file saved alert
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        if (temp) {
            a.setContentText("File successfully saved");
            a.setHeaderText("File saved");
        }
        //this will never happened.
        else {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setHeaderText("Cannot save file");
        }
        a.showAndWait();
    }

    @FXML
    public void changeEditorBack() {
        if (darkBack.isSelected()) {
            htmlEditor.setStyle("-fx-fill: white; -fx-background-color: gray");
        } else {
            htmlEditor.setStyle("-fx-background-color: azure");
        }
    }

    private void autoComplete() {
        htmlEditor.textProperty().addListener(r -> {
            htmlEditor.setOnKeyPressed(abc -> {
                if (abc.getCode().getName().equalsIgnoreCase("backspace")) {
                    String a = htmlEditor.getText().substring(0, htmlEditor.getCaretPosition() - 1);
                    a += htmlEditor.getText().substring(htmlEditor.getCaretPosition());
                    int caret = htmlEditor.getCaretPosition();
                    htmlEditor.replaceText(a);
                    htmlEditor.positionCaret(caret - 1);
                } else {

                    Platform.runLater(() -> {
                        int temp2 = htmlEditor.getCaretPosition() - 1;
                        char temp = htmlEditor.getText().charAt(temp2);
                        char temp3;
                        int i = 0;
                        if (temp == '>' && autoCompleteText) {
                            autoCompleteText = false;
                            for (i = 0; i < htmlEditor.getText().length(); i++) {
                                temp3 = htmlEditor.getText().charAt(temp2 - i);
                                if (temp3 == '<' && htmlEditor.getText().charAt(temp2 - i + 1) != '/') {
                                    String tag = htmlEditor.getText().substring(temp2 - i + 1, temp2);
                                    if (tag.equalsIgnoreCase("h1") || tag.equalsIgnoreCase("input")) {
                                        htmlEditor.insertText(temp2, ">  </" + htmlEditor.getText(temp2 - i + 1, temp2));
                                        break;
                                    } else {
                                        htmlEditor.insertText(temp2, ">\n\n</" + htmlEditor.getText(temp2 - i + 1, temp2));
                                        break;
                                    }
                                } else if (temp3 == ' ') {

                                }
                            }
                            return;
                        } else {
                            autoCompleteText = true;
                        }

                    });
                }
            });

        });
    }
}
