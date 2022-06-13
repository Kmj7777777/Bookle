package classify;
// pwchange 수정
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import database.BookleDB;
import server.BookleServer;
import table.Member;

public class Account {
	
	private Socket sock = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String cmd = null;
//	private HashMap<String, String> userMap;
	private BookleDB bookleDB = null;
	private Member mb = null;
	private String user_id = null;
	
	public Account(Socket sock, DataInputStream dis, DataOutputStream dos, String cmd) {
		this.sock= sock;
		this.dis = dis;
		this.dos = dos;
		this.cmd = cmd;
	}
	

	
	private void info(HashMap userMap, int type) {
		System.out.println("==== 예약 또는 대여 정보 리스트 ====");
		// type 1 : reserved data, type 2 : rental data
		bookleDB = new BookleDB();
		String ip = this.sock.getInetAddress().toString();
		String user_id = userMap.get(ip).toString();
		String sql = null;
		ArrayList<String[]> result = null;
		
		if(type == 1) {
			// 사용자 예약현황
			System.out.println("---예약현황 보기---");
			sql = "select b.id, b.title, b.writer, b.publisher, r.reserved_date from book b, reservation r where r.member_id='" + user_id + "' and b.id=r.book_id";
			result = bookleDB.select_info(sql);
			
		} else if (type == 2) {
			// 사용자 대여현황
			System.out.println("---대여현황 보기---");
			sql = "select b.id, b.title, b.writer, b.publisher, i.io_date from book b, inventory_management i where i.member_id='" + user_id + "' and b.id=i.book_id";
			result = bookleDB.select_info(sql);
		}
		
		try {
			int size = 0;
			size = result.size();
			dos.writeInt(size);
			dos.flush();
			
			String msg = "";
			for(String[] tmp : result) {
				for(int i = 0; i < tmp.length; i++) {
					if(i != 4) {
						System.out.println(tmp[i]);
						msg += tmp[i] + "|";
					}
					else {
						msg += tmp[i];
					}
				}
				System.out.println("전달할 메세지 : " + msg);
				dos.writeUTF(msg);
			}
		} catch (Exception e) {
			try {
				dos.writeUTF("error");
			} catch(Exception e1) {}
		}
		//bookleDB.closeConnection();
	}
	
	private void pw_change(HashMap userMap) {
		bookleDB = new BookleDB();

		try {
			String present_pw = null;
			String ip = this.sock.getInetAddress().toString();
			String id_ = userMap.get(ip).toString();
			System.out.println("접속자 id : " + id_);
			System.out.println("=== pw 변경하기 ===");
			while (true) {
				// flag 1 : 현재 비밀번호 입력창
				// flag 2 : 새로운 비밀번호 입력창
				// flag 0 : cancel
				int flag = dis.readInt();
				System.out.println("flag : " + flag);
				
				if(flag == 1) {
					System.out.println("===현재 비밀번호 일치 확인을 위한 창===");
					String pw_ = dis.readUTF(); // 현재 비번
					
						System.out.println("현재 사용자가 입력한 pw : " + pw_); // 현재 pw 입력 받음

						String sql = "select * from member where id='" + id_ + "'";
						ArrayList<Member> result = bookleDB.selectMemberData(sql);
						present_pw = result.get(0).getPw(); // 사용자 pw db로부터 받아옴
						
						if (pw_.equals(result.get(0).getPw())) { // 입력된 pw와 사용자의 pw를 비교
							System.out.println("입력한 비밀번호와 저장된 비밀번호가 일치합니다.");
							dos.writeUTF("inputnewpw"); // 같으면 새로운 pw입력을 위해 msg 보냄
							dos.flush();
						} else {
							dos.writeUTF("pw_fail");
							dos.flush();
						}
					
				} else if(flag == 2) {
					System.out.println("===새로운 비밀번호를 입력하는 창===");
					String new_pw_ = dis.readUTF(); // 새로운 pw를 입력 받음
					
					if (present_pw.equals(new_pw_)) { // 사용자의 현재 pw와 변경할 pw가 같을 경우 중복 처리
						System.out.println("저장된 pw와 새로 입력한 pw가 중복");
						dos.writeUTF("pwduplicate"); // 문제 발생
						dos.flush();
					} else {
						System.out.println("변경할 pw : " + new_pw_);
						
						String sql = "update member set pw='" + new_pw_ + "' where id='" + id_ + "'";
						if (bookleDB.update(sql) == 1) {
							dos.writeUTF("success");
							dos.flush();
							break;
						} else {
							System.out.println("db에 저장 실패");
							dos.writeUTF("change_error");
							dos.flush();
							break;
						}
						
					}
				} else if(flag == 0) {
					System.out.println("창 닫음");
					break;
				}
			}
			
		} catch (Exception e) {
			try {
				dos.writeUTF("change_fail");
				dos.flush();
			} catch (Exception e1) {
				System.out.println("접속 종료");
			}
		}
		//bookleDB.closeConnection();
	}

	
	private void mypage(HashMap userMap) {
		//사용자 id, 이름, 대여현황, 예약 현황
		bookleDB = new BookleDB();
		
		try {
			String ip = this.sock.getInetAddress().toString();
			user_id = userMap.get(ip).toString();
			System.out.println("(account)user id : " + user_id);
			// 사용자의 id와 이름
			String sql = "select * from member where id='" + user_id + "'";
			ArrayList<Member> mem = bookleDB.selectMemberData(sql);
			String id_ = mem.get(0).getId(); String name_ = mem.get(0).getName();
			String birth_ = mem.get(0).getBirth(); String phone_ = mem.get(0).getPhone();
			String email_ = mem.get(0).getE_mail(); String date_ = mem.get(0).getJoin_date();
			int current_ = mem.get(0).getCurrent_rental(); int possible_ = mem.get(0).getPossible_rental();
			String user_info_msg = id_ + "|" + name_ + "|" + birth_ + "|" + phone_ + "|" + email_ + "|" + date_ + "|" + current_ + "|" + possible_;
			
			dos.writeUTF(user_info_msg); // id|name
			dos.flush();
			//-------------------------------------------------------------------------
//			// 사용자 대여현황
//			sql = null;
//			sql = "select b.title, i.io_date from book b, inventory_management i where i.member_id='"+ id_ +"' and b.id=i.book_id";
//			ArrayList<String> inventory_info = new ArrayList<>();
//			inventory_info = bookleDB.selectJoinTitleDate(sql);
//			
//			int size = inventory_info.size();
//			dos.writeInt(size);
//			dos.flush();			
//			for(String tmp : inventory_info) {
//				dos.writeUTF(tmp);
//				dos.flush();
//			}
			
//			// 사용자 예약현황
//			sql = null;
//			sql = "select title from book b, reservation_management r where r.member_id='"+ user_id +"' and b.id=r.book_id";
//			ArrayList<String> reservation_info = new ArrayList<>();
//			reservation_info = bookleDB.selectJoinTitle(sql);
//			
//			size = reservation_info.size();
//			dos.writeInt(size);
//			dos.flush();
//			for(String tmp : reservation_info) {
//				dos.writeUTF(tmp);
//				dos.flush();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}
	
	private void user_pw_search(){
		bookleDB = new BookleDB();
		try {

			String id_ = dis.readUTF();
			String birth_ = dis.readUTF();
			String name_ = dis.readUTF();
			String question_ = dis.readUTF();
			String answer_ = dis.readUTF();

			String sql = "select * from member where id='" + id_ + "' and birth='" + birth_ + "' and name='" + name_ + "'and question='" + question_ + "' and answer='" + answer_ + "'";
			ArrayList<Member> mem = bookleDB.selectMemberData(sql);

			Random rnd =new Random();
			StringBuffer buf =new StringBuffer();
			
			if (mem.isEmpty()) {
				dos.writeUTF("not_exist_id");
				dos.flush();
			} else {
				for(int i=0;i<6;i++){
				    // rnd.nextBoolean() 는 랜덤으로 true, false 를 리턴. true일 시 랜덤 한 소문자를, false 일 시 랜덤 한 숫자를 StringBuffer 에 append 한다.
				    if(rnd.nextBoolean()){
				        buf.append((char)((int)(rnd.nextInt(26))+97));
				    }else{
				        buf.append((rnd.nextInt(10)));
				    }
				}

				String new_pw = buf.toString();
				
				dos.writeUTF(new_pw);
				dos.flush();
				//update member set pw='new_pw' where id='id_'
				sql = "update member set pw='" + new_pw + "' where id='" + id_ + "'";
				
				int result = bookleDB.update(sql);
				if (result == 1) {
					dos.writeUTF("success");
					dos.flush();
				} else if (result == 0) {
					dos.writeUTF("pw_change_fail");
					dos.flush();
				}

			}
		} catch (Exception e) {
			try {
				dos.writeUTF("pw_search_error");
				dos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//bookleDB.closeConnection();
	}
	
	private void user_id_search() {
		bookleDB = new BookleDB();
		try {
			String name_ = dis.readUTF();
			String birth_ = dis.readUTF();
			
			System.out.println(name_ + " " + birth_);
			// Member 테이블에서 name과 birth를 갖는 사람의 id select
			// sql = select id from Member where name=name, birth=birth
			String sql = "select * from member where name='" + name_ + "'and birth='" + birth_ + "'";
			ArrayList<Member> mem = bookleDB.selectMemberData(sql);
			if (mem.isEmpty()) { // 찾는 id가 없음
				dos.writeUTF("not_found");
				dos.flush();
			} else {
				String user_id = mem.get(0).getId();
				dos.writeUTF(user_id);
				dos.flush();
			}
		} catch (Exception e) {
			try {
				dos.writeUTF("id_search_error");
				dos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//bookleDB.closeConnection();
	}
	
	private void id_duplicate_check() {
		/*
		 * id 중복 확인 -> id_duplicate
		 */

		bookleDB = new BookleDB();
		try {
			String id_ = dis.readUTF();
			ArrayList<Member> mem = bookleDB.selectMemberData("select * from member where id='" + id_ + "'");
			
			if(mem.isEmpty()) {
				dos.writeUTF("success");
				dos.flush();
			} else {
				System.out.println("id 중복");
				dos.writeUTF("id_duplicate");
				dos.flush();
			}
		} catch (Exception e) {
			try {
				dos.writeUTF("duplicate_error");
				dos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//bookleDB.closeConnection();
	}
	
	private void login(HashMap userMap){
		BookleDB bookleDB = new BookleDB();
		try {
			String id_ = dis.readUTF();
			String pw_ = dis.readUTF();

			String sql = "select * from member where id='" + id_ + "'";
			ArrayList<Member> result = bookleDB.selectMemberData(sql);

			if (result.isEmpty()) {
				dos.writeUTF("id_error");
				dos.flush();
			} else {
				// 패스워드 확인 하는 방법 찾기 ***
				if (pw_.equals(result.get(0).getPw())) { // 로그인 성공
					if(id_.equals("admin")) {
						dos.writeUTF("admin");
						dos.flush();
					} else {
						dos.writeUTF("general");
						
						String name_ = result.get(0).getName();
						dos.writeUTF(name_);
						
						String email_ = result.get(0).getE_mail();
						if (email_ == null) {
							email_ = "";
						}
						dos.writeUTF(email_);
						
						String gender_ = result.get(0).getGender();
						dos.writeUTF(gender_);
						dos.flush();
					}
					
					String ip = sock.getInetAddress().toString();
					System.out.println("==== 로그인 ====");
					System.out.println("ip : " + ip + ", id : " + id_ + "님 로그인 성공");
					userMap.put(ip, id_);
					
				} else { // 로그인 실패
					dos.writeUTF("pw_error");
					dos.flush();
				}
			}
		} catch (Exception e) {
			try {
				dos.writeUTF("login_error");
				dos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//bookleDB.closeConnection();
	}
	
	private void logout(HashMap userMap) {
		userMap.remove(this.sock.getInetAddress().toString());
		try {
			System.out.println(this.sock.getInetAddress() + " 해제");
			this.sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}
	
	private void memberJoin() {
		bookleDB = new BookleDB();
		
		long time = BookleServer.time;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String today = sdf.format(new Date(time));
		
		try {
			// client로부터 회원 정보를 receive
			// split 사용하는 방법 건의하기
			String id_ = dis.readUTF();
			String psw_ = dis.readUTF();
			String name_ = dis.readUTF();
			String gender_ = dis.readUTF();
			String birth_ = dis.readUTF();
			String phone_ = dis.readUTF();
			String e_mail_ = dis.readUTF();
			if(e_mail_.length() == 0) {
				e_mail_ = "";
			}
			String question_ = dis.readUTF();
			String answer_ = dis.readUTF();
			String join_date_ = today;
			
			int current_rental = 0; int possible_rental = 7; 
			mb = new Member(id_, psw_, name_, gender_, birth_, phone_, e_mail_, question_, answer_, today, current_rental, possible_rental);
			
			int result = bookleDB.insertMemberData(mb);

			if (result == 1) {
				System.out.println("저장 완료");
				dos.writeUTF("success");
				dos.flush();
			} else {
				System.out.println("저장 실패");
				dos.writeUTF("join_fail");
				dos.flush();
			}
		} catch (Exception e) {
			try {
				dos.writeUTF("join_error");
				dos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//bookleDB.closeConnection();
	}
	
	private void phoneAuth() {
		try {
			bookleDB = new BookleDB();
			String userId = dis.readUTF();
			
			String[] result = bookleDB.phoneAuth(userId);	
			dos.writeUTF(result[0]);
			dos.writeUTF(result[1]);
			dos.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
		//bookleDB.closeConnection();
	}
	
	public void exe_command(HashMap userMap) {
		if(cmd.endsWith("join")) {
			memberJoin();
		} else if(cmd.endsWith("logout")) {
			logout(userMap);
		} else if(cmd.endsWith("login")) {
			login(userMap);
		} else if(cmd.endsWith("idcheck")) {
			id_duplicate_check();
		} else if(cmd.endsWith("idsearch")) {
			user_id_search();
		} else if(cmd.endsWith("pwsearch")) {
			user_pw_search();
		} else if(cmd.endsWith("mypage")) {
			mypage(userMap);
		} else if(cmd.endsWith("pwchange")) {
			pw_change(userMap);
		} else if(cmd.endsWith("reservedinfo")) {
			info(userMap, 1);
		} else if(cmd.endsWith("rentalinfo")) {
			info(userMap, 2);
		} else if(cmd.endsWith("phoneAuth")){
			phoneAuth();
		}else {
			
		}
		
		
	}
	
}