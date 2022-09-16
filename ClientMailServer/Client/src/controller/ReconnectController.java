package controller;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import model.Client;
import org.controlsfx.control.Notifications;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReconnectController implements Initializable, Runnable{


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        System.out.println("Initialize reconnection...");
    }

    public void run() {

        boolean offline= true;

        while (offline){
            try{
                Socket socket=new Socket("localhost", 9999);
                if(socket.isConnected()){
                    System.out.println("[CLIENT]: Connesso al server.");
                    offline=false;
                    socket.close();
                    Platform.runLater(new Client());
                }
            }catch (ConnectException e){
                System.out.println("[CLIENT]: Riconnessione al server ...");
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

    }



}
