package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import database.BookleDB;
import storage.ImageFile;
import storage.SummaryFile;
import table.Book;
import table.InventoryManagement;
import table.Member;
import table.Reservation;
import table.Standby;

public class ListAdmin {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private BookleDB bookleDB = null;

	public ListAdmin(DataInputStream dis, DataOutputStream dos, String cmd) {
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}

	public void reservation_List() {
		bookleDB = new BookleDB();

		try {
			String keyword = dis.readUTF();
			ArrayList<Reservation> list = bookleDB.getReservationList(keyword);
			int listSize = list.size();
			dos.writeInt(listSize);
			for (Reservation tmp : list) {
				dos.writeUTF(tmp.getReserved_date());
				dos.writeUTF(tmp.getBook_id());
				Book book = bookleDB.getBook(tmp.getBook_id());
				dos.writeUTF(book.getTitle());
				dos.writeUTF(tmp.getMember_id());
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getName());
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void member_List() {
		bookleDB = new BookleDB();

		try {
			String keyword = dis.readUTF();
			String option = dis.readUTF();
			ArrayList<Member> list = bookleDB.getMemberList(keyword, option);
			int listSize = list.size();
			dos.writeInt(listSize);
			for (Member tmp : list) {
				dos.writeUTF(tmp.getId());
				dos.writeUTF(tmp.getName());
				dos.writeUTF(tmp.getGender());
				dos.writeUTF(tmp.getBirth());
				dos.writeUTF(tmp.getPhone());
				if (tmp.getE_mail() == null) {
					dos.writeUTF("");
				} else {
					dos.writeUTF(tmp.getE_mail());
				}
				if (option.equals("all_admin")) {
					dos.writeInt(tmp.getCurrent_rental());
					dos.writeInt(tmp.getPossible_rental());
				}
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void book_List() {
		bookleDB = new BookleDB();

		try {
			String keyword = dis.readUTF();
			String option = dis.readUTF();
			ArrayList<Book> list = bookleDB.getBookList(keyword, option);
			int listSize = list.size();
			dos.writeInt(listSize);
			for (Book tmp : list) {
				dos.writeUTF(tmp.getId());
				dos.writeUTF(tmp.getTitle());
				dos.writeUTF(tmp.getWriter());
				dos.writeUTF(tmp.getPublisher());
				dos.writeUTF(tmp.getCategory());
				if (option.equals("all_admin") || option.equals("all_client")) {
					dos.writeInt(tmp.getCurrent_quantity());
					dos.writeInt(tmp.getTotal_quantity());
					if (option.equals("all_client")) {
						ImageFile image = new ImageFile(tmp.getId());
						dos.writeLong(image.getFileLength());
						dos.write(image.getImageFile());
						SummaryFile summary = new SummaryFile(tmp.getId());
						dos.writeLong(summary.getFileLength());
						dos.write(summary.getSummaryFile());
					}
				}
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void return_List() {
		bookleDB = new BookleDB();

		try {
			String keyword = dis.readUTF();
			ArrayList<InventoryManagement> list = bookleDB.getInventoryManagementList(keyword, "return");
			int listSize = list.size();
			dos.writeInt(listSize);
			for (InventoryManagement tmp : list) {
				dos.writeUTF(tmp.getRental_date());
				dos.writeUTF(tmp.getReturn_date());
				dos.writeUTF(tmp.getBook_id());
				Book book = bookleDB.getBook(tmp.getBook_id());
				dos.writeUTF(book.getTitle());
				dos.writeUTF(tmp.getMember_id());
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getName());
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void rental_List() {
		bookleDB = new BookleDB();

		try {
			String keyword = dis.readUTF();
			ArrayList<InventoryManagement> list = bookleDB.getInventoryManagementList(keyword, "rental");
			int listSize = list.size();
			dos.writeInt(listSize);
			for (InventoryManagement tmp : list) {
				dos.writeUTF(tmp.getRental_date());
				dos.writeUTF(tmp.getBook_id());
				Book book = bookleDB.getBook(tmp.getBook_id());
				dos.writeUTF(book.getTitle());
				dos.writeUTF(tmp.getMember_id());
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getName());
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void bookDetail_List() {
		bookleDB = new BookleDB();

		try {
			String book_id = dis.readUTF();

			ArrayList<InventoryManagement> imList = bookleDB.getRentalListByBook(book_id);
			ArrayList<Reservation> reservList = bookleDB.getReservationListByBook(book_id);

			int listSize = imList.size();
			dos.writeInt(listSize);
			for (InventoryManagement tmp : imList) {
				dos.writeUTF(tmp.getRental_date());
				dos.writeUTF(tmp.getReturn_date());
				dos.writeUTF(tmp.getMember_id());
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getName());
				dos.writeUTF(member.getPhone());
				if (member.getE_mail() == null) {
					dos.writeUTF("");
				} else {
					dos.writeUTF(member.getE_mail());
				}
			}

			listSize = reservList.size();
			dos.writeInt(listSize);
			for (Reservation tmp : reservList) {
				dos.writeUTF(tmp.getReserved_date());
				dos.writeUTF(tmp.getMember_id());
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getName());
				dos.writeUTF(member.getPhone());
				if (member.getE_mail() == null) {
					dos.writeUTF("");
				} else {
					dos.writeUTF(member.getE_mail());
				}
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void memberDetail_List() {
		bookleDB = new BookleDB();

		try {
			String member_id = dis.readUTF();
			String option = dis.readUTF();

			ArrayList<InventoryManagement> imList = bookleDB.getRentalListByMember(member_id);
			ArrayList<Reservation> reservList = bookleDB.getReservationListByMember(member_id);
			int listSize = imList.size();
			if (option.equals("rental") || option.equals("all")) {
				dos.writeInt(listSize);
				for (InventoryManagement tmp : imList) {
					dos.writeUTF(tmp.getRental_date());
					dos.writeUTF(tmp.getReturn_date());
					dos.writeUTF(tmp.getBook_id());
					Book book = bookleDB.getBook(tmp.getBook_id());
					dos.writeUTF(book.getTitle());
					dos.writeUTF(book.getWriter());
					dos.writeUTF(book.getPublisher());
				}
				dos.flush();
			}

			if (option.equals("reservation") || option.equals("all")) {
				listSize = reservList.size();
				dos.writeInt(listSize);
				for (Reservation tmp : reservList) {
					dos.writeUTF(tmp.getReserved_date());
					dos.writeUTF(tmp.getBook_id());
					Book book = bookleDB.getBook(tmp.getBook_id());
					dos.writeUTF(book.getTitle());
					dos.writeUTF(book.getWriter());
					dos.writeUTF(book.getPublisher());
				}
				dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void standbyMember_List() {
		bookleDB = new BookleDB();

		try {
			String member_id = dis.readUTF();

			ArrayList<Standby> standbyList = bookleDB.getStandbyByMember(member_id);
			int listSize = standbyList.size();
			dos.writeInt(listSize);
			for (Standby tmp : standbyList) {
				Book bookList = bookleDB.getBook(tmp.getBook_id());
				dos.writeUTF(bookList.getId());
				dos.writeUTF(bookList.getTitle());
				dos.writeUTF(bookList.getWriter());
				dos.writeUTF(bookList.getPublisher());
				dos.writeUTF(tmp.getOrder_number() + "");
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void standbyBook_List() {
		bookleDB = new BookleDB();

		try {
			String book_id = dis.readUTF();

			ArrayList<Standby> standbyList = bookleDB.getStandbyList(book_id);
			int listSize = standbyList.size();
			dos.writeInt(listSize);
			for (Standby tmp : standbyList) {
				Member member = bookleDB.getMember(tmp.getMember_id());
				dos.writeUTF(member.getId());
				dos.writeUTF(member.getName());
				dos.writeUTF(member.getPhone());
				if (member.getE_mail() == null) {
					dos.writeUTF("");
				} else {
					dos.writeUTF(member.getE_mail());
				}
				dos.writeUTF(tmp.getOrder_number() + "");
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}

	public void exe_command() {

		if (cmd.endsWith("rental")) {
			rental_List();
		} else if (cmd.endsWith("return")) {
			return_List();
		} else if (cmd.endsWith("book")) {
			book_List();
		} else if (cmd.endsWith("book_detail")) {
			bookDetail_List();
		} else if (cmd.endsWith("member")) {
			member_List();
		} else if (cmd.endsWith("member_detail")) {
			memberDetail_List();
		} else if (cmd.endsWith("reservation")) {
			reservation_List();
		} else if (cmd.endsWith("standby_m")) {
			standbyMember_List();
		} else if (cmd.endsWith("standby_b")) {
			standbyBook_List();
		} else {

		}
	}
}
