package admin.adminBook;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import admin.custom.RoundButton;
import admin.custom.TableColumnResize;

public class BookList extends JPanel {

	private BookList self = this;

	private JLabel labelTitle = new JLabel("도서 관리");

	private JButton buttonDetail = new RoundButton("대여/예약 정보");
	private JButton buttonInsert = new RoundButton("도서 입력");
	private JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

	private String[] headers = new String[] { "ID", "제목", "저자", "출판사", "카테고리", "재고 수량" };
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

		dos.writeUTF("list_book");
		dos.writeUTF(fieldSearch.getText().trim());
		dos.writeUTF("all_admin");
		dos.flush();
		
		int listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String id = dis.readUTF();
			String title = dis.readUTF();
			String writer = dis.readUTF();
			String publisher = dis.readUTF();
			String category = dis.readUTF();
			int current_quantity = dis.readInt();
			int total_quantity = dis.readInt();
			dtm.addRow(new Object[] { id, title, writer, publisher, category,
					current_quantity + "  /  " + total_quantity });
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
		this.labelTitle.setFont(labelTitle.getFont().deriveFont(30f));
		this.add(labelTitle, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridy = 1;
		this.panelButtons.setPreferredSize(new Dimension(900, 40));
		this.panelButtons.add(buttonDetail);
		this.panelButtons.add(buttonInsert);
		this.add(panelButtons, gbc);

		gbc.gridy = 2;
		new TableColumnResize(table).resizeColumnWidth();
		this.paneTable.setPreferredSize(new Dimension(900, 500));
		this.add(paneTable, gbc);

		gbc.gridy = 3;
		this.panelSearch.setPreferredSize(new Dimension(900, 40));
		this.fieldSearch.setPreferredSize(new Dimension(200, 27));
		this.panelSearch.add(fieldSearch);
		this.panelSearch.add(buttonSearch);
		this.add(panelSearch, gbc);
	}

	private void eventInit() {
		this.buttonDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String id = (String) dtm.getValueAt(table.getSelectedRow(), 0);
					String title = (String) dtm.getValueAt(table.getSelectedRow(), 1);
					String quantity = (String) dtm.getValueAt(table.getSelectedRow(), 5);
					BookDetail detail = new BookDetail(self, sock, id, title, quantity);
					detail.setVisible(true);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(self, "항목을 선택해주세요.");
				}
			}
		});

		this.buttonInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BookInsert insert = new BookInsert(self, sock);
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

	public BookList(Socket sock) {
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
