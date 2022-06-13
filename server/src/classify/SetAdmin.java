package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import server.BookleServer;

public class SetAdmin {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;

	public SetAdmin(DataInputStream dis, DataOutputStream dos, String cmd) {
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}
	
	public void date_set() {
		try {
			long days = dis.readLong();
			BookleServer.time += days;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exe_command() {
		if (cmd.endsWith("date")) {
			date_set();
		} else {
			
		}
	}
}
