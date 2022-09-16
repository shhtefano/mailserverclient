package controller;

import org.json.simple.*;
import org.json.simple.parser.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import model.*;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class LoginController implements Initializable {

    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;

    @FXML
    void loginUser(ActionEvent event) throws IOException {
        // Utente selezionato.

        Users utente = listView.getFocusModel().getFocusedItem();
        Client.user=utente;
        Window scene = listView.getScene().getWindow();
        Stage stage = (Stage) scene.getScene().getWindow();
        stage.close();

        // Creazione nuova finestra.
        Stage stage2 = new Stage();
        FXMLLoader loader = new FXMLLoader((getClass().getResource("views/listmail.fxml")));
        Parent root = loader.load();

        Text mymail = (Text) root.lookup("#mymail");
        mymail.setText(utente.getMail());
        stage2.setTitle("Mail Visualizer");
        stage2.setScene(new Scene(root));
        stage2.show();

        System.out.println("[CLIENT]: Accesso effettuato "+ utente.getMail());
        Notifications notificationBuilder = Notifications.create().title("Connesso al server").text("Account: " + utente.getMail()).graphic(null).hideAfter(Duration.seconds(3)).position(Pos.BOTTOM_RIGHT);
        notificationBuilder.showInformation();

    }

    @FXML
    private ListView<Users> listView;

    @FXML
    private Button loginbutton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

            //Socket socket = null;
            try {
                socket = new Socket("localhost", 9999);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                JSONObject request = new JSONObject();
                request.put("request", "loginrequest");
                bufferedWriter.write(request.toJSONString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
//              System.out.println("[CLIENT]: Richiesta di login mandata");

                assert  bufferedReader.ready() && !socket.isClosed();
                String users = bufferedReader.readLine();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(users);

                JSONArray ja = (JSONArray) json.get("users");
                ArrayList<Users> u = new ArrayList<>();

                for( int i =0; i<ja.size(); i++){
                    JSONObject user = (JSONObject) ja.get(i);
                    u.add(new Users((String) user.get("nome"), (String) user.get("cognome"), (String) user.get("mail")));
                }

                ObservableList<Users> userlist;

                userlist= FXCollections.observableArrayList(u);
                this.listView.setItems(userlist);


            } catch (ConnectException e){

                System.out.println("Connection Exception in Login Controller");

            } catch ( IOException | ParseException e) {
                e.printStackTrace();
            }

        }





}
