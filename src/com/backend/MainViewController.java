package com.backend;

import com.backend.editor.HTMLKeywords;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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


    private Stage stage;

    @FXML
    public void initialize() {
        htmlEditor.setParagraphGraphicFactory(LineNumberFactory.get(htmlEditor));
        htmlEditor.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> htmlEditor.setStyleSpans(0, HTMLKeywords.computeHighlighting(htmlEditor.getText())));
        htmlEditor.insertText(0, template);
        htmlEditor.textProperty().addListener(listen -> {
            webview.getEngine().loadContent(htmlEditor.getText());
        });
    }

    @FXML
    public void loadContent() {
        webview.getEngine().loadContent(htmlEditor.getText());

    }

    public void openFile(ActionEvent actionEvent) {
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

    public void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void createFile(ActionEvent actionEvent) {

    }

}
