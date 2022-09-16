import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Application implements Runnable {

    //public static ObservableList<String> list;


    static ExecutorService fixedPool = Executors.newFixedThreadPool(10);



    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {



        try {
            FXMLLoader loader = new FXMLLoader((getClass().getResource("views/log.fxml")));
            Parent root = loader.load();
            stage.setTitle("Server Log");
            stage.setScene(new Scene(root));
            stage.show();
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        ServerSocket serversocket = null;

        try {
            serversocket = new ServerSocket(9999);
            System.out.println("[SERVER]: Server On");
        }catch (IOException e){
            System.out.println("IOException: errore in serversocket port 9999.");
        }

        while (true){
            try{
                assert serversocket != null;
                Socket client = serversocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                ClientHandler clientThread = new ClientHandler(client, in, out);

                fixedPool.execute(clientThread);

            }catch(IOException e){
                System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                break;
            }
        }

    }
}