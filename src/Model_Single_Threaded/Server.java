package Model_Single_Threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread {

	private static final int DEFAULT_PORT = 7777;
	private static final int MAX = 15;
	private static final int MIN = 2;
	int clientCounter = 0;
	List<Long> clientTimes = new ArrayList<>();

	@Override
	public void run() {
		BufferedReader clientIn;
		Socket conn = null;
		try {
			ServerSocket server = new ServerSocket(DEFAULT_PORT, 8);
			System.out.println("Server eingericht!");
			long serverStartTime = System.nanoTime();
			do {
				try {
					conn = server.accept();
					clientCounter++;
					System.out.println("Client Nr." + clientCounter + " verbunden!");
					clientIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String userMsg = clientIn.readLine();
					int startOfUID = userMsg.indexOf(": ");
					int endOfUID = userMsg.indexOf("|");
					int startTimeIndex = userMsg.indexOf("Server: ");
					String userID = userMsg.substring((startOfUID + 1), endOfUID);
					Instant startTime = null;
					if (startTimeIndex != -1) {
						startTime = Instant.parse(userMsg.substring((startTimeIndex + 8)));
						System.out.println("Verweildauer vor erfolgreichem Verbindungsaufbau: " + startTime + "des Client mit der ID: " + userID);
					}
					System.out.println("Clientnachricht: " + userMsg);
					sleep((long) (Math.floor(Math.random() * (MAX - MIN + 1) + MIN) * 1000));
					Instant endTime = Instant.now();
					long elapsedTime = Duration.between(startTime, endTime).getSeconds();
					clientTimes.add(elapsedTime);
					System.out.println("Verweildauer: " + elapsedTime + " sek für Client mit der ID:" + userID);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} while (clientCounter < Client.CLIENT_COUNT);
			long serverEndTime = System.nanoTime();
			long serverElapsedTime = serverEndTime - serverStartTime;
			long serverElapsedTimeInSeconds = (serverElapsedTime / 1000000000) / 60;
			System.out.println("Serverlaufzeit: " + serverElapsedTimeInSeconds + " min");
			long sumTime = 0;
			for (Long clientTime : clientTimes) {
				sumTime += clientTime;
			}
			long averageTimeSpendByClient = sumTime / clientTimes.size();
			System.out.println("Durschnittsverweildauer: " + averageTimeSpendByClient + " sek");
			long maxTimeSpendByClient = Collections.max(clientTimes);
			System.out.println("Maximale Verweildauer: " + maxTimeSpendByClient + " sek");
			long sumCounter = 0;
			for (int rejectCounter : Client.rejectCounters) {
				sumCounter += rejectCounter;
			}
			System.out.println("Durschnittliche Anzahl an Abweisungen vom Server an den Client: " + sumCounter / Client.rejectCounters.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
