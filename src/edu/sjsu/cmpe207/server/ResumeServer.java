package edu.sjsu.cmpe207.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ResumeServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	public void serverService() {
		ServerSocket server = null;
		Socket clientSocket = null;
		ResumeService handler = null;
		System.out.println("Server listening on 8888");

		try {
			server = new ServerSocket(8888);

			while (true) {

				clientSocket = server.accept();
				System.out.print("Server has connected!\n");

				handler = new ResumeService(clientSocket);
				
				clientSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (server != null){
					server.close();
				}
				if (clientSocket != null){
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
