package Model_Multi_Threaded;

import static Classes_Needed_For_Both_Models.Constants.CLIENT_COUNT;
import static Classes_Needed_For_Both_Models.Constants.SERVER_PORT;
import static Model_Multi_Threaded.HowToWorkWithUserMsg.ADD;
import static Model_Multi_Threaded.HowToWorkWithWorkerThread.IS_EMPTY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import Classes_Needed_For_Both_Models.Client;

public class Server extends Thread {

	static List<Long> clientTimes = new ArrayList<>();
	static int clientCounter = 0;
	static LinkedList<String> userMessages = new LinkedList<>();
	static LinkedList<WorkerThread> freeWorkerThreads = new LinkedList<>();

	static synchronized void incrementClientCounter() {
		clientCounter++;
	}

	static synchronized int getClientCounter() {
		return clientCounter;
	}

	static synchronized String workWithUserMsg(HowToWorkWithUserMsg addOrGet, String userMsg) {
		switch (addOrGet) {
		case GET:
			if (!userMessages.isEmpty()) {
				return userMessages.removeFirst();
			}
			return null;
		case ADD:
			userMessages.addLast(userMsg);
		default:
			return null;
		}
	}

	static synchronized boolean workWithWorkerThread(HowToWorkWithWorkerThread isEmptyOrAdd, WorkerThread workerThread) {
		switch (isEmptyOrAdd) {
		case IS_EMPTY:
			return freeWorkerThreads.isEmpty();
		case ADD:
			freeWorkerThreads.addLast(workerThread);
			return true;
		case REMOVE:
			freeWorkerThreads.remove(workerThread);
			return true;
		default:
			return false;
		}
	}

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
					if (!workWithWorkerThread(IS_EMPTY, new WorkerThread(1000000))) {
						conn = server.accept();
						clientIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						workWithUserMsg(ADD, clientIn.readLine());
					}
				} catch (IOException e) {
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
			} while (getClientCounter() < CLIENT_COUNT);
			Instant serverEndTime = Instant.now();
			long serverElapsedTime = Duration.between(serverStartTime, serverEndTime).getSeconds();
			System.out.println("Serverlaufzeit: " + serverElapsedTime + " min");
			long sumTime = 0;
			while (clientTimes.size() != CLIENT_COUNT) {
				//server is waiting for threads to finish work
			}
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
