package main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;

public class FindIdPwDialog extends JDialog{
	private Font f;
	private FindIdPwDialog self = this;
	private JButton findIdBtn = new JButton("아이디 찾기");
	private JButton findPwBtn = new JButton("비밀번호 찾기");
	
	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;
	
	
	public void compInit() {
		this.setTitle("아이디 / 비밀번호 분실");
		this.setLayout(null);
		this.findIdBtn.setBounds(15,80,150,50);
		this.add(findIdBtn);
		this.findPwBtn.setBounds(170,80,150,50);
		this.add(findPwBtn);
	}
	
	public void eventInit() {
		this.findIdBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new FindIdDialog(self, f, sock_in, sock_out);	
			}
		});
		this.findPwBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new FindPwDialog(self,f, sock_in, sock_out);
			}
		});
	}
	
	public FindIdPwDialog(LoginDialog self, Font font,DataInputStream sock_in, DataOutputStream sock_out) {
		this.f = font;
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		this.setSize(350,250);
		this.setLocationRelativeTo(self);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		
		this.findIdBtn.setFont(f.deriveFont(Font.PLAIN,15f));
		this.findPwBtn.setFont(f.deriveFont(Font.PLAIN,15f));
		this.eventInit();
		this.compInit();
		
		this.setVisible(true);
	}
}