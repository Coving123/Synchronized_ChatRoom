package Client;

public class Client1 {
	public static void main(String[] args) {
		ClientThread chatClient = new ClientThread();
		chatClient.launch();
	}
}