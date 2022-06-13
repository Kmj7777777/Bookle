package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;

import classify.Account;
import classify.DeleteFunc;
import classify.GetAdmin;
import classify.InsertAdmin;
import classify.ListAdmin;
import classify.SetAdmin;
import classify.UpdateAdmin;

public class SockThread extends Thread {

	private Socket sock = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private HashMap<String, String> userMap = new HashMap<String, String>();

	private void connectStream() {

		try {
			dis = new DataInputStream(sock.getInputStream());
			dos = new DataOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			System.out.println("Stream connect FAIL");
		}
	}

	public void run() {
		System.out.println(sock.getInetAddress() + " 연결");
		connectStream();
		while (true) {
			try {
				cmd = dis.readUTF();
				if (!cmd.equals("get_date")) {
					System.out.println("cmd 확인 : " + cmd);
				}

				// Account
				if (cmd.startsWith("account")) {
					new Account(sock, dis, dos, cmd).exe_command(userMap);

					// ListAdmin
				} else if (cmd.startsWith("list")) {
					// 관리자
					new ListAdmin(dis, dos, cmd).exe_command();
					// InsertAdmin
				} else if (cmd.startsWith("insert")) {
					// 관리자
					new InsertAdmin(sock, dis, dos, cmd).exe_command(userMap);

				} else if (cmd.startsWith("update")) {
					new UpdateAdmin(dis, dos, cmd).exe_command();

				} else if (cmd.startsWith("delete")) {
					new DeleteFunc(dis, dos, cmd).exe_command();
				} else if (cmd.startsWith("get")) {
					new GetAdmin(dis, dos, cmd).exe_command();

				} else if (cmd.startsWith("set")) {
					new SetAdmin(dis, dos, cmd).exe_command();

				} else if (cmd.startsWith("cancel")) {
					System.out.println("종료");

				} else {
					dos.writeUTF("cmdError"); // 잘못된 커맨드를 받음
				}
			} catch (Exception e) {
				try {
					dos.writeUTF("not_command");
					dos.flush();
				} catch (Exception e1) {
					try {
						sock.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					sock = null;
				}
				break;
			}
		}
	}

	public SockThread(Socket sock) {
		this.sock = sock;
	}
}