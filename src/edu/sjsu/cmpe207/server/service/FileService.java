package edu.sjsu.cmpe207.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.sjsu.cmpe207.server.protocol.EnumResponse;
import edu.sjsu.cmpe207.server.protocol.ProtocolService;

public class FileService {
	private Socket clientSocket;
	private static boolean CLOSE = false;
	private BufferedReader in;
	private PrintWriter out;

	public FileService(Socket skt) {
		this.setClientSocket(skt);
	}

	FileService() {

	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	private void signalSocketClose() {
		CLOSE = true;
	}

	private void signalSocketOpen() {
		CLOSE = false;
	}

	private void closeSocket() throws IOException {
		clientSocket.close();
	}

	public void handleClientRequest() {

		ProtocolService protocol = null;
		String inputLine = "";
		EnumResponse resp = null;
		try {
			signalSocketOpen();
			protocol = new ProtocolService();
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);

			while (true) {
				// logic to process input request
				// then call recv or send file
				if ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
					if (checkByeFromClient(inputLine)) {
						closeSocket();
					}

					resp = protocol.processClientRequest(inputLine);
					System.out.println("response type = " + resp);
					switch (resp) {
					case ACKNOWLEDGE:
						acknowledgeConnectionRequest();
						break;
					case READ:
						receiveFile();
						break;
					case USAGE:
						sendUsage();
						break;
					case WRITE:
						sendFile();
						break;
					default:
						break;
					}
				}
				if (CLOSE) {
					break;
				}
			}
			clientSocket.shutdownInput();
			clientSocket.shutdownOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				closeSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void acknowledgeConnectionRequest() throws IOException {
		out.println("ack");
	}

	private void sendUsage() throws IOException {
		String usage = "Usage details - application run command with arguments:\n";
		usage += "java Client host port message [path]\n";
		usage += "host : ip address of server\n";
		usage += "port : port number the server is listening on\n";
		usage += "message : 12 character string\n";
		usage += "message : op(1 char) + UserID(10 char) + file version(1 char)\n";
		usage += "op : 1 = upload file - specify [file] argument - specify file version\n";
		usage += "op : 2 = download file - specify proper file version - same as uploaded file version\n";
		usage += "UserId : currently only digits(0-90 allowed - use the same UserId to upload/download\n";
		usage += "file version : specify version(single digit only 0 - 9)\n";
		usage += "[path] - path/to/file/fileName - optional\n";
		usage += "when uploading - specify path to target file\n";
		usage += "when downloading - specify path to download location \n";
		out.println(usage);
		out.println("bye");
	}

	private void receiveFile() throws IOException {
		System.out.println("receiving file");
		String line = null;
		while ((line = in.readLine()) != null) {
			if (checkByeFromClient(line)) {
				closeSocket();
			}
			out.println("bye");
			signalSocketClose();
			closeSocket();
			break;
		}
	}

	private void sendFile() throws IOException {
		out.println("bye");
	}

	private boolean checkByeFromClient(String line) {
		boolean bye = false;
		if (line.equals("bye")) {
			bye = true;
		}
		return bye;
	}

}
