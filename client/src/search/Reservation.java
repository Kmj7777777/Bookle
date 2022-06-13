package search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.LoginDialog;
import main.MainFrame;

public class Reservation extends JDialog {
	private BookDTO dto;
	private Detail detail;
	// -------------------------------------------------------------------------------------------------------------------
	private JLabel bookIdLabel = new JLabel("고유번호 ");
	private JLabel bookId = new JLabel();
	private JLabel titleLabel = new JLabel("제목 ");
	private JLabel title = new JLabel();
	private JLabel reservationLabel = new JLabel("예약일 ");
	private JTextField reservation = new JTextField(10);
	private JButton calendarBtn = new JButton(Main.getScaledImageIcon("calendar.png", 24, 24));
	private JButton requestBtn = new JButton("신청");
	
	private JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel reservationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel combinePanel = new JPanel(new GridLayout(0, 1)); // idPanel, titlePanel, reservationPanel을 합쳐주는 Panel
	private JPanel requestPanel = new JPanel(new FlowLayout());
	// -------------------------------------------------------------------------------------------------------------------
	
	// ----------------- ◆ 예약 대기 관련 필드 ◆ -----------------
	private int orderNum; // 대기번호
	
	private JLabel noInventoryLabel1 = new JLabel("재고 없음");
	private JLabel noInventoryLabel2 = new JLabel();
	// ---------------------------------------------------------

	
	public void compInit() throws Exception {
		orderNum = Main.sock.getOrderNum(dto.getId());
		
		bookId.setText(dto.getId());
		title.setText(dto.getTitle());

		bookIdLabel.setFont(new Font("Serif", Font.BOLD, 15));
		titleLabel.setFont(new Font("Serif", Font.BOLD, 15));
		reservationLabel.setFont(new Font("Serif", Font.BOLD, 15));
		reservation.setEditable(false);
		reservation.setHorizontalAlignment(JTextField.CENTER);
		calendarBtn.setPreferredSize(new Dimension(34, 34));
		// --------------------------------------------------------------
		idPanel.add(bookIdLabel);
		idPanel.add(bookId);
		titlePanel.add(titleLabel);
		titlePanel.add(title);

		combinePanel.add(idPanel);
		combinePanel.add(titlePanel);
		if(dto.getCurrent_quantity() == 0) {
			noInventoryLabel1.setFont(new Font("Serif", Font.BOLD, 15));
			noInventoryLabel1.setForeground(Color.RED);
			noInventoryLabel2.setFont(new Font("Serif", Font.PLAIN, 15));
			noInventoryLabel2.setText("  |  대기자 : " + orderNum + "명");
			
			reservationPanel.add(noInventoryLabel1);
			reservationPanel.add(noInventoryLabel2);
		}else {
			reservationPanel.add(reservationLabel);
			reservationPanel.add(reservation);
			reservationPanel.add(calendarBtn);
		}
		combinePanel.add(reservationPanel);
		requestPanel.add(requestBtn);

		this.setLayout(new BorderLayout());
		this.add(combinePanel, BorderLayout.CENTER);
		this.add(requestPanel, BorderLayout.SOUTH);
	}

	public void eventInit() {
		requestBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(!MainFrame.login_state) {
						JOptionPane.showMessageDialog(Reservation.this, "먼저 로그인을 해주세요.");
						dispose();
						new LoginDialog(search.Main.mf_addr, main.Main.sock_in, main.Main.sock_out, main.Main.customFont);
					}else {
						if(dto.getCurrent_quantity() == 0) {
							int[] result = Main.sock.reservation(dto.getId());
							if(result[0] == 0) { // 예약 대기 성공
								JOptionPane.showMessageDialog(Reservation.this, "회원님의 대기번호는 " + result[1] + "번입니다.", "", JOptionPane.INFORMATION_MESSAGE);
								noInventoryLabel2.setText("  |  대기자 : " + result[1] + "명");
							}else if(result[0] == 3){
								JOptionPane.showMessageDialog(Reservation.this, "대기번호가 가득찼습니다.\n다음에 다시 이용해주세요.", "", JOptionPane.ERROR_MESSAGE);
							}else if(result[0] == 4) {
								JOptionPane.showMessageDialog(Reservation.this, "대여 가능 횟수를 모두 사용하셨습니다.", "", JOptionPane.ERROR_MESSAGE);
							}
						}else {
							String reservationDate = reservation.getText();

							if(reservationDate.isEmpty()) {
								JOptionPane.showMessageDialog(Reservation.this, "예약일을 선택해주세요.", "", JOptionPane.ERROR_MESSAGE);
							}else {
								int result = Main.sock.reservation(reservationDate, dto.getId());
								if (result == 0) {
									JOptionPane.showMessageDialog(Reservation.this, "예약 성공!", "", JOptionPane.INFORMATION_MESSAGE);
									Main.sock.revalidateCurrentQuantity(dto.getId());
									if(detail != null) { // 상세보기 중일 때는 뒤에 메인화면도 보이므로 둘 다 갱신
										detail.inventoryRevalidate();
										SearchedItemPanel.searchedItemList.get(dto.getId()).inventoryRevalidate();
									}else {
										SearchedItemPanel.searchedItemList.get(dto.getId()).inventoryRevalidate();
									}
								}else {
									JOptionPane.showMessageDialog(Reservation.this, "예약 실패...", "", JOptionPane.ERROR_MESSAGE);
								}
								dispose();
							}
						}
					}
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		});

		reservation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new ReservationCalendar(reservation);
			}
		});

		this.calendarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ReservationCalendar(reservation);
			}
		});
	}

	public Reservation(BookDTO dto, Detail detail) {
		try {
			this.dto = dto;
			this.detail = detail;

			this.setSize(280, 230);
			this.setResizable(false);
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(null);
			
			this.compInit();
			this.eventInit();

			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Reservation(BookDTO dto) {
		this(dto, null);
	}
}