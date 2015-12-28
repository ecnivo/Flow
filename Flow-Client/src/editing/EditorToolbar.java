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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import login.CreateAccountPane;

public class EditorToolbar extends JToolBar {
    private JPopupMenu popup;

    public EditorToolbar() {
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	setBorder(FlowClient.EMPTY_BORDER);

	popup = new JPopupMenu("Project Management");
	JMenuItem newProjectButton = new JMenuItem();
	newProjectButton.setText("New project");
	newProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String newProjectName = JOptionPane
			.showInputDialog(
				null,
				"Please enter a name for your new Project\nNo characters such as: \\ / ? % * : | "
					+ "\" < > . # & { } $ @ = ` + ",
				"New Project", JOptionPane.QUESTION_MESSAGE)
			.trim();
		while (CreateAccountPane.stringContains(newProjectName,
			CreateAccountPane.INVALID_CHARS)
			|| newProjectName.length() > 1) {
		    newProjectName = JOptionPane
			    .showInputDialog(
				    null,
				    "That name is invalid.\nPlease enter a name for your new Project\nNo characters such as: \\ / ? % * : | "
					    + "\" < > . # & { } $ @ = ` + ",
				    "Invalid name",
				    JOptionPane.QUESTION_MESSAGE).trim();
		}
		// TODO I dunno what happens when a new project is created
	    }
	});
	JMenuItem renameProjectButton = new JMenuItem();
	renameProjectButton.setText("Rename current project");
	renameProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String renameProjectName = JOptionPane
			.showInputDialog(
				null,
				"Please enter new name for the current Project\nNo characters such as: \\ / ? % * : | "
					+ "\" < > . # & { } $ @ = ` + ",
				"Rename project", JOptionPane.QUESTION_MESSAGE)
			.trim();
		while (CreateAccountPane.stringContains(renameProjectName,
			CreateAccountPane.INVALID_CHARS)
			|| renameProjectName.length() > 1) {
		    renameProjectName = JOptionPane
			    .showInputDialog(
				    null,
				    "That name is invalid.\nPlease enter an appropriate new name for your Project\nNo characters such as: \\ / ? % * : | "
					    + "\" < > . # & { } $ @ = ` + ",
				    "Invalid name",
				    JOptionPane.QUESTION_MESSAGE).trim();
		}

		// TODO rename project somehow?
	    }
	});

	JMenuItem deleteProjectButton = new JMenuItem();
	deleteProjectButton.setText("Delete current project");
	deleteProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String confirm = JOptionPane
			.showInputDialog(
				null,
				"Please type the project name that you are intending\n"
					+ "to delete EXACTLY AS IT IS in the following box.\n\n"
					+ "Deleting a project means you will lose ALL data and\n"
					+ "all collaborators will be removed. Back up code accordingly.",
				"Confirm project deletion",
				JOptionPane.WARNING_MESSAGE);
		//TODO double check if projects match
		// TODO get current project, and delete it.
	    }

	});

	add(new SearchButton());
	// add(new ProjectManageButton());
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

    // TODO replace this with a "project management" button that will: 1) create
    // new, 2) rename current, 3) delete current

    private class ProjectManageButton extends JButton {
	private ProjectManageButton() {
	    try {
		setIcon(new ImageIcon(
			ImageIO.read(new File("images/projectManage.png"))
				.getScaledInstance(FlowClient.BUTTON_ICON_SIZE,
					FlowClient.BUTTON_ICON_SIZE,
					Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    popup.show(EditorToolbar.this,
			    ProjectManageButton.this.getX(),
			    ProjectManageButton.this.getY());
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
