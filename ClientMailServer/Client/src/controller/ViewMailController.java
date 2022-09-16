package controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewMailController implements Initializable {

    @FXML
    private Text cognome;

    @FXML
    private Text content;

    @FXML
    private Text mail;

    @FXML
    private Text oggetto;

    @FXML
    private Text data;

    public void setContent(String content){
        this.content.setText(String.valueOf(content));
    }

    public void setMittente(String mittente){
        this.mail.setText(String.valueOf(mittente));
    }

    public void setObject(String oggetto){
        this.oggetto.setText(String.valueOf(oggetto));
    }

    public void setData(String data){
        this.data.setText(String.valueOf(data));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    }

}
