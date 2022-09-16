package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Casella;
import model.Mail;
import org.controlsfx.control.Notifications;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailListener implements Runnable, Preloader.PreloaderNotification {

    Casella mailbox;
    TableView<Mail> TableViewMail;
    TableColumn<Mail, String> ColumnMittente;
    TableColumn<Mail, String> ColumnContenuto;
    TableColumn<Mail, String> ColumnData;
    TableColumn<Mail, String> ColumnOggetto;

    public MailListener( Casella mailbox, TableView<Mail> tableViewMail, TableColumn<Mail, String> columnMittente, TableColumn<Mail, String> columnContenuto, TableColumn<Mail, String> columnData, TableColumn<Mail, String> columnOggetto){
        this.mailbox=mailbox;
        this.TableViewMail=tableViewMail;
        this.ColumnMittente=columnMittente;
        this.ColumnContenuto=columnContenuto;
        this.ColumnData=columnData;
        this.ColumnOggetto=columnOggetto;
    }

    @Override
    public void run() {
        while (true){

            Socket socket=null;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;
            List<Mail> mails = null;
            try {
                socket=new Socket("localhost", 9999);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                JSONObject request = new JSONObject();
                request.put("request", "mailrequest");
                request.put("utente", mailbox.getUser().getMail());

                bufferedWriter.write(request.toJSONString());
                bufferedWriter.newLine();
                bufferedWriter.flush();

            } catch (ConnectException ex) {
                System.out.println("[CLIENT]: Server Offline, riconnessione...");
                Runnable Reconnection=new Reconnection();
                Thread t= new Thread(Reconnection);
                t.start();
                try {
                    t.join();
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String s = bufferedReader.readLine();
                mails = new Gson().fromJson(s, new TypeReference<ArrayList<Mail>>() {
                }.getType());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex){
                ex.printStackTrace();
            }

            if (mails != null) {
                if(TableViewMail.getItems().size()>0){

                    for (int j=0; j<mails.size(); j++){
                        boolean old_mail=false;
                        for(int i = 0; i < TableViewMail.getItems().size(); i++) {
                            if (TableViewMail.getItems().get(i).toString().equals(mails.get(j).toString())) {
                                old_mail=true;
                            }
                        }
                        if (!old_mail){
                            MailController.MailRicevuta(mails.get(j).getMittente());
                        }
                    }
                }
                TableViewMail.getItems().clear();/////
                for (int i = 0; i < mails.size(); i++) {
                        mailbox.addMail(mails.get(i));
                        TableViewMail.getItems().add(mails.get(i));

                }
            }

            ColumnMittente.setCellValueFactory(new PropertyValueFactory<>("mittente"));
            ColumnOggetto.setCellValueFactory(new PropertyValueFactory<>("oggetto"));
            ColumnData.setCellValueFactory(new PropertyValueFactory<>("date"));
            ColumnContenuto.setCellValueFactory(new PropertyValueFactory<>("contenuto"));
            try {
                socket.close();
                bufferedWriter.close();
                bufferedReader.close();
                Thread.sleep(5000);
            } catch (InterruptedException | IOException e) {
                System.out.println(e);
                break;
            }
        }
    }
}
