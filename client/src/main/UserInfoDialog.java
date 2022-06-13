package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class UserInfoDialog extends JDialog {
	private Font f;
	private DataInputStream sock_in = null;
	private DataOutputStream sock_out = null;

	private UserInfoDialog self = this;

	private ImageIcon getScaledImageIcon(String fileName, int width, int height) {
		URL imageURL = this.getClass().getResource("/images/" + fileName);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);
		return imageIcon;
	}

	private ImageIcon ic = getScaledImageIcon("bookle_logo.png", 130, 35);
	private JLabel logo = new JLabel(ic);
	public String userid = null;
	public String username = null;
	public String userphone = null;
	public String useremail = null;
	public String userjoindate = null;
	public String userbirth = null;
	public String usergend = null;
	public int currentRental = -1;
	public int possibleRental = -1;

	private JLabel profileIcon = new JLabel(getScaledImageIcon("profile_icon.png", 110, 110));
	private JLabel profileIcon2 = new JLabel(getScaledImageIcon("profile_icon2.png", 110, 110));
	private JLabel idLabel = new JLabel("userid");
	private JLabel nameLabel = new JLabel("사용자 님! 안녕하세요");
	private JLabel nameETCLabel = new JLabel("사용자 정보가 없습니다.");

	private JPanel infoPanel = new JPanel();

	private int menu_sel = 1;

	private JPanel btnPanel = new JPanel(new GridLayout(9, 1));
	public JPanel contentPanel = new JPanel(null);

	private JPanel btn1 = new JPanel();
	private JLabel btn1_label = new JLabel("회  원  정  보");
	private JPanel btn2 = new JPanel();
	private JLabel btn2_label = new JLabel("예  약  현  황");
	private JPanel btn3 = new JPanel();
	private JLabel btn3_label = new JLabel("대  여  현  황");
	private JPanel btn4 = new JPanel();
	private JLabel btn4_label = new JLabel("대  여  내  역");

	public void menuInit() {

		this.contentPanel.removeAll();
		this.infoPanel.remove(contentPanel);

		this.contentPanel.setBackground(Color.WHITE);
		if (menu_sel == 1)
			new Content1Comp(self, f, sock_in, sock_out);
		else if (menu_sel == 2)
			new Content2Comp(self, f, sock_in, sock_out);
		else if (menu_sel == 3)
			new Content3Comp(self, f, sock_in, sock_out, 3);
		else if (menu_sel == 4)
			new Content3Comp(self, f, sock_in, sock_out, 4);
		this.infoPanel.add(contentPanel);
		this.revalidate();
		this.repaint();
	}

	public void sockInit() {
		try {
			sock_out.writeUTF("account_mypage");
			System.out.println("id sent");
			String tmp = sock_in.readUTF();
			System.out.println(tmp);
			this.userid = tmp.split("\\|")[0];
			this.username = tmp.split("\\|")[1];
			this.userbirth = tmp.split("\\|")[2];
			this.userphone = tmp.split("\\|")[3];
			this.useremail = tmp.split("\\|")[4];
			this.userjoindate = tmp.split("\\|")[5];
			this.currentRental = Integer.parseInt(tmp.split("\\|")[6]);
			this.possibleRental = Integer.parseInt(tmp.split("\\|")[7]);
			System.out.println("성공");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "회원정보를 가져오는 중 문제가 발생했습니다.");
		}
	}

	public void compInit() {
		menuInit();
		// 프로필 아이콘
		this.setLayout(null);

		if (usergend.equals("여")) {
			this.profileIcon2.setBounds(30, 20, 110, 110);
			this.add(profileIcon2);
		} else {
			this.profileIcon.setBounds(30, 20, 110, 110);
			this.add(profileIcon);
		}

		// bookle 로고
		this.logo.setBounds(this.getWidth() - 140, 115, ic.getIconWidth(), ic.getIconHeight());
		this.add(logo);

		// 프로필 이름
		this.nameLabel.setText(username + " 님! 안녕하세요");
		this.nameLabel.setForeground(new Color(0, 84, 255));
		this.nameLabel.setFont(f.deriveFont(Font.BOLD, 26f));
		if (usergend.equals("여")) {
			this.nameLabel.setBounds(profileIcon2.getX() + profileIcon2.getWidth() + 20, profileIcon2.getY() + 20, 400,
					30);
		} else {
			this.nameLabel.setBounds(profileIcon.getX() + profileIcon.getWidth() + 20, profileIcon.getY() + 20, 400,
					30);
		}

		this.add(nameLabel);

		// 프로필 아이디
		this.idLabel.setText(userid);
		this.idLabel.setForeground(new Color(0, 84, 255));
		this.idLabel.setFont(f.deriveFont(Font.PLAIN, 14f));
		this.idLabel.setBounds(nameLabel.getX(), nameLabel.getY() + 30, 400, 20);
		this.add(idLabel);

		// 프로필 기타정보
		this.nameETCLabel.setForeground(new Color(93, 93, 93));
		if (useremail.equals("null")) {
			this.nameETCLabel.setText("이메일 정보가 없습니다");
		} else {
			this.nameETCLabel.setText(useremail);
		}
		this.nameETCLabel.setFont(f.deriveFont(Font.PLAIN, 12f));
		this.nameETCLabel.setBounds(idLabel.getX(), idLabel.getY() + 20, 800, 20);
		this.add(nameETCLabel);

		// 버튼 1
		this.btn1.setLayout(new BorderLayout());
		this.btn1_label.setFont(f.deriveFont(Font.PLAIN, 18f));
		this.btn1_label.setForeground(Color.WHITE);
		this.btn1.setPreferredSize(new Dimension(180, 50));
		this.btn1_label.setHorizontalAlignment(SwingConstants.CENTER);
		this.btn1.setBackground(new Color(0, 51, 153));
		this.btn1.add(btn1_label);

		this.btnPanel.add(btn1);

		// 버튼 2
		this.btn2.setLayout(new BorderLayout());
		this.btn2_label.setFont(f.deriveFont(Font.PLAIN, 18f));
		this.btn2.setPreferredSize(new Dimension(200, 60));
		this.btn2.setBackground(new Color(178, 204, 255));
		this.btn2_label.setHorizontalAlignment(SwingConstants.CENTER);
		this.btn2.add(btn2_label);

		this.btnPanel.add(btn2);

		// 버튼 3
		this.btn3.setLayout(new BorderLayout());
		this.btn3_label.setFont(f.deriveFont(Font.PLAIN, 18f));
		this.btn3.setPreferredSize(new Dimension(200, 60));
		this.btn3_label.setHorizontalAlignment(SwingConstants.CENTER);
		this.btn3.setBackground(new Color(178, 204, 255));
		this.btn3.add(btn3_label);

		this.btnPanel.add(btn3);

		// 버튼 4
		this.btn4.setLayout(new BorderLayout());
		this.btn4_label.setFont(f.deriveFont(Font.PLAIN, 18f));
		this.btn4.setPreferredSize(new Dimension(200, 60));
		this.btn4_label.setHorizontalAlignment(SwingConstants.CENTER);
		this.btn4.setBackground(new Color(178, 204, 255));
		this.btn4.add(btn4_label);

		this.btnPanel.add(btn4);

		// 정보표시 패널
		this.infoPanel.setLayout(new BorderLayout());
		this.infoPanel.setBackground(Color.WHITE);
		this.infoPanel.setBounds(0, 150, this.getWidth() - 6, this.getHeight() - 180);
		this.contentPanel.setBackground(Color.WHITE);
		this.contentPanel
				.setPreferredSize(new Dimension(infoPanel.getWidth() - btnPanel.getWidth(), btnPanel.getHeight()));
		this.infoPanel.add(btnPanel, BorderLayout.WEST);
		this.infoPanel.add(contentPanel);
		this.add(infoPanel);
	}

	public void eventInit() {
		this.btn1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (menu_sel != 1) {
					btn1.setBackground(new Color(178, 204, 255));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (menu_sel != 1) {
					btn1.setBackground(new Color(103, 153, 255));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (menu_sel != 1) {
					menu_sel = 1;
					btn1.setBackground(new Color(0, 51, 153));
					btn1_label.setForeground(Color.white);
					btn2.setBackground(new Color(178, 204, 255));
					btn2_label.setForeground(null);
					btn3.setBackground(new Color(178, 204, 255));
					btn3_label.setForeground(null);
					btn4.setBackground(new Color(178, 204, 255));
					btn4_label.setForeground(null);
					menuInit();
				}
			}
		});

		this.btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (menu_sel != 2) {
					btn2.setBackground(new Color(178, 204, 255));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (menu_sel != 2) {
					btn2.setBackground(new Color(103, 153, 255));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (menu_sel != 2) {
					menu_sel = 2;
					btn1.setBackground(new Color(178, 204, 255));
					btn1_label.setForeground(null);
					btn2.setBackground(new Color(0, 51, 153));
					btn2_label.setForeground(Color.white);
					btn3.setBackground(new Color(178, 204, 255));
					btn3_label.setForeground(null);
					btn4.setBackground(new Color(178, 204, 255));
					btn4_label.setForeground(null);
					menuInit();
				}
			}
		});

		this.btn3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (menu_sel != 3) {
					btn3.setBackground(new Color(178, 204, 255));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (menu_sel != 3) {
					btn3.setBackground(new Color(103, 153, 255));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (menu_sel != 3) {
					menu_sel = 3;
					btn1.setBackground(new Color(178, 204, 255));
					btn1_label.setForeground(null);
					btn2.setBackground(new Color(178, 204, 255));
					btn2_label.setForeground(null);
					btn3.setBackground(new Color(0, 51, 153));
					btn3_label.setForeground(Color.white);
					btn4.setBackground(new Color(178, 204, 255));
					btn4_label.setForeground(null);
					menuInit();
				}
			}
		});

		this.btn4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (menu_sel != 4) {
					btn4.setBackground(new Color(178, 204, 255));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (menu_sel != 4) {
					btn4.setBackground(new Color(103, 153, 255));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (menu_sel != 4) {
					menu_sel = 4;
					btn1.setBackground(new Color(178, 204, 255));
					btn1_label.setForeground(null);
					btn2.setBackground(new Color(178, 204, 255));
					btn2_label.setForeground(null);
					btn3.setBackground(new Color(178, 204, 255));
					btn3_label.setForeground(null);
					btn4.setBackground(new Color(0, 51, 153));
					btn4_label.setForeground(Color.white);
					menuInit();
				}
			}
		});
	}

	public UserInfoDialog(MainFrame self, Font font, DataInputStream sock_in, DataOutputStream sock_out, String id,
			String gend) {
		this.setTitle("Bookle > 마이페이지");
		this.f = font;
		this.sock_in = sock_in;
		this.sock_out = sock_out;
		this.userid = id;
		this.usergend = gend;

		this.setSize(900, 800);
		this.setLocationRelativeTo(self);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.sockInit();
		this.compInit();
		this.eventInit();
		this.setResizable(false);
		this.setModal(true);

		this.setVisible(true);
	}
}