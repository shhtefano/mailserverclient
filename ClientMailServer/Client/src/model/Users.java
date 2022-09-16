package model;

public class Users {
    private final String nome;
    private final String cognome;
    private final String mail;

    public String getNome(){
        return this.nome;
    }

    public String getCognome(){
        return this.cognome;
    }

    public String getMail(){
        return this.mail;
    }
    @Override
    public String toString(){
        return this.mail + " " + this.nome + " " + this.cognome;
    }

    public Users(String nome,String cognome,String mail){
        this.nome=nome;
        this.cognome=cognome;
        this.mail=mail;
    }
}
