package Model_Single_Threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server extends Thread {

	private static final int DEFAULT_PORT = 7777;
	private static final int MAX = 15;
	private static final int MIN = 2;

	@Override
	public void run() {
		BufferedReader clientIn;
		Random random = new Random();

		Socket conn = null;
		try {
			ServerSocket server = new ServerSocket(DEFAULT_PORT);
			System.out.println("Server eingericht!");
			while (true) {
				try {
					conn = server.accept();
					System.out.println("Client verbunden!");
					clientIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String userID = clientIn.readLine();
					System.out.println("Clientnachricht: " + userID);
					sleep((long) (Math.floor(Math.random() * (MAX - MIN + 1) + MIN) * 1000));
					
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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().run();
	}

}
