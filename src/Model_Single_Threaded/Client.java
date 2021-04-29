package Model_Single_Threaded;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {

	private static final int serverPort = 7777;
	public static final int CLIENT_COUNT = 353;
	public static List<Integer> rejectCounters = new ArrayList<>();

	private int clientID;
	int connectTryCounter;
	int rejectedCounter;
	// private static int MAX = 1200; //20 Stunden
	private static int MAX = 12; //20 Stunden
	private static int MIN = 4; //8 Stunden
	// private static int MIN = 480; //8 Stunden

	public Client(int clientID) {
		this.clientID = clientID;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	@Override
	public void run() {
		connectTryCounter = 0;
		rejectedCounter = 0;
		connect();
	}

	synchronized void addRejectedCounter(int rejectedCounter) {
		Client.rejectCounters.add(rejectedCounter);
	}

	public void connect() {
		String hostname = "localhost";
		PrintWriter clientOut;
		Socket socket = null;
		try {
			connectTryCounter++;
			socket = new Socket(hostname, serverPort);
			System.out.println("Verbindung zum Message Server hergstellt!");
			addRejectedCounter(rejectedCounter);
			clientOut = new PrintWriter(socket.getOutputStream());
			if (rejectedCounter > 0) {
				clientOut.println("Anfrage von: " + getClientID() + "| Durch Thread:" + getId() + "| Nach " + rejectedCounter + " Versuchen durchgekommen");
			} else {
				clientOut.println("Anfrage von: " + getClientID() + "| Durch Thread:" + getId());
			}
			clientOut.flush();
		} catch (ConnectException e) {
			System.out.println("Client " + getClientID() + " wurde vom Server abgewiesen" + "| Durch Thread: " + getId());
			rejectedCounter++;
			if (connectTryCounter == 5) {
				try {
					long sleepTime = (long) (Math.floor(Math.random() * (MAX - MIN + 1) + MIN) * 1000);
					System.out.println("Client mit der ID: " + getClientID() + " macht eine Pause für " + ((int) sleepTime / 60000) + " Minuten");
					sleep(sleepTime);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				connectTryCounter = 0;
			}
			connect();
		} catch (IOException e) {
			System.out.println("Es ist ein unvorhergesehener Fehler aufgetreten (ClientID: " + getClientID() + ")");
		} finally {
			if (socket != null) {
				try {
					socket.close();
					System.out.println("Client mit der ID: " + getClientID() + " hat die Verbindung zum Server abgebaut!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
