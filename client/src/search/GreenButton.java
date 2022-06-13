package search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class GreenButton extends JButton {
	private GreenButton self = this;
	private Dimension dimension = new Dimension(60, 40);

	private ImageIcon ImageIconNormal;
	private ImageIcon ImageIconFocused;
	private ImageIcon ImageIconPressed;
	
	
	public void init() {
		ImageIconNormal = Main.getScaledImageIcon("GreenButtonNormal.png");
		ImageIconFocused = Main.getScaledImageIcon("GreenButtonFocused.png");
		ImageIconPressed = Main.getScaledImageIcon("GreenButtonPressed.png");
		
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.CENTER);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		
		setMouseListener();
		setImage();
	}

	public void setPreferredSize(Dimension dimension) {
		this.dimension = dimension;
		setImage();
	}

	private void setImage() {
		Image image = ImageIconNormal.getImage();
		Image newImage = null;
		newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
		this.setIcon(new ImageIcon(newImage));
	}

	private void setMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Image image = ImageIconFocused.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Image image = ImageIconPressed.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Image image = ImageIconNormal.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Image image = ImageIconFocused.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
				self.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
	}
	
	public GreenButton() {
		super();
		init();
	}

	public GreenButton(String text) {
		super(text);
		init();
	}
}