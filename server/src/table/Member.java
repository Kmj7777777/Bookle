package table;

public class Member {
	public Member(String id, String pw, String name, String gender, String birth, String phone, String e_mail,
			int current_rental, int possible_rental) {
		super();
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.phone = phone;
		this.gender = gender;
		this.birth = birth;
		this.e_mail = e_mail;
		this.current_rental = current_rental;
		this.possible_rental = possible_rental;
	}
	
	public Member(String id, String pw, String name, String gender, String birth, String phone, String e_mail,
			String join_date, int current_rental, int possible_rental) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.phone = phone;
		this.gender = gender;
		this.birth = birth;
		this.e_mail = e_mail;
		this.join_date = join_date;
		this.current_rental = current_rental;
		this.possible_rental = possible_rental;
	}

	public Member(String id, String pw, String name, String gender, String birth, String phone, String e_mail,
			String join_date) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.phone = phone;
		this.gender = gender;
		this.birth = birth;
		this.e_mail = e_mail;
		this.join_date = join_date;
	}
	public Member(String id, String pw, String name, String gender, String birth, String phone, String e_mail,
			String question, String answer, String join_date, int current_rental, int possible_rental) {
		super();
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.gender = gender;
		this.birth = birth;
		this.phone = phone;
		this.e_mail = e_mail;
		this.question = question;
		this.answer = answer;
		this.join_date = join_date;
		this.current_rental = current_rental;
		this.possible_rental = possible_rental;
	}
	// Member Å×ÀÌºí~~~
	private String id;
	private String pw;
	private String name;
	private String phone;
	private String gender;
	private String birth;
	private String e_mail;
	private String question;
	private String answer;
	private String join_date;
	private int current_rental;
	private int possible_rental;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getE_mail() {
		return e_mail;
	}
	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getJoin_date() {
		return join_date;
	}
	public void setJoin_date(String join_date) {
		this.join_date = join_date;
	}
	public int getCurrent_rental() {
		return current_rental;
	}
	public void setCurrent_rental(int current_rental) {
		this.current_rental = current_rental;
	}
	public int getPossible_rental() {
		return possible_rental;
	}
	public void setPossible_rental(int possible_rental) {
		this.possible_rental = possible_rental;
	}
	
	
}
