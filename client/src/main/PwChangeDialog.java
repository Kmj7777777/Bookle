package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PwChangeDialog extends JDialog{
	private JPanel panel = new JPanel(new GridLayout(2, 1));
	private JLabel label = new JLabel("현재 패스워드를 한 번 더 입력하세요.");
	private JPasswordField pw = new JPasswordField("");
	private JButton send = new JButton("확인");
	
	private PwChangeDialog thisadd = this;
	
	private DataInputStream sock_in;
	private DataOutputStream sock_out;
	private Font font;
	
	
	@SuppressWarnings("deprecation")
	public void pwEvent() {
		if(pw.getText().length()==0) {
			label.setText("패스워드가 입력되지 않았습니다.");
		}else {
			try {
				//현재비밀번호 입력 (1: 서버에서 내 커맨드를 구분할 신호값)
				sock_out.writeInt(1);
				sock_out.writeUTF(pw.getText());
				String cmd = sock_in.readUTF();
				if(cmd.equals("inputnewpw")) {
					new NewPwChangeDialog(thisadd, font, sock_in, sock_out);
				}else if (cmd.equals("pw_fail")) {
					label.setText("비밀번호가 틀렸습니다.다시 입력해주세요.");
				}
			}catch(Exception e1) {
				JOptionPane.showMessageDialog(thisadd, "서버 접속 오류입니다.");
				dispose();
			}
		}
	}
	
	public PwChangeDialog(UserInfoDialog self, Font font, DataInputStream sock_in, DataOutputStream sock_out){
		this.font = font;
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		
		this.addWindowListener(new WindowAdapter() {	
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					sock_out.writeInt(0);
					System.out.println("도중 중단 메세지 전송");
				}catch(Exception e1) {}
				dispose();
				
			}
		});
		
		
		this.setLayout(new FlowLayout());
		
		this.setTitle("비밀번호 변경");
		this.setLocationRelativeTo(self);
		this.setSize(350, 120);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.label.setFont(font.deriveFont(Font.PLAIN, 13f));
		this.label.setForeground(Color.RED);
		this.label.setPreferredSize(new Dimension(200, 20));
		this.panel.add(label);
		this.pw.setEchoChar('*');
		this.pw.setFont(font.deriveFont(Font.PLAIN, 20f));
		this.pw.setPreferredSize(new Dimension(240, 35));
		this.panel.add(pw);
		this.add(panel);
		
		this.send.setPreferredSize(new Dimension(70, 70));
		this.add(send);
		
		this.pw.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					pwEvent();
				}
			}
		});
		
		this.send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pwEvent();
			}
		});
		
		this.setResizable(false);
		this.setModal(true);
		this.setVisible(true);
	}
}