package Model_Multi_Threaded;

public class WorkerThread extends Thread {

	private String clientToHandle = null;
	private String msgOfClientToHandle = null;

	public WorkerThread() {

	}

	@Override
	public void run() {
		System.out.println("Thread-" + getId() + " wurde gestartet!");
		while (true) {
			if (clientToHandle != null && msgOfClientToHandle != null) {
				System.out.println("Thread-" + getId() + " hat die Verarbeitung des Clients mit der ID" + clientToHandle + " gestartet!");

			}
		}
	}
}
