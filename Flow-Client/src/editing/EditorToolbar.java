
package editing;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import login.CreateAccountPane;
import message.Data;
import shared.Communicator;
import shared.EditArea;
import shared.EditTabs;
import shared.FileTree;
import shared.FileTree.DirectoryNode;
import shared.FileTree.FileNode;
import shared.FileTree.ProjectNode;

/**
 * Toolbar with Search/Import/Export/Project options buttons
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class EditorToolbar extends JToolBar {

	// Keeps track of the buttons
	private JPopupMenu	popup;
	private JMenuItem	createProjectButton;
	private JMenuItem	renameProjectButton;
	private EditPane	editPane;

	/**
	 * Creates a new EditorToolbar
	 * 
	 * @param pane
	 *        the parent EditPane
	 */
	public EditorToolbar(EditPane pane) {
		// Swing setup
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(FlowClient.EMPTY_BORDER);
		this.editPane = pane;

		// Creates the project options dialog
		popup = new JPopupMenu("Project Management");
		// Creates a button to create a new project button
		createProjectButton = new JMenuItem();
		createProjectButton.setText("New project");
		createProjectButton.addActionListener(new ActionListener() {

			/**
			 * Asks user for new project name, then sends request to server
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Asks user for new name
				String projectName = JOptionPane.showInputDialog(null, "Please enter a name for your new Project\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "New Project", JOptionPane.QUESTION_MESSAGE);
				if (projectName == null) {
					return;
				}
				projectName = projectName.trim();
				while (CreateAccountPane.stringContains(projectName, CreateAccountPane.INVALID_CHARS) || projectName.length() < 1) {
					projectName = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter a name for your new Project\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.QUESTION_MESSAGE);
					if (projectName == null) {
						return;
					}
					projectName = projectName.trim();
				}

				// Sends request to server
				Data createProjectRequest = new Data("new_project");
				createProjectRequest.put("project_name", projectName);
				createProjectRequest.put("session_id", Communicator.getSessionID());
				switch (Communicator.communicate(createProjectRequest).get("status", String.class)) {
				// Success case
					case "OK":
						break;

					case "ACCESS_DENIED":
						JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
						break;

					// Other cases
					default:
						JOptionPane.showConfirmDialog(null, "Your project name is invalid. Please choose another one.\nThe most likely case is that your project name conflicts with another project name.", "Project creation failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;
				}
				// Refreshes
				pane.getEditorFileTree().refresh();
			}
		});

		// Rename project button
		renameProjectButton = new JMenuItem();
		renameProjectButton.setText("Rename current project");
		renameProjectButton.addActionListener(new ActionListener() {

			/**
			 * Gets the selected project, then asks to be renamed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Gets the path
				TreePath path = pane.getEditorFileTree().getSelectionPath();
				if (path == null) {
					return;
				}
				Object[] pathArray = path.getPath();
				if (pathArray == null || pathArray.length < 2) {
					return;
				}
				ProjectNode projectNode = (ProjectNode) pathArray[1];

				// Asks user for new name
				String rename = JOptionPane.showInputDialog(null, "Please enter new name for the project " + projectNode.toString() + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Rename project", JOptionPane.QUESTION_MESSAGE);
				if (rename == null) {
					return;
				}
				rename = rename.trim();
				while (CreateAccountPane.stringContains(rename, CreateAccountPane.INVALID_CHARS) || rename.length() < 1) {
					rename = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this project." + "\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.QUESTION_MESSAGE);
					if (rename == null) {
						return;
					}
					rename = rename.trim();
				}

				// Preps request to server
				Data modifyRequest = new Data("project_modify");
				modifyRequest.put("project_modify_type", "RENAME_PROJECT");
				modifyRequest.put("project_uuid", projectNode.getProjectUUID());
				modifyRequest.put("session_id", Communicator.getSessionID());
				modifyRequest.put("new_name", rename);
				// Sends request to server
				switch (Communicator.communicate(modifyRequest).get("status", String.class)) {
				// Success case
					case "OK":
						((ProjectNode) pane.getEditorFileTree().getSelectionPath().getPath()[1]).setName(rename);
						break;
					// Failure cases
					case "PROJECT_NAME_INVALID":
						JOptionPane.showConfirmDialog(null, "Your project name is invalid.\nPlease choose another one.", "Project renaming failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;
					case "ACCESS_DENIED":
						JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
						break;
					case "PROJECT_DOES_NOT_EXIST":
						JOptionPane.showConfirmDialog(null, "The project you are trying to rename does not exist.\n" + "Try refreshing the list of projects by moving your mouse cursor into,\n" + "then out of the project list.", "Project renaming failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;
				}
				// Forces a refresh
				pane.getEditorFileTree().refresh();
			}
		});

		// Delete project button
		JMenuItem deleteProjectButton = new JMenuItem();
		deleteProjectButton.setText("Delete current project");
		deleteProjectButton.addActionListener(new ActionListener() {

			/**
			 * Gets the active project, makes sure that the user is ABSOLUTELY
			 * SURE they want to delete it, then proceeds
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Gets the project
				TreePath path = pane.getEditorFileTree().getSelectionPath();
				if (path == null)
					return;
				Object[] pathArray = path.getPath();
				if (pathArray == null || pathArray.length < 2)
					return;
				UUID projectUUID = ((ProjectNode) pathArray[1]).getProjectUUID();
				if (projectUUID == null) {
					return;
				}
				// Confirmation dialog
				String confirm = JOptionPane.showInputDialog(null, "Please type the project name that you are intending\n" + "to delete EXACTLY AS IT IS in the following box.\n\n" + "Deleting a project means you will lose ALL data and\n" + "all collaborators will be removed. Back up code accordingly.", "Confirm project deletion",
						JOptionPane.WARNING_MESSAGE);
				if (confirm == null) {
					return;
				}

				// Gets project information (for project name)
				Data projectRequest = new Data("project_info");
				projectRequest.put("session_id", Communicator.getSessionID());
				projectRequest.put("project_uuid", projectUUID);
				Data project = Communicator.communicate(projectRequest);
				if (project.get("status", String.class).equals("ACCESS_DENIED")) {
					JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				// Confirms that they match
				if (confirm.equals(project.get("project_name", String.class))) {
					// Secondary confirmation
					int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + project.get("project_name", String.class) + "?", "Confirm project deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (confirmation == JOptionPane.YES_OPTION) {
						// Creates deletion message
						Data deleteProjectRequest = new Data("project_modify");
						deleteProjectRequest.put("project_modify_type", "DELETE_PROJECT");
						deleteProjectRequest.put("project_uuid", projectUUID);
						deleteProjectRequest.put("session_id", Communicator.getSessionID());

						// Sends deletion message
						Data reply = Communicator.communicate(deleteProjectRequest);
						String status = reply.get("status", String.class);
						switch (status) {
						// Success case
							case "OK":
								project = null;
								pane.getEditorFileTree().refreshProjectList();
								pane.getEditorFileTree().setSelectionRow(0);
								break;

							case "ACCESS_DENIED":
								JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
								break;

							// Failure cases
							default:
								break;
						}
					} else
						return;
				} else {
					// Typo!
					JOptionPane.showConfirmDialog(null, "The project name is incorrect.\nNothing has been changed.", "Deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

		});

		// Adds the three buttons to the popup menu
		popup.add(createProjectButton);
		popup.add(renameProjectButton);
		popup.add(deleteProjectButton);

		// Adds buttons
		// add(new SearchButton());
		add(new ProjectManageButton());
		add(new ImportButton());
		add(new ExportButton());
		addSeparator();

		// Does things.
		setFloatable(false);
		setRollover(true);
	}

	/**
	 * Button to search the current document for text
	 * 
	 * @author Vince Ou
	 *
	 */
	private class SearchButton extends JButton {

		/**
		 * Creates a new SearchButton
		 */
		private SearchButton() {
			// Sets an icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/search.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			setToolTipText("Search...");
			addActionListener(new ActionListener() {

				/**
				 * Goes through the currently active window and searches for a
				 * string
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO pop open a search window and search for something
					System.out.println("Search button pressed");
				}
			});
		}
	}

	/**
	 * Button that pens the project management menu
	 * 
	 * @author Vince Ou
	 *
	 */
	private class ProjectManageButton extends JButton {

		/**
		 * Opens up the good ol' project management menu created up there
		 */
		private ProjectManageButton() {
			setToolTipText("Project management");
			// Sets an icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/projectManage.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * Shows the JPopup Menu
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					popup.show(EditorToolbar.this, ProjectManageButton.this.getX(), ProjectManageButton.this.getY());
				}
			});
		}
	}

	/**
	 * Button that will import a file to a FlowProject
	 * 
	 * @author Vince Ou
	 *
	 */
	private class ImportButton extends JButton {

		/**
		 * Creates a new ImportButton
		 */
		private ImportButton() {
			setToolTipText("Import a file");
			// Sets an icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/import.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * Gets the selected directory, and imports a file into there
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// Gets the node to put the file under
					DefaultMutableTreeNode selectedDir = (DefaultMutableTreeNode) editPane.getEditorFileTree().getSelectionPath().getLastPathComponent();
					if (selectedDir == null || !(selectedDir instanceof DirectoryNode)) {
						JOptionPane.showConfirmDialog(null, "Please select a directory to place your imported file under", "Select a directory first", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					}
					UUID dirUUID = ((DirectoryNode) selectedDir).getDirectoryUUID();

					// Opens a file chooser to get new file
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

						@Override
						public String getDescription() {
							return "Only .JAVA or .TXT files (for now)";
						}

						@Override
						public boolean accept(File f) {
							String name = f.getName();
							return name.endsWith(".java") || name.endsWith(".txt") || f.isDirectory();
						}
					});
					fileChooser.setDialogTitle("Select file to import...");
					File importFile;
					if (fileChooser.showOpenDialog(EditorToolbar.this) == JFileChooser.APPROVE_OPTION) {
						importFile = fileChooser.getSelectedFile();
					} else {
						return;
					}

					// Asks server to create new file
					Data createFileRequest = new Data("new_text_file");
					createFileRequest.put("session_id", Communicator.getSessionID());
					createFileRequest.put("file_name", importFile.getName());
					UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
					createFileRequest.put("project_uuid", projectUUID);
					createFileRequest.put("directory_uuid", dirUUID);
					Data response = Communicator.communicate(createFileRequest);
					switch (response.get("status", String.class)) {
						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							return;

						case "OK":
							break;

						default:
							JOptionPane.showConfirmDialog(null, "There was an error importing your file.\nTry a force refresh on the documents tree by Alt + clicking it.", "Import error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							return;
					}

					// Gets the file contents into a String
					String fileContents = "";
					BufferedReader br;
					try {
						br = new BufferedReader(new FileReader(importFile));
						String line;
						while ((line = br.readLine()) != null) {
							fileContents += line + '\n';
						}
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						return;
					}

					// Writes file contents to server
					Data modify = new Data("file_text_modify");
					modify.put("file_uuid", response.get("file_uuid", UUID.class));
					modify.put("session_id", Communicator.getSessionID());
					modify.put("mod_type", "INSERT");
					modify.put("idx", 0);
					modify.put("str", fileContents);
					Data modifyResponse = Communicator.communicate(modify);
					switch (modifyResponse.get("status", String.class)) {
						case "OK":
							break;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							return;

						default:
							return;
					}

					// Inserts a new child node
					FileTree.FileNode child = editPane.getEditorFileTree().generateFileNode(response.get("file_uuid", UUID.class));
					((DefaultTreeModel) editPane.getEditorFileTree().getModel()).insertNodeInto(child, selectedDir, selectedDir.getChildCount());
				}
			});
		}
	}

	/**
	 * Button to export a file out of Flow onto the user's desktop
	 * 
	 * @author Vince Ou
	 *
	 */
	private class ExportButton extends JButton {

		private ExportButton() {
			setToolTipText("Export the current file");
			// Sets the icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/export.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * Confirm that is the file to be exported, and copies it into a
				 * directory of their choosing
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// Gets the source for export
					UUID fileUUID;
					EditTabs tabs = editPane.getEditTabs();
					if (tabs == null) {
						TreePath path = editPane.getEditorFileTree().getSelectionPath();
						if (path == null) {
							JOptionPane.showConfirmDialog(null, "Please select a file to export", "Select a file first", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							return;
						}
						Object[] pathArray = path.getPath();
						if (pathArray.length < 2 || !(path.getLastPathComponent() instanceof FileNode)) {
							JOptionPane.showConfirmDialog(null, "Please select a file to export", "Select a file first", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							return;
						}

						FileNode node = (FileNode) path.getLastPathComponent();
						fileUUID = node.getFileUUID();
					} else {
						fileUUID = ((EditArea) ((JScrollPane) tabs.getSelectedComponent()).getViewport().getView()).getFileUUID();
					}

					// Gets the export contents
					Data getFileContents = new Data("file_request");
					getFileContents.put("session_id", Communicator.getSessionID());
					getFileContents.put("file_uuid", fileUUID);
					Data reply = Communicator.communicate(getFileContents);
					switch (reply.get("status", String.class)) {
						case "OK":
							break;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							return;

						default:
							return;
					}
					String fileConts = new String(reply.get("file_data", byte[].class));

					// Gets the name of the export file
					Data getFileName = new Data("file_info");
					getFileName.put("session_id", Communicator.getSessionID());
					getFileName.put("file_uuid", fileUUID);
					String fileName = Communicator.communicate(getFileName).get("file_name", String.class);

					// Checks. Makes it a txt by default.
					boolean valid = true;
					int dotIdx = fileName.lastIndexOf('.');
					if (dotIdx == -1)
						valid = false;
					if (!valid) {
						fileName += ".txt";
					}

					// Gets the user to choose the destination
					JFileChooser destChooser = new JFileChooser();
					destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					destChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					destChooser.setDialogTitle("Choose export destination");
					String dest;
					if (destChooser.showSaveDialog(EditorToolbar.this) == JFileChooser.APPROVE_OPTION) {
						dest = destChooser.getCurrentDirectory().getPath() + "\\" + fileName;
					} else {
						return;
					}
					JOptionPane.showConfirmDialog(null, "Confirm export destination to be " + dest + "?", "Confirm export", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
					File outFile = new File(dest);

					// Writes it into the destination TODO (this may be the
					// cause of the errors)
					try {
						if (!outFile.createNewFile()) {
							JOptionPane.showConfirmDialog(null, "Could not export. Are you sure you have permissions to the destination folder?", "Could not export", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							return;
						}

						FileWriter fw = new FileWriter(outFile);

						// Cleans up
						fw.write(fileConts);
						fw.close();

					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// Shows confirmation
					JOptionPane.showConfirmDialog(null, "Finished exporting file " + fileName + " to " + dest, "Done!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	/**
	 * To avoid duplicate code. Will create a new project.
	 */
	public void createProjectButtonDoClick() {
		createProjectButton.doClick();
	}

	/**
	 * To avoid duplicate code. Will rename a project.
	 */
	public void renameProjectButtonDoClick() {
		renameProjectButton.doClick();
	}
}
