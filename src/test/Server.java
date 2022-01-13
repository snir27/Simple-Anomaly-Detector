package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static final int TIME_OUT = 20000000;
	private Integer clientLimit = 1;
	private Integer count = 0;
	private Socket currentClient;

	public interface ClientHandler {
		void handleClient(Socket currentClient);
	}

	volatile boolean stop;

	public Server() {
		stop = false;
	}

	private void startServer(int port, ClientHandler ch) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(TIME_OUT);
			while (!stop) {
				currentClient = serverSocket.accept();
				if (count < clientLimit) {
					count++;
					ch.handleClient(currentClient);
					currentClient.close();
					count--;
				}
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// runs the server in its own thread
	public void start(int port, ClientHandler ch) {
		new Thread(() -> startServer(port, ch)).start();
	}

	public void stop() {
		stop = true;
	}

}
