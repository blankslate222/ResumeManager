package edu.sjsu.cmpe207.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ResumeService implements Runnable {

	private Socket socket;

	ResumeService() {

	}

	ResumeService(Socket clientSocket) {
		this.socket = clientSocket;
	}

	@Override
	public void run() {
		ServerSocket server = null;
		Socket clientSocket = null;

		try {
			while (true) {
				server = new ServerSocket(8888);
				System.out.println("Server listening on 8888");
				clientSocket = server.accept();
				System.out.println("Accepted connection");
				clientSocket.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (server != null) {
					server.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
