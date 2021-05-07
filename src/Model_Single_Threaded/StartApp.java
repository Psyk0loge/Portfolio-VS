package Model_Single_Threaded;

import static Model_Single_Threaded.Constants.CLIENT_COUNT;

public class StartApp {
	public static void main(String[] args) {
		new Server().start();
		for (int i = 1; i <= CLIENT_COUNT; i++) {
			new Client(i).start();
		}
	}
}
