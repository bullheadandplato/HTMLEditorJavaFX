package com.backend;

import com.backend.editor.HTMLKeywords;
import com.backend.filehandling.FileHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
    private String template = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "</body>\n" +
            "</html>";
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
            webview.getEngine().loadContent(htmlEditor.getText());
        }
    };
    private ChangeListener listenerCheckBox = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (!toggleMode.isSelected()) {
                htmlEditor.textProperty().removeListener(listenerEditor);
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

    @FXML
    public void initialize() {
        htmlEditor.setParagraphGraphicFactory(LineNumberFactory.get(htmlEditor));
        htmlEditor.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> htmlEditor.setStyleSpans(0, HTMLKeywords.computeHighlighting(htmlEditor.getText())));
        toggleMode.selectedProperty().addListener(listenerCheckBox);
        //create the initial default file
        fileHandler = new FileHandler();
        fileHandler.createFile(fileLocation);

/*TODO
        htmlEditor.textProperty().addListener(ch->{
            String abc="</";
            if(htmlEditor.getText().equals("<")){
                abc+=htmlEditor.getText(htmlEditor.getCaretPosition());
            }
            if(htmlEditor.getText(htmlEditor.getCaretPosition()).equalsIgnoreCase(">")){
                htmlEditor.insertText(htmlEditor.getCaretPosition(),abc+">");
            }

        });
        */

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
                fileLocation.concat(".html");
            }

            //create and save file in file system.
            fileHandler.createFile(fileLocation);
            fileHandler.saveFile(htmlEditor.getText());
        }

    }

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        //if it is automatically created file i, user not create file by itself
        //file is default file created by program than give access to user to choose filename
        //and save it.
        if (fileLocation.equals(tempFile)) {
            FileChooser fx = new FileChooser();
            File f = fx.showSaveDialog(stage);
            fileLocation = f.getAbsolutePath();
            //verify file name
            if (!fileHandler.verifyFile(fileLocation)) {
                fileLocation.concat(".html");
            }
            //create and save file in filesystem.
            fileHandler.createFile(fileLocation);
            fileHandler.saveFile(htmlEditor.getText());
        } else {
            fileHandler.saveFile(htmlEditor.getText());

        }

    }

    @FXML
    public void changeEditorBack() {
        if (darkBack.isSelected()) {
            htmlEditor.setStyle("-fx-fill: white; -fx-background-color: gray");
        } else {
            htmlEditor.setStyle("-fx-background-color: azure");
        }
    }
}
