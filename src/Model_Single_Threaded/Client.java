package Model_Single_Threaded;

import static Model_Single_Threaded.Constants.CLIENT_COUNT;
import static Model_Single_Threaded.Constants.MAX_RETRIES;
import static Model_Single_Threaded.Constants.MAX_TIME_PAUSING;
import static Model_Single_Threaded.Constants.MIN_RETRIES;
import static Model_Single_Threaded.Constants.MIN_TIME_PAUSING;
import static Model_Single_Threaded.Constants.SERVER_PORT;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.time.Instant;

public class Client extends Thread {

	public static int[] rejectCounters = new int[CLIENT_COUNT];

	private int clientID;
	int connectTryCounter;
	int rejectedCounter;

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
		int retriesBeforePausing = (int) Math.floor(Math.random() * (MAX_RETRIES - MIN_RETRIES + 1) + MIN_RETRIES);
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
			socket = new Socket(hostname, SERVER_PORT);
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
			System.out.println("Client mit der ID: " + getClientID() + " wurde vom Server abgewiesen" + "| Durch Thread: " + getId());
			rejectedCounter++;
			if (connectTryCounter == retriesBeforePausing) {
				System.out.println("Versuche vor der Pause: " + retriesBeforePausing + ", von Client mit der ID: " + getClientID());
				try {
					long sleepTime = (long) (Math.floor(Math.random() * (MAX_TIME_PAUSING - MIN_TIME_PAUSING + 1) + MIN_TIME_PAUSING) * 10);
					System.out.println("Client mit der ID: " + getClientID() + " macht eine Pause für " + ((int) sleepTime / 600) + " Minuten");
					sleep(sleepTime);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				connectTryCounter = 0;
			}
			retriesBeforePausing = (int) Math.floor(Math.random() * (MAX_RETRIES - MIN_RETRIES + 1) + MIN_RETRIES);
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
