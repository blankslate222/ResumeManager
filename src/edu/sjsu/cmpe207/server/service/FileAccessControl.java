package edu.sjsu.cmpe207.server.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author Nikhil
 *
 */
public class FileAccessControl {
	/*
	 * Class containing static methods which are synchronized and hence, thread
	 * safe
	 */
	public static synchronized byte[] readFromFile(File fromFile) {

		byte[] contents = null;
		FileChannel fc = null;
		ByteBuffer buf = null;
		FileLock lock = null;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fromFile, "rw");
			fc = raf.getChannel();
			lock = fc.lock();
			buf = ByteBuffer.allocate((int) fc.size());
			fc.read(buf);
			contents = buf.array();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				lock.release();
				fc.close();
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contents;
	}

	public static synchronized int writeToFile(InputStream in, File toFile,
			int size) {

		FileOutputStream fOut = null;
		FileChannel outCh = null;
		FileLock lock = null;
		ByteBuffer bbuf = null;
		byte[] buf = null;
		int totalRead = 0;
		long len = 0;
		int result = 0;
		try {
			fOut = new FileOutputStream(toFile);
			outCh = fOut.getChannel();
			lock = outCh.lock();
			buf = new byte[1024];
			bbuf = ByteBuffer.wrap(buf);
			while (totalRead <= size) {
				len = in.read(buf);
				if (len != -1) {
					outCh.write(bbuf);
					bbuf.flip();
					totalRead += len;
				}
				if (len == -1 && totalRead == size) {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = -1;
		} catch (IOException e) {
			e.printStackTrace();
			result = -1;
		} finally {
			try {
				lock.release();
				fOut.close();
				outCh.close();
			} catch (IOException e) {
				e.printStackTrace();
				result = -1;
			}
		}
		return result;

	}
}
