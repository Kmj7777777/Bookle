package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FindPwDialog extends JDialog{
	private Font f;
	private SendAuthNum sendAuthNum = new SendAuthNum();
	private int authNum;
	private boolean isAuth;
	private boolean isReceive;
	
	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;
	
	private JLabel findPwTitle = new JLabel("비밀번호 찾기");
	
	private JLabel idTitle = new JLabel("ID");
	private JLabel birthTitle = new JLabel("생년월일");
	private JLabel nameTitle = new JLabel("이름");
	private JLabel hintTitle = new JLabel("비밀번호 Q&A");
	private JLabel authTitle = new JLabel("휴대폰 번호");
	private JTextField rand = new JTextField();
	
	private JTextField idInput = new HintTextField("아이디를 입력해주세요.");
	private JComboBox<String> birthYear = new JComboBox<>();
	private JComboBox<String> birthMonth = new JComboBox<>();
	private JComboBox<String> birthDay = new JComboBox<>();
	private JTextField nameInput = new HintTextField("이름을 입력해주세요.");
	private JComboBox<String> hintBox = new JComboBox<>();
	private JTextField ansInput = new HintTextField("해당 힌트에 대한 답변을 적어주세요.");
	private JTextField phoneInput = new HintTextField("'-' 문자 제외");
	private JTextField authInput = new HintTextField("인증번호 입력");
	
	private String hintDef,hint0,hint1,hint2,hint3,hint4,hint5,hint6,hint7,hint8,hint9;
	private JButton receiveAuthNumBtn = new JButton("인증번호 받기");
	private JButton findPwBtn = new JButton("찾기");
	private JButton authBtn = new JButton("인증");
	
	private JLabel alarmMsg1 = new JLabel("");
	private JLabel alarmMsg2 = new JLabel("");
	private JLabel alarmMsg3 = new JLabel("");
	
	
	public void compInit() {
		this.setLayout(null);
		this.setTitle("비밀번호 찾기");
		
		this.rand.setBounds(0,0,0,0);
		this.add(rand);
		this.findPwTitle.setBounds(20,20,380,30);
		this.findPwTitle.setFont(f.deriveFont(Font.BOLD,17f));
		this.add(findPwTitle);
		
		for(int i =2018;i>=1900;i--) {
			this.birthYear.addItem(i+"년");
		}
		for(int i = 1;i<=9;i++) {
			this.birthMonth.addItem("0"+i+"월");
		}
		for(int i = 10;i<=12;i++) {
			this.birthMonth.addItem(i+"월");
		}
		for(int i = 1;i<=9;i++) {
			this.birthDay.addItem("0"+i+"일");
		}
		for(int i = 10;i<=31;i++) {
			this.birthDay.addItem(i+"일");
		}
		
		this.idTitle.setBounds(15,70,80,40);
		this.idTitle.setFont(f.deriveFont(Font.BOLD,16f));
		// this.nameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(idTitle);
		
		
		this.idInput.setFont(f.deriveFont(Font.PLAIN,14f));
		this.idInput.setBounds(90,70,287,40);
		this.add(idInput);
		
		this.birthTitle.setBounds(15,130,80,40);
		this.birthTitle.setFont(f.deriveFont(Font.BOLD,14f));
		this.add(birthTitle);
		
		this.birthYear.setBounds(90,130,100,40);
		this.birthYear.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthYear);
		
		this.birthMonth.setBounds(200,130,85,40);
		this.birthMonth.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthMonth);
		
		this.birthDay.setBounds(295,130,83,40);
		this.birthDay.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthDay);
		
		this.nameTitle.setBounds(15,190,80,40);
		this.nameTitle.setFont(f.deriveFont(Font.BOLD,14f));
		this.add(nameTitle);
		
		this.nameInput.setBounds(90,190,287,40);
		this.nameInput.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(nameInput);
		
		this.alarmMsg1.setBounds(15,227,250,20);
		this.alarmMsg1.setForeground(new Color(255, 0, 0));
		this.alarmMsg1.setFont(f.deriveFont(Font.PLAIN|Font.ITALIC,12f));
		this.add(alarmMsg1);
		
		this.hintTitle.setBounds(15,260,120,30);
		this.hintTitle.setFont(f.deriveFont(Font.BOLD,14f));
		this.add(hintTitle);
		
		this.hintBox.setBounds(15,300,362,33);
		this.hintBox.setFont(f.deriveFont(Font.PLAIN,14f));
		this.hintDef = "힌트를 선택해주세요.";
		this.hint1 = "당신의 보물 제 1호는?";this.hint2 = "당신의 아버지 성함은?";this.hint3 = "당신의 어머니 성함은?";
		this.hint4 = "당신의 가장 친한 친구 이름은?";this.hint5 = "당신의 첫사랑 이름은?";this.hint6 = "당신이 가장 좋아하는 가수는?";
		this.hint7 = "당신이 가장 기억에 남는 여행지는?";this.hint8 = "당신의 별명은?";this.hint9 = "당신이 태어난 곳은?";
		this.hint0 = "당신이 생각하는 가장 섹시한 신체부위는?";
		
		this.hintBox.addItem(hintDef);
		this.hintBox.addItem(hint1);this.hintBox.addItem(hint2);this.hintBox.addItem(hint3);
		this.hintBox.addItem(hint4);this.hintBox.addItem(hint5);this.hintBox.addItem(hint6);
		this.hintBox.addItem(hint7);this.hintBox.addItem(hint8);this.hintBox.addItem(hint9);
		this.hintBox.addItem(hint0);
		this.add(hintBox);
		
		this.ansInput.setBounds(15,340,362,33);
		this.ansInput.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(ansInput);

		this.alarmMsg2.setBounds(15,370,250,20);
		this.alarmMsg2.setForeground(new Color(255, 0, 0));
		this.alarmMsg2.setFont(f.deriveFont(Font.PLAIN|Font.ITALIC,12f));
		this.add(alarmMsg2);
		
		// ------------------------- 휴대폰 인증 관련 -------------------------
		this.authTitle.setBounds(15, 410, 80, 40);
		this.authTitle.setFont(f.deriveFont(Font.BOLD,14f));
		this.add(authTitle);
		this.phoneInput.setBounds(90, 410, 150, 40);
		this.add(phoneInput);
		this.receiveAuthNumBtn.setBounds(240, 412, 103, 35);
		this.add(receiveAuthNumBtn);
		
		this.authInput.setBounds(90, 452, 150, 40);
		this.add(authInput);
		this.authBtn.setBounds(240, 454, 70, 35);
		this.add(authBtn);

		this.alarmMsg3.setBounds(15, 489, 250, 20);
		this.alarmMsg3.setForeground(new Color(255, 0, 0));
		this.alarmMsg3.setFont(f.deriveFont(Font.PLAIN|Font.ITALIC,12f));
		this.add(alarmMsg3);
		// -----------------------------------------------------------------
		
		this.findPwBtn.setBounds(277,525,100,35);
		this.findPwBtn.setBackground(new Color(178, 204, 255));
		this.ansInput.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(findPwBtn);
	}
	
	public void findPwInit(String id, String birth, String name, String hint, String ans) {
		try {
			sock_out.writeUTF("account_pwsearch");
			sock_out.writeUTF(id);
			sock_out.writeUTF(birth);
			sock_out.writeUTF(name);
			sock_out.writeUTF(hint);
			sock_out.writeUTF(ans);
			sock_out.flush();
			String s = sock_in.readUTF();
			if(s.equals("not_exist_id")) {
				JOptionPane.showMessageDialog(this, "정보가 존재하지 않습니다.\n 입력하신 정보를 다시 한번 확인해주세요.");
			}else {
				String msg = sock_in.readUTF();
				if(msg.equals("success")) {
					JOptionPane.showMessageDialog(this, "비밀번호가 다음과 같이 설정되었습니다.\n6자리 임시비밀번호 : " + s);
					dispose();
				}else if(msg.equals("pw_change_fail")) {
					JOptionPane.showMessageDialog(this, "서버에 문제가 발생하였습니다.\n다시 시도해주세요.");
				}
			}
		}catch(Exception e) {
			JOptionPane.showMessageDialog(this, "패스워드 찾기에 문제가 발생하였습니다.");
		}
	}
	
	public void eventInit() {
		phoneInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if(!phoneInput.getText().isEmpty()) {
						Integer.parseInt(phoneInput.getText());
						alarmMsg3.setText("");
					}
				}catch(Exception e2) {
					phoneInput.setText("");
					alarmMsg3.setText("숫자만 입력해주세요.");
				}
			}
		});
		
		// ◆ '인증번호 받기' 버튼 눌림 ◆
		receiveAuthNumBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!idInput.getText().trim().isEmpty()) {
					String name = "";
					String registedPhoneNum = null;
					try {
						sock_out.writeUTF("account_phoneAuth");
						sock_out.writeUTF(idInput.getText());
						sock_out.flush();
						
						name = sock_in.readUTF();
						registedPhoneNum = sock_in.readUTF();
					}catch(Exception e2) {
						e2.printStackTrace();
					}
					
					if(!registedPhoneNum.isEmpty()) {
						if(!phoneInput.getText().isEmpty()) {
							String[] temp = registedPhoneNum.split("-");
							registedPhoneNum = "";
							for(String tmp : temp) {
								registedPhoneNum += tmp;
							}
							
							if(registedPhoneNum.equals(phoneInput.getText())) {
								authNum = sendAuthNum.send(name, registedPhoneNum);
								
								phoneInput.setEnabled(false);
								receiveAuthNumBtn.setEnabled(false);
								isReceive = true;
								JOptionPane.showMessageDialog(FindPwDialog.this, "인증번호를 전송하였습니다!", "", JOptionPane.INFORMATION_MESSAGE);
							}else {
								JOptionPane.showMessageDialog(FindPwDialog.this, "해당 아이디에 등록된 번호와 일치하지 않습니다.", "", JOptionPane.ERROR_MESSAGE);
							}
						}else {
							JOptionPane.showMessageDialog(FindPwDialog.this, "휴대폰 번호를 입력해주세요.", "", JOptionPane.ERROR_MESSAGE);
						}
					}else {
						JOptionPane.showMessageDialog(FindPwDialog.this, "아이디를 다시 한번 확인해주세요.", "", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					JOptionPane.showMessageDialog(FindPwDialog.this, "아이디를 먼저 입력해주세요.", "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// ◆ '인증' 버튼 눌림 ◆
		authBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isReceive) {
					if(authInput.getText().equals(String.valueOf(authNum))) {
						authInput.setEnabled(false);
						authBtn.setEnabled(false);
						isAuth = true;
						JOptionPane.showMessageDialog(FindPwDialog.this, "인증에 성공하였습니다!", "인증 성공!", JOptionPane.INFORMATION_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(FindPwDialog.this, "인증번호가 일치하지 않습니다.", "인증 실패", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					JOptionPane.showMessageDialog(FindPwDialog.this, "인증번호를 먼저 받아주세요.", "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		
		
		//1. idsearch 송신
		//2. ok 수신
		//3. name, birth
		//4. ok
		this.findPwBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idInput.getText().isEmpty()&&!(nameInput.getText().isEmpty())) {
					alarmMsg1.setText("ID가 입력되지 않았습니다.");
				}else if(!(idInput.getText().isEmpty())&&nameInput.getText().isEmpty()) {
					alarmMsg1.setText("이름이 입력되지 않았습니다.");
				}else if(idInput.getText().isEmpty()&&nameInput.getText().isEmpty()) {
					alarmMsg1.setText("ID 또는 이름이 입력되지 않았습니다.");
				}else {
					alarmMsg1.setText("");
				}
				
				if(ansInput.getText().isEmpty()) {
					alarmMsg2.setText("답변이 입력되지 않았습니다.");
				}else if (hintBox.getSelectedItem()==hintDef){
					alarmMsg2.setText("비밀번호 힌트를 골라주세요.");
				}else {
					alarmMsg2.setText("");
				}
				
				if(!isAuth) {
					alarmMsg3.setText("휴대폰 인증을 해주세요.");
				}else {
					alarmMsg3.setText("");
				}
					
				if(!(idInput.getText().isEmpty()) && !(nameInput.getText().isEmpty())
				   && !(ansInput.getText().isEmpty()) && hintBox.getSelectedItem()!=hintDef && isAuth){
					String yearData = birthYear.getSelectedItem().toString().substring(0, 4);
					String monthData = birthMonth.getSelectedItem().toString().substring(0, 2);
					String dayData = birthDay.getSelectedItem().toString().substring(0, 2);
					System.out.println(yearData + monthData + dayData);
					findPwInit(idInput.getText(), yearData + "/" + monthData + "/"+ dayData, nameInput.getText(), hintBox.getSelectedItem().toString(), ansInput.getText());
				}
			}
		});
	}
	
	public FindPwDialog(FindIdPwDialog self, Font font,DataInputStream sock_in, DataOutputStream sock_out) {
		this.f = font;
		
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		
		this.setSize(400, 605);
		this.setLocationRelativeTo(self);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setModal(true);
		
		this.eventInit();
		this.compInit();
		
		this.setVisible(true);
	}
}