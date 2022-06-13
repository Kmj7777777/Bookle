package classify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import database.BookleDB;
import server.BookleServer;
import table.Standby;

public class InsertAdmin {
	private Socket sock = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
	private BookleDB bookleDB = null;

	public InsertAdmin(Socket sock, DataInputStream dis, DataOutputStream dos, String cmd) {
		this.sock = sock;
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}

	public void reservation_standby_insert(HashMap userMap, int type) {
		// type 1 : 예약
		// type 2 : 대기
		bookleDB = new BookleDB();

		try {
			String reserved_date = null;

			if (type == 1) { // type가 1인 경우
				reserved_date = dis.readUTF();
			}

			String book_id = dis.readUTF();
			String ip = this.sock.getInetAddress().toString();
			System.out.println(ip);
			String member_id = userMap.get(ip).toString();
			if (bookleDB.getBook(book_id) == null) { // 존재하지 않는 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else if (bookleDB.getMember(member_id) == null) { // 존재하지 않는 회원 ID : 2
				dos.writeInt(2);
				dos.flush();
			} else if (bookleDB.getBookCount("standby", member_id) < 7) {
				int current_quantity = bookleDB.getCurrentQuantityOfBook(book_id);

				if (current_quantity > 0) { // 현재 책의 수량이 1권 이상일 때
					System.out.println("재고가 0 이상입니다. 예약을 진행합니다.");
					int result1 = bookleDB.insertReservation(reserved_date, book_id, member_id);
					if (result1 > 0) {
						int result2 = bookleDB.updateBookQuantity(book_id, "dec");
						if (result2 > 0) { // 예약 입력, 도서 재고 감소 성공 : 0
							System.out.println("예약 성공");
							dos.writeInt(0);
							dos.writeInt(bookleDB.getCurrentQuantityOfBook(book_id));
							dos.flush();
						} else {

						}
					} else { // 입력 실패 : 3
						dos.writeInt(3);
						dos.flush();
					}
				} else { // 책의 수량이 없을 때
					System.out.println("재고가 0입니다. 대기자 번호를 뽑습니다.");
					int total = bookleDB.getBook(book_id).getTotal_quantity();
					int result[] = new int[2];
					result = bookleDB.insertStandby(book_id, member_id, total);
					if (result[0] > 0) {
						System.out.println("일단 성공");
						dos.writeInt(0);
						dos.writeInt(result[1]);
					} else {
						System.out.println("예약 불가능");
						dos.writeInt(3); // 예약 불가능 : 3
						dos.writeInt(-1);
					}
				}
			} else {
				dos.writeInt(4);
				dos.writeInt(0);
				dos.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void return_insert() {
		bookleDB = new BookleDB();

		try {
			String return_date = dis.readUTF();
			String rental_date = dis.readUTF();
			String book_id = dis.readUTF();
			String member_id = dis.readUTF();
			if (bookleDB.getBook(book_id) == null) { // 존재하지 않는 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else if (bookleDB.getMember(member_id) == null) { // 존재하지 않는 회원 ID : 2
				dos.writeInt(2);
				dos.flush();
			} else {
				if (bookleDB.getRentalInfo(rental_date, book_id, member_id) == null) { // 존재하지 않는 대여 정보 : 4
					dos.writeInt(4);
					dos.flush();
				} else {
					int result1 = bookleDB.updateInventoryManagement(rental_date, return_date, book_id, member_id);
					if (result1 > 0) {
						int result2 = bookleDB.updateBookQuantity(book_id, "inc");
						if (result2 > 0) {
							int result3 = bookleDB.updateMemberRental(member_id, "dec");
							if (result3 > 0) { // 반납 입력, 도서 재고 증가, 회원 대여 갯수 감소 성공 : 0
								// 도서 재고가 증가함에 따라 해당 도서의 예약 대기가 있는지 확인 후 처리 필요
								ArrayList<Standby> list = bookleDB.getStandbyList(book_id);
								if (list.size() > 0) {
									String first_member_id = list.get(0).getMember_id();
									long time = BookleServer.time;
									time += (86400000 * 3);
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
									String reserved_date = sdf.format(new Date(time));
									bookleDB.insertReservation(reserved_date, book_id, first_member_id);
									bookleDB.updateBookQuantity(book_id, "dec");
									bookleDB.deleteStandbyFirstMember(book_id, first_member_id);
									bookleDB.updateStandbyPriority(book_id);
								}
								dos.writeInt(0);
								dos.flush();
							} else {
								// 입력 초기화 해야할 듯
							}
						} else {
							// 입력 초기화 해야할 듯
						}
					} else { // 입력 실패 : 3
						dos.writeInt(3);
						dos.flush();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rental_insert() {
		bookleDB = new BookleDB();

		try {
			String rental_date = dis.readUTF();
			String book_id = dis.readUTF();
			String member_id = dis.readUTF();
			if (bookleDB.getBook(book_id) == null) { // 존재하지 않는 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else if (bookleDB.getMember(member_id) == null) { // 존재하지 않는 회원 ID : 2
				dos.writeInt(2);
				dos.flush();
			} else if (bookleDB.getBookCount("inventory_management", member_id) < 7) { // ------------>수정 부분
				int result1 = bookleDB.insertInventoryManagement(rental_date, "대여 중", book_id, member_id);
				if (result1 > 0) {
					int result2 = bookleDB.updateBookQuantity(book_id, "dec");
					if (result2 > 0) {
						int result3 = bookleDB.updateMemberRental(member_id, "inc");
						if (result3 > 0) { // 대여 입력, 도서 재고 감소, 회원 대여 갯수 증가 성공 : 0
							dos.writeInt(0);
							dos.flush();
						} else {
							// 입력 초기화 해야할 듯
						}
					} else {
						// 입력 초기화 해야할 듯
					}
				} else { // 입력 실패 : 3
					dos.writeInt(3);
					dos.flush();
				}
			} else {
				dos.writeInt(3);
				dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rental_reserved_insert() {
		bookleDB = new BookleDB();

		try {
			String rental_date = dis.readUTF();
			String reserved_date = dis.readUTF();
			String book_id = dis.readUTF();
			String member_id = dis.readUTF();
			if (bookleDB.getBook(book_id) == null) { // 존재하지 않는 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else if (bookleDB.getMember(member_id) == null) { // 존재하지 않는 회원 ID : 2
				dos.writeInt(2);
				dos.flush();
			} else if (bookleDB.getReservation(reserved_date, book_id, member_id) == null) { // 존재하지 않는 예약 정보 : 4
				dos.writeInt(4);
				dos.flush();
			} else {
				int result1 = bookleDB.insertInventoryManagement(rental_date, "대여 중", book_id, member_id);
				if (result1 > 0) {
					int result3 = bookleDB.updateMemberRental(member_id, "inc");
					if (result3 > 0) {
						int result4 = bookleDB.deleteReservation(reserved_date, book_id, member_id);
						if (result4 > 0) { // 대여 입력, 회원 대여 갯수 증가, 예약 정보 삭제 성공 : 0
							dos.writeInt(0);
							dos.flush();
						} else {
							// 입력 초기화 해야할 듯
						}
					} else {
						// 입력 초기화 해야할 듯
					}
				} else { // 입력 실패 : 3
					dos.writeInt(3);
					dos.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void book_insert() {
		bookleDB = new BookleDB();

		try {
			String id = dis.readUTF();
			String title = dis.readUTF();
			String writer = dis.readUTF();
			String publisher = dis.readUTF();
			String category = dis.readUTF();
			int quantity = dis.readInt();

			if (bookleDB.getBook(id) != null) { // 중복된 도서 ID : 1
				dos.writeInt(1);
				dos.flush();
			} else {
				int result = bookleDB.insertBook(id, title, writer, publisher, category, quantity);
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
	}

	public void exe_command(HashMap userMap) {
		if (cmd.endsWith("rental")) {
			rental_insert();
		} else if (cmd.endsWith("rental_reserved")) {
			rental_reserved_insert();
		} else if (cmd.endsWith("return")) {
			return_insert();
		} else if (cmd.endsWith("reservation")) {
			reservation_standby_insert(userMap, 1);
		} else if (cmd.endsWith("standby")) {
			reservation_standby_insert(userMap, 2);
		} else if (cmd.endsWith("book")) {
			book_insert();
		} else {

		}
	}
}
