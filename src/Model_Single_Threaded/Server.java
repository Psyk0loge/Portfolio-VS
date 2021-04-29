package Model_Single_Threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
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
					long startTime = System.nanoTime();
					clientIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String userMsg = clientIn.readLine();
					int startOfUID = userMsg.indexOf(": ");
					int endOfUID = userMsg.indexOf("|");
					String userID = userMsg.substring((startOfUID + 1), endOfUID);
					System.out.println("Clientnachricht: " + userMsg);
					sleep((long) (Math.floor(Math.random() * (MAX - MIN + 1) + MIN) * 1000));
					long endTime = System.nanoTime();
					long elapsedTime = endTime - startTime;
					clientTimes.add(elapsedTime);
					long elapsedTimeInSeconds = elapsedTime / 1000000000;
					System.out.println("Verweildauer: " + elapsedTimeInSeconds + " sek für Client mit der ID:" + userID);
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
			System.out.println("Durschnittsverweildauer: " + averageTimeSpendByClient / 1000000000 + " sek");
			long maxTimeSpendByClient = Collections.max(clientTimes);
			System.out.println("Maximale Verweildauer: " + maxTimeSpendByClient / 1000000000 + " sek");
			long sumCounter = 0;
			System.out.println("rejectedCtrList ID: " + Client.rejectCounters.hashCode());
			for (int rejectCounter : Client.rejectCounters) {
				System.out.println("rejectCounter: " + rejectCounter);
				sumCounter += rejectCounter;
				System.out.println("sumCounter: " + sumCounter);
			}
			System.out.println("Durschnittliche Anzahl an Abweisungen vom Server an den Client: " + sumCounter / Client.rejectCounters.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().run();
	}

}
