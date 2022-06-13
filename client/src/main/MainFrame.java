package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame {

	public int counter = 0;
	private MainFrame self = this;

	public static boolean login_state = false;
	public static int frameChange = 1;

	public String login_userid = null;
	public String login_gend = null;

	// 연습용 책정보
	private String[] bookInfo = null;

	public Dimension screenSize = null;
	private Font f;

	private JLabel login_mention1 = new JLabel("사용자 님!");
	private JLabel login_mention2 = new JLabel("환영합니다.");

	private DataInputStream sock_in = Main.sock_in;
	private DataOutputStream sock_out = Main.sock_out;

	public search.Main pp;

	private ImageIcon getScaledImageIcon(String fileName, int width, int height) {
		URL imageURL = this.getClass().getResource("/images/" + fileName);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);
		return imageIcon;
	}

	private ImageIcon img;
	private JLabel ImgBox;

	public JPanel panel_logout = new JPanel();
	public JPanel panel_login = new JPanel();

	private JLabel logo_ment = new JLabel("세상의 모든 책─");
	private JLabel searchBtn = new JLabel(getScaledImageIcon("search_icon.png", 24, 24));
	private JLabel profileIcon = new JLabel(getScaledImageIcon("profile_icon.png", 30, 30));
	private JLabel profileIconBig = new JLabel(getScaledImageIcon("profile_icon.png", 70, 70));
	private JLabel profileIcon2 = new JLabel(getScaledImageIcon("profile_icon2.png", 30, 30));
	private JLabel profileIcon2Big = new JLabel(getScaledImageIcon("profile_icon2.png", 70, 70));

	private JLabel arrow_up = new JLabel(getScaledImageIcon("arrow_up.png", 30, 30));
	private JLabel arrow_down = new JLabel(getScaledImageIcon("arrow_down.png", 30, 30));
	public JPanel arrowPanel = new JPanel();

	public JLabel mention = new JLabel("로그인으로 다양한 서비스를 이용하세요!");

	private ImageIcon ic = getScaledImageIcon("bookle_logo.png", 230, 61);
	private JLabel logo = new JLabel(ic);

	private JPanel searchBtnPanel = new JPanel();

	public JLabel loginBtn = new JLabel("로그인");
	public JPanel loginPanel = new JPanel();
	public JTextField rand = new JTextField();
	private JPanel profilePanel = new JPanel();
	private HintTextField search = new HintTextField("전체 도서 목록을 보려면 검색어 없이 ENTER");
	private JPanel panel = new JPanel();
	private TitledBorder tb = new TitledBorder(new LineBorder(new Color(0, 199, 60), 3));
	private TitledBorder tb_profile = new TitledBorder(new LineBorder(new Color(103, 153, 255), 2, true));

	private JPanel login_info = new JPanel(new GridLayout(2, 1));

	public JLabel login_name = new JLabel("사용자");
	public JLabel login_email = new JLabel("이메일 없음");

	public JPanel profileMainPanel = new JPanel();
	public JPanel profileSubPanel = new JPanel(new GridLayout(2, 1));
	public JPanel profileBtnPanel = new JPanel(new GridLayout(1, 2));

	public JLabel profileBtn1 = new JLabel("회원정보");
	public JPanel profileBtn1Panel = new JPanel();
	public JLabel profileBtn2 = new JLabel("로그아웃");
	public JPanel profileBtn2Panel = new JPanel();

	public MouseListener ml3 = new MouseAdapter() {
		@Override
		public void mouseExited(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(178, 204, 255));
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(103, 153, 255));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(0, 51, 153));
			int a = JOptionPane.showConfirmDialog(self, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.WARNING_MESSAGE);
			if (a == 0) {
				compLogout();
			}
		}
	};

	public MouseListener ml2 = new MouseAdapter() {
		@Override
		public void mouseExited(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(178, 204, 255));
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(103, 153, 255));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			btn.setBackground(new Color(0, 51, 153));
			new UserInfoDialog(self, f, sock_in, sock_out, login_userid, login_gend);
		}
	};

	public MouseListener ml = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			counter++;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (counter % 2 == 1) {
				compProfilePressed(true);
			} else {
				compProfilePressed(false);
			}
		}
	};

	public void searchInit(String search_msg) {
		try {
			// sock_out.writeUTF("datasearch");
			// sock_out.writeUTF(search_msg);
			// int bookCount = sock_in.readInt();
			// if (bookCount == 0) {
			// String msg = sock_in.readUTF();
			// if (msg.equals("not_found")) {
			// JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
			// }
			// } else {
			// bookInfo = new String[bookCount];
			// for (int i = 0; i < bookCount; i++) {
			// bookInfo[i] = sock_in.readUTF();
			// System.out.println(bookInfo[i]);
			// }
			// }

			this.getContentPane().removeAll();
			this.setLayout(new BorderLayout());
			this.pp = new search.Main(self, sock_in, sock_out, search_msg, login_state);
			this.pp.setVisible(true);
			this.search.setText("");
			this.add(pp);
			this.revalidate();
			this.repaint();
		} catch (Exception e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(this, "검색에 문제가 발생했습니다.");
		}
	}

	public void compLogin(String name, String email) {
		this.panel_login.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		this.login_name.setText(name);
		if (email.equals("")) {
			login_email.setText("이메일 없음");
		} else {
			this.login_email.setText(email);
		}

		String n = name;
		if (name.length() > 3) {
			n = name.substring(0, 3) + "...";
		}

		// 로그인후 프로필 아이콘

		if (login_gend.equals("여")) {
			this.profileIcon2.setBounds(self.getWidth() - 400, self.getHeight() - 110, 30, 30);
			this.panel_login.add(profileIcon2);
		} else {
			this.profileIcon.setBounds(self.getWidth() - 400, self.getHeight() - 110, 30, 30);
			this.panel_login.add(profileIcon);
		}

		// 로그인 후 프로필 옆 내용

		this.login_mention1.setText(" " + n + " 님!");
		this.login_mention1.setBounds(self.getWidth() - 350, self.getHeight() - 110, 200, 35);
		this.login_mention1.setFont(f.deriveFont(Font.BOLD, 14f));
		this.login_mention1.setForeground(new Color(15, 15, 139));
		this.panel_login.add(login_mention1);

		this.login_mention2.setBounds(self.getWidth() - 290, self.getHeight() - 110, 200, 35);
		this.login_mention2.setFont(f.deriveFont(Font.PLAIN, 14f));
		this.login_mention2.setForeground(new Color(15, 15, 139));
		this.panel_login.add(login_mention2);

		this.panel_login.setBounds(self.getWidth() - 300, self.getHeight() - 110, 200, 50);
		this.panel_login.setBackground(new Color(178, 204, 255));
		this.arrowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		this.arrowPanel.setBackground(new Color(178, 204, 255));
		this.arrowPanel.setBounds(self.getWidth() - 100, self.getHeight() - 110, 50, 50);

		this.arrowPanel.add(arrow_up);
		this.add(arrowPanel);
		this.getContentPane().remove(panel_logout);
		this.getContentPane().add(panel_login);

		this.revalidate();
		this.repaint();
	}

	public void compLogout() {
		counter = 0;
		this.login_state = false;
		if (frameChange == 1) {
			this.getContentPane().remove(profileMainPanel);
			this.arrowPanel.remove(arrow_down);
			this.getContentPane().remove(arrowPanel);
			this.getContentPane().remove(panel_login);
			logoutInit(false);
			this.revalidate();
			this.repaint();
		} else if (frameChange == 2) {
			pp.loginInit(false);
		}
	}

	public void compProfilePressed(boolean a) {
		if (a) {
			this.panel_login.setBackground(null);
			this.arrowPanel.setBackground(null);
			this.arrowPanel.remove(arrow_up);

			this.profileMainPanel.setLayout(new BorderLayout());
			this.profileMainPanel.setBounds(self.getWidth() - 300, self.getHeight() - 235, 250, 125);
			this.profileMainPanel.setBorder(tb_profile);
			this.profileMainPanel.setBackground(Color.WHITE);

			this.profileMainPanel.add(profileBtnPanel, BorderLayout.SOUTH);

			this.profileBtn1Panel.setBackground(new Color(178, 204, 255));
			this.profileBtn2Panel.setBackground(new Color(178, 204, 255));

			this.profileBtn1.setPreferredSize(new Dimension(80, 30));
			this.profileBtn1.setHorizontalAlignment(SwingConstants.CENTER);
			this.profileBtn1.setFont(f.deriveFont(Font.PLAIN, 13f));
			this.profileBtn2.setPreferredSize(new Dimension(80, 30));
			this.profileBtn2.setHorizontalAlignment(SwingConstants.CENTER);
			this.profileBtn2.setFont(f.deriveFont(Font.PLAIN, 13f));
			this.profileBtn1Panel.add(profileBtn1);
			this.profileBtn2Panel.add(profileBtn2);
			this.profileBtnPanel.add(profileBtn1Panel, BorderLayout.CENTER);
			this.profileBtnPanel.add(profileBtn2Panel, BorderLayout.CENTER);

			this.profileMainPanel.add(profilePanel, BorderLayout.WEST);
			if (login_gend.equals("여")) {
				this.profilePanel.add(profileIcon2Big, new FlowLayout(FlowLayout.LEFT, 20, 40));
			} else {
				this.profilePanel.add(profileIconBig, new FlowLayout(FlowLayout.LEFT, 20, 40));
			}

			this.login_name.setFont(f.deriveFont(Font.BOLD, 16f));
			this.profilePanel.add(login_name, new FlowLayout(FlowLayout.LEFT, 110, 20));
			this.profilePanel.setBackground(Color.white);
			this.login_email.setFont(f.deriveFont(Font.PLAIN, 12f));
			this.login_email.setForeground(Color.GRAY);

			this.login_info.setBackground(Color.WHITE);
			this.login_info.add(login_name);
			this.login_info.add(login_email);

			this.profilePanel.add(login_info, new FlowLayout(FlowLayout.LEFT));

			this.login_mention1.setForeground(new Color(0, 0, 0));
			this.login_mention2.setForeground(new Color(0, 0, 0));

			this.arrowPanel.add(arrow_down);

			this.add(profileMainPanel);

			this.invalidate();
			this.validate();
			this.repaint();
		} else {
			this.panel_login.setBackground(new Color(178, 204, 255));
			this.arrowPanel.setBackground(new Color(178, 204, 255));
			this.arrowPanel.remove(arrow_down);
			this.getContentPane().remove(profileMainPanel);
			this.login_mention1.setForeground(new Color(15, 15, 139));
			this.login_mention2.setForeground(new Color(15, 15, 139));
			this.arrowPanel.add(arrow_up);
			this.invalidate();
			this.validate();
			this.repaint();
		}
	}

	public void eventInit() {
		this.profileBtn1Panel.addMouseListener(ml2);
		this.profileBtn2Panel.addMouseListener(ml3);

		this.loginPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				loginPanel.setBackground(new Color(68, 133, 243));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				loginPanel.setBackground(new Color(19, 81, 186));

			}

			@Override
			public void mouseExited(MouseEvent e) {
				loginPanel.setBackground(new Color(68, 133, 243));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				loginPanel.setBackground(new Color(68, 133, 243));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				new LoginDialog(self, sock_in, sock_out, f);
			}
		});

		this.search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// 검색 엔터키 동작

					System.out.println("검색 활성화");
					searchInit(search.getText());

				}
			}
		});

		this.searchBtnPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 검색 버튼 클릭 시
				System.out.println("검색버튼 클릭");
				searchInit(search.getText());

			}
		});

		this.panel_login.addMouseListener(ml);
		this.arrowPanel.addMouseListener(ml);
	}

	public void logoutInit(boolean signal) { // signal값이 0이면 로그아웃 상태, 1이면 로그인 상태
		if (!signal) {
			// 오른쪽 아래 "로그인하세요!" 멘트
			this.mention.setPreferredSize(new Dimension(200, 35));
			this.mention.setFont(f.deriveFont(Font.PLAIN, 11f));
			this.panel_logout.add(mention);

			// 오른쪽 아래 로그인 버튼
			this.loginPanel.setPreferredSize(new Dimension(65, 35));
			this.loginPanel.setBackground(new Color(68, 133, 243));
			this.loginBtn.setFont(f.deriveFont(Font.BOLD, 14f));
			this.loginBtn.setForeground(new Color(255, 255, 255));
			this.loginPanel.setLayout(new BorderLayout());
			this.loginBtn.setHorizontalAlignment(SwingConstants.CENTER);
			this.loginPanel.add(loginBtn);
			// 로그인 되기 전 로그인버튼
			this.panel_logout.add(loginPanel);
			this.panel_logout.setBounds(self.getWidth() - 350, self.getHeight() - 110, 300, 50);

			panel_logout.revalidate();
			panel_logout.repaint();
			this.add(panel_logout);
		} else if (signal) {
			compLogin(login_name.getText(), login_email.getText());
		}
	}

	public void compInit(boolean signal) {
		this.setLayout(null);

		// 배경그림
		img = getScaledImageIcon("main_bg.png", screenSize.width, screenSize.height);
		ImgBox = new JLabel(img);
		ImgBox.setBounds(0, 0, screenSize.width, screenSize.height);
		// 패널에 컴퍼턴트 등록
		this.getContentPane().add(ImgBox);
		this.rand.setBounds(0,0,0,0);
		this.add(rand);

		// 메인화면 가운데 로고
		this.logo.setBounds(screenSize.width / 2 - 160, screenSize.height / 2 - 230, ic.getIconWidth(),
				ic.getIconHeight());
		this.add(logo);

		this.logo_ment.setForeground(Color.GRAY);
		this.logo_ment.setFont(f.deriveFont(Font.PLAIN, 14f));
		this.logo_ment.setBounds(screenSize.width / 2 + 80, screenSize.height / 2 - 185, 100, 15);
		this.add(logo_ment);

		// 메인화면 가운데 검색 입력창
		this.search.setBounds(screenSize.width / 2 - 265, screenSize.height / 2 - 125, 450, 40);
		this.search.setFont(f.deriveFont(Font.PLAIN, 22.0F));
		this.search.setBorder(null);
		this.search.setOpaque(false);
		this.add(search);

		// 메인화면 가운데 검색창 테두리, 검색버튼
		this.searchBtnPanel.setBounds(screenSize.width / 2 + 217, screenSize.height / 2 - 130, 50, 50);
		this.searchBtnPanel.setBackground(new Color(0, 199, 60));
		this.searchBtnPanel.setLayout(new BorderLayout());
		this.searchBtnPanel.add(searchBtn);
		this.add(searchBtnPanel);

		this.panel.setBackground(new Color(255, 255, 255));
		this.panel.setBorder(tb);
		this.panel.setBounds(screenSize.width / 2 - 280, screenSize.height / 2 - 130, 550, 50);
		this.add(panel);

		logoutInit(signal);
	}

	public MainFrame(Font font) {

		this.f = font;
		this.setTitle("Bookle");
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width - 10, screenSize.height - 50);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setResizable(false);

		this.compInit(false);

		this.eventInit();

		this.setVisible(true);
	}
}