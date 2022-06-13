package server;

public class DateThread extends Thread {
	
	public DateThread() {

	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				BookleServer.time += 1000;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
