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
import message.Data;
import shared.Communicator;

public class EditorToolbar extends JToolBar {
    private JPopupMenu popup;
    private JMenuItem createProjectButton;
    private JMenuItem renameProjectButton;

    public EditorToolbar(EditPane pane) {
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	setBorder(FlowClient.EMPTY_BORDER);

	popup = new JPopupMenu("Project Management");
	createProjectButton = new JMenuItem();
	createProjectButton.setText("New project");
	createProjectButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		String projectName = JOptionPane.showInputDialog(null, "Please enter a name for your new Project\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "New Project", JOptionPane.QUESTION_MESSAGE).trim();
		while (CreateAccountPane.stringContains(projectName, CreateAccountPane.INVALID_CHARS) || projectName.length() < 1) {
		    projectName = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter a name for your new Project\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.QUESTION_MESSAGE).trim();
		}

		Data createProjectRequest = new Data("new_project");
		createProjectRequest.put("project_name", projectName);
		createProjectRequest.put("session_id", Communicator.getSessionID());
		switch (Communicator.communicate(createProjectRequest).get("status", String.class)) {
		case "OK":
		    JOptionPane.showConfirmDialog(null, "Your project " + projectName + " has been succesfully created.", "Project creation success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		    break;

		case "PROJECT_NAME_INVALID":
		    JOptionPane.showConfirmDialog(null, "Your project name is invalid.\nPlease choose another one.", "Project creation failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    break;
		}
		pane.getDocTree().refreshProjectList();
	    }
	});
	renameProjectButton = new JMenuItem();
	renameProjectButton.setText("Rename current project");
	renameProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String projectName = JOptionPane.showInputDialog(null, "Please enter new name for the project " + pane.getDocTree().getActiveProject().toString() + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Rename project", JOptionPane.QUESTION_MESSAGE).trim();
		while (CreateAccountPane.stringContains(projectName, CreateAccountPane.INVALID_CHARS) || projectName.length() < 1) {
		    projectName = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this project." + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.QUESTION_MESSAGE).trim();
		}

		Data modifyRequest = new Data("project_modify");
		modifyRequest.put("project_modify_type", "RENAME_PROJECT");
		modifyRequest.put("project_uuid", pane.getDocTree().getActiveProject().getProjectUUID());
		modifyRequest.put("session_id", Communicator.getSessionID());
		modifyRequest.put("new_name", projectName);
		switch (Communicator.communicate(modifyRequest).get("status", String.class)) {
		case "OK":
		    JOptionPane.showConfirmDialog(null, "Your project has been succesfully renamed to " + projectName + ".", "Project renaming success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		    break;
		case "PROJECT_NAME_INVALID":
		    JOptionPane.showConfirmDialog(null, "Your project name is invalid.\nPlease choose another one.", "Project renaming failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    break;
		case "PROJECT_DOES_NOT_EXIST":
		    JOptionPane.showConfirmDialog(null, "The project you are trying to rename does not exist.\n" + "Try refreshing the list of projects by moving your mouse cursor into,\n" + "then out of the project list.", "Project renaming failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    break;
		}
		pane.getDocTree().refreshProjectList();
	    }
	});

	JMenuItem deleteProjectButton = new JMenuItem();
	deleteProjectButton.setText("Delete current project");
	deleteProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String confirm = JOptionPane.showInputDialog(null, "Please type the project name that you are intending\n" + "to delete EXACTLY AS IT IS in the following box.\n\n" + "Deleting a project means you will lose ALL data and\n" + "all collaborators will be removed. Back up code accordingly.", "Confirm project deletion", JOptionPane.WARNING_MESSAGE);
		// TODO double check if projects match
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
		setIcon(new ImageIcon(ImageIO.read(new File("images/search.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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
		setIcon(new ImageIcon(ImageIO.read(new File("images/projectManage.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    popup.show(EditorToolbar.this, ProjectManageButton.this.getX(), ProjectManageButton.this.getY());
		}
	    });
	}
    }

    private class ImportButton extends JButton {
	private ImportButton() {
	    try {
		setIcon(new ImageIcon(ImageIO.read(new File("images/import.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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
		setIcon(new ImageIcon(ImageIO.read(new File("images/export.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
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

    public void createProjectButtonDoClick() {
	createProjectButton.doClick();
    }

    public void renameProjectButtonDoClick() {
	renameProjectButton.doClick();
    }
}
