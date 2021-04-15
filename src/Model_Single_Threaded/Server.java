package Model_Single_Threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static final int DEFAULT_PORT = 7777;

	public static void main(String[] args) {
		BufferedReader clientIn;

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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
