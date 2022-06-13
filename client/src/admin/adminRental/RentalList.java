package admin.adminRental;

import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import admin.custom.RoundButton;
import admin.custom.TableColumnResize;

public class RentalList extends JPanel {

	private RentalList self = this;

	private JLabel labelTitle = new JLabel("도서 대여 현황");

	private JButton buttonInsert = new RoundButton("도서 대여 입력");
	private JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

	private String[] headers = new String[] { "대여 일자", "대여 도서", "대여 회원" };
	private DefaultTableModel dtm = new DefaultTableModel(headers, 0);
	private JTable table = new JTable(dtm);
	private JScrollPane paneTable = new JScrollPane(table);

	private JTextField fieldSearch = new JTextField();
	private JButton buttonSearch = new RoundButton("검색");
	private JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.CENTER));

	private Socket sock = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;

	public void listUpdate() throws Exception {
		while (dtm.getRowCount() > 0) {
			dtm.removeRow(0);
		}

		dos.writeUTF("list_rental");
		dos.writeUTF(fieldSearch.getText().trim());
		dos.flush();
		
		int listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String rental_date = dis.readUTF();
			String book_id = dis.readUTF();
			String book_title = dis.readUTF();
			String member_id = dis.readUTF();
			String member_name = dis.readUTF();
			dtm.addRow(
					new Object[] { rental_date, book_title + " (" + book_id + ")", member_name + " (" + member_id + ")" });
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

		gbc.insets = new Insets(0, 0, 20, 0);
		gbc.gridy = 0;
		this.labelTitle.setFont(labelTitle.getFont().deriveFont(30f));
		this.add(labelTitle, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridy = 2;
		this.panelButtons.setPreferredSize(new Dimension(600, 40));
		this.panelButtons.add(buttonInsert);
		this.add(panelButtons, gbc);

		gbc.gridy = 3;
		new TableColumnResize(table).resizeColumnWidth();
		this.paneTable.setPreferredSize(new Dimension(600, 500));
		this.add(paneTable, gbc);

		gbc.gridy = 4;
		this.panelSearch.setPreferredSize(new Dimension(600, 40));
		this.fieldSearch.setPreferredSize(new Dimension(200, 27));
		this.panelSearch.add(fieldSearch);
		this.panelSearch.add(buttonSearch);
		this.add(panelSearch, gbc);
	}

	private void eventInit() {
		this.buttonInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RentalInsert insert = new RentalInsert(self, sock);
				insert.setVisible(true);
			}
		});

		this.buttonSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					listUpdate();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public RentalList(Socket sock) {
		this.sock = sock;
		this.setLayout(new GridBagLayout());

		try {
			this.socketInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		compInit();
		eventInit();

		this.setVisible(true);
	}
}
