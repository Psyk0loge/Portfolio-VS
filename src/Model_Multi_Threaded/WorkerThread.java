package Model_Multi_Threaded;

import static Classes_Needed_For_Both_Models.Constants.CLIENT_COUNT;
import static Classes_Needed_For_Both_Models.Constants.MAX_TIME_SPENT;
import static Classes_Needed_For_Both_Models.Constants.MIN_TIME_SPENT;
import static Model_Multi_Threaded.HowToWorkWithUserMsg.GET;
import static Model_Multi_Threaded.HowToWorkWithWorkerThread.ADD;
import static Model_Multi_Threaded.HowToWorkWithWorkerThread.REMOVE;
import static Model_Multi_Threaded.Server.clientCounter;
import static Model_Multi_Threaded.Server.clientTimes;
import static Model_Multi_Threaded.Server.getClientCounter;
import static Model_Multi_Threaded.Server.incrementClientCounter;
import static Model_Multi_Threaded.Server.workWithUserMsg;
import static Model_Multi_Threaded.Server.workWithWorkerThread;

import java.time.Duration;
import java.time.Instant;

public class WorkerThread extends Thread {
	int workerThreadID;

	public WorkerThread(int workerThreadID) {
		this.workerThreadID = workerThreadID;
	}

	private synchronized void addElapsedTime(long elapsedTime) {
		clientTimes.add(elapsedTime);
	}

	@Override
	public void run() {
		System.out.println("Thread-" + getId() + " wurde gestartet!");
		workWithWorkerThread(ADD, this);
		do {
			String userMsg = workWithUserMsg(GET, "");
			if (userMsg != null) {
				try {
					incrementClientCounter();
					workWithWorkerThread(REMOVE, this);
					System.out.println("Client Nr." + clientCounter + " verbunden!");
					int startOfUID = userMsg.indexOf(": ");
					int endOfUID = userMsg.indexOf("|");
					int startTimeIndex = userMsg.indexOf("Server: ");
					String userID = userMsg.substring((startOfUID + 1), endOfUID);
					System.out.println("Thread-" + getId() + " hat die Verarbeitung des Clients mit der ID: " + userID + " gestartet!");
					Instant startTime = null;
					if (startTimeIndex != -1) {
						startTime = Instant.parse(userMsg.substring((startTimeIndex + 8)));
						System.out.println("Verweildauer vor erfolgreichem Verbindungsaufbau: " + startTime + "des Client mit der ID: " + userID);
					}
					sleep((long) (Math.floor(Math.random() * (MAX_TIME_SPENT - MIN_TIME_SPENT + 1) + MIN_TIME_SPENT) * 1000));
					Instant endTime = Instant.now();
					long elapsedTime = Duration.between(startTime, endTime).getSeconds();
					addElapsedTime(elapsedTime);
					System.out.println("Verweildauer: " + elapsedTime + " sek für Client mit der ID:" + userID);
					workWithWorkerThread(ADD, this);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (getClientCounter() < CLIENT_COUNT);

	}
}