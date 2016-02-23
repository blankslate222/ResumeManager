package edu.sjsu.cmpe207.server.singlethread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.sjsu.cmpe207.server.service.FileService;

/**
 * @author Nikhil
 *
 */
public class FileServer {
	/*
	 * Main class of single threaded server
	 */

	public static void main(String[] args) {
		FileServer server = new FileServer();
		server.serverService();
	}

	private void serverService() {
		ServerSocket server = null;
		FileService handler = null;
		Socket skt = null;

		try {
			server = new ServerSocket(8888);
			System.out.println("Server listening on 8888");
			while (true) {

				skt = server.accept();
				System.out.println("Connected to "
						+ skt.getInetAddress().getHostAddress());
				handler = new FileService(skt);
				handler.handleClientRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
