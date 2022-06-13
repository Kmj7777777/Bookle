package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {
	public MainFrame main_addr = null;

	private ImageIcon getScaledImageIcon(String fileName, int width, int height) {
		URL imageURL = this.getClass().getResource("/images/" + fileName);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(width, height, 4);
		imageIcon = new ImageIcon(newimg);
		return imageIcon;
	}

	private int width = 70;
	private int height = 18;
	private JLabel logoImg = new JLabel(getScaledImageIcon("bookle_logo.png", this.width, this.height));
	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;
	private LoginDialog self = this;
	private JLabel idTitle = new JLabel("ID");
	private JLabel pwTitle = new JLabel("PW");
	private JLabel loginTitle = new JLabel("LOGIN");
	private JTextField idInput = new JTextField();
	private JPasswordField pwInput = new JPasswordField("");
	private JLabel alarmMsg = new JLabel();
	private JButton login = new JButton("로그인");
	private JButton findIdPwBtn = new JButton("ID 또는 패스워드가 기억나지 않습니다.");
	private JButton joinBtn = new JButton("아직 회원이 아니신가요?");
	private Font f;
	
	private JPanel admin_addr;

	public void loginEvent(String id, String pw) {
		try {
			this.sock_out.writeUTF("account_login");
			sock_out.flush();
			System.out.println("로그인 시도");
			this.sock_out.writeUTF(id);
			this.sock_out.writeUTF(pw);
			sock_out.flush();
			String cmd2 = this.sock_in.readUTF();
			System.out.println(cmd2);
			if (cmd2.equals("general")) {
				String name = sock_in.readUTF();
				System.out.println(name);
				String email = sock_in.readUTF();
				System.out.println(email);
				if (email.equals("null")) {
					email = "";
				}
				String gend = sock_in.readUTF();
				System.out.println("로그인 최종성공");
				MainFrame.login_state = true;
				main_addr.login_userid = id;
				main_addr.login_gend = gend;
				main_addr.compLogin(name, email);
				if (MainFrame.frameChange == 2) {
					main_addr.pp.loginInit(true);
				}
				dispose();
			} else if(cmd2.equals("admin")) {
//				this.setLayout(new BorderLayout());
//				this.pp = new search.Main(self, sock_in, sock_out, search_msg, login_state);
//				this.pp.setVisible(true);
//				this.search.setText("");
//				this.add(pp);
//				this.revalidate();
//				this.repaint();
				
				MainFrame.login_state = true;
				main_addr.getContentPane().removeAll();
				main_addr.panel_login.removeAll();
				main_addr.counter = 0;
				main_addr.setLayout(new BorderLayout());
				admin_addr = new admin.client.Main(main_addr);
				admin_addr.setVisible(true);
				main_addr.add(admin_addr);
				main_addr.revalidate();
				main_addr.repaint();
				dispose();
			}
			else if (cmd2.equals("id_error")) {
				JOptionPane.showMessageDialog(this.self, "아이디가 존재하지 않습니다.");
			} else if (cmd2.equals("pw_error")) {
				JOptionPane.showMessageDialog(this.self, "비밀번호가 틀렸습니다.");
			} else if (cmd2.equals("login_fail")) {
				this.alarmMsg.setText("로그인 과정에 문제가 발생하였습니다.");
			}
		} catch (Exception e) {
			this.alarmMsg.setText("로그인 과정에 문제가 발생하였습니다.");
		}
	}

	public void eventInit() {
		this.idInput.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			@SuppressWarnings("deprecation")
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					if (LoginDialog.this.idInput.getText().length() == 0) {
						LoginDialog.this.alarmMsg.setText("아이디가 입력되지 않았습니다.");
					} else if (LoginDialog.this.pwInput.getText().equals("")) {
						LoginDialog.this.alarmMsg.setText("비밀번호가 입력되지 않았습니다.");
					} else {
						LoginDialog.this.loginEvent(LoginDialog.this.idInput.getText(),
								LoginDialog.this.pwInput.getText());
					}
				}
			}
		});
		this.pwInput.addKeyListener(new KeyAdapter() {
			@SuppressWarnings("deprecation")
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					if (LoginDialog.this.pwInput.getText().equals("")) {
						LoginDialog.this.alarmMsg.setText("비밀번호가 입력되지 않았습니다.");
					} else if (LoginDialog.this.idInput.getText().length() == 0) {
						LoginDialog.this.alarmMsg.setText("아이디가 입력되지 않았습니다.");
					} else {
						LoginDialog.this.loginEvent(LoginDialog.this.idInput.getText(),
								LoginDialog.this.pwInput.getText());
					}
				}
			}
		});

		this.login.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if (LoginDialog.this.idInput.getText().length() == 0) {
					LoginDialog.this.alarmMsg.setText("아이디가 입력되지 않았습니다.");
				} else if (LoginDialog.this.pwInput.getText().equals("")) {
					LoginDialog.this.alarmMsg.setText("비밀번호가 입력되지 않았습니다.");
				} else {
					LoginDialog.this.loginEvent(LoginDialog.this.idInput.getText(), LoginDialog.this.pwInput.getText());
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	public void compInit() {
		this.logoImg.setBounds(7, 7, this.width, this.height);
		add(this.logoImg);

		this.pwInput.setEchoChar('*');
		setLayout(null);
		setTitle("Bookle > Login");

		this.loginTitle.setBounds(0, 20, 350, 30);
		this.loginTitle.setHorizontalAlignment(0);
		this.loginTitle.setFont(this.f.deriveFont(1, 25.0F));
		add(this.loginTitle);

		this.idTitle.setBounds(15, 70, 30, 30);
		this.idTitle.setFont(this.f.deriveFont(1, 15.0F));
		add(this.idTitle);
		this.idInput.setNextFocusableComponent(this.pwInput);

		this.pwTitle.setBounds(15, 110, 30, 30);
		this.pwTitle.setFont(this.f.deriveFont(1, 15.0F));
		add(this.pwTitle);

		this.idInput.setBounds(50, 70, 187, 30);
		this.idInput.setFont(this.f.deriveFont(0, 12.0F));
		add(this.idInput);

		this.pwInput.setBounds(50, 110, 187, 30);
		this.pwInput.setFont(this.f.deriveFont(0, 12.0F));
		add(this.pwInput);

		this.login.setBounds(245, 70, 80, 70);
		this.login.setBackground(new Color(178, 204, 255));
		this.login.setFont(this.f.deriveFont(0, 14.0F));
		add(this.login);

		this.alarmMsg.setBounds(23, 150, 300, 20);
		this.alarmMsg.setForeground(new Color(255, 0, 0));
		this.alarmMsg.setFont(this.f.deriveFont(2, 12.0F));
		add(this.alarmMsg);

		this.findIdPwBtn.setBounds(10, 185, 300, 20);
		this.findIdPwBtn.setContentAreaFilled(false);
		this.findIdPwBtn.setHorizontalAlignment(2);
		this.findIdPwBtn.setFont(this.f.deriveFont(0, 12.0F));
		this.findIdPwBtn.setForeground(new Color(0, 0, 204));
		this.findIdPwBtn.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(0, 12.0F));
				btn.setForeground(new Color(0, 0, 204));
			}

			public void mousePressed(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btn.setFont(LoginDialog.this.f.deriveFont(1, 12.0F));
				btn.setForeground(new Color(5, 0, 153));
			}

			public void mouseExited(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(0, 12.0F));
			}

			public void mouseEntered(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(1, 12.0F));
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btn.setForeground(new Color(0, 0, 255));
			}

			public void mouseClicked(MouseEvent e) {
				new FindIdPwDialog(self, f, sock_in, sock_out);
			}
		});
		add(this.findIdPwBtn);

		this.joinBtn.setBounds(10, 210, 300, 20);
		this.joinBtn.setContentAreaFilled(false);
		this.joinBtn.setHorizontalAlignment(2);
		this.joinBtn.setFont(this.f.deriveFont(0, 12.0F));
		this.joinBtn.setForeground(new Color(0, 0, 204));
		this.joinBtn.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(0, 12.0F));
				btn.setForeground(new Color(0, 0, 204));
			}

			public void mousePressed(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(1, 12.0F));
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btn.setForeground(new Color(5, 0, 153));
			}

			public void mouseExited(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setFont(LoginDialog.this.f.deriveFont(0, 12.0F));
			}

			public void mouseEntered(MouseEvent e) {
				JButton btn = (JButton) e.getSource();
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btn.setFont(LoginDialog.this.f.deriveFont(1, 12.0F));
				btn.setForeground(new Color(0, 0, 255));
			}

			public void mouseClicked(MouseEvent e) {
				new JoinDialog(self, f, sock_in, sock_out);
			}
		});
		add(this.joinBtn);

	}

	public LoginDialog(MainFrame main_addr, DataInputStream sock_in, DataOutputStream sock_out, Font font) {
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		this.main_addr = main_addr;

		try {
			this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("logindialog_bg.png")))));
		} catch (Exception e) {
		}
		this.f = font;
		setSize(350, 270);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);

		compInit();
		eventInit();

		setVisible(true);
	}
}