package Model_Single_Threaded;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.time.Instant;

public class Client extends Thread {

	private static final int serverPort = 7777;
	public static final int CLIENT_COUNT = 353;
	public static int[] rejectCounters = new int[CLIENT_COUNT];

	private int clientID;
	int connectTryCounter;
	int rejectedCounter;
	private static int MAX_RETRY = 10;
	private static int MIN_RETRY = 0;
	private static int MAX_SLEEP = 1200; //20 Stunden
	// private static int MAX = 12; //20 Stunden
	// private static int MIN = 4; //8 Stunden
	private static int MIN_SLEEP = 480; //8 Stunden

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
		int retriesBeforePausing = (int) Math.floor(Math.random() * (MAX_RETRY - MIN_RETRY + 1) + MIN_RETRY);
		Instant startTime = Instant.now();
		connect(retriesBeforePausing, startTime);
	}

	synchronized void addRejectedCounter(int rejectedCounter) {
		Client.rejectCounters[getClientID() - 1] = rejectedCounter;
	}

	public void connect(int retriesBeforePausing, Instant startTime) {
		String hostname = "localhost";
		PrintWriter clientOut;
		Socket socket = null;
		try {
			connectTryCounter++;
			socket = new Socket(hostname, serverPort);
			System.out.println("Verbindung zum Message Server hergstellt!");
			Instant startTimeOfThread = startTime;
			addRejectedCounter(rejectedCounter);
			clientOut = new PrintWriter(socket.getOutputStream());
			String clientMsg = "Anfrage von: " + getClientID() + " | Durch Thread: " + getId();
			if (rejectedCounter > 0) {
				clientMsg += " | " + rejectedCounter + " Versuchen durchgekommen";
			}
			clientMsg += " | Verweildauer bis zum erfolgreichen Verbindungsaufbau zum Server: " + startTimeOfThread;
			System.out.println("CLIENT: " + getClientID() + " | " + startTimeOfThread);
			clientOut.println(clientMsg);
			clientOut.flush();
		} catch (ConnectException e) {
			System.out.println("Client " + getClientID() + " wurde vom Server abgewiesen" + "| Durch Thread: " + getId());
			rejectedCounter++;
			if (connectTryCounter == retriesBeforePausing) {
				System.out.println("Versuche vor der Pause: " + retriesBeforePausing + ", von Client mit der ID: " + getClientID());
				try {
					long sleepTime = (long) (Math.floor(Math.random() * (MAX_SLEEP - MIN_SLEEP + 1) + MIN_SLEEP) * 1000);
					System.out.println("Client mit der ID: " + getClientID() + " macht eine Pause für " + ((int) sleepTime / 60000) + " Minuten");
					sleep(sleepTime);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				connectTryCounter = 0;
			}
			retriesBeforePausing = (int) Math.floor(Math.random() * (MAX_RETRY - MIN_RETRY + 1) + MIN_RETRY);
			connect(retriesBeforePausing, startTime);
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
