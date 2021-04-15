package Model_Single_Threaded;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Client extends Thread {

	private static final int serverPort = 7777;//Standard Port des Message Servers

	private int clientID = 0;

<<<<<<< HEAD
    public Client(int cLientID){
        this.clientID=cLientID;
    }

=======
	public int getClientID() {
		return clientID;
	}
>>>>>>> d647a81b5ae199f6197d04dd9547f051596e9201

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	@Override
	public void run() {
		connect();
	}

	public void connect() {
		String hostname = "localhost";
		PrintWriter clientOut;
		//BufferedReader serverIn=null;
		Socket socket = null;
		try {
			socket = new Socket(hostname, serverPort);
			System.out.println("Verbindung zum Message Server hergstellt!");
			clientOut = new PrintWriter(socket.getOutputStream());
			clientOut.println("Anfrage von: " + getId());
			clientOut.flush();

			//für den Fall das wir dann doch mal etwas vom Server empfangen wollen.
            /*String serverReadLine = serverIn.readLine();
            if(serverReadLine!=null){
                System.out.println(serverReadLine);
            }*/
		} catch (ConnectException e) {
			e.printStackTrace();
			System.out.println("Client " + getClientID() + " wurde vom Server abgewiesen");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Es ist ein unvorhergesehener Fehler aufgetreten");
		} finally {
			if (socket != null) {
				try {
					socket.close();
					System.out.println("Client: " + getId() + "hat Verbindung zum Server abgebaut!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
