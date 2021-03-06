package admin.adminSearch;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import admin.adminBook.BookInsert;
import admin.adminRental.RentalInsert;
import admin.adminReturn.ReturnInsert;

public class BookSearch extends JDialog {

	private JDialog parent = null;
	private BookSearch self = this;

	private String[] headers = new String[] { "id", "제목", "저자", "출판사", "카테고리" };
	private DefaultTableModel dtm = new DefaultTableModel(headers, 0);
	private JTable table = new JTable(dtm);
	private JScrollPane paneTable = new JScrollPane(table);

	private JTextField fieldSearch = new JTextField();
	private JButton buttonSearch = new JButton("검색");
	private JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.CENTER));

	private JButton buttonSelect = new JButton("선택");
	private JButton buttonExit = new JButton("닫기");
	private JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));

	private Socket sock = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;

	private String keyword;

	private void listUpdate() throws Exception {
		while (dtm.getRowCount() > 0) {
			dtm.removeRow(0);
		}

		dos.writeUTF("list_book");
		dos.writeUTF(keyword);
		if (parent instanceof RentalInsert) {
			dos.writeUTF("rental");
		} else if (parent instanceof BookInsert) {
			dos.writeUTF("all_admin");
		}
		dos.flush();
		
		int listSize = dis.readInt();
		for (int i = 0; i < listSize; i++) {
			String id = dis.readUTF();
			String title = dis.readUTF();
			String writer = dis.readUTF();
			String publisher = dis.readUTF();
			String category = dis.readUTF();
			if (parent instanceof BookInsert) {
				dis.readInt();
				dis.readInt();
			}
			dtm.addRow(new Object[] { id, title, writer, publisher, category });
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

		this.paneTable.setPreferredSize(new Dimension(600, 300));
		gbc.gridy = 0;
		this.add(paneTable, gbc);

		gbc.gridy = 1;
		this.fieldSearch.setPreferredSize(new Dimension(200, 27));
		this.fieldSearch.setText(keyword);
		this.panelSearch.add(fieldSearch);
		this.panelSearch.add(buttonSearch);
		this.add(panelSearch, gbc);

		gbc.insets = new Insets(30, 0, 0, 0);
		gbc.gridy = 2;
		this.panelButtons.add(buttonSelect);
		this.panelButtons.add(buttonExit);
		this.add(panelButtons, gbc);
	}

	private void eventInit() {
		this.buttonSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keyword = fieldSearch.getText().trim();
				try {
					listUpdate();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		this.buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String id = (String) dtm.getValueAt(table.getSelectedRow(), 0);
					if (parent instanceof RentalInsert) {
						((RentalInsert) parent).setFieldRentalBook(id);
					} else if (parent instanceof BookInsert) {
						((BookInsert) parent).setFieldExistingBook(id);
					}
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(self, "항목을 선택해주세요.");
				}
			}
		});

		this.buttonExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public BookSearch(JDialog parent, Socket sock, String keyword) {
		this.parent = parent;
		this.sock = sock;
		this.keyword = keyword;
		this.setTitle("도서 검색");
		this.setSize(800, 500);
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
