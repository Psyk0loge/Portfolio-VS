package Model_Single_Threaded;

import static Model_Single_Threaded.Constants.CLIENT_COUNT;
import static Model_Single_Threaded.Constants.MAX_TIME_SPENT;
import static Model_Single_Threaded.Constants.MIN_TIME_SPENT;
import static Model_Single_Threaded.Constants.SERVER_PORT;

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

	int clientCounter = 0;
	List<Long> clientTimes = new ArrayList<>();

	@Override
	public void run() {
		BufferedReader clientIn;
		Socket conn = null;
		try {
			ServerSocket server = new ServerSocket(SERVER_PORT, 1);
			System.out.println("Server eingericht!");
			Instant serverStartTime = Instant.now();
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
					}
					Instant endTime = Instant.now();
					long elapsedTime = Duration.between(startTime, endTime).getSeconds() * 100;
					clientTimes.add(elapsedTime);
					System.out.println("Verweildauer: " + elapsedTime + " sek für Client mit der ID:" + userID);
					sleep((long) (Math.floor(Math.random() * (MAX_TIME_SPENT - MIN_TIME_SPENT + 1) + MIN_TIME_SPENT) * 10));
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
			} while (clientCounter < CLIENT_COUNT);
			Instant serverEndTime = Instant.now();
			long serverElapsedTime = Duration.between(serverStartTime, serverEndTime).toMinutes() * 100;
			long sumTime = 0;
			for (Long clientTime : clientTimes) {
				sumTime += clientTime;
			}
			long averageTimeSpendByClient = (sumTime / clientTimes.size());
			long maxTimeSpendByClient = Collections.max(clientTimes);
			long sumCounter = 0;
			for (int rejectCounter : Client.rejectCounters) {
				sumCounter += rejectCounter;
			}
			System.out.println("-----------------------------------------------------------------");
			System.out.println("ENDERGEBNIS DES MODELLS:");
			System.out.println("");
			System.out.println(" Serverlaufzeit: " + serverElapsedTime + " min");
			System.out.println(" Durschnittsverweildauer: " + ((int) averageTimeSpendByClient / 60) + " Stunden");
			System.out.println(" Maximale Verweildauer: " + ((int) maxTimeSpendByClient / 60) + " Stunden");
			System.out.println(" Durschnittliche Anzahl an Abweisungen vom Server an den Client: " + sumCounter / Client.rejectCounters.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
