package controller;

import javafx.scene.control.Button;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Reconnection implements Runnable{

    Button deletebutton=null;

    public Reconnection(Button button){
        this.deletebutton=button;
        deletebutton.setDisable(true);

    }

    public Reconnection(){
        deletebutton=new Button();
    }

    @Override
    public void run() {
        boolean offline= true;




        while (offline){
            try{
                Socket socket=new Socket("localhost", 9999);
                if(socket.isConnected()){
                    System.out.println("[CLIENT]: Connesso al server.");
                    offline=false;
                    socket.close();
                    deletebutton.setDisable(false);

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
