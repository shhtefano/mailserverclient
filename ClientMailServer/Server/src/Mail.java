public class Mail {
    private final long id;
    private final String mittente;
    private final String destinatario;
    private final String oggetto;
    private final String contenuto;
    private final String date;

    public String getDate(){
        return this.date;
    }
    public long getId() {
        return this.id;
    }

    public String getContenuto() {
        return this.contenuto;
    }

    public String getOggetto() {
        return this.oggetto;
    }

    public String getDestinatario() {
        return this.destinatario;
    }

    public String getMittente() {
        return this.mittente;
    }



    @Override
    public String toString(){
        return (id + " "+ mittente+ " "+ destinatario+ " "+oggetto+ " "+contenuto+ " "+date);
    }

    public Mail(int id, String mittente, String destinatario, String oggetto, String contenuto, String date ){
        this.id=id;
        this.mittente=mittente;
        this.destinatario= destinatario;
        this.oggetto=oggetto;
        this.contenuto=contenuto;
        this.date=date;
    }
}
