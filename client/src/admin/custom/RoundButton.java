package admin.custom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class RoundButton extends JButton {

	private RoundButton self = this;
	private Dimension dimension = new Dimension(self.getPreferredSize().width, self.getPreferredSize().height);

	private ImageIcon imageIconNormal = search.Main.getScaledImageIcon("RoundButtonNormal.png");
	private ImageIcon imageIconFocused = search.Main.getScaledImageIcon("RoundButtonFocused.png");
	private ImageIcon imageIconPressed = search.Main.getScaledImageIcon("RoundButtonPressed.png");
	
	public void init() {
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.CENTER);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		setMouseListener();
		setImage();
	}

	public RoundButton() {
		super();
		init();
	}

	public RoundButton(String text) {
		super(text);
		init();
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
		this.addMouseListener(new MouseListener() {

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

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
	}
}
