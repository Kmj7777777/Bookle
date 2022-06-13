package search;

public class BookDTO {
	private String id; // 고유번호
	private String title; // 제목
	private String writer; // 저자
	private String publisher; // 출판사
	private String category; // 장르
	private int current_quantity; // 현재 수량
	private int total_quantity; // 총수량
	
	public BookDTO() { }

	public BookDTO(String id, String title, String writer, String publisher, String category, int current_quantity, int total_quantity) {
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.publisher = publisher;
		this.category = category;
		this.current_quantity = current_quantity;
		this.total_quantity = total_quantity;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getWriter() {
		return writer;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getCategory() {
		return category;
	}

	public int getCurrent_quantity() {
		return current_quantity;
	}

	public int getTotal_quantity() {
		return total_quantity;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCurrent_quantity(int current_quantity) {
		this.current_quantity = current_quantity;
	}

	public void setTotal_quantity(int total_quantity) {
		this.total_quantity = total_quantity;
	}
}