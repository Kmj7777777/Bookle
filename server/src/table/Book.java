package table;

public class Book {

	public Book(String id, String title, String writer, String publisher, String category) {
		super();
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.publisher = publisher;
		this.category = category;
	}
	
	public Book(String id, String title, String writer, String publisher, String category, int current_quantity,
			int total_quantity) {
		super();
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.publisher = publisher;
		this.category = category;
		this.current_quantity = current_quantity;
		this.total_quantity = total_quantity;
	}

	private String id;
	private String title;
	private String writer;
	private String publisher;
	private String category;
	private int current_quantity;
	private int total_quantity;

	public Book() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getCurrent_quantity() {
		return current_quantity;
	}

	public void setCurrent_quantity(int current_quantity) {
		this.current_quantity = current_quantity;
	}

	public int getTotal_quantity() {
		return total_quantity;
	}

	public void setTotal_quantity(int total_quantity) {
		this.total_quantity = total_quantity;
	}

}
