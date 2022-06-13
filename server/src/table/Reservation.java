package table;

public class Reservation {

	private String reserved_date;
	private String book_id;
	private String member_id;

	public Reservation() {

	}

	public Reservation(String reserved_date, String book_id, String member_id) {
		this.reserved_date = reserved_date;
		this.book_id = book_id;
		this.member_id = member_id;
	}

	public String getReserved_date() {
		return reserved_date;
	}

	public void setReserved_date(String reserved_date) {
		this.reserved_date = reserved_date;
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

}
