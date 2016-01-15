package editing;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
import shared.DocTree.ProjectNode;

@SuppressWarnings("serial")
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
		    JOptionPane.showConfirmDialog(null, "Your project name is invalid. Please choose another one.\nThe most likely case is that your project name conflicts with another project name.", "Project creation failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
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
		ProjectNode selectedNode = (ProjectNode) pane.getDocTree().getSelectionPath().getPath()[1];
		String modifiedProjectName = JOptionPane.showInputDialog(null, "Please enter new name for the project " + selectedNode.getName() + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Rename project", JOptionPane.QUESTION_MESSAGE).trim();
		while (CreateAccountPane.stringContains(modifiedProjectName, CreateAccountPane.INVALID_CHARS) || modifiedProjectName.length() < 1) {
		    modifiedProjectName = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this project." + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.QUESTION_MESSAGE).trim();
		}

		Data modifyRequest = new Data("project_modify");
		modifyRequest.put("project_modify_type", "RENAME_PROJECT");
		modifyRequest.put("project_uuid", selectedNode.getProjectUUID());
		modifyRequest.put("session_id", Communicator.getSessionID());
		modifyRequest.put("new_name", modifiedProjectName);
		switch (Communicator.communicate(modifyRequest).get("status", String.class)) {
		case "OK":
		    ((ProjectNode) pane.getDocTree().getSelectionPath().getPath()[1]).setName(modifiedProjectName);
		    JOptionPane.showConfirmDialog(null, "Your project has been succesfully renamed to " + modifiedProjectName + ".", "Project renaming success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
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
		UUID projectUUID = ((ProjectNode) pane.getDocTree().getSelectionPath().getPath()[1]).getProjectUUID();
		if (projectUUID == null) {
		    return;
		}
		String confirm = JOptionPane.showInputDialog(null, "Please type the project name that you are intending\n" + "to delete EXACTLY AS IT IS in the following box.\n\n" + "Deleting a project means you will lose ALL data and\n" + "all collaborators will be removed. Back up code accordingly.", "Confirm project deletion", JOptionPane.WARNING_MESSAGE);
		Data projectRequest = new Data("project_info");
		projectRequest.put("session_id", Communicator.getSessionID());
		projectRequest.put("project_uuid", projectUUID);
		Data project = Communicator.communicate(projectRequest);
		if (confirm.equals(project.get("project_name", String.class))) {
		    int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + project.get("project_name", String.class) + "?", "Confirm project deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		    if (confirmation == JOptionPane.YES_OPTION) {
			Data deleteProjectRequest = new Data("project_modify");
			deleteProjectRequest.put("project_modify_type", "DELETE_PROJECT");
			deleteProjectRequest.put("project_uuid", projectUUID);
			deleteProjectRequest.put("session_id", Communicator.getSessionID());

			Data reply = Communicator.communicate(deleteProjectRequest);
			String status = reply.get("status", String.class);
			switch (status) {
			case "OK":
			    JOptionPane.showConfirmDialog(null, "Your project has been deleted", "Deletion success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			    project = null;
			    pane.getDocTree().refreshProjectList();
			    break;

			default:
			    break;
			}
		    } else
			return;
		} else {
		    JOptionPane.showConfirmDialog(null, "The project name is incorrect.\nNothing has been changed.", "Deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    return;
		}
	    }

	});

	popup.add(createProjectButton);
	popup.add(renameProjectButton);
	popup.add(deleteProjectButton);

	add(new SearchButton());
	add(new ProjectManageButton());
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
