package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class NewPwChangeDialog extends JDialog {
	
	private PwChangeDialog pcd_addr = null;
	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;
	private JPasswordField pw = new JPasswordField("");
	private JPasswordField pwconfirm = new JPasswordField("");
	private JLabel label = new JLabel("대소문자 또는 특수문자 포함 8~20자리로 입력하세요.");
	private JLabel label_pw = new JLabel("새로운 비밀번호");
	private JLabel label_pwconfirm = new JLabel("비밀번호 확인");
	private JButton send = new JButton("전송");
	private final String pw_regex = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,20}$";
	
	private KeyListener kl = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 10) {
				pwEvent();
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	public void pwEvent() {
		String passwd = String.valueOf(pw.getPassword());
		String confirmPw = String.valueOf(pwconfirm.getPassword());
		
		if(passwd.trim().isEmpty() || confirmPw.trim().isEmpty()) {
			label.setText("정보가 충분히 입력되지 않았습니다.");
		}else if(!passwd.equals(confirmPw)) {
			label.setText("두 비밀번호가 일치하지 않습니다.");
		}else if(!Pattern.matches(pw_regex, passwd)) {
			label.setText("사용하실 수 없는 비밀번호입니다.");
		}else if(passwd.contains("|")){
			label.setText("사용하실 수 없는 비밀번호입니다.");
		}else {
			try {
				sock_out.writeInt(2);
				sock_out.writeUTF(String.valueOf(pw.getPassword()));
				String s = sock_in.readUTF();
				System.out.println(s);
				if(s.equals("success")){
					JOptionPane.showMessageDialog(pcd_addr, "변경 성공");
					dispose();
					pcd_addr.dispose();
				}else if(s.equals("change_error")) {
					JOptionPane.showMessageDialog(pcd_addr, "변경 오류");
				}else if(s.equals("pwduplicate")) {
					label.setText("중복된 비밀번호입니다. 다시 입력해주세요.");
				}
			}catch(Exception e1) {
				JOptionPane.showMessageDialog(pcd_addr, "변경 불가");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void compInit(Font f) {
		
		this.setLayout(null);
				
		this.pw.setEchoChar('*');
		this.pwconfirm.setEchoChar('*');
		
		this.label.setBounds(10,10,300,40);
		this.label.setFont(f.deriveFont(Font.BOLD, 14f));
		this.label.setForeground(Color.BLUE);
		this.add(label);
		
		this.label_pw.setBounds(10,50,110,50);
		this.label_pw.setFont(f.deriveFont(Font.BOLD, 12f));
		this.label_pw.setForeground(Color.RED);
		this.add(label_pw);
		
		this.pw.setNextFocusableComponent(this.pwconfirm);
		
		this.label_pwconfirm.setBounds(10,110,110,50);
		this.label_pwconfirm.setFont(f.deriveFont(Font.BOLD, 12f));
		this.label_pwconfirm.setForeground(Color.RED);
		this.add(label_pwconfirm);
		
		
		this.pw.setFont(f.deriveFont(Font.PLAIN, 12f));
		this.pw.setBounds(120,50,180,50);
		this.add(pw);
		
		this.pwconfirm.setFont(f.deriveFont(Font.PLAIN, 12f));
		this.pwconfirm.setBounds(120,110,180,50);
		this.add(pwconfirm);
		
		this.send.setFont(f.deriveFont(Font.PLAIN, 14f));
		this.send.setBounds(310,50,80,100);
		this.add(send);
	}
	
	public NewPwChangeDialog(PwChangeDialog self, Font f, DataInputStream sock_in, DataOutputStream sock_out){
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		this.pcd_addr = self;
		
		this.addWindowListener(new WindowAdapter() {	
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					sock_out.writeInt(0);
					System.out.println("도중 중단 메세지 전송");
				}catch(Exception e1) {}
				self.dispose();
				dispose();
			}
		});
		
		this.setTitle("새 비밀번호 생성");
		this.setSize(400, 200);
		this.setLocationRelativeTo(self);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.compInit(f);
		
		
		this.pw.addKeyListener(kl);
		this.send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pwEvent();
				
			}
		});
		
		this.pwconfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send.doClick();
			}
		});
		
		this.send.setPreferredSize(new Dimension(100, 100));
		this.add(send);
		this.setResizable(false);
		this.setModal(true);
		
		this.setVisible(true);
	}
}