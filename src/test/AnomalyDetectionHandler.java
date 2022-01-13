package test;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler {

	public class SocketIO implements DefaultIO {

		Scanner in;
		PrintWriter out;

		public SocketIO(Socket currentClient) {
			try {
				out = new PrintWriter(currentClient.getOutputStream());
				in = new Scanner(currentClient.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public String readText() {
			return in.nextLine();
		}

		@Override
		public void write(String text) {
			out.print(text);
			out.flush();
		}

		@Override
		public float readVal() {
			return in.nextFloat();
		}

		@Override
		public void write(float val) {
			out.print(val);
			out.flush();
		}

		public void close() {
			in.close();
			out.close();
		}
	}

	public void start(SocketIO socket) {
		CLI cli = new CLI(socket);
		cli.start();
	}

	@Override
	public void handleClient(Socket currentClient) {
		SocketIO clientIo = new SocketIO(currentClient);
		start(clientIo);
		clientIo.close();
	}



}
