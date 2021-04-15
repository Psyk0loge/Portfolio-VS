package Model_Single_Threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
	//richtig sicker Server

	private static final int DEFAULT_PORT = 7777;

	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		BufferedReader clientIn;
		PrintWriter serverOut;

		Socket conn = null;
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Server eingericht!");
			while (true) {
				try {
					conn = server.accept();
					System.out.println("Client verbunden!");
					clientIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					serverOut = new PrintWriter(conn.getOutputStream());
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
