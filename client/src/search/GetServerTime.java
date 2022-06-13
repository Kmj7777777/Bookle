package search;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetServerTime extends Thread{
	private Socket sock;
	private DataInputStream sock_in;
	private DataOutputStream sock_out;

	public static long serverTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
	
	public GetServerTime() {
		try {
			sock = new Socket(main.Main.serverIP, main.Main.serverHost);
			sock_in = new DataInputStream(sock.getInputStream());
			sock_out = new DataOutputStream(sock.getOutputStream());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				sock_out.writeUTF("get_date");
				sock_out.flush();
				
				serverTime = sock_in.readLong();
				String today = sdf.format(new Date(serverTime));
				Main.serverDateLabel.setText(today);
				
				Thread.sleep(1000);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}