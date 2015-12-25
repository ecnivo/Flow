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
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import settings.SettingsPane;

public class EditorToolbar extends JToolBar {
    public EditorToolbar() {
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

	add(new SearchButton());
	add(new ShareButton());
	add(new ImportButton());
	add(new ExportButton());
	addSeparator();

	setFloatable(false);
	setRollover(false);
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
	    setFocusable(false);
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
	    setFocusable(false);
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

    private class ImportButton extends JButton {
	private ImportButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/import.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO pop open a window for the user to first select a
		    // file, then to choose the project to insert it in
		    System.out.println("Import button pressed");
		}
	    });
	}
    }

    private class ExportButton extends JButton {
	private ExportButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(
			new File("images/export.png")).getScaledInstance(
			FlowClient.BUTTON_ICON_SIZE,
			FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO pop open a window asking where the user would like
		    // the file exported, then export file to that location.
		    System.out.println("Export button pressed");
		}
	    });
	}
    }
}
