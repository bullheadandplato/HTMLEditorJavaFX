package com.backend;

import com.backend.editor.HTMLKeywords;
import com.backend.filehandling.FileHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by osama on 4/28/16.
 * Controller for the fxml file.
 */
public class MainViewController {
    private static boolean fileStatus = false;
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
    private String fileLocation;
    private FileHandler fileHandler;

    @FXML
    public void initialize() {
        htmlEditor.setParagraphGraphicFactory(LineNumberFactory.get(htmlEditor));
        htmlEditor.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> htmlEditor.setStyleSpans(0, HTMLKeywords.computeHighlighting(htmlEditor.getText())));
        toggleMode.selectedProperty().addListener(listenerCheckBox);
        //create the initial default file
        fileHandler = new FileHandler();
        createFile();

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
        htmlEditor.replaceText(0, 0, fileData(file));

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private String fileData(File file) {
        String temp = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                temp += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
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
        File file = fc.showOpenDialog(stage);
        if (file.exists()) {
            fileLocation = file.getAbsolutePath();
            fileHandler.createFile(fileLocation);
            System.out.println("html: " + htmlEditor.getText());
            fileHandler.saveFile(htmlEditor.getText());
            fileStatus = true;
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

    @FXML
    public void saveFile(ActionEvent actionEvent) {
        //be sure to call create file before save file.
        if (!fileStatus) {
            createFile();
        } else {
            fileHandler.saveFile(htmlEditor.getText());
        }

    }
}
