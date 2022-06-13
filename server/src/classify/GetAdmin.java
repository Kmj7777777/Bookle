package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import database.BookleDB;
import server.BookleServer;

public class GetAdmin {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private BookleDB bookleDB = null;

	public GetAdmin(DataInputStream dis, DataOutputStream dos, String cmd) {
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}

	public void lastId_get() {
		bookleDB = new BookleDB();

		try {
			String category = dis.readUTF();
			String lastId = bookleDB.getLastIdOfBook(category);
			int num = Integer.parseInt(lastId.substring(1));
			String plusOne = String.format("%04d", ++num);
			String result = lastId.substring(0, 1) + plusOne;
			dos.writeUTF(result);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// bookleDB.closeConnection();
	}

	public void orderNum_get() {
		bookleDB = new BookleDB();

		try {
			String book_id = dis.readUTF();

			int order_number = bookleDB.getOrderNumber(book_id);
			dos.writeInt(order_number);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void date_get() {
		try {
			dos.writeLong(BookleServer.time);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exe_command() {
		if (cmd.endsWith("lastId")) {
			lastId_get();
		} else if (cmd.endsWith("date")) {
			date_get();
		} else if (cmd.endsWith("orderNum")) {
			orderNum_get();
		} else {

		}
	}
}
