package edu.sjsu.cmpe207.server;

import java.io.IOException;
import java.net.Socket;

class ResumeService implements Runnable{
	
	private Socket socket;
	
	ResumeService(){
		
	}
	ResumeService(Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
