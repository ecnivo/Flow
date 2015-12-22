package editing;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class EditorToolbar extends JToolBar {
    public EditorToolbar() {
	setLayout(new FlowLayout());

	add(new SearchButton());
	add(new ShareButton());
	
	setFloatable(false);
    }

    private class SearchButton extends JButton {
	private SearchButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/search.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO pop open a search window and search for something
		    System.out.println("Search button pressed");
		}
	    });
	}
    }

    private class ShareButton extends JButton {
	private ShareButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/addCollab.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO pop open a sharing window to search for other users
		    System.out.println("Share button pressed");
		}
	    });
	}
    }
}
