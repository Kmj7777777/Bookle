package server;

import java.net.ServerSocket;
import java.net.Socket;

public class BookleServer {
	
	public static long time = System.currentTimeMillis();
	
	public static void main(String[] args) {
		new DateThread().start();
		
		try {
			ServerSocket server = new ServerSocket(30731);
			System.out.println("Server Start");
			while (true) {
				Socket sock = server.accept();
				SockThread st = new SockThread(sock);
				st.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}