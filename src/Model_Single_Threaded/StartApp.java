package Model_Single_Threaded;

import static Classes_Needed_For_Both_Models.Constants.CLIENT_COUNT;

import Classes_Needed_For_Both_Models.Client;

public class StartApp {
	public static void main(String[] args) {
		new Server().start();
		for (int i = 1; i <= CLIENT_COUNT; i++) {
			new Client(i).start();
		}
	}
}
