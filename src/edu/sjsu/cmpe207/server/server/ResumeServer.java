package edu.sjsu.cmpe207.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.sjsu.cmpe207.server.service.ConcurrentFileService;
import edu.sjsu.cmpe207.server.service.StatService;

/**
 * @author Nikhil
 *
 */
public class ResumeServer implements Runnable {
	/*
	 * Main class of multi-threaded server creates the required threads and
	 * keeps listening on the server_thread on port 8888
	 */
	public static void main(String[] args) {

		StatService statServ = null;
		ResumeServer server = new ResumeServer();
		Thread serverThread = new Thread(server, "server_thread");
		serverThread.start();
		statServ = new StatService();
		Thread statThread = new Thread(statServ, "stat_thread");
		statThread.start();
		Thread mainThread = Thread.currentThread();
		mainThread.setName("main_thread");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ConcurrentFileService service = null;
		ServerSocket server = null;
		Socket clientSock = null;

		try {
			server = new ServerSocket(8888);
			System.out.println("listening on 8888");
			while (true) {
				clientSock = server.accept();
				service = new ConcurrentFileService(clientSock);

				Thread serviceThread = new Thread(service, "service_thread");
				serviceThread.start();

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
