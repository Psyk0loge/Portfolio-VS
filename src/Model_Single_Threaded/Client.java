package Model_Single_Threaded;

import java.net.ConnectException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class Client extends Thread{

    private static final int serverPort = 7777;//Standard Port des Message Servers

    private int clientID=0;

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }


    @Override
    public void run(){
        connect();
    }

    public void connect() {
        String hostname = "localhost";
        PrintWriter clientOut;//Writer zum versenden der Clientnachricht an den Server über den Client Outputstream
        //BufferedReader serverIn=null;//Reader zum lesen der Servernachricht über den Client InputStream
        Socket socket = null;//Initalisierung mit null zur Fehlervermeidung
        try {
            socket = new Socket(hostname,serverPort);
            System.out.println("Verbindung zum Message Server hergstellt!");
            clientOut = new PrintWriter(socket.getOutputStream());
            clientOut.println("Anfrage von: "+this.getId());//einlesen der Clientnachricht, schreiben in den Puffer
            clientOut.flush();//leeren des Puffers, senden der Clientnachricht


            //für den Fall das wir dann doch mal etwas vom Server empfangen wollen.
            /*String serverReadLine = serverIn.readLine();//einlesen und speichern der Servernachricht
            if(serverReadLine!=null){
                System.out.println(serverReadLine);//ausgeben der Servernachricht auf der Konsole
            }*/
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("Client "+this.getClientID()+" wurde vom Server abgewiesen");
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Es ist ein unvorhergesehener Fehler aufgetreten");
        } finally{
            if(socket!=null){//falls der Client eine Verbindung zum Server hat
                try {
                    socket.close();//wird diese abgebaut
                    System.out.println("Client: "+this.getId()+"hat Verbindung zum Server abgebaut!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
