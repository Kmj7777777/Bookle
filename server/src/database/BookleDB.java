package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import server.BookleServer;
import table.Book;
import table.InventoryManagement;
import table.Member;
import table.Reservation;
import table.Standby;

public class BookleDB {

	private Connection con;
	Statement stat;

	public int getBookCount(String type, String member_id) throws Exception {
		con = DBcon.getConnection();

		// 대여 : inventory_management
		// 대기 : standby
		System.out.println("type : " + type);
		String sql = "";
		if (type.equals("inventory_management")) {
			sql = "select count(*) from inventory_management where member_id= ?";
		} else if (type.equals("standby")) {
			sql = "select count(*) from standby where member_id= ?";
		}
		PreparedStatement ps;
		ps = con.prepareStatement(sql);
		ps.setString(1, member_id);
		ResultSet rs = ps.executeQuery();

		int result = 0;
		while (rs.next()) {
			result = rs.getInt(1);
		}
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int deleteReservation_Standby(int type, String classify, String member_id, String book_id) throws Exception {
		con = DBcon.getConnection();

		// classify : date, order_number
		int result = 0;
		if (type == 1) {
			String sql = "delete from reservation where reserved_date = ? and book_id = ? and member_id = ? and rownum = 1";
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ps.setString(1, classify);
			ps.setString(2, book_id);
			ps.setString(3, member_id);
			result = ps.executeUpdate();
			if (result > 0) {
				con.commit();
				updateBookQuantity(book_id, "inc");
				ArrayList<Standby> list = getStandbyList(book_id);
				if (list.size() > 0) {
					String first_member_id = list.get(0).getMember_id();
					long time = BookleServer.time;
					time += (86400000 * 3);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					String reserved_date = sdf.format(new Date(time));
					insertReservation(reserved_date, book_id, first_member_id);
					updateBookQuantity(book_id, "dec");
					deleteStandbyFirstMember(book_id, first_member_id);
					updateStandbyPriority(book_id);
				}
				ps.close();
				con.close();
			}
		} else if (type == 2) {
			String sql = "delete from standby where member_id = ? and book_id = ? and order_number = ?";
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ps.setString(1, member_id);
			ps.setString(2, book_id);
			ps.setInt(3, Integer.parseInt(classify));
			result = ps.executeUpdate();
			con.commit();
			updateStandbyPriority(book_id);
			ps.close();
			con.close();
		}

		return result;

	}

	public int[] insertStandby(String book_id, String member_id, int total) throws Exception {
		con = DBcon.getConnection();

		// 해당 책의 대기번호 가져오기
		int order_number = 0;

		String sql = "select * from standby where book_id = ? order by order_number desc";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			order_number = rs.getInt("order_number");
		}

		int result[] = new int[2];
		if (order_number < total) { // 대기자 수가 전체 책의 수량보다 작아야 수행
			sql = "insert into standby values(?, ?, ?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, member_id);
			ps.setString(2, book_id);
			ps.setInt(3, ++order_number);

			result[0] = ps.executeUpdate();
			result[1] = order_number;
		}

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public ArrayList<Standby> getStandbyByMember(String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from standby where member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, member_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<Standby> list = new ArrayList<>();
		while (rs.next()) {
			String book_id = rs.getString("book_id");
			int order_number = rs.getInt("order_number");
			Standby standby = new Standby(member_id, book_id, order_number);
			list.add(standby);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<Standby> getStandbyList(String book_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from standby where book_id = ? order by order_number";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<Standby> list = new ArrayList<>();
		while (rs.next()) {
			String member_id = rs.getString("member_id");
			int order_number = rs.getInt("order_number");
			Standby standby = new Standby(member_id, book_id, order_number);
			list.add(standby);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public int deleteStandbyFirstMember(String book_id, String first_member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "delete from standby where book_id = ? and member_id = ? and order_number = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ps.setString(2, first_member_id);
		int result = ps.executeUpdate();

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int updateStandbyPriority(String book_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "update standby set order_number = order_number - 1 where book_id = ? and order_number > 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		int result = ps.executeUpdate();

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public Book getBook(String id) throws Exception {
		con = DBcon.getConnection();
		while (con == null) {
			con = DBcon.getConnection();
		}

		String sql = "select * from book where id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();

		Book book = null;
		if (rs.next()) {
			String title = rs.getString("title");
			String writer = rs.getString("writer");
			String publisher = rs.getString("publisher");
			String category = rs.getString("category");
			int current_quantity = rs.getInt("current_quantity");
			int total_quantity = rs.getInt("total_quantity");
			book = new Book(id, title, writer, publisher, category, current_quantity, total_quantity);
		}
		rs.close();
		ps.close();
		con.close();

		return book;
	}

	public String getLastIdOfBook(String category) throws Exception {
		con = DBcon.getConnection();

		String sql = "select id from book where category = ? order by id";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, category);
		ResultSet rs = ps.executeQuery();

		ArrayList<String> list = new ArrayList<>();
		while (rs.next()) {
			String id = rs.getString("id");
			list.add(id);
		}
		rs.close();
		ps.close();
		con.close();

		return list.get(list.size() - 1);
	}

	public int getCurrentQuantityOfBook(String id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select current_quantity from book where id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();

		int result = -1;
		if (rs.next()) {
			result = rs.getInt("current_quantity");
		}
		rs.close();
		ps.close();
		con.close();

		return result;
	}

	public Member getMember(String id) throws Exception {
		con = DBcon.getConnection();
		while (con == null) {
			con = DBcon.getConnection();
		}

		String sql = "select * from member where id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();

		Member member = null;
		if (rs.next()) {
			String pw = rs.getString("pw");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			String birth = rs.getString("birth");
			String phone = rs.getString("phone");
			String e_mail = rs.getString("e_mail");
			int current_rental = rs.getInt("current_rental");
			int possible_rental = rs.getInt("possible_rental");
			member = new Member(id, pw, name, gender, birth, phone, e_mail, current_rental, possible_rental);
		}
		rs.close();
		ps.close();
		con.close();

		return member;
	}

	public InventoryManagement getRentalInfo(String rental_date, String book_id, String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from inventory_management where rental_date = ? and book_id = ? and member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, rental_date);
		ps.setString(2, book_id);
		ps.setString(3, member_id);
		ResultSet rs = ps.executeQuery();

		InventoryManagement im = null;
		if (rs.next()) {
			String return_date = rs.getString("return_date");
			im = new InventoryManagement(rental_date, return_date, book_id, member_id);
		}
		rs.close();
		ps.close();
		con.close();

		return im;
	}

	public Reservation getReservation(String reserved_date, String book_id, String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from reservation where reserved_date = ? and book_id = ? and member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, reserved_date);
		ps.setString(2, book_id);
		ps.setString(3, member_id);
		ResultSet rs = ps.executeQuery();

		Reservation res = null;
		if (rs.next()) {
			res = new Reservation(reserved_date, book_id, member_id);
		}
		rs.close();
		ps.close();
		con.close();

		return res;
	}

	public int getOrderNumber(String book_id) throws Exception {
		con = DBcon.getConnection();

		int order_number = 0;

		String sql = "select order_number from standby where book_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ResultSet rs = ps.executeQuery(); // table 존재하지 않음

		while (rs.next()) {
			order_number = rs.getInt("order_number");
		}
		rs.close();
		ps.close();
		con.close();

		return order_number;
	}

	public ArrayList<Book> getBookList(String keyword, String option) throws Exception {
		con = DBcon.getConnection();

		String sql = "";
		if (option.equals("all_admin") || option.equals("all_client")) {
			sql = "select * from book where id like '%' || ? || '%' or upper(title) like '%' || upper(?) || '%' "
					+ "or upper(writer) like '%' || upper(?) || '%' or publisher like '%' || ? || '%' "
					+ "or category like '%' || ? || '%' order by title";
		} else if (option.equals("rental")) {
			sql = "select * from book where (id like '%' || ? || '%' or upper(title) like '%' || upper(?) || '%' "
					+ "or upper(writer) like '%' || upper(?) || '%' or publisher like '%' || ? || '%' "
					+ "or category like '%' || ? || '%') and current_quantity > 0 order by title";
		}
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, keyword);
		ps.setString(2, keyword);
		ps.setString(3, keyword);
		ps.setString(4, keyword);
		ps.setString(5, keyword);
		ResultSet rs = ps.executeQuery();

		ArrayList<Book> list = new ArrayList<>();
		while (rs.next()) {
			String id = rs.getString("id");
			String title = rs.getString("title");
			String writer = rs.getString("writer");
			String publisher = rs.getString("publisher");
			String category = rs.getString("category");
			int current_quantity = rs.getInt("current_quantity");
			int total_quantity = rs.getInt("total_quantity");
			Book book = new Book(id, title, writer, publisher, category, current_quantity, total_quantity);
			list.add(book);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<Member> getMemberList(String keyword, String option) throws Exception {
		con = DBcon.getConnection();

		String sql = "";
		if (option.equals("all_admin")) {
			sql = "select * from member where id like '%' || ? || '%' or name like '%' || ? || '%' or gender like '%' || ? || '%' "
					+ "or birth like '%' || ? || '%' or phone like '%' || ? || '%' or e_mail like '%' || ? || '%' order by name";
		} else if (option.equals("rental")) {
			sql = "select * from member where (id like '%' || ? || '%' or name like '%' || ? || '%' or gender like '%' || ? || '%' "
					+ "or birth like '%' || ? || '%' or phone like '%' || ? || '%' or e_mail like '%' || ? || '%') "
					+ "and current_rental < possible_rental order by name";
		}
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, keyword);
		ps.setString(2, keyword);
		ps.setString(3, keyword);
		ps.setString(4, keyword);
		ps.setString(5, keyword);
		ps.setString(6, keyword);
		ResultSet rs = ps.executeQuery();

		ArrayList<Member> list = new ArrayList<>();
		while (rs.next()) {
			String id = rs.getString("id");
			String pw = rs.getString("pw");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			String birth = rs.getString("birth");
			String phone = rs.getString("phone");
			String email = rs.getString("e_mail");
			int current_rental = rs.getInt("current_rental");
			int possible_rental = rs.getInt("possible_rental");
			Member member = new Member(id, pw, name, gender, birth, phone, email, current_rental, possible_rental);
			list.add(member);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<InventoryManagement> getInventoryManagementList(String keyword, String option) throws Exception {
		con = DBcon.getConnection();

		String sql = "";
		if (option.equals("rental")) {
			sql = "select * from inventory_management where return_date = '대여 중' and "
					+ "((rental_date like '%' || ? || '%' or book_id like '%' || ? || '%' or member_id like '%' || ? || '%') "
					+ "or book_id in (select id from book where title like '%' || ? || '%') "
					+ "or member_id in (select id from member where name like '%' || ? || '%')) order by rental_date desc";
		} else if (option.equals("return")) {
			sql = "select * from inventory_management where return_date != '대여 중' and "
					+ "((return_date like '%' || ? || '%' or book_id like '%' || ? || '%' or member_id like '%' || ? || '%') "
					+ "or book_id in (select id from book where title like '%' || ? || '%') "
					+ "or member_id in (select id from member where name like '%' || ? || '%')) order by return_date desc";
		}
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, keyword);
		ps.setString(2, keyword);
		ps.setString(3, keyword);
		ps.setString(4, keyword);
		ps.setString(5, keyword);
		ResultSet rs = ps.executeQuery();

		ArrayList<InventoryManagement> list = new ArrayList<>();
		while (rs.next()) {
			String rental_date = rs.getString("rental_date");
			String return_date = rs.getString("return_date");
			String book_id = rs.getString("book_id");
			String member_id = rs.getString("member_id");
			InventoryManagement im = new InventoryManagement(rental_date, return_date, book_id, member_id);
			list.add(im);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<InventoryManagement> getRentalListByBook(String book_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from inventory_management where book_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<InventoryManagement> list = new ArrayList<>();
		while (rs.next()) {
			String rental_date = rs.getString("rental_date");
			String return_date = rs.getString("return_date");
			String member_id = rs.getString("member_id");
			InventoryManagement im = new InventoryManagement(rental_date, return_date, book_id, member_id);
			list.add(im);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<InventoryManagement> getRentalListByMember(String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from inventory_management where member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, member_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<InventoryManagement> list = new ArrayList<>();
		while (rs.next()) {
			String rental_date = rs.getString("rental_date");
			String return_date = rs.getString("return_date");
			String book_id = rs.getString("book_id");
			InventoryManagement im = new InventoryManagement(rental_date, return_date, book_id, member_id);
			list.add(im);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<Reservation> getReservationListByBook(String book_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from reservation where book_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<Reservation> list = new ArrayList<>();
		while (rs.next()) {
			String reserved_date = rs.getString("reserved_date");
			String member_id = rs.getString("member_id");
			Reservation reserv = new Reservation(reserved_date, book_id, member_id);
			list.add(reserv);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<Reservation> getReservationListByMember(String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from reservation where member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, member_id);
		ResultSet rs = ps.executeQuery();

		ArrayList<Reservation> list = new ArrayList<>();
		while (rs.next()) {
			String reserved_date = rs.getString("reserved_date");
			String book_id = rs.getString("book_id");
			Reservation reserv = new Reservation(reserved_date, book_id, member_id);
			list.add(reserv);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public ArrayList<Reservation> getReservationList(String keyword) throws Exception {
		con = DBcon.getConnection();

		String sql = "select * from reservation where ((reserved_date like '%' || ? || '%' or book_id like '%' || ? || '%' "
				+ "or member_id like '%' || ? || '%') or book_id in (select id from book where title like '%' || ? || '%') "
				+ "or member_id in (select id from member where name like '%' || ? || '%')) order by reserved_date desc";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, keyword);
		ps.setString(2, keyword);
		ps.setString(3, keyword);
		ps.setString(4, keyword);
		ps.setString(5, keyword);
		ResultSet rs = ps.executeQuery();

		ArrayList<Reservation> list = new ArrayList<>();
		while (rs.next()) {
			String reserved_date = rs.getString("reserved_date");
			String book_id = rs.getString("book_id");
			String member_id = rs.getString("member_id");
			Reservation reserv = new Reservation(reserved_date, book_id, member_id);
			list.add(reserv);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public int insertBook(String id, String title, String writer, String publisher, String category, int quantity)
			throws Exception {
		con = DBcon.getConnection();

		String sql = "insert into book values(?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, id);
		ps.setString(2, title);
		ps.setString(3, writer);
		ps.setString(4, publisher);
		ps.setString(5, category);
		ps.setInt(6, quantity);
		ps.setInt(7, quantity);

		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int insertInventoryManagement(String rental_date, String return_date, String book_id, String member_id)
			throws Exception {
		con = DBcon.getConnection();

		String sql = "insert into inventory_management values(?, ?, ?, ?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, rental_date);
		ps.setString(2, return_date);
		ps.setString(3, book_id);
		ps.setString(4, member_id);

		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int updateInventoryManagement(String rental_date, String return_date, String book_id, String member_id)
			throws Exception {
		con = DBcon.getConnection();

		String sql = "update inventory_management set return_date = ? where rental_date = ? and book_id = ? and member_id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, return_date);
		ps.setString(2, rental_date);
		ps.setString(3, book_id);
		ps.setString(4, member_id);

		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int insertReservation(String reserved_date, String book_id, String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "insert into reservation values(?, ?, ?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, reserved_date);
		ps.setString(2, book_id);
		ps.setString(3, member_id);

		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int updateBookQuantity(String book_id, String option) throws Exception {
		con = DBcon.getConnection();

		String sql = "";
		if (option.equals("inc")) {
			sql = "update book set current_quantity = current_quantity + 1 where id = ? and current_quantity < total_quantity";
		} else {
			sql = "update book set current_quantity = current_quantity - 1 where id = ? and current_quantity > 0";
		}
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, book_id);

		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int updateMemberRental(String member_id, String option) throws Exception {
		con = DBcon.getConnection();

		String sql = "";
		if (option.equals("inc")) {
			sql = "update member set current_rental = current_rental + 1 where id = ? and current_rental < possible_rental";
		} else {
			sql = "update member set current_rental = current_rental - 1 where id = ? and current_rental > 0";
		}
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, member_id);
		int result = ps.executeUpdate();
		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int updateBook(String book_id, int quantity) throws Exception {
		con = DBcon.getConnection();

		String sql = "update book set current_quantity = current_quantity + ?, total_quantity = total_quantity + ? where id = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, quantity);
		ps.setInt(2, quantity);
		ps.setString(3, book_id);
		int result = ps.executeUpdate();

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public ArrayList<String> getExpiredBookIdList(String today) throws Exception {
		con = DBcon.getConnection();

		String sql = "select book_id from reservation where reserved_date < ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, today);
		ResultSet rs = ps.executeQuery();

		ArrayList<String> list = new ArrayList<>();
		while (rs.next()) {
			String book_id = rs.getString("book_id");
			list.add(book_id);
		}
		rs.close();
		ps.close();
		con.close();

		return list;
	}

	public int deleteReservation(String today) throws Exception {
		con = DBcon.getConnection();

		String sql = "delete from reservation where reserved_date < ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, today);
		int result = ps.executeUpdate();

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	public int deleteReservation(String reserved_date, String book_id, String member_id) throws Exception {
		con = DBcon.getConnection();

		String sql = "delete from reservation where reserved_date = ? and book_id = ? and member_id = ? and rownum = 1";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, reserved_date);
		ps.setString(2, book_id);
		ps.setString(3, member_id);
		int result = ps.executeUpdate();

		con.commit();
		ps.close();
		con.close();

		return result;
	}

	////////////////////////////////////
	public ArrayList<String> selectJoinTitle(String sql) {
		con = DBcon.getConnection();

		ArrayList<String> result = new ArrayList<>();
		try {

			stat = con.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				String title = rs.getString(1);
				result.add(rs.getString(1));
			}

			rs.close();
			stat.close();
			con.close();
		} catch (Exception e) {
		}

		return result;
	}

	public ArrayList<String> selectJoinTitleDate(String sql) {
		con = DBcon.getConnection();

		ArrayList<String> result = new ArrayList<>();

		try {

			stat = con.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				String title = rs.getString(1);
				String date = rs.getString(2);
				String tmp = rs.getString(1) + "|" + rs.getString(2);
				result.add(tmp);
			}

			rs.close();
			stat.close();
			con.close();
		} catch (Exception e) {
		}

		return result;
	}

	// insert
	public int insertMemberData(Member mem) {
		con = DBcon.getConnection();

		try {

			stat = con.createStatement();

			String id_ = mem.getId();
			String pw_ = mem.getPw();
			String name_ = mem.getName();
			String gender_ = mem.getGender();
			String birth_ = mem.getBirth();
			String phone_ = mem.getPhone();
			String email_ = mem.getE_mail();
			String question_ = mem.getQuestion();
			String answer_ = mem.getAnswer();
			String join_date_ = mem.getJoin_date();
			int cur = mem.getCurrent_rental();
			int max = mem.getPossible_rental();

			String sql = "insert into member values('" + id_ + "', '" + pw_ + "', '" + name_ + "', '" + gender_ + "', '"
					+ birth_ + "', '" + phone_ + "', '" + email_ + "', '" + question_ + "', '" + answer_ + "', '"
					+ join_date_ + "', '" + cur + "', '" + max + "')";

			int result = stat.executeUpdate(sql);
			if (result == 1) {
				System.out.println("success");
			} else {
				System.out.println("fail");
			}

			con.commit(); // 저장하기
			stat.close();
			con.close();

			return result; // 1 : 성공

		} catch (Exception e) {
			e.printStackTrace();
			return -1; // -1 : 실패
		}
	}

	public ArrayList<String[]> select_info(String sql) {
		con = DBcon.getConnection();

		String[] data = new String[5];
		ArrayList<String[]> result = new ArrayList<>();
		try {

			stat = con.createStatement();

			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				// r.reserved_date, b.id, b.title, b.writer, b.publisher
				data[0] = rs.getString(1);
				data[1] = rs.getString(2);
				data[2] = rs.getString(3);
				data[3] = rs.getString(4);
				data[4] = rs.getString(5);
				result.add(data);
			}

			rs.close();
			stat.close();
			con.close();

			return result;
		} catch (Exception e) {
			System.out.println("select_info 에러, BookleDB / 539");
			return null;
		}
	}

	public int insertReservationManagement(String sql) {
		con = DBcon.getConnection();

		try {

			stat = con.createStatement();

			int result = stat.executeUpdate(sql);
			con.commit(); // 저장하기
			stat.close();
			con.close();

			return result;
		} catch (Exception e) {
			System.out.println("실패...!");
			return 0;
		}
	}

	// select
	public ArrayList<Member> selectMemberData(String sql) {
		con = DBcon.getConnection();

		ArrayList<Member> result = new ArrayList<>();
		try {
			stat = con.createStatement();
			// String sql = "select * from member where name='" + name_ + "', birth='" +
			// birth_ + "'";

			ResultSet rs = stat.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("id");
				String pw = rs.getString("pw");
				String name = rs.getString("name");
				String gender = rs.getString("gender");
				String birth = rs.getString("birth");
				String phone = rs.getString("phone");
				String e_mail = rs.getString("e_mail");
				String date_ = rs.getString("join_date");
				int current_ = rs.getInt("current_rental");
				int possible_ = rs.getInt("possible_rental");

				Member tmp = new Member(id, pw, name, gender, birth, phone, e_mail, date_, current_, possible_);
				result.add(tmp);
			}

			rs.close();
			stat.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// public HashMap<String, Book> selectBookData(String contents) {
	// HashMap<String, Book> hs = new HashMap<>();
	//
	// // 저자, 책, 출판사
	// try {
	//
	// stat = con.createStatement();
	// String sql = "";
	//
	// if (contents.equals("")) {
	// sql = "select * from book";
	// ResultSet rs = stat.executeQuery(sql);
	//
	// while (rs.next()) {
	// String jenre = rs.getString("category");
	// String bookTitle = rs.getString("title");
	// String bookId = rs.getString("id");
	// String writer = rs.getString("writer");
	// String publisher = rs.getString("publisher");
	//
	// Book booksearch = new Book(jenre, bookTitle, bookId, writer, publisher);
	// hs.put(bookTitle, booksearch);
	// }
	// } else {
	// String[] split = contents.split(" ");
	//
	// for (String tmp1 : split) {
	// String[] category = new String[3];
	// category[0] = "select * from book where title like '%" + tmp1 + "%' or title
	// like '%" + tmp1
	// + "' or title like '" + tmp1 + "%'";
	// category[1] = "select * from book where writer like '%" + tmp1 + "%' or
	// writer like '%" + tmp1
	// + "' or writer like '" + tmp1 + "%'";
	// category[2] = "select * from book where publisher like '%" + tmp1 + "%' or
	// publisher like '%" + tmp1
	// + "' or writer like '" + tmp1 + "%'";
	// for (String tmp2 : category) {
	// // ex) select * from book where title='title_'
	// sql = tmp2;
	// ResultSet rs = stat.executeQuery(sql);
	//
	// while (rs.next()) {
	// String jenre = rs.getString("jenre");
	// String bookTitle = rs.getString("title");
	// String bookId = rs.getString("id");
	// String writer = rs.getString("writer");
	// String publisher = rs.getString("publisher");
	//
	// Book booksearch = new Book(jenre, bookTitle, bookId, writer, publisher);
	// hs.put(bookTitle, booksearch);
	// }
	// }
	// }
	// }
	//
	//
	// return hs;
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	//
	// }

	public HashMap<Integer, Reservation> selectReservationManagement(String sql) {
		con = DBcon.getConnection();

		// ArrayList<ReservationManagement> list = new ArrayList<>();
		HashMap<Integer, Reservation> hs = new HashMap<>();

		try {

			stat = con.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			int num = 1;
			while (rs.next()) {
				String reserved_date = rs.getString("reserved_data");
				String book_id = rs.getString("book_id");
				String member_id = rs.getString("member_id");
				Reservation im = new Reservation(reserved_date, book_id, member_id);
				hs.put(num++, im);
			}

			rs.close();
			stat.close();
			con.close();

		} catch (Exception e) {

		}
		return hs;
	}

	public InventoryManagement selectRentalInfo(String type, String book_id, String member_id) {
		con = DBcon.getConnection();

		InventoryManagement im = null;

		try {

			stat = con.createStatement();
			ResultSet rs = stat.executeQuery("select * from inventory_management where type = '" + type
					+ "' and book_id = '" + book_id + "' and member_id = '" + member_id + "'");

			if (rs.next()) {
				String io_date = rs.getString("io_date");
				im = new InventoryManagement(io_date, type, book_id, member_id);
			}

			rs.close();
			stat.close();
			con.close();

		} catch (Exception e) {
		}
		return im;
	}

	// update
	public int update(String sql) {
		con = DBcon.getConnection();

		try {

			stat = con.createStatement();

			int result = stat.executeUpdate(sql);
			System.out.println("update 성공");
			con.commit(); // 저장하기
			stat.close();
			con.close();

			return result;
		} catch (Exception e) {
			System.out.println("실패...");
			return 0;
		}
	}

	// delete
	public int deleteData() {
		con = DBcon.getConnection();

		try {

			stat = con.createStatement();
			int result = stat.executeUpdate("delete from student where name='");

			con.commit(); // 저장하기
			stat.close();
			con.close();

			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public String[] phoneAuth(String userId) throws Exception {
		con = DBcon.getConnection();

		String[] temp = new String[] { "", "" };

		stat = con.createStatement();
		ResultSet rs = stat.executeQuery("select name, phone from member where id='" + userId + "'");

		if (rs.next()) {
			temp[0] = rs.getString("name");
			temp[1] = rs.getString("phone");
		}

		rs.close();
		stat.close();
		con.close();

		return temp;
	}
}
