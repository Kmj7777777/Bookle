package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import database.BookleDB;

public class DeleteFunc {

	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private BookleDB bookleDB = null;

	public DeleteFunc(DataInputStream dis, DataOutputStream dos, String cmd) {
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}

	public void reservationCancel() {
		// member_id, book_id
		try {
			bookleDB = new BookleDB();
			String member_id = dis.readUTF();
			String book_id = dis.readUTF();
			String date = dis.readUTF();

			int result = bookleDB.deleteReservation_Standby(1, date, member_id, book_id);
			if (result > 0) {
				dos.writeInt(0);
			} else {
				dos.writeInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// bookleDB.closeConnection();
	}

	public void standbyCancel() {
		try {
			bookleDB = new BookleDB();
			String member_id = dis.readUTF();
			String book_id = dis.readUTF();
			String order_number = dis.readUTF();

			int result = bookleDB.deleteReservation_Standby(2, order_number, member_id, book_id);
			if (result > 0) {
				dos.writeInt(0);
			} else {
				dos.writeInt(1);
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// bookleDB.closeConnection();
	}

	public void exe_command() {
		if (cmd.endsWith("reservation_cancel")) {
			reservationCancel();
		} else if (cmd.endsWith("standby_cancel")) {
			standbyCancel();
		}
	}
}
