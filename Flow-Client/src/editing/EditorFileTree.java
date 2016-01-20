
package editing;

import login.CreateAccountPane;
import message.Data;
import shared.Communicator;
import shared.EditArea;
import shared.EditTabs;
import shared.FileTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * Special documents tree for the editing view
 * 
 * @author Vince Ou
 */
@SuppressWarnings("serial")
public class EditorFileTree extends FileTree {

	// The parent pane
	private final EditPane editPane;
	// private FlowFile clipboard;

	// Each of the popup menus for right clicks
	private final JPopupMenu workspacePopup;
	private final JPopupMenu projectPopup;
	private final JPopupMenu dirPopup;
	private final JPopupMenu filePopup;

	/**
	 * Creates a new EditorDocTree
	 * 
	 * @param editPane
	 *        the parent pane (MUST be an EditPane, however)
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
				refresh();
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

		JMenuItem renameDirectoryButton = new JMenuItem();
		renameDirectoryButton.setText("Rename");
		renameDirectoryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = getSelectionPath();
				if (path == null) {
					JOptionPane.showConfirmDialog(null, "Please select a directory to rename first.", "No directory selected", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (!(selected instanceof DirectoryNode) || selected instanceof ProjectNode) {
					JOptionPane.showConfirmDialog(null, "Please select a directory to rename first.\nThe selected is not a directory.", "No directory selected", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					return;
				}
				DirectoryNode node = (DirectoryNode) selected;
				String rename = JOptionPane.showInputDialog(null, "What do you want to rename " + node + " to?");
				if (rename == null) {
					return;
				}
				Data dirModRequest = new Data("directory_modify");
				dirModRequest.put("directory_uuid", node.getDirectoryUUID());
				dirModRequest.put("mod_type", "RENAME");
				dirModRequest.put("new_name", rename);
				Data reply = Communicator.communicate(dirModRequest);
				if (reply == null) {
					return;
				}
				String status = reply.get("status", String.class);
				if (status == null) {
					return;
				}
				switch (status) {
					case "OK":
						node.setName(rename);
						break;

					case "DIRECTORY_NAME_INVALID":
						JOptionPane.showConfirmDialog(null, "The directory name " + rename + " is not valid.\nTry with a better name.", "Invalid Name", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;

					default:
						JOptionPane.showConfirmDialog(null, "The directory renaming failed. Nothing has been changed.", "Rename failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;
				}

				refresh();
			}
		});
		dirPopup.add(renameDirectoryButton);

		// Since the "delete folder" is only used in one JPopupMenu, it can be
		// declared here
		JMenuItem deleteDirectoryButton = new JMenuItem();
		deleteDirectoryButton.setText("Delete");
		deleteDirectoryButton.addActionListener(new ActionListener() {

			/**
			 * Asks the user for confirmation, then sends request to the server
			 * to delete it. Removes nodes as necessary.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Asks the user for confirmation
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + getSelectionPath().getLastPathComponent().toString() + "?", "Confirm directory deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				ProjectNode projectNode = null;
				if (confirm == JOptionPane.YES_OPTION) {
					// Prepares request for the server
					Data directoryModifyRequest = new Data("directory_modify");
					DirectoryNode selectedDir = (DirectoryNode) getSelectionPath().getLastPathComponent();
					projectNode = (ProjectNode) selectedDir.getPath()[1];
					directoryModifyRequest.put("directory_uuid", selectedDir.getDirectoryUUID());
					directoryModifyRequest.put("mod_type", "DELETE");

					// Sends request to server
					String status = Communicator.communicate(directoryModifyRequest).get("status", String.class);
					switch (status) {
					// Success case
						case "OK":
							((DefaultTreeModel) getModel()).removeNodeFromParent(selectedDir);
							break;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
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
		dirPopup.add(deleteDirectoryButton);

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
		// it can be declared right here
		JMenuItem renameFileButton = new JMenuItem();
		renameFileButton.setText("Rename");
		renameFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
				if (selected == null) {
					return;
				}
				FileNode node = (FileNode) selected;

				String rename = JOptionPane.showInputDialog(null, "What do you want to rename " + node.toString() + " to?");
				if (rename == null || rename.length() < 1) {
					return;
				}

				Data renameFileRequest = new Data("file_metadata_modify");
				renameFileRequest.put("file_uuid", node.getFileUUID());
				renameFileRequest.put("mod_type", "RENAME");
				renameFileRequest.put("name", rename);
				Data response = Communicator.communicate(renameFileRequest);
				if (response == null) {
					return;
				}
				String status = response.get("status", String.class);
				switch (status) {
					case "OK":
						node.setName(rename);
						break;

					case "FILE_NAME_INVALID":
						JOptionPane.showConfirmDialog(null, "The new name for this file is invalid.\nTry a different name.", "Invalid name", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
						break;

					default:
						break;
				}
				refresh();
			}
		});
		filePopup.add(renameFileButton);

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
					delFileReq.put("mod_type", "DELETE");

					// Sends request to server
					String status = Communicator.communicate(delFileReq).get("status", String.class);
					switch (status) {
					// Success case
						case "OK":
							EditTabs tabs = editPane.getEditTabs();
							if (tabs == null) {
								return;
							}
							int tabCount = tabs.getTabCount();
							for (int i = 0; i < tabCount; i++) {
								UUID tabFileUUID = ((EditArea) ((JScrollPane) tabs.getComponentAt(i)).getViewport().getView()).getFileUUID();
								if (tabFileUUID.equals(selectedNode.getFileUUID())) {
									tabs.remove(i);
									break;
								}
							}
							tabs.revalidate();
							((DefaultTreeModel) getModel()).removeNodeFromParent(selectedNode);
							return;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							return;

							// Failure case
						default:
							JOptionPane.showConfirmDialog(null, "An error occurred during the deletion.\nTry refreshing the project list again.", "Deletion failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showConfirmDialog(null, "Nothing was changed", "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		filePopup.add(deleteFileButton);

		// Adds functionality when clicking
		addMouseListener(new MouseAdapter() {

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
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isAltDown() || e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode selected = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
					if (selected instanceof DirectoryNode || selected instanceof FileNode)
						EditorFileTree.this.editPane.getCollabsList().refreshUserList();
				}
			}
		});
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// nothing
			}

			@Override
			public void focusGained(FocusEvent e) {
				TreePath path = getSelectionPath();
				if (path == null)
					return;
				DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (selected instanceof DirectoryNode || selected instanceof FileNode)
					EditorFileTree.this.editPane.getCollabsList().refreshUserList();
			}
		});
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					TreePath path = getSelectionPath();
					if (path == null) {
						return;
					}
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					if (node instanceof ProjectNode) {
						EditorFileTree.this.editPane.getEditToolbar().deletProjectButtonDoClick();
					} else if (node instanceof DirectoryNode) {
						deleteDirectoryButton.doClick();
					} else if (node instanceof FileNode) {
						deleteFileButton.doClick();
					}
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
		Data fileData = Communicator.communicate(fileRequest);

		if (fileData.get("status", String.class).equals("ACCESS_DENIED"))
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);

		// Asks the server for the file's data
		Data fileContentsRequest = new Data("file_request");
		fileContentsRequest.put("file_uuid", fileToOpen);
		Data fileContents = Communicator.communicate(fileContentsRequest);

		if (fileContents.get("status", String.class).equals("ACCESS_DENIED"))
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);

		// Asks the server for the project's info
		Data permissionRequest = new Data("project_info");
		permissionRequest.put("project_uuid", projectUUID);
		Data permissions = Communicator.communicate(permissionRequest);

		// Checks tha this user has access to this project
		String permissionsStatus = permissions.get("status", String.class);
		if (permissionsStatus.equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
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
					while (CreateAccountPane.stringContains(name) || name.length() < 1) {
						name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE);
						if (name == null) {
							return;
						}
						name.trim();
					}

					// Creates request to the server for a new directory
					Data createDirReq = new Data("new_directory");
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
					} else if (response.get("status", String.class).equals("ACCESS_DENIED"))
						JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					else
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
		 * Creates a button which, when pressed, will create a new file on that directory
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
					while (CreateAccountPane.stringContains(name) || name.length() < 1) {
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

						case "INVALID_FILE_NAME":
							JOptionPane.showConfirmDialog(null, "The file name is invalid.\nThis is typically due to a conflict with another file name.\nTry a different document name.", "Document name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
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
					refresh();
					throw new UnsupportedOperationException();
				}
			});
		}
	}
}
