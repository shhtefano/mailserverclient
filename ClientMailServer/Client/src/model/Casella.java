package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Casella {
    private List<Mail> mailbox = null;
    private Users utente;

    @Override
    public String toString(){

        return mailbox.toString();
    }


    public boolean addMail(Mail mail){
        return this.mailbox.add(mail);
    }

    public boolean removeMail(Mail mail){
        return this.mailbox.remove(mail);
    }

    public Users getUser(){return this.utente;}

    public Casella(Users user){
        //Legge i msg e li mette in una observable list di tipo Email
        List<Mail> mailbox = new ArrayList<Mail>();
        ObservableList<Mail> observableMailbox = FXCollections.observableList(mailbox);
        this.mailbox=observableMailbox;
        this.utente=user;


    }

}
