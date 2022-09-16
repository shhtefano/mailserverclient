package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.*;
import org.json.simple.JSONObject;
import model.*;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MailController implements Initializable, Runnable {

    protected Casella mailbox;

    @FXML
    private TableView<Mail> TableViewMail;

    @FXML
    private TableColumn<Mail, String> ColumnMittente;

    @FXML
    private TableColumn<Mail, String> ColumnContenuto;

    @FXML
    private TableColumn<Mail, String> ColumnData;

    @FXML
    private TableColumn<Mail, String> ColumnOggetto;

    @FXML
    private Text mymail;

    @FXML
    private Button DeleteButton;

    @FXML
    private Button sendmail;

    @FXML
    private Button replyto;

    @FXML
    private Label label;

    static private String ss;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            this.setU(Client.user);
            Runnable listner =new MailListener( mailbox, TableViewMail, ColumnMittente, ColumnContenuto, ColumnData, ColumnOggetto);
            Thread t= new Thread(listner);
            t.setDaemon(true);
            t.start();
    }

    @FXML
    public void setMyMail(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){

            try{
                Stage stage=new Stage();
                FXMLLoader loader = new FXMLLoader((getClass().getResource("views/viewmail.fxml")));
                Parent root=loader.load();
                System.out.println(TableViewMail.getFocusModel().getFocusedItem().getDate());

                ((ViewMailController)loader.getController()).setContent(TableViewMail.getFocusModel().getFocusedItem().getContenuto());
                ((ViewMailController)loader.getController()).setObject(TableViewMail.getFocusModel().getFocusedItem().getOggetto());
                ((ViewMailController)loader.getController()).setMittente(TableViewMail.getFocusModel().getFocusedItem().getMittente());
                ((ViewMailController)loader.getController()).setData(TableViewMail.getFocusModel().getFocusedItem().getDate());

                stage.setTitle("Mail Visualizer");
                stage.setScene(new Scene(root));
                stage.show();

            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    @FXML
    public void deleteMail(ActionEvent event) {

        Mail mail=TableViewMail.getFocusModel().getFocusedItem();
        Socket socket=null;
        BufferedReader bufferedReader=null;
        BufferedWriter bufferedWriter=null;
        try {
            socket = new Socket("localhost", 9999);
            bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            JSONObject request = new JSONObject();
            request.put("request", "deletemail");
            request.put("utente", mailbox.getUser().getMail());
            request.put("id", mail.getId());
            bufferedWriter.write(request.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();

            this.mailbox.removeMail(mail);
            TableViewMail.getItems().remove(mail);
        }catch (ConnectException ex){
            Notifications notificationBuilder = Notifications.create().title("Server Offline").text("Il client proverà automaticamente a riconnettersi.").graphic(null).hideAfter(Duration.seconds(3)).position(Pos.BOTTOM_RIGHT);
            notificationBuilder.showInformation();

            Runnable reconnection=new Reconnection(DeleteButton);
            Thread t= new Thread(reconnection);
            t.start();

            DeleteButton.setDisable(true);

        }catch (Exception e){
            System.out.println(e);
        }finally {
            try {
                socket.close();
                bufferedWriter.close();
                bufferedReader.close();
            }catch (Exception e){
                System.out.println(e);
            }
        }


    }

    @FXML
    public void NewMail(ActionEvent event) throws IOException {
        SendMailController.mailbox=mailbox;
        Stage stage=new Stage();
        FXMLLoader loader = new FXMLLoader((getClass().getResource("views/sendmail.fxml")));
        Parent root=loader.load();

        stage.setTitle("Mail Writer");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void Reply(ActionEvent event) throws IOException {
        SendMailController.mailbox=mailbox;
        try{
            Mail mail=TableViewMail.getFocusModel().getFocusedItem();
            System.out.println(mail.toString());

            try{
                Stage stage=new Stage();
                FXMLLoader loader = new FXMLLoader((getClass().getResource("views/replymail.fxml")));
                Parent root=loader.load();

                ((SendMailController)loader.getController()).setObject(mail.getOggetto());
                ((SendMailController)loader.getController()).setDestinatario(mail.getMittente());

                stage.setTitle("Reply Mail Writer");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Notifications notificationBuilder = Notifications.create().title("Nessuna mail selezionata").text("Per rispondere ad una mail, selezionane una");
            notificationBuilder.showInformation();
        }

    }

    @FXML
    public void Forward(ActionEvent event) throws IOException {
        SendMailController.mailbox=mailbox;
        try{
            Mail mail=TableViewMail.getFocusModel().getFocusedItem();
            System.out.println(mail.toString());

            try{
                Stage stage=new Stage();
                FXMLLoader loader = new FXMLLoader((getClass().getResource("views/forwardmail.fxml")));
                Parent root=loader.load();

                ((SendMailController)loader.getController()).setObject(mail.getOggetto());
                ((SendMailController)loader.getController()).setContenuto(mail.getContenuto());

                stage.setTitle("Forward Mail Writer");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Notifications notificationBuilder = Notifications.create().title("Nessuna mail selezionata").text("Per rispondere ad una mail, selezionane una");
            notificationBuilder.showInformation();
        }

    }

    @FXML
    public void ChangeAccount(ActionEvent event) throws Exception {

        new Client().start(new Stage());

    }

    public static void MailRicevuta(String mittente){
        ss=mittente;
        Platform.runLater(new MailController());

    }

    @Override
    public void run() {
        Notifications notificationBuilder = Notifications.create().title("Nuova mail ricevuta").text("Da: "+ this.ss).position(Pos.BOTTOM_RIGHT);
        notificationBuilder.showInformation();
    }

    public void setU(Users u) {
        mailbox=new Casella(u);
    }

}
