package search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import main.MainFrame;

public class Main extends JPanel{
	public static MainFrame mf_addr;
	private DataInputStream sock_in;
	private DataOutputStream sock_out;
	public static String keyword;
	private boolean loginState;
	public static Sock sock;
	public static Revalidate revalidate;
	// -----------------------------------------------------------------------------------------------------------------------------------------------
	private JLabel logo = new JLabel(getScaledImageIcon("logo.png", 280, 70));
	private JTextField searchText = new JTextField(30);
	private SearchButton searchBtn = new SearchButton();
	public static JLabel serverDateLabel = new JLabel();
	private JPanel combinePanel1 = new JPanel(new GridBagLayout()); // logo, searchText, searchBtn을 합쳐주는 Panel
	private JPanel combine1ParentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel combinePanel2 = new JPanel(new BorderLayout()); // serverDateLabel과 mf_addr.panel_logout 또는 mf_addr.panel_login을 합쳐주는 Panel
	private JPanel northPanel = new JPanel(new BorderLayout());
	// -----------------------------------------------------------------------------------------------------------------------------------------------
	private JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	// -----------------------------------------------------------------------------------------------------------------------------------------------
	private ArrayList<JButton> pageBtnList = new ArrayList<>();
	private JButton beforeBtn = new JButton("이전");
	private JButton nextBtn = new JButton("다음");
	private JPanel southPanel = new JPanel(new FlowLayout());
	// -----------------------------------------------------------------------------------------------------------------------------------------------
	
	// ----------------------------- ◆ mf_addr.panel_login 관련 필드 ◆ -----------------------------
	private JLabel memberInfoBtn = new JLabel(" 회원정보 ");
	private JLabel logoutBtn = new JLabel(" 로그아웃 ");
	private JPanel memberInfoBtnPanel = new JPanel();
	private JPanel logoutBtnPanel = new JPanel();
	
	private TitledBorder tb = new TitledBorder(new LineBorder(new Color(103, 153, 255), 2, true));
	// --------------------------------------------------------------------------------------------
	
	
	public void loginInit(boolean isLogin) {
		memberInfoBtnPanel.setBackground(new Color(178, 204, 255));
		memberInfoBtnPanel.setBorder(tb);
		memberInfoBtnPanel.add(memberInfoBtn);
		logoutBtnPanel.setBackground(new Color(178, 204, 255));
		logoutBtnPanel.setBorder(tb);
		logoutBtnPanel.add(logoutBtn);
		
		combinePanel2.removeAll();
		combinePanel2.add(serverDateLabel, BorderLayout.NORTH);
		
		if(!isLogin) { // 로그아웃 상태
			combinePanel2.add(mf_addr.panel_logout, BorderLayout.SOUTH);
			combinePanel2.revalidate();
			combinePanel2.repaint();
		}else if(isLogin) { // 로그인 상태
			mf_addr.counter = 0;
			mf_addr.panel_login.setBackground(null);
			mf_addr.panel_login.removeMouseListener(mf_addr.ml); // 로그인 Panel 클릭 시 배경색을 변경해주는 listener 제거
			
			mf_addr.panel_login.add(memberInfoBtnPanel);
			mf_addr.panel_login.add(logoutBtnPanel);
			
			combinePanel2.add(mf_addr.panel_login, BorderLayout.SOUTH);
			combinePanel2.revalidate();
			combinePanel2.repaint();
			
			for(MouseListener ml : memberInfoBtnPanel.getMouseListeners()) {
				memberInfoBtnPanel.removeMouseListener(ml);
			}
			for(MouseListener ml : logoutBtnPanel.getMouseListeners()) {
				logoutBtnPanel.removeMouseListener(ml);
			}
			memberInfoBtnPanel.addMouseListener(mf_addr.ml2);
			logoutBtnPanel.addMouseListener(mf_addr.ml3);
		}
	}
	
	private void compInit() throws Exception{
		this.setLayout(new BorderLayout());
		
		mf_addr.frameChange = 2;
		
		searchText.setText(keyword);
		
		for(int i = 0; i < 5; i++) {
			pageBtnList.add(new JButton());
		}

		new GetServerTime().start();
		sock = new Sock(sock_in, sock_out);
		revalidate = new Revalidate(scroll, pageBtnList, beforeBtn, nextBtn, southPanel);
		
		// ----------------------------------------------------------------------------
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.gridx = 0; gbc.gridy = 0; combinePanel1.add(logo, gbc);
		gbc.gridx = 0; gbc.gridy = 1; combinePanel1.add(searchText, gbc);
		gbc.gridx = 1; gbc.gridy = 1; combinePanel1.add(searchBtn, gbc);
		combine1ParentPanel.add(combinePanel1);
		
		serverDateLabel.setHorizontalAlignment(JLabel.RIGHT);
		northPanel.add(combine1ParentPanel, BorderLayout.WEST);
		northPanel.add(combinePanel2, BorderLayout.EAST);
		this.add(northPanel, BorderLayout.NORTH);
		// ----------------------------------------------------------------------------
		revalidate.searchedContentsRevalidate(1);
		scroll.getVerticalScrollBar().setUnitIncrement(20); // Scroll 속도 조절
		this.add(scroll, BorderLayout.CENTER);
		// ----------------------------------------------------------------------------
		revalidate.buttonRevalidate();
		this.add(southPanel, BorderLayout.SOUTH);
		// ----------------------------------------------------------------------------
	}
	
	private void eventInit() {
		logo.addMouseListener(new MouseAdapter() { // 로고 클릭 시 메인 페이지로 이동
			@Override
			public void mouseClicked(MouseEvent e) {
				mf_addr.frameChange = 1;
				mf_addr.counter = 0;
				mf_addr.panel_login.addMouseListener(mf_addr.ml);
				
				mf_addr.getContentPane().removeAll();
				mf_addr.panel_login.removeAll();
				mf_addr.arrowPanel.removeAll();
				
				mf_addr.compInit(mf_addr.login_state);
				mf_addr.revalidate();
				mf_addr.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				logo.setCursor(cursor);
			}
		});
		
		searchText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchBtn.doClick();
			}
		});
		
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keyword = searchText.getText();
				revalidate.searchedContentsRevalidate(1);
				revalidate.buttonRevalidate();
			}
		});
		
		beforeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				revalidate.searchedContentsRevalidate(Revalidate.paging.getStartPage() - 5);
				revalidate.buttonRevalidate();
			}
		});
		
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				revalidate.searchedContentsRevalidate(Revalidate.paging.getStartPage() + 5);
				revalidate.buttonRevalidate();
			}
		});
	}
	
	public static ImageIcon getScaledImageIcon(byte[] imageData, int width, int height) {		
		ImageIcon imageIcon = new ImageIcon(imageData);
		Image image = imageIcon.getImage(); 
		Image newImage = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); 
		imageIcon = new ImageIcon(newImage);
		
		return imageIcon;
	}
	
	public static ImageIcon getScaledImageIcon(String fileName, int width, int height) {
		URL imageURL = Main.class.getResource("/images/" + fileName);
		ImageIcon imageIcon = new ImageIcon(imageURL);
		Image image = imageIcon.getImage(); 
		Image newImage = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); 
		imageIcon = new ImageIcon(newImage);
		
		return imageIcon;
	}
	
	public static ImageIcon getScaledImageIcon(String fileName) {
		URL imageURL = Main.class.getResource("/images/" + fileName);
		ImageIcon imageIcon = new ImageIcon(imageURL); 
		
		return imageIcon;
	}
	
	public static Font getFont(String fontName, float fontSize) {
		Font font = null;
		
		try {
			InputStream is = Main.class.getResourceAsStream("/fonts/" + fontName);
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return font;
	}
	
	public Main(MainFrame mf_addr, DataInputStream sock_in, DataOutputStream sock_out, String keyword, boolean loginState) {
		try {
			this.mf_addr = mf_addr;
			this.sock_in = sock_in;
			this.sock_out = sock_out;
			this.keyword = keyword;
			this.loginState = loginState;
			
			loginInit(loginState);
			compInit();
			eventInit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}