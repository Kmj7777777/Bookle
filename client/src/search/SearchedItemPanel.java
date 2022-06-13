package search;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mommoo.flat.layout.linear.LinearLayout;
import com.mommoo.flat.layout.linear.Orientation;
import com.mommoo.flat.layout.linear.constraints.LinearConstraints;
import com.mommoo.flat.layout.linear.constraints.LinearSpace;

public class SearchedItemPanel extends JPanel {
	private ArrayList<BookDTO> searchedContents;
	public static HashMap<String, SearchedItem> searchedItemList = new HashMap<>();
	
	private JPanel bodyPanel = new JPanel();
	
	
	private void compInit() {
		bodyPanel.setLayout(new LinearLayout(Orientation.VERTICAL, 35));
		
		for(int i = 0; i < searchedContents.size(); i++) {
			BookDTO dto = searchedContents.get(i);
			SearchedItem searchedItem = new SearchedItem(dto);
			searchedItemList.put(dto.getId(), searchedItem);
			
			bodyPanel.add(searchedItem, new LinearConstraints().setLinearSpace(LinearSpace.WRAP_CONTENT));
			if(i < (searchedContents.size() - 1)) {
				bodyPanel.add(new JLabel(Main.getScaledImageIcon("division line.png", 700, 35)));
			}
		}
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(bodyPanel);
	}

	public SearchedItemPanel(ArrayList<BookDTO> searchedContents) {
		this.searchedContents = searchedContents;

		compInit();

		this.setVisible(true);
	}
}