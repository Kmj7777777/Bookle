package search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.mommoo.flat.layout.linear.LinearLayout;
import com.mommoo.flat.layout.linear.Orientation;
import com.mommoo.flat.layout.linear.constraints.LinearConstraints;
import com.mommoo.flat.layout.linear.constraints.LinearSpace;

public class SearchedItem extends JPanel {
	private BookDTO dto;
	// ------------------------------------------------------------------------------------------------------------------------------
	private JButton bookImage;
	private GreenButton reservationBtn = new GreenButton("예약");
	
	private JLabel title = new JLabel();
	private JLabel combineLabel = new JLabel(); // writer + publisher + category
	private JLabel currentQuantityLabel = new JLabel();
	private JTextArea bookIntroduce= new JTextArea();
	
	private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel combineLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel currentQuantityLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel bookIntroducePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel bookCoverPanel = new JPanel(); // titlePanel + combineLabelPanel + currentQuantityLabelPanel + bookIntroducePanel
	
	
	private void compInit() throws Exception{
		bookImage = new JButton(Main.getScaledImageIcon(Sock.imageDataList.get(dto.getId()), 130, 195));
		bookImage.setContentAreaFilled(false);
		
		Font customFont = Main.getFont("Typo_SsangmunDongB.ttf", 20f);
		title.setFont(customFont);
		title.setText(dto.getTitle());
		
		String conbineString = dto.getWriter() + "　|　" + dto.getPublisher() + "　|　" + dto.getCategory();
		combineLabel.setText(conbineString);
		
		currentQuantityLabel.setFont(new Font("함초롬돋움", Font.BOLD, 15));
		currentQuantityLabelPanel.setBorder(BorderFactory.createEtchedBorder());
		inventoryRevalidate();
		
		String[] bookInfo = Sock.bookInfoList.get(dto.getId());
		String bookIntroduceContents = new String(bookInfo[0]);
		String summary;
		if(bookIntroduceContents.length() > 180) {
			summary = bookIntroduceContents.substring(0, 180) + "...";
		}else {
			summary = bookIntroduceContents.substring(0, bookIntroduceContents.length()); 
		}
		bookIntroduce.setPreferredSize(new Dimension(400, 110));
		bookIntroduce.setBorder(BorderFactory.createEmptyBorder());
		bookIntroduce.setBackground(new Color(214, 217, 223));
		bookIntroduce.setEditable(false);
		bookIntroduce.setLineWrap(true);
		bookIntroduce.setText(summary);
		// --------------------------------------------------------------------------------------------------------------------
		titlePanel.add(title);
		bookIntroducePanel.add(bookIntroduce);
		combineLabelPanel.add(combineLabel);
		currentQuantityLabelPanel.add(currentQuantityLabel);
		
		bookCoverPanel.setLayout(new LinearLayout(Orientation.VERTICAL, 5));
		bookCoverPanel.add(titlePanel);
		bookCoverPanel.add(combineLabelPanel);
		bookCoverPanel.add(currentQuantityLabelPanel);
		bookCoverPanel.add(bookIntroducePanel);
		
		this.setLayout(new LinearLayout(Orientation.HORIZONTAL, 15));
		this.add(bookImage, new LinearConstraints().setWeight(1).setLinearSpace(LinearSpace.MATCH_PARENT));
		this.add(bookCoverPanel, new LinearConstraints().setWeight(3).setLinearSpace(LinearSpace.WRAP_CENTER_CONTENT));
		this.add(reservationBtn, new LinearConstraints().setWeight(0).setLinearSpace(LinearSpace.WRAP_CENTER_CONTENT));
	}

	private void eventInit(BookDTO dto) {
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		
		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				title.setForeground(Color.MAGENTA);
				title.setCursor(cursor);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				title.setForeground(null);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				new Detail(dto);
			}
		});
		
		bookImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				bookImage.setCursor(cursor);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				new Detail(dto);
			}
		});
		
		reservationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Reservation(dto);
			}
		});
	}
	
	public void inventoryRevalidate() {
		if(dto.getCurrent_quantity() == 0) {
			currentQuantityLabel.setForeground(Color.RED);
			currentQuantityLabel.setText("재고 없음");
		}else {
			currentQuantityLabel.setText("재고 : " + dto.getCurrent_quantity());
		}
	}
	
	public SearchedItem(BookDTO dto) {
		this.dto = dto;
		this.setPreferredSize(new Dimension(700, 260));

		try {
			compInit();
			eventInit(dto);
		}catch(Exception e) {
			e.printStackTrace();
		}

		this.setVisible(true);
	}
}