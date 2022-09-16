package controller;
import model.Casella;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.*;
import com.google.gson.*;

import org.controlsfx.control.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class


public class SendMailController implements Initializable {

    @FXML
    private Button send;

    @FXML
    private TextField destinatari;

    @FXML
    private TextField oggetto;

    @FXML
    private TextArea contenuto;

    static Casella mailbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void sendMail(ActionEvent event){

        Socket socket=null;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);

        JSONObject request=new JSONObject();
        JSONObject response=new JSONObject();
        String recipients = destinatari.getText();
        int num_recipients = recipients.split(",").length;

        String[] destinatari = recipients.split(",");
        System.out.println(destinatari[0]);
        if (!syntaxMails(destinatari))return;

        request.put("request", "sendmail");
        request.put("mittente", this.mailbox.getUser().getMail());
        request.put("data", formattedDate);
        request.put("oggetto",oggetto.getText());
        request.put("contenuto", contenuto.getText());
        request.put("destinatari", recipients);

        try {
            socket=new Socket("localhost", 9999);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write(request.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("[CLIENT]: Richiesta di invio nuova mail mandata");

            response= (JSONObject) new JSONParser().parse(bufferedReader);

            if( (Long)response.get("status")==200){
                Notifications notificationBuilder = Notifications.create().text("Mail inviata con successo!");
                notificationBuilder.showConfirm();

            }else if((Long)response.get("status")==404){
                ArrayList<String> wrongmail =new Gson().fromJson((String) response.get("wrong_mail"), new TypeReference<List<String>>(){}.getType()) ;

                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text(num_recipients- wrongmail.size()+" Email inviate \n"+ wrongmail.size()+ " Indirizi errati: "+ wrongmail);
                notificationBuilder.showConfirm();

            }else if((Long)response.get("status")==400){
                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text("Server offline, il client proverà a riconnettersi automaticamente.");
                notificationBuilder.showConfirm();
            }





        }catch (ConnectException connectException){
            Notifications notificationBuilder = Notifications.create().title("Errore").text("Server offline, il client proverà a riconnettersi automaticamente.");
            notificationBuilder.showConfirm();
            Runnable reconnect = new Reconnection(send);
            Thread t=new Thread(reconnect);
            t.start();
        }catch (Exception e){
            System.out.println(e);
        }



    }


    public void ReplySendMail(ActionEvent event) {
        Socket socket=null;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);

        JSONObject request=new JSONObject();
        JSONObject response=new JSONObject();
        String recipients = destinatari.getText();
        int num_recipients = recipients.split(",").length;

        String[] destinatari = recipients.split(",");
        if (!syntaxMails(destinatari))return;

        request.put("request", "replymail");
        request.put("mittente", this.mailbox.getUser().getMail());
        request.put("data", formattedDate);
        request.put("oggetto",oggetto.getText());
        request.put("contenuto", contenuto.getText());
        request.put("destinatari", recipients);
        System.out.println(request.toJSONString());
        try {
            socket=new Socket("localhost", 9999);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write(request.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("[CLIENT]: Richiesta di invio nuova mail mandata");

            response= (JSONObject) new JSONParser().parse(bufferedReader);



            if( (Long)response.get("status")==200){
                Notifications notificationBuilder = Notifications.create().text("Mail mandata con successo!");
                notificationBuilder.showConfirm();

//                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
//                stage.close();

            }else if((Long)response.get("status")==404){
                ArrayList<String> wrongmail =new Gson().fromJson((String) response.get("wrong_mail"), new TypeReference<List<String>>(){}.getType()) ;

                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text(num_recipients- wrongmail.size()+" Email inviate \n"+ wrongmail.size()+ " Indirizi errati: "+ wrongmail);
                notificationBuilder.showConfirm();

            }else if((Long)response.get("status")==400){
                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text("Server offline, il client proverà a riconnettersi automaticamente.");
                notificationBuilder.showConfirm();
            }

        }catch (ConnectException connectException){
            Notifications notificationBuilder = Notifications.create().title("Errore").text("SServer offline, il client proverà a riconnettersi automaticamente.");
            notificationBuilder.showConfirm();
            Runnable reconnect = new Reconnection(send);
            Thread t=new Thread(reconnect);
            t.start();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @FXML
    public void ForwardMail(ActionEvent event) {
        Socket socket=null;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);

        JSONObject request=new JSONObject();
        JSONObject response=new JSONObject();
        String recipients = destinatari.getText();
        int num_recipients = recipients.split(",").length;

        String[] destinatari = recipients.split(",");
        if (!syntaxMails(destinatari))return;


        request.put("request", "forwardmail");
        request.put("mittente", this.mailbox.getUser().getMail());
        request.put("data", formattedDate);
        request.put("oggetto",oggetto.getText());
        request.put("contenuto", contenuto.getText());
        request.put("destinatari", recipients);
        System.out.println(request.toJSONString());
        try {
            socket=new Socket("localhost", 9999);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write(request.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("[CLIENT]: Richiesta di invio nuova mail mandata");

            response= (JSONObject) new JSONParser().parse(bufferedReader);



            if( (Long)response.get("status")==200){
                Notifications notificationBuilder = Notifications.create().text("Mail mandata con successo!");
                notificationBuilder.showConfirm();

            }else if((Long)response.get("status")==404){
                ArrayList<String> wrongmail =new Gson().fromJson((String) response.get("wrong_mail"), new TypeReference<List<String>>(){}.getType()) ;
                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text(num_recipients- wrongmail.size()+" Email inviate \n"+ wrongmail.size()+ " Indirizi errati: "+ wrongmail);
                notificationBuilder.showConfirm();

            }else if((Long)response.get("status")==400){
                Notifications notificationBuilder = Notifications.create().title("Errore "+ response.get("status")).text("Server offline");
                notificationBuilder.showConfirm();
            }

        }catch (ConnectException connectException){
            Notifications notificationBuilder = Notifications.create().title("Errore").text("Server offline");
            notificationBuilder.showConfirm();
            Runnable reconnect = new Reconnection(send);
            Thread t=new Thread(reconnect);
            t.start();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public void setObject(String oggetto) {
        this.oggetto.setText(oggetto);
    }

    public void setDestinatario(String mittente) {
        this.destinatari.setText(mittente);
    }

    public void setContenuto(String contenuto) {
        this.contenuto.setText(contenuto);
    }

    private boolean syntaxMails(String[] destinatari){
//        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]");
//        for(String destinatario : destinatari){
//            Matcher matcher = pattern.matcher(destinatario.trim());
//            if(!matcher.matches()){
//                System.out.println("[CLIENT]: Mail errata " + destinatario);
//                Notifications notificationBuilder = Notifications.create().title("Mail errata: "+destinatario).text("Riprovare");
//                notificationBuilder.showConfirm();
//                return false;
//            }
//        }
//        System.out.println("[CLIENT]: Mail " + destinatari[0]);

        return true;
    }
}
