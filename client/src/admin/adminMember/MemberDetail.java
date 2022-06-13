package admin.adminMember;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import admin.custom.TableColumnResize;

public class MemberDetail extends JDialog {

	private JLabel labelTitle = new JLabel();
	private JLabel labelQuantity = new JLabel();
	private JPanel panelTitle = new JPanel(new BorderLayout());

	private JLabel labelRental = new JLabel("< 대여 현황 >");
	private JPanel panelRental = new JPanel(new FlowLayout());
	private String[] headersRental = new String[] { "대여 일자", "도서 ID", "제목", "저자", "출판사" };
	private DefaultTableModel dtmRental = new DefaultTableModel(headersRental, 0);
	private JTable tableRental = new JTable(dtmRental);
	private JScrollPane paneTableRental = new JScrollPane(tableRental);

	private JLabel labelReservation = new JLabel("< 예약 현황 >");
	private JPanel panelReservation = new JPanel(new FlowLayout());
	private String[] headersReservation = new String[] { "예약 일자", "도서 ID", "제목", "저자", "출판사" };
	private DefaultTableModel dtmReservation = new DefaultTableModel(headersReservation, 0);
	private JTable tableReservation = new JTable(dtmReservation);
	private JScrollPane paneTableReservation = new JScrollPane(tableReservation);
	
	private JLabel labelStandby = new JLabel("< 예약 대기 현황 >");
	private JPanel panelStandby = new JPanel(new FlowLayout());
	private String[] headersStandby = new String[] { "대기 번호", "도서 ID", "제목", "저자", "출판사", };
	private DefaultTableModel dtmStandby = new DefaultTableModel(headersStandby, 0);
	private JTable tableStandby = new JTable(dtmStandby);
	private JScrollPane paneTableStandby = new JScrollPane(tableStandby);

	private JButton buttonExit = new JButton("닫기");

	private String id = null;
	private String name = null;
	private String quantity = null;

	private Socket sock = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;

	public void listUpdate() throws Exception {
		while (dtmRental.getRowCount() > 0) {
			dtmRental.removeRow(0);
		}
		while (dtmReservation.getRowCount() > 0) {
			dtmReservation.removeRow(0);
		}
		while (dtmStandby.getRowCount() > 0) {
			dtmStandby.removeRow(0);
		}

		dos.writeUTF("list_member_detail");
		dos.writeUTF(id);
		dos.writeUTF("all");
		dos.flush();

		int listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String rental_date = dis.readUTF();
			String return_date = dis.readUTF();
			String book_id = dis.readUTF();
			String book_title = dis.readUTF();
			String book_writer = dis.readUTF();
			String book_publisher = dis.readUTF();
			if (return_date.equals("대여 중")) {
				dtmRental.addRow(new Object[] { rental_date, book_id, book_title, book_writer, book_publisher });
			}
		}

		listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String reserved_date = dis.readUTF();
			String book_id = dis.readUTF();
			String book_title = dis.readUTF();
			String book_writer = dis.readUTF();
			String book_publisher = dis.readUTF();
			dtmReservation.addRow(new Object[] { reserved_date, book_id, book_title, book_writer, book_publisher });
		}
		
		dos.writeUTF("list_standby_m");
		dos.writeUTF(id);
		dos.flush();

		listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String book_id = dis.readUTF();
			String book_title = dis.readUTF();
			String book_writer = dis.readUTF();
			String book_publisher = dis.readUTF();
			String order_number = dis.readUTF();
			dtmStandby.addRow(new Object[] { order_number, book_id, book_title, book_writer, book_publisher });
		}
	}

	private void socketInit() throws Exception {
		dos = new DataOutputStream(sock.getOutputStream());
		dis = new DataInputStream(sock.getInputStream());

		listUpdate();
	}

	private void compInit() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridy = 0;
		this.labelTitle.setFont(labelTitle.getFont().deriveFont(20f));
		this.labelTitle.setText(name + "(" + id + ")");
		this.labelQuantity.setFont(labelTitle.getFont().deriveFont(20f));
		this.labelQuantity.setText(quantity);
		this.panelTitle.add(labelTitle);
		this.panelTitle.add(labelQuantity, BorderLayout.EAST);
		this.panelTitle.setPreferredSize(new Dimension(700, 50));
		this.add(panelTitle, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridy = 1;
		this.panelRental.setFont(panelRental.getFont().deriveFont(15f));
		this.panelRental.add(labelRental);
		this.panelRental.setPreferredSize(new Dimension(700, 27));
		this.add(panelRental, gbc);

		gbc.gridy = 2;
		new TableColumnResize(tableRental).resizeColumnWidth();
		this.paneTableRental.setPreferredSize(new Dimension(700, 150));
		this.add(paneTableRental, gbc);

		gbc.gridy = 3;
		this.panelReservation.setFont(panelReservation.getFont().deriveFont(15f));
		this.panelReservation.add(labelReservation);
		this.panelReservation.setPreferredSize(new Dimension(700, 27));
		this.add(panelReservation, gbc);

		gbc.gridy = 4;
		new TableColumnResize(tableReservation).resizeColumnWidth();
		this.paneTableReservation.setPreferredSize(new Dimension(700, 150));
		this.add(paneTableReservation, gbc);
		
		gbc.gridy = 5;
		this.panelStandby.setFont(panelStandby.getFont().deriveFont(15f));
		this.panelStandby.add(labelStandby);
		this.panelStandby.setPreferredSize(new Dimension(700, 27));
		this.add(panelStandby, gbc);
		
		gbc.gridy = 6;
		new TableColumnResize(tableStandby).resizeColumnWidth();
		this.paneTableStandby.setPreferredSize(new Dimension(700, 150));
		this.add(paneTableStandby, gbc);

		gbc.gridy = 7;
		this.add(buttonExit, gbc);
	}

	private void eventInit() {
		this.buttonExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public MemberDetail(JPanel parent, Socket sock, String id, String name, String quantity) {
		this.sock = sock;
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.setTitle("회원별 대여/예약 정보");
		this.setSize(800, 700);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);

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
