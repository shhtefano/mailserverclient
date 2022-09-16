import com.fasterxml.jackson.core.type.*;
import com.google.gson.*;
import org.json.simple.*;

import org.json.simple.parser.*;
import model.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static java.lang.Thread.sleep;

public class ClientHandler implements Runnable{

    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;


    public ClientHandler(Socket clientSocket, BufferedReader in, BufferedWriter out) throws IOException {
        this.client=clientSocket;
        this.in=in;
        this.out=out;
    }

    @Override
    public void run() {
        Date date = new Date();
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        Calendar calendar = GregorianCalendar.getInstance();


        JSONObject JSONresponse;

        try{

            JSONObject request = (JSONObject) new  JSONParser().parse(in.readLine());
            System.out.println("[SERVER]: Richiesta ricevuta: " + request);
            String userMail = (String) request.get("utente");
            if((String) request.get("request")==null) return; //DA SISTEMARE
            switch((String) request.get("request")){

                case "loginrequest":
                    JSONresponse = new JSONObject();
                    ArrayList<Users> users = GetUsers();
                    ArrayList<JSONObject> u=new ArrayList<>();
                    for(int i = 0; i<users.size(); i++){
                        JSONObject temp=new JSONObject();
                        temp.put("nome", users.get(i).getNome());
                        temp.put("cognome", users.get(i).getCognome());
                        temp.put("mail", users.get(i).getMail());
                        u.add(temp);
                    }
                    LogController.list.add("["+formattedDate+"]" + " Richiesta di login ricevuta da un client.");
                    JSONresponse.put("users", u);
                    out.write(JSONresponse.toJSONString());
                    out.newLine();
                    out.flush();
                    break;

                case "mailrequest":
                    ArrayList<Mail> mailss = GetListMail((String) request.get("utente"));
                    out.write(new Gson().toJson(mailss));
                    out.newLine();
                    out.flush();
                    break;

                case "deletemail":
                    Delete(userMail, ((long)request.get("id")));
                    LogController.list.add("["+formattedDate+"]" + " Richiesta di cancellazione mail da " + request.get("utente"));
                    break;


                case "sendmail":
                    System.out.println(request.toJSONString() +"  " + request.get("destinatari"));
                    SendMail((String)request.get("mittente"), (String)request.get("data"),  (String)request.get("oggetto"), (String)request.get("contenuto"), (String)request.get("destinatari") );
                    LogController.list.add("["+formattedDate+"]" + " Richiesta invio nuova mail da: " + request.get("mittente") + " a: " + request.get("destinatari"));

                    break;

                case "replymail":
                    System.out.println(request.toJSONString() +"  " + request.get("destinatari"));
                    SendMail((String)request.get("mittente"), (String)request.get("data"),  (String)request.get("oggetto"), (String)request.get("contenuto"), (String)request.get("destinatari") );
                    try{
                        LogController.list.add("["+formattedDate+"]" + " Richiesta invio risposta mail da: " + request.get("mittente") + " a: " + request.get("destinatari"));
                    }catch (Exception e){
                        System.out.println("girolamo");
                        //e.printStackTrace();
                    }
                    break;

                case "forwardmail":
                    System.out.println(request.toJSONString() +"  " + request.get("destinatari"));
                    SendMail((String)request.get("mittente"), (String)request.get("data"),  (String)request.get("oggetto"), (String)request.get("contenuto"), (String)request.get("destinatari") );
                    LogController.list.add("["+formattedDate+"]" + " Richiesta invio inoltro mail da: " + request.get("mittente") + " a: " + request.get("destinatari"));
                    break;

                default:
                    break;
            }

        }catch (NullPointerException nullPointerException){

        }catch (Exception e){
            System.out.println("Eccezione ClientHandler! "+ e);
            e.getStackTrace();
        }finally{
            try {
                this.client.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static ArrayList<Mail> GetListMail(String user){
        List<Mail> mails=null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\"+user));
            mails = new Gson().fromJson(reader, new TypeReference<List<Mail>>(){}.getType());
            reader.close();

        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return (ArrayList<Mail>) mails;
    }

    public static boolean Delete(String user, long id){
        try {

            FileReader reader = new FileReader("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\"+user);
            List<Mail> mailz = new Gson().fromJson(reader, new TypeReference<List<Mail>>(){}.getType());
            reader.close();
            for(int i=0; i<mailz.size();i++){
                if(mailz.get(i).getId() == id){
                    mailz.remove(i);
                    try {

                        FileWriter fileWriter = new FileWriter("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\"+user);
                        fileWriter.write(new Gson().toJson(mailz));
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("[SERVER]: Mail con id " + id + " rimossa!") ;
                    }
                    break;
                }
            }
            System.out.println(mailz);
        }catch (Exception e){
            System.out.println("Eccezione Client Handler "+e);
        }
        return false;
    }

    public String SendMail(String mittente, String data, String oggetto, String contenuto, String destinatari){
        String[] recipients = destinatari.split(",");
        List<String> wrong_mail = new ArrayList<>();
        int status=200;
        for(int i=0; i<recipients.length; i++){
            long max_id=0;
            recipients[i]=recipients[i].trim();
            if(checkEmail(recipients[i])){
                try {
                    FileReader reader = new FileReader("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\"+recipients[i]);
                    List<Mail> mailz = new Gson().fromJson(reader, new TypeReference<List<Mail>>(){}.getType());
                    reader.close();
                    for (int j=0; j<mailz.size(); j++){
                        if(max_id<mailz.get(j).getId()) max_id=mailz.get(j).getId();
                    }
                    Mail mail = new Mail((int)max_id+1, mittente, recipients[i], oggetto, contenuto, data);
                    mailz.add(mail);
                    FileWriter fileWriter = new FileWriter("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\"+recipients[i]);
                    fileWriter.write(new Gson().toJson(mailz));
                    fileWriter.flush();
                    fileWriter.close();

                    System.out.println("[SERVER]: Mail mandata a :" + mail.getDestinatario() + " - Mittente: " + mail.getMittente() + " - Contenuto: " + mail.getContenuto());
                }catch (Exception e){
                    System.out.println("eccezione "+ Arrays.toString(e.getStackTrace()));
                }

            }else{
                assert wrong_mail != null;
                wrong_mail.add(recipients[i]);
                System.out.println("[SERVER]: Mail non trovata: "+recipients[i]);
                status=404;


            }
        }

        JSONObject response=new JSONObject();
        response.put("status", status);
        if (status==404){
            response.put("wrong_mail", new Gson().toJson(wrong_mail) );
        }

        try {
            this.out.write(response.toJSONString());
            this.out.newLine();
            this.out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "OK";
    }

    public static ArrayList<Users> GetUsers(){
        ArrayList<Users> users=null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\users.json"));
            users = new Gson().fromJson(reader, new TypeReference<List<Users>>(){}.getType());
            reader.close();
        } catch (Exception e) {
            System.out.println("An error occurred." + e);
            e.printStackTrace();
        }
        return users;
    }

    private static boolean checkEmail(String mail) {
        Reader reader=null;
        List<Users> users=null;
        try {
            reader = Files.newBufferedReader(Paths.get("C:\\Users\\Stefano\\Desktop\\progettodemo1\\Server\\resources\\users.json"));
            users = new Gson().fromJson(reader, new TypeReference<List<Users>>(){}.getType());
            reader.close();
        }catch (Exception e){
            System.out.println(e);
        }

        for (int i=0;i<users.size();i++){

            if(users.get(i).getMail().equals(mail)){
                return true;
            }
        }
        return false;
    }

}


