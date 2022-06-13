package search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class SearchButton extends JButton {
	private SearchButton self = this;
	private Dimension dimension = new Dimension(30, 28);

	private ImageIcon imageIconNormal;
	private ImageIcon imageIconFocused;
	private ImageIcon imageIconPressed;

	
	public void init() {
		imageIconNormal = Main.getScaledImageIcon("SearchButtonNormal.png");
		imageIconFocused = Main.getScaledImageIcon("SearchButtonFocused.png");
		imageIconPressed = Main.getScaledImageIcon("SearchButtonPressed.png");
		
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
		Image image = imageIconNormal.getImage();
		Image newImage = null;
		newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
		this.setIcon(new ImageIcon(newImage));
	}

	private void setMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Image image = imageIconFocused.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Image image = imageIconPressed.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Image image = imageIconNormal.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Image image = imageIconFocused.getImage();
				Image newImage = null;
				newImage = image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
				self.setIcon(new ImageIcon(newImage));
				self.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
	}
	
	public SearchButton() {
		super();
		init();
	}

	public SearchButton(String text) {
		super(text);
		init();
	}
}