package main;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main {
	public static Font customFont;
	public static DataInputStream sock_in = null;
	public static DataOutputStream sock_out = null;
	public final static String serverIP = "localhost";
	public final static int serverHost = 30731;
	public static Socket sock;
	
	public static void main(String[] args) {
		try {
			InputStream is = main.Main.class.getResourceAsStream("/fonts/NanumBarunGothic.ttf");
			customFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {}

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}catch (Exception e) {
		}
		
		try {
			sock = new Socket(serverIP, serverHost);
			sock_in = new DataInputStream(sock.getInputStream());
			sock_out = new DataOutputStream(sock.getOutputStream());
			new MainFrame(customFont);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다.");
			System.exit(0);
		}
	}
}