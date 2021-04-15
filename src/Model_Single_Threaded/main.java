package Model_Single_Threaded;

public class main {
	public static void main(String[] args) {
		Server.main(args);
		for (int i = 1; i <= 52; i++) {
			Client a = new Client(i);
			a.run();
		}
	}
}
