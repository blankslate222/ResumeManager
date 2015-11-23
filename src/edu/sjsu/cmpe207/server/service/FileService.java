package edu.sjsu.cmpe207.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.sjsu.cmpe207.server.bean.ClientData;
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
		clientSocket.shutdownInput();
		clientSocket.shutdownOutput();
		clientSocket.close();
	}

	public void handleClientRequest() {

		ProtocolService protocol = null;
		ClientData clientData = null;
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
					// System.out.println(inputLine);
					if (checkByeFromClient(inputLine)) {
						closeSocket();
					}
					clientData = new ClientData();
					resp = protocol.processClientRequest(inputLine, clientData);
					// System.out.println("response type = " + resp);
					switch (resp) {
					case ACKNOWLEDGE:
						acknowledgeConnectionRequest();
						break;
					case READ:
						receiveFile(clientData);
						signalSocketClose();
						break;
					case USAGE:
						sendUsage();
						signalSocketClose();
						break;
					case WRITE:
						sendFile(clientData);
						signalSocketClose();
						break;
					default:
						signalSocketClose();
						break;
					}
				}
				if (CLOSE) {
					break;
				}
			}
			// clientSocket.shutdownInput();
			// clientSocket.shutdownOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
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
		try {
			out.println(usage);
		} finally {
			out.println("bye");
		}
	}

	private String getFilePath(ClientData clientData) {
		String separator = File.separator;
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home"));
		sb.append(separator);
		sb.append(clientData.getUserId());
		sb.append(separator);
		sb.append(clientData.getFileVersion());
		String filePath = sb.toString();
		return filePath;
	}

	private void receiveFile(ClientData clientData) throws IOException {

		String path = "";
		File directory = null;
		File prevDir = null;
		File destinationFile = null;
		byte[] buf = null;
		InputStream in = null;
		FileOutputStream out = null;

		System.out.println("receiving file...");
		acknowledgeConnectionRequest();
		path = getFilePath(clientData);
		System.out.println("saving in path = " + path);
		String prevPath = "";
		for (int i = path.length() - 1; i >= 0; i--) {
			if (path.charAt(i) == File.separatorChar) {
				prevPath = path.substring(0, i);
				break;
			}
		}
		prevDir = new File(prevPath);
		if (!prevDir.exists()) {
			prevDir.mkdir();
		}
		directory = new File(path);
		if (!directory.exists()) {
			directory.mkdir();
		}
		destinationFile = new File(path + File.separator
				+ clientData.getUserId() + ".pdf");
		if (!destinationFile.exists()) {
			destinationFile.createNewFile();
		}

		buf = new byte[8192];
		int len = 0;
		in = getClientSocket().getInputStream();
		out = new FileOutputStream(destinationFile);

		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		out.close();
	}

	private void sendFile(ClientData clientData) throws IOException {

		String path = "";
		File destinationDir = null;
		File destinationFile = null;
		FileInputStream is = null;
		OutputStream os = null;
		byte[] buf = null;

		System.out.println("sending file...");
		acknowledgeConnectionRequest();

		path = getFilePath(clientData);
		// System.out.println("sending file from path = " + path);
		destinationDir = new File(path);
		if (!destinationDir.exists()) {
			System.out.println("Error: Directory does not exist");
			return;
		}
		if (destinationDir.list().length <= 0) {
			System.out.println("Error: File does not exist");
			return;
		}
		if (destinationDir.list().length > 1) {
			System.out.println("Error: Too many files - can send only 1 file");
			return;
		}
		for (File f : destinationDir.listFiles()) {
			destinationFile = f;
		}
		
		System.out.println("sending file from path = "
				+ destinationFile.getAbsolutePath());
		is = new FileInputStream(destinationFile);
		os = getClientSocket().getOutputStream();
		buf = new byte[8192];
		int len = 0;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
		is.close();
	}

	private boolean checkByeFromClient(String line) {
		boolean bye = false;
		if (line.equals("bye")) {
			bye = true;
		}
		return bye;
	}

}
