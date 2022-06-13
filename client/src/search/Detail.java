package search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mommoo.flat.layout.linear.LinearLayout;
import com.mommoo.flat.layout.linear.Orientation;
import com.mommoo.flat.layout.linear.constraints.LinearConstraints;
import com.mommoo.flat.layout.linear.constraints.LinearSpace;

public class Detail extends JDialog {
	private BookDTO dto;
	private String[] bookInfo;
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	private JLabel bookImage = new JLabel();
	private JLabel title = new JLabel();
	private JLabel writerLabel = new JLabel("저자");
	private JLabel writer = new JLabel();
	private JLabel publisherLabel = new JLabel("출판사");
	private JLabel publisher = new JLabel();
	private JLabel categoryLabel = new JLabel("장르");
	private JLabel category = new JLabel();
	private GreenButton reservationBtn = new GreenButton("예약");
	private JButton bookIntroduceBtn = new JButton("책 소개");
	private JButton writerIntroduceBtn = new JButton("저자 소개");
	private JButton tableOfContentsBtn = new JButton("목차");
	private JTextArea contents = new JTextArea();
	private JScrollPane scroll = new JScrollPane(contents);
	private JLabel current_quantityLabel = new JLabel("", JLabel.CENTER);
	
	private JPanel bookCoverPanel = new JPanel(new GridLayout(0, 2));
	private JPanel combinePanel1 = new JPanel(new GridLayout(0, 1, 0, 10)); // bookCoverPanel과 current_quantityLabel을 합쳐주는 Panel
	private JPanel combinePanel2 = new JPanel(new LinearLayout(Orientation.HORIZONTAL, 40)); // bookImage, combinePanel1, reservationBtn을 합쳐주는 Panel
	private JPanel combinePanel3 = new JPanel(new LinearLayout(Orientation.VERTICAL, 5)); // title과 combinePanel2를 합쳐주는 Panel
	private JPanel bookInfoBtnPanel = new JPanel(new GridLayout(1, 3)); // bookIntroduceBtn, writerIntroduceBtn, tableOfContentsBtn을 합쳐주는 Panel
	private JPanel bookInfoPanel = new JPanel(new BorderLayout()); // bookInfoBtnPanel과 scroll을 합쳐주는 Panel
	
	
	public void fillContents() {
		try{
			bookImage.setIcon(Main.getScaledImageIcon(Sock.imageDataList.get(dto.getId()), 100, 150));
			title.setText(dto.getTitle());
			writer.setText(dto.getWriter());
			publisher.setText(dto.getPublisher());
			category.setText(dto.getCategory());
			
			contents.setEditable(false);
			contents.setLineWrap(true);
			contents.setText(bookInfo[0]);
			contents.setCaretPosition(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void compInit() throws Exception {
		Font customFont = Main.getFont("야놀자체.ttf", 25);
		title.setFont(customFont);
		title.setForeground(new Color(250, 0, 130));
		
		customFont = Main.getFont("빛고을광주체.ttf", 15);
		writerLabel.setFont(customFont);
		bookCoverPanel.add(writerLabel);
		bookCoverPanel.add(writer);
		
		publisherLabel.setFont(customFont);
		bookCoverPanel.add(publisherLabel);
		bookCoverPanel.add(publisher);
		
		categoryLabel.setFont(customFont);
		bookCoverPanel.add(categoryLabel);
		bookCoverPanel.add(category);
		
		current_quantityLabel.setFont(new Font("함초롬돋움", Font.BOLD, 15));
		current_quantityLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		inventoryRevalidate();
		combinePanel1.add(bookCoverPanel);
		combinePanel1.add(current_quantityLabel);
		
		combinePanel2.setPreferredSize(new Dimension(410, 155));
		combinePanel2.add(bookImage, new LinearConstraints().setWeight(0).setLinearSpace(LinearSpace.MATCH_PARENT));
		combinePanel2.add(combinePanel1, new LinearConstraints().setWeight(1).setLinearSpace(LinearSpace.WRAP_CENTER_CONTENT));
		combinePanel2.add(reservationBtn, new LinearConstraints().setWeight(0).setLinearSpace(LinearSpace.WRAP_CENTER_CONTENT));
		
		combinePanel3.setPreferredSize(new Dimension(0, 210));
		combinePanel3.add(title);
		combinePanel3.add(combinePanel2);
		
		bookInfoBtnPanel.add(bookIntroduceBtn);
		bookInfoBtnPanel.add(writerIntroduceBtn);
		bookInfoBtnPanel.add(tableOfContentsBtn);
		
		bookInfoPanel.add(bookInfoBtnPanel, BorderLayout.NORTH);
		bookInfoPanel.add(scroll, BorderLayout.CENTER);
		
		this.add(combinePanel3, BorderLayout.NORTH);
		this.add(bookInfoPanel, BorderLayout.CENTER);
	}
	
	public void eventInit() {
		reservationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Reservation(dto, Detail.this);
			}
		});
		
		bookIntroduceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contents.setText(bookInfo[0]);
				contents.setCaretPosition(0);
			}
		});
		
		writerIntroduceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contents.setText(bookInfo[1]);
				contents.setCaretPosition(0);
			}
		});
		
		tableOfContentsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contents.setText(bookInfo[2]);
				contents.setCaretPosition(0);
			}
		});
	}

	public void inventoryRevalidate() {
		if(dto.getCurrent_quantity() == 0) {
			current_quantityLabel.setForeground(Color.RED);
			current_quantityLabel.setText("재고 없음");
		}else {
			current_quantityLabel.setText("재고 : " + dto.getCurrent_quantity());
		}
	}
	
	public Detail(BookDTO dto) {
		try {
			this.dto = dto;
			this.bookInfo = Sock.bookInfoList.get(dto.getId());
			
			this.setSize(415, 530);
			this.setResizable(false);
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(null);
	
			fillContents();
			compInit();
			eventInit();
			
			this.setVisible(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
}