package edu.sjsu.cmpe207.server.service;

/**
 * @author Nikhil
 *
 */
public class StatService implements Runnable {

	@Override
	public void run() {
		/*
		 * this overriding run method keeps runs an infinite loop which keeps
		 * printing at regular intervals, the number of active threads at any
		 * point in time
		 */
		while (true) {
			int count = Thread.activeCount();
			System.out.println("num of active threads = " + count);
			Thread[] tArr = new Thread[count];
			Thread.enumerate(tArr);

			for (Thread th : tArr) {
				System.out.println(th);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
