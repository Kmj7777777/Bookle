package table;

public class InventoryManagement {

	private String rental_date;
	private String return_date;
	private String book_id;
	private String member_id;

	public InventoryManagement() {

	}

	public InventoryManagement(String rental_date, String return_date, String book_id, String member_id) {
		this.rental_date = rental_date;
		this.return_date = return_date;
		this.book_id = book_id;
		this.member_id = member_id;
	}

	public String getRental_date() {
		return rental_date;
	}

	public void setRental_date(String rental_date) {
		this.rental_date = rental_date;
	}

	public String getReturn_date() {
		return return_date;
	}

	public void setReturn_date(String return_date) {
		this.return_date = return_date;
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
