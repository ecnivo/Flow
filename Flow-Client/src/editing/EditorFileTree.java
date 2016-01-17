
package editing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import login.CreateAccountPane;
import message.Data;
import shared.Communicator;
import shared.FileTree;
import shared.EditTabs;

/**
 * Special documents tree for the editing view
 * 
 * @author Vince Ou
 */
@SuppressWarnings("serial")
public class EditorFileTree extends FileTree {

	// The parent pane
	private EditPane	editPane;
	// private FlowFile clipboard;

	// Each of the popup menus for right clicks
	JPopupMenu			workspacePopup;
	JPopupMenu			projectPopup;
	JPopupMenu			dirPopup;
	JPopupMenu			filePopup;

	/**
	 * Creates a new EditorDocTree
	 * 
	 * @param editPane
	 *            the parent pane (MUST be an EditPane, however)
	 */
	public EditorFileTree(EditPane editPane) {
		super();
		this.editPane = editPane;

		// Initializes all the popup menus
		workspacePopup = new JPopupMenu();
		projectPopup = new JPopupMenu();
		dirPopup = new JPopupMenu();
		filePopup = new JPopupMenu();

		// Adds the button to the workspace menu
		workspacePopup.add(new CreateProjectButton());

		// Adds buttons to the projects' menu
		projectPopup.add(new CreateProjectButton());

		projectPopup.add(new CreateDirectoryOnDirectoryButton());

		JMenuItem renameProjectButton = new JMenuItem();
		renameProjectButton.setText("Rename project");
		renameProjectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editPane.getEditToolbar().renameProjectButtonDoClick();
				EditorFileTree.this.refreshProjectList();
			}
		});
		projectPopup.add(renameProjectButton);

		projectPopup.add(new CreateFileOnDirectoryButton());

		projectPopup.add(new PasteOnDirectoryButton());

		// JMenuItem deleteProjectLabel = new JMenuItem();
		// deleteProjectLabel.setText("Deleting a project should be done through\nthe button in the editor toolbar");

		// Adds buttons to the directories' menu
		dirPopup.add(new CreateDirectoryOnDirectoryButton());

		dirPopup.add(new CreateFileOnDirectoryButton());

		// Since the "delete folder" is only used in one JPopupMenu, it can be
		// declared here
		JMenuItem deleteFolderButton = new JMenuItem();
		deleteFolderButton.setText("Delete");
		deleteFolderButton.addActionListener(new ActionListener() {

			/**
			 * Asks the user for confirmation, then sends request to the server
			 * to delete it. Removes nodes as necessary.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Asks the user for confirmation
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + ((DirectoryNode) getSelectionPath().getLastPathComponent()).getName() + "?", "Confirm directory deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				ProjectNode projectNode = null;
				if (confirm == JOptionPane.YES_OPTION) {
					// Prepares request for the server
					Data directoryModifyRequest = new Data("directory_modify");
					DirectoryNode selectedDir = (DirectoryNode) getSelectionPath().getLastPathComponent();
					projectNode = (ProjectNode) selectedDir.getPath()[1];
					directoryModifyRequest.put("directory_uuid", selectedDir.getDirectoryUUID());
					directoryModifyRequest.put("session_id", Communicator.getSessionID());
					directoryModifyRequest.put("mod_type", "DELETE");

					// Sends request to server
					String status = Communicator.communicate(directoryModifyRequest).get("status", String.class);
					switch (status) {
					// Success case
						case "OK":
							((DefaultTreeModel) getModel()).removeNodeFromParent(selectedDir);
							break;

						// Failure case
						default:
							JOptionPane.showConfirmDialog(null, "Deletion failed.\nTry refreshing by Alt + clicking on the documents tree, or try again at another time.", "Failed to delete", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;
					}
				}
				// Whoops. Something failed in this case.
				if (projectNode == null)
					reloadProjectFiles(projectNode);
			}
		});
		dirPopup.add(deleteFolderButton);

		dirPopup.add(new PasteOnDirectoryButton());

		// Files' menu
		// JMenuItem copyFileButton = new JMenuItem();
		// copyFileButton.setText("Copy");
		// copyFileButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// clipboard = getActiveFileNode().getFile();
		// }
		// });

		// JMenuItem pasteFileButton = new JMenuItem();
		// pasteFileButton.setText("Paste");
		// pasteFileButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO tell server to add new file in this directory
		// EditorDocTree.this.refreshProjectList();
		// }
		// });

		// Same case as "delete directory" in which it won't be used again, so
		// it can be decleared right here
		JMenuItem deleteFileButton = new JMenuItem();
		deleteFileButton.setText("Delete");
		deleteFileButton.addActionListener(new ActionListener() {

			/**
			 * Asks the user for confirmation, then deletes
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// COnfirmation message
				FileNode selectedNode = (FileNode) getSelectionPath().getLastPathComponent();
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + selectedNode.toString() + "?", "Deletion confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirm == JOptionPane.YES_OPTION) {
					// Prepares request
					Data delFileReq = new Data("file_metadata_modify");
					delFileReq.put("file_uuid", selectedNode.getFileUUID());
					delFileReq.put("session_id", Communicator.getSessionID());
					delFileReq.put("mod_type", "DELETE");

					// Sends request to server
					String status = Communicator.communicate(delFileReq).get("status", String.class);
					switch (status) {
					// Success case
						case "OK":
							break;

						// Failure case
						default:
							JOptionPane.showConfirmDialog(null, "An error occurred during the deletion.\nTry refreshing the project list again.", "Deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;
					}
				} else {
					JOptionPane.showConfirmDialog(null, "Nothing was changed", "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
				// EditorDocTree.this.reloadProjectFiles((ProjectNode)
				// getActiveDirectoryNode().getPath()[1]);
				reloadProjectFiles((ProjectNode) selectedNode.getPath()[1]);
			}
		});

		// Adds functionality when clicking
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// nothing
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// nothing
			}

			/**
			 * Gets the path, then decides as necessary what to do
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				// Gets the selected path
				TreePath treePath = getSelectionPath();
				if (treePath == null) {
					return;
				}
				Object selected = treePath.getLastPathComponent();
				// So that I won't have to do e.getX() and getY() every time
				int x = e.getX();
				int y = e.getY();

				if (selected instanceof ProjectNode) {
					// If project selected, will show the project popup menu
					if (e.getButton() == MouseEvent.BUTTON3) {
						projectPopup.show(EditorFileTree.this, x, y);
					}
				} else if (selected instanceof DirectoryNode) {
					// Directory menu for selected directory
					if (e.getButton() == MouseEvent.BUTTON3) {
						dirPopup.show(EditorFileTree.this, x, y);
					}
					if (e.isAltDown()) {
						// Will reload the project files for that particular
						// directory if ALT pressed
						reloadProjectFiles((ProjectNode) ((DirectoryNode) selected).getPath()[1]);
					}
				} else if (selected instanceof FileNode) {
					// If double click, opens the file
					if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
						openSelectedFile();
					}
					// If right click, then opens the menu
					else if (e.getButton() == MouseEvent.BUTTON3) {
						filePopup.show(EditorFileTree.this, x, y);
					}
				} else if (selected instanceof DefaultMutableTreeNode) {
					// This case is guaranteed to be the root node ("Workspace")
					// and shows the workspace menu
					if (e.getButton() == MouseEvent.BUTTON3)
						workspacePopup.show(EditorFileTree.this, x, y);
				}
			}
		});
		// Adds fuctionality for pressing [ENTER] to open a file
		addKeyListener(new KeyListener() {

			/**
			 * Checks, and does the same command as the above to open the file
			 */
			@Override
			public void keyTyped(KeyEvent e) {
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
				if (selected instanceof FileNode && e.getKeyChar() == KeyEvent.VK_ENTER) {
					openSelectedFile();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// nothing
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// nothing
			}
		});
		// Updates the collabs list if user selects a different part of the tree
		addTreeSelectionListener(new TreeSelectionListener() {

			/**
			 * Checks for the user's click position, then refreshes
			 */
			@Override
			public void valueChanged(TreeSelectionEvent selected) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) (selected.getPath().getLastPathComponent());
				// Protection so that it doesn't try to update when the
				// workspace node is pressed
				if (node.getPath().length > 1) {
					editPane.getCollabsList().refreshUserList();
				}
			}
		});
	}

	/**
	 * Opens the currently selected (highlighted) file
	 */
	private void openSelectedFile() {
		// Gets the file and project's UUIDs
		UUID fileToOpen = ((FileNode) getSelectionPath().getLastPathComponent()).getFileUUID();
		UUID projectUUID = ((ProjectNode) getSelectionPath().getPath()[1]).getProjectUUID();

		// Asks the server for the file's info
		Data fileRequest = new Data("file_info");
		fileRequest.put("file_uuid", fileToOpen);
		fileRequest.put("session_id", Communicator.getSessionID());
		Data fileData = Communicator.communicate(fileRequest);

		// Asks the server for the file's data
		Data fileContentsRequest = new Data("file_request");
		fileContentsRequest.put("session_id", Communicator.getSessionID());
		fileContentsRequest.put("file_uuid", fileToOpen);
		Data fileContents = Communicator.communicate(fileContentsRequest);

		// Asks the server for the project's info
		Data permissionRequest = new Data("project_info");
		permissionRequest.put("session_id", Communicator.getSessionID());
		permissionRequest.put("project_uuid", projectUUID);
		Data permissions = Communicator.communicate(permissionRequest);

		// Checks tha this user has access to this project
		String permissionsStatus = permissions.get("status", String.class);
		if (permissionsStatus.equals("ACCESS_DENIED")) {
			return;
		}

		// Gets the list of editors and the owner to see if the user has edit
		// permissions
		String[] editors = permissions.get("editors", String[].class);
		String owner = permissions.get("owner", String.class);
		boolean canEdit = false;
		if (Communicator.getUsername().equals(owner)) {
			canEdit = true;
		} else if (Arrays.asList(editors).contains(Communicator.getUsername())) {
			canEdit = true;
		}

		// If everything so far is OK, then it will try to open the file
		String status = fileContents.get("status", String.class);
		switch (status) {
		// Passing case
			case "OK":
				String type = fileData.get("file_type", String.class);
				// "Arbitrary" files are those which are not source code (e.g.
				// images, database files, etc.)
				if (type.contains("ARBIT")) {
					// try {
					// //TODO write byte array to local directory
					// Desktop.getDesktop().open();
					// } catch (IOException e1) {
					// e1.printStackTrace();
					// }
				}
				// "Text" files are source code
				else if (type.contains("TEXT")) {
					EditTabs tabs = editPane.getEditTabs();
					if (tabs != null) {
						// Gets the data, then sends it to the tab to be opened
						String fileName = fileData.get("file_name", String.class);
						byte[] bytes = fileContents.get("file_data", byte[].class);
						String fileContentsString = new String(bytes);
						UUID versionUUID = fileContents.get("version_uuid", UUID.class);
						tabs.openTab(fileName, fileContentsString, projectUUID, fileToOpen, versionUUID, canEdit);
					}
				}
				break;
			// Project UUID does not match the server's queries
			case "PROJECT_NOT_FOUND":
				JOptionPane.showConfirmDialog(null, "The project that this file is in cannot be found for some reason.\n" + "Try refreshing the list of projects (move the mouse cursor into the console and back here)" + "\nand see if it is resolved.", "Project not found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				return;
				// File cannot be found inside the Project
			case "FILE_NOT_FOUND":
				JOptionPane.showConfirmDialog(null, "The file you are trying to open cannot be found.\n" + "Try refreshing the list of projects/files (move the mouse cursor into the console and back here)" + "\nand see if it is resolved.", "Project not found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				return;
		}
	}

	/**
	 * Button to create new project
	 * 
	 * @author Vince Ou
	 */
	private class CreateProjectButton extends JMenuItem {

		/**
		 * The same as a JMenuItem but with a custom ActionListener
		 */
		private CreateProjectButton() {
			super("New project");
			addActionListener(new ActionListener() {

				/**
				 * Does the same thing as pressing the "create project" button in the edittoolbar
				 * (see EditToolbar)
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					editPane.getEditToolbar().createProjectButtonDoClick();
					EditorFileTree.this.refreshProjectList();
				}
			});
		}

	}

	/**
	 * button to create a directory
	 * 
	 * @author Vince Ou
	 *
	 */
	private class CreateDirectoryOnDirectoryButton extends JMenuItem {

		/**
		 * Creates a new directory inside the currently selected one
		 */
		private CreateDirectoryOnDirectoryButton() {
			super("New directory");
			addActionListener(new ActionListener() {

				/**
				 * Gets the directory, and a request to the server and creates the new node
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Gets the path to the selected node
					TreePath selectionPath = getSelectionPath();
					if (selectionPath == null) {
						JOptionPane.showConfirmDialog(null, "Please select a directory to put your new directory in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					}
					// Gets the selected node
					DirectoryNode selectedDir = (DirectoryNode) selectionPath.getLastPathComponent();
					if (selectedDir == null) {
						JOptionPane.showConfirmDialog(null, "Please select a directory to put your new directory in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					}
					// Asks the user for their new directory's name 
					String name = JOptionPane.showInputDialog(null, "What is the name of your new directory?", "Name", JOptionPane.QUESTION_MESSAGE);
					if (name == null) {
						return;
					}
					name = name.trim();
					if (name.length() == 0) {
						return;
					}
					// Checks for validity of name
					while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
						name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE);
						if (name == null) {
							return;
						}
						name.trim();
					}

					// Creates request to the server for a new directory
					Data createDirReq = new Data("new_directory");
					createDirReq.put("session_id", Communicator.getSessionID());
					UUID parentDirUUID = selectedDir.getDirectoryUUID();
					createDirReq.put("parent_directory_uuid", parentDirUUID);
					UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
					createDirReq.put("project_uuid", projectUUID);
					createDirReq.put("directory_name", name);

					// Sends request data packet
					Data response = Communicator.communicate(createDirReq);
					// If it is passing, then it just goes to create a new directory node
					if (response.get("status", String.class).equals("OK")) {
						UUID newDirUUID = response.get("directory_uuid", UUID.class);
						((DefaultTreeModel) EditorFileTree.this.getModel()).insertNodeInto(new DirectoryNode(newDirUUID), selectedDir, selectedDir.getChildCount());
					}
					// Failure cases
					else if (response.get("status", String.class).equals("DIRECTORY_NAME_INVALID")) {
						JOptionPane.showConfirmDialog(null, "The directory name is invalid. Try another name.\nThe most likely issue is that the name is conflicting with another name.", "Directory name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					} else
						JOptionPane.showConfirmDialog(null, "The directory was not created because of an error.\nTry refreshing by Alt + clicking the project tree, or try again some other time.", "Directory creation failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

					// Expands the created directory
					expandRow(getRowForPath(new TreePath(selectedDir.getPath())));
				}
			});

		}
	}

	/**
	 * Button to create a new File on a Directory
	 * 
	 * @author Vince Ou
	 *
	 */
	private class CreateFileOnDirectoryButton extends JMenuItem {

		/**
		 * Creates a button which, when pressed, wil create a new file on that directory
		 */
		private CreateFileOnDirectoryButton() {
			super("New (source code) file");
			addActionListener(new ActionListener() {

				/**
				 * Asks for new name, then sends it off to server.
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Gets the selected directory
					TreePath path = getSelectionPath();
					if (path == null) {
						JOptionPane.showConfirmDialog(null, "Please select a directory to put your new file in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						return;
					}
					DirectoryNode selectedDir = (DirectoryNode) path.getLastPathComponent();
					// asks for name
					String name = JOptionPane.showInputDialog(null, "What is the name of your new  file?\nInclude extensions such as .java", "Name", JOptionPane.QUESTION_MESSAGE);
					if (name == null) {
						return;
					}
					while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
						name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE);
						if (name == null) {
							return;
						}
						name = name.trim();
					}

					// Generates a request packet
					Data createFileRequest = new Data("new_text_file");
					UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
					createFileRequest.put("project_uuid", projectUUID);
					createFileRequest.put("file_name", name);
					createFileRequest.put("session_id", Communicator.getSessionID());
					createFileRequest.put("directory_uuid", selectedDir.getDirectoryUUID());

					// Sends request packet
					Data reply = Communicator.communicate(createFileRequest);
					String status = reply.get("status", String.class);
					switch (status) {
					// Success case (gets the new file's UUID to create a new node)
						case "OK":
							UUID fileUUID = reply.get("file_uuid", UUID.class);
							((DefaultTreeModel) EditorFileTree.this.getModel()).insertNodeInto(new FileNode(fileUUID), selectedDir, selectedDir.getChildCount());
							break;

						// Failure cases
						case "DIRECTORY_DOES_NOT_EXIST":
							JOptionPane.showConfirmDialog(null, "The directory you are trying to create this file in does not exist.\nTry refreshing the list of projects by Alt + clicking the projects list.", "Project cannot be found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;

						case "DOCUMENT_NAME_INVALID":
							JOptionPane.showConfirmDialog(null, "The file name is invalid.\nThis is typically due to a conflict with another file name.\nTry a different document name.", "Document name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;

						default:
							break;
					}
					// Expands it for the user as a convenience
					expandRow(getRowForPath(new TreePath(selectedDir.getPath())));
				}
			});

		}
	}

	/**
	 * Creates a button that allows the user to paste a file (or directory) onto a directory node
	 * 
	 * @author Vince Ou
	 *
	 */
	private class PasteOnDirectoryButton extends JMenuItem {

		/**
		 * Creates a new PasteOnDirectoryButton
		 */
		private PasteOnDirectoryButton() {
			super("Paste");
			addActionListener(new ActionListener() {

				/**
				 * Not much of a clue what to do.
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO authorize with server to put these files here
					EditorFileTree.this.refreshProjectList();
					throw new UnsupportedOperationException();
				}
			});
		}
	}
}
