package edu.sjsu.cmpe207.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.sjsu.cmpe207.server.bean.ClientData;
import edu.sjsu.cmpe207.server.protocol.EnumResponse;
import edu.sjsu.cmpe207.server.protocol.ProtocolService;

/**
 * @author Nikhil
 *
 */
public class ConcurrentFileService implements Runnable {
	/*
	 * Service class which contains helper methods and methods which make use of
	 * the synchronized file IO methods defined in FileAccessControl.java
	 */
	private Socket clientSocket;
	private boolean CLOSE = false;
	private BufferedReader in;
	private PrintWriter out;

	public ConcurrentFileService(Socket clientSock) {
		this.clientSocket = clientSock;
	}

	public ConcurrentFileService() {

	}

	@Override
	public void run() {

		ProtocolService protocol = null;
		ClientData clientData = null;
		String inputLine = "";
		EnumResponse resp = null;
		long startTime = 0l;
		try {
			startTime = System.nanoTime();
			signalSocketOpen();
			protocol = new ProtocolService();
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);

			while (true) {

				if ((inputLine = in.readLine()) != null) {

					if (checkByeFromClient(inputLine)) {
						closeSocket();
					}
					clientData = new ClientData();
					resp = protocol.processClientRequest(inputLine, clientData);

					switch (resp) {
					case ACKNOWLEDGE:
						acknowledgeConnectionRequest(0l);
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeSocket();
				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				duration /= 1000000;
				System.out.println("request serviced in " + duration + " ms");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	private void acknowledgeConnectionRequest(long l) throws IOException {
		String ack = "ack";
		if (l > 0) {
			ack += l;
		}
		out.println(ack);
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
		System.out.println("running from thread - "
				+ Thread.currentThread().getName());
		int size = 0;
		String path = "";
		File directory = null;
		File prevDir = null;
		File destinationFile = null;
		InputStream in = null;

		System.out.println("receiving file...");
		acknowledgeConnectionRequest(0l);
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
				+ clientData.getUserId());
		if (!destinationFile.exists()) {
			destinationFile.createNewFile();
		}
		size = clientData.getFileSize();
		in = clientSocket.getInputStream();

		int resp = FileAccessControl.writeToFile(in, destinationFile, size);
		if (resp < 0) {
			System.out.println("file not received properly");
			return;
		}
	}

	private void sendFile(ClientData clientData) throws IOException {
		System.out.println("running from thread - "
				+ Thread.currentThread().getName());
		String path = "";
		File destinationDir = null;
		File destinationFile = null;
		FileInputStream is = null;
		OutputStream os = null;
		byte[] buf = null;

		System.out.println("sending file...");

		path = getFilePath(clientData);
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

		acknowledgeConnectionRequest(destinationFile.length());
		System.out.println("sending file from path = "
				+ destinationFile.getAbsolutePath());
		is = new FileInputStream(destinationFile);

		os = clientSocket.getOutputStream();
		byte[] fileArray = FileAccessControl.readFromFile(destinationFile);

		int len = 0;
		while (len < fileArray.length) {
			buf = new byte[1024];
			int remain = (int) (destinationFile.length() - len);
			int length = remain > 1024 ? 1024 : remain;
			System.arraycopy(fileArray, len, buf, 0, length);
			os.write(buf, 0, length);
			os.flush();
			len += length;
		}
		System.out.println("done sending file");
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
