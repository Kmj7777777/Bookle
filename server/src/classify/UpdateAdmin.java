package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import database.BookleDB;

public class UpdateAdmin {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private BookleDB bookleDB = null;

	public UpdateAdmin(DataInputStream dis, DataOutputStream dos, String cmd) {
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}

	public void book_update() {
		bookleDB = new BookleDB();

		try {
			String book_id = dis.readUTF();
			int quantity = dis.readInt();
			if (bookleDB.getBook(book_id) == null) { // 존재하지 않는 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else {
				int result = bookleDB.updateBook(book_id, quantity);
				if (result > 0) { // 입력 성공 : 0
					dos.writeInt(0);
					dos.flush();
				} else { // 입력 실패 : 2
					dos.writeInt(2);
					dos.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void reservation_update() {
		bookleDB = new BookleDB();

		try {
			String today = dis.readUTF();
			ArrayList<String> expiredBookIdList = bookleDB.getExpiredBookIdList(today);
			int result = bookleDB.deleteReservation(today);
			if (result > 0) { // 삭제(예약 리스트 새로고침) 성공 : 0
				for (String book_id : expiredBookIdList) {
					bookleDB.updateBookQuantity(book_id, "inc");
				}
				dos.writeInt(0);
				dos.writeInt(result);
				dos.flush();
			} else { // 삭제(예약 리스트 새로고침) 실패 : 1
				dos.writeInt(1);
				dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void exe_command() {
		if (cmd.endsWith("book")) {
			book_update();
		} else if (cmd.endsWith("reservation")) {
			reservation_update();
		} else {

		}
	}
}
