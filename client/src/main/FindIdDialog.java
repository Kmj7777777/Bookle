package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FindIdDialog extends JDialog {
	private Font f;

	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;
	
	private JLabel findIdTitle = new JLabel("아이디 찾기");

	private JLabel nameTitle = new JLabel("이름");
	private JLabel birthTitle = new JLabel("생년월일");
	
	private JTextField rand = new JTextField();
	
	private JTextField nameInput = new HintTextField("이름을 입력해주세요.");
	private JComboBox<String> birthYear = new JComboBox<>();
	private JComboBox<String> birthMonth = new JComboBox<>();
	private JComboBox<String> birthDay = new JComboBox<>();

	private JButton findIdBtn = new JButton("찾기");
	
	private JLabel alarmMsg = new JLabel();

	
	public void findIdEvent(String name, String birth) {
		try {
			// 1. idsearch 송신
			sock_out.writeUTF("account_idsearch");
			
			// 3. name, birth
			sock_out.writeUTF(name);
			sock_out.writeUTF(birth);
			sock_out.flush();
			String s = sock_in.readUTF();
			if(s.equals("not_found")) {
				JOptionPane.showMessageDialog(this, "정보가 없습니다.");
			}else {
				JOptionPane.showMessageDialog(this, "찾으시는 아이디는 다음과 같습니다.\n"+s);
				dispose();
			}
		}catch(Exception e) {
			JOptionPane.showMessageDialog(this, "아이디 찾기에 문제가 발생했습니다.");
		}
	}

	public void eventInit() {
		this.findIdBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (nameInput.getText().isEmpty()) {
					alarmMsg.setText("이름이 입력되지 않았습니다.");
				} else {
					String yearData = birthYear.getSelectedItem().toString().substring(0, 4);
					String monthData = birthMonth.getSelectedItem().toString().substring(0, 2);
					String dayData = birthDay.getSelectedItem().toString().substring(0, 2);

					findIdEvent(nameInput.getText(), yearData+"/"+monthData+"/"+dayData);				
				}
			}
		});
	}

	public void compInit() {
		this.setLayout(null);
		this.setTitle("아이디 찾기");
		
		this.rand.setBounds(0,0,0,0);
		this.add(rand);
		this.findIdTitle.setBounds(20, 20, 300, 30);
		this.findIdTitle.setFont(f.deriveFont(Font.BOLD, 17f));
		this.add(findIdTitle);

		for (int i = 2018; i >= 1900; i--) {
			this.birthYear.addItem(i + "년");
		}
		for (int i = 1; i <= 9; i++) {
			this.birthMonth.addItem("0" + i + "월");
		}
		for (int i = 10; i <= 12; i++) {
			this.birthMonth.addItem(i + "월");
		}
		for (int i = 1; i <= 9; i++) {
			this.birthDay.addItem("0" + i + "일");
		}
		for (int i = 10; i <= 31; i++) {
			this.birthDay.addItem(i + "일");
		}
		this.nameTitle.setBounds(10, 70, 80, 40);
		this.nameTitle.setFont(f.deriveFont(Font.BOLD,14f));
		// this.nameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(nameTitle);

		this.nameInput.setBounds(90, 70, 240, 40);
		this.nameInput.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(nameInput);

		this.birthTitle.setBounds(10, 120, 80, 40);
		this.birthTitle.setFont(f.deriveFont(Font.BOLD,14f));
		// this.birthTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(birthTitle);

		this.birthYear.setBounds(90, 120, 80, 40);
		this.birthYear.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthYear);

		this.birthMonth.setBounds(180, 120, 70, 40);
		this.birthMonth.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthMonth);

		this.birthDay.setBounds(260, 120, 70, 40);
		this.birthDay.setFont(f.deriveFont(Font.PLAIN,14f));
		this.add(birthDay);
		
		this.alarmMsg.setBounds(10,177,220,20);
		this.alarmMsg.setForeground(new Color(255, 0, 0));
		this.alarmMsg.setFont(f.deriveFont(Font.PLAIN|Font.ITALIC,12f));
		this.add(alarmMsg);

		this.findIdBtn.setBounds(230, 170, 100, 35);
		this.findIdBtn.setBackground(new Color(178, 204, 255));
		this.findIdBtn.setFont(f.deriveFont(Font.BOLD,14f));
		this.add(findIdBtn);

	}

	public FindIdDialog(FindIdPwDialog self, Font font,DataInputStream sock_in, DataOutputStream sock_out) {
		this.f = font;
		
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		
		this.setSize(350, 270);
		this.setLocationRelativeTo(self);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setModal(true);
		this.eventInit();
		this.compInit();

		this.setVisible(true);
	}
}