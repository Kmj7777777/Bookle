package table;

public class Standby {
	private String member_id = null;
	private String book_id = null;
	private int order_number = 0;

	public Standby() {

	}

	public Standby(String member_id, String book_id, int order_number) {
		this.member_id = member_id;
		this.book_id = book_id;
		this.order_number = order_number;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}

	public int getOrder_number() {
		return order_number;
	}

	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	
}
