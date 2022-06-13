package admin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import admin.adminBook.BookList;
import admin.adminMember.MemberList;
import admin.adminRental.RentalList;
import admin.adminReservation.ReservationList;
import admin.adminReturn.ReturnList;
import admin.custom.BlueButton;
import main.MainFrame;

public class Main extends JPanel {

	private Main self = this;
	private MainFrame main_addr;

	private JLabel labelLogo = new JLabel(search.Main.getScaledImageIcon("Bookle.png"));
	private JLabel labelTime = new JLabel();
	private JButton buttonLogout = new JButton("로그아웃");
	private JPanel panelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	private JPanel panelHeader = new JPanel(new BorderLayout());

	private JButton buttonRental = new BlueButton("대여 관리");
	private JButton buttonReturn = new BlueButton("반납 관리");
	private JButton buttonReservation = new BlueButton("예약 관리");
	private JButton buttonBook = new BlueButton("도서 관리");
	private JButton buttonMember = new BlueButton("회원 관리");
	private JPanel panelSideBar = new JPanel(new GridLayout(8, 1));
	
	

	private JPanel panelCurrent = null;

//	Dimension screenSize = null;

	private Socket sock = null;
	private Socket sockTime = null;
	private DataOutputStream dosTime = null;
	private DataInputStream disTime = null;
	
	private void socketInit() throws Exception {
		sock = new Socket(main.Main.serverIP, main.Main.serverHost);
		sockTime = new Socket(main.Main.serverIP, main.Main.serverHost);

		dosTime = new DataOutputStream(sockTime.getOutputStream());
		disTime = new DataInputStream(sockTime.getInputStream());
	}

	private void setToday() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						dosTime.writeUTF("get_date");
						dosTime.flush();
						long time = disTime.readLong();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						String today = sdf.format(new Date(time));
						labelTime.setText(today + "   ");
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void compInit() {
		this.setLayout(new BorderLayout());
		
		setToday();
		this.panelHeader.add(labelLogo, BorderLayout.WEST);
		this.panelHeader.add(labelTime, BorderLayout.EAST);
		this.panelHeader.setBackground(Color.WHITE);
		this.add(panelHeader, BorderLayout.NORTH);
		
		this.buttonRental.setFont(buttonRental.getFont().deriveFont(25f));
		this.buttonReturn.setFont(buttonReturn.getFont().deriveFont(25f));
		this.buttonReservation.setFont(buttonReservation.getFont().deriveFont(25f));
		this.buttonBook.setFont(buttonBook.getFont().deriveFont(25f));
		this.buttonMember.setFont(buttonMember.getFont().deriveFont(25f));

		this.panelSideBar.setPreferredSize(new Dimension(200, 0));
		this.panelSideBar.setBackground(Color.LIGHT_GRAY);
		this.panelSideBar.add(buttonRental);
		this.panelSideBar.add(buttonReturn);
		this.panelSideBar.add(buttonReservation);
		this.panelSideBar.add(buttonBook);
		this.panelSideBar.add(buttonMember);
		this.add(panelSideBar, BorderLayout.WEST);
		this.panelLogout.add(buttonLogout);
		this.add(panelLogout, BorderLayout.SOUTH);
		this.panelCurrent = new RentalList(sock);
		this.add(panelCurrent);
	}

	private void eventInit() {
		this.buttonLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int a = JOptionPane.showConfirmDialog(self, "관리자 계정에서 로그아웃 하시겠습니까?", "로그아웃", JOptionPane.WARNING_MESSAGE);
				if(a==0) {
					main_addr.login_state = false;
					main_addr.frameChange = 1;
					main_addr.arrowPanel.removeAll();
					main_addr.counter = 0;
					main_addr.getContentPane().removeAll();
					main_addr.compInit(false);
					main_addr.revalidate();
					main_addr.repaint();
				}
			}
		});
		this.buttonRental.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.remove(panelCurrent);
				panelCurrent = new RentalList(sock);
				self.add(panelCurrent);
				self.revalidate();
			}
		});

		this.buttonReturn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.remove(panelCurrent);
				panelCurrent = new ReturnList(sock);
				self.add(panelCurrent);
				self.revalidate();
				self.repaint();
			}
		});

		this.buttonReservation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.remove(panelCurrent);
				panelCurrent = new ReservationList(sock);
				self.add(panelCurrent);
				self.revalidate();
				self.repaint();
			}
		});

		this.buttonBook.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.remove(panelCurrent);
				panelCurrent = new BookList(sock);
				self.add(panelCurrent);
				self.revalidate();
				self.repaint();
			}
		});

		this.buttonMember.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.remove(panelCurrent);
				panelCurrent = new MemberList(sock);
				self.add(panelCurrent);
				self.revalidate();
				self.repaint();
			}
		});
	}

	public Main(MainFrame main_addr) {

//		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		this.setSize(screenSize.width - 10, screenSize.height - 50);
		this.main_addr = main_addr;

		try {
			this.socketInit();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		this.compInit();
		this.eventInit();
	}
}
