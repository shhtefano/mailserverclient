package model;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;

public class Client extends Application implements Runnable{

    static Stage mainstage;
    public static Users user;
    @Override
    public void start(Stage stage) throws Exception{

        Runnable reconnectController=new ReconnectController();
        Thread t= new Thread(reconnectController);
        t.start();

        FXMLLoader loader = new FXMLLoader((getClass().getResource("/controller/views/reconnect.fxml")));
        Parent root=loader.load();
        stage.setTitle("Connecting to server...");
        stage.setScene(new Scene(root));
        stage.show();
        mainstage=stage;

    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }


    @Override
    public void run() {
        try {
            Stage stage2  = new Stage();
            FXMLLoader loader2 = new FXMLLoader((ReconnectController.class.getResource("/controller/views/selectuser.fxml")));
            Parent root2=loader2.load();
            stage2.setTitle("Client Mail");
            stage2.setScene(new Scene(root2));
            stage2.show();
            mainstage.close();

        }catch (IOException e){
            System.out.println(e);
        }
    }
}
