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
import shared.DocTree;
import shared.EditTabs;

@SuppressWarnings("serial")
public class EditorDocTree extends DocTree {

    private EditPane editPane;
    // private FlowFile clipboard;

    JPopupMenu workspacePopup;
    JPopupMenu projectPopup;
    JPopupMenu dirPopup;
    JPopupMenu filePopup;

    public EditorDocTree(EditPane editPane) {
	super();
	this.editPane = editPane;

	workspacePopup = new JPopupMenu();
	projectPopup = new JPopupMenu();
	dirPopup = new JPopupMenu();
	filePopup = new JPopupMenu();

	// Workspace menu
	workspacePopup.add(new CreateProjectButton());

	// Projects' menu
	projectPopup.add(new CreateProjectButton());

	projectPopup.add(new CreateFolderOnFolderButton());

	JMenuItem renameProjectButton = new JMenuItem();
	renameProjectButton.setText("Rename project");
	renameProjectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		editPane.getEditToolbar().renameProjectButtonDoClick();
		EditorDocTree.this.refreshProjectList();
	    }
	});
	projectPopup.add(renameProjectButton);

	projectPopup.add(new CreateFileOnFolderButton());

	projectPopup.add(new PasteOnFolderButton());

	JMenuItem deleteProjectLabel = new JMenuItem();
	deleteProjectLabel.setText("Deleting a project should be done through\nthe button in the editor toolbar");

	// Folders' menu
	dirPopup.add(new CreateFolderOnFolderButton());

	dirPopup.add(new CreateFileOnFolderButton());

	JMenuItem deleteFolderButton = new JMenuItem();
	deleteFolderButton.setText("Delete");
	deleteFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + ((DirectoryNode) getSelectionPath().getLastPathComponent()).getName() + "?", "Confirm directory deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		ProjectNode projectNode = null;
		if (confirm == JOptionPane.YES_OPTION) {
		    Data ddr = new Data("directory_modify");
		    DirectoryNode selectedDir = (DirectoryNode) getSelectionPath().getLastPathComponent();
		    projectNode = (ProjectNode) selectedDir.getPath()[1];
		    ddr.put("directory_uuid", selectedDir.getDirectoryUUID());
		    ddr.put("session_id", Communicator.getSessionID());
		    ddr.put("mod_type", "DELETE");

		    String status = Communicator.communicate(ddr).get("status", String.class);
		    switch (status) {
		    case "OK":
			((DefaultTreeModel) getModel()).removeNodeFromParent(selectedDir);
			break;

		    default:
			JOptionPane.showConfirmDialog(null, "Deletion failed.\nTry refreshing by Alt + clicking on the documents tree, or try again at another time.", "Failed to delete", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;
		    }
		}
		if (projectNode == null)
		    reloadProjectFiles(projectNode);
	    }
	});
	dirPopup.add(deleteFolderButton);

	dirPopup.add(new PasteOnFolderButton());

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

	JMenuItem pasteFileButton = new JMenuItem();
	pasteFileButton.setText("Paste");
	pasteFileButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO tell server to add new file in this directory
		EditorDocTree.this.refreshProjectList();
	    }
	});

	JMenuItem deleteFileButton = new JMenuItem();
	deleteFileButton.setText("Delete");
	deleteFileButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		FileNode selectedNode = (FileNode) getSelectionPath().getLastPathComponent();
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + selectedNode.getName() + "?", "Deletion confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
		    Data delFileReq = new Data("file_metadata_modify");
		    delFileReq.put("file_uuid", selectedNode.getFileUUID());
		    delFileReq.put("session_id", Communicator.getSessionID());
		    delFileReq.put("mod_type", "DELETE");

		    String status = Communicator.communicate(delFileReq).get("status", String.class);
		    switch (status) {
		    case "OK":
			break;

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

	    @Override
	    public void mouseClicked(MouseEvent e) {
		TreePath treePath = getSelectionPath();
		if (treePath == null) {
		    return;
		}
		Object selected = treePath.getLastPathComponent();
		int x = e.getX();
		int y = e.getY();

		if (selected instanceof ProjectNode) {
		    if (e.getButton() == MouseEvent.BUTTON3) {
			projectPopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof DirectoryNode) {
		    if (e.getButton() == MouseEvent.BUTTON3) {
			dirPopup.show(EditorDocTree.this, x, y);
		    }
		    if (e.isShiftDown()) {
			reloadProjectFiles((ProjectNode) ((DirectoryNode) selected).getPath()[1]);
		    }
		} else if (selected instanceof FileNode) {
		    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			openFile(((FileNode) selected).getFileUUID(), ((ProjectNode) ((FileNode) selected).getPath()[1]).getProjectUUID());
		    } else if (e.getButton() == MouseEvent.BUTTON3) {
			// TODO re-enable when it's done
			// filePopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof DefaultMutableTreeNode) {
		    if (e.getButton() == MouseEvent.BUTTON3)
			workspacePopup.show(EditorDocTree.this, x, y);
		}
	    }
	});
	addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent e) {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
		if (selected instanceof FileNode && e.getKeyChar() == KeyEvent.VK_ENTER) {
		    openFile(((FileNode) selected).getFileUUID(), ((ProjectNode) ((FileNode) selected).getPath()[1]).getProjectUUID());
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
	addTreeSelectionListener(new TreeSelectionListener() {

	    @Override
	    public void valueChanged(TreeSelectionEvent selected) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) (selected.getPath().getLastPathComponent());
		if (node.getPath().length > 1) {
		    editPane.getCollabsList().refreshUserList();
		}
	    }
	});
    }

    private void openFile(UUID fileToOpen, UUID projectUUID) {
	Data fileRequest = new Data("file_info");
	fileRequest.put("file_uuid", fileToOpen);
	fileRequest.put("session_id", Communicator.getSessionID());
	Data fileData = Communicator.communicate(fileRequest);

	Data fileContentsRequest = new Data("file_request");
	fileContentsRequest.put("session_id", Communicator.getSessionID());
	fileContentsRequest.put("file_uuid", fileToOpen);
	Data fileContents = Communicator.communicate(fileContentsRequest);

	Data permissionRequest = new Data("project_info");
	permissionRequest.put("session_id", Communicator.getSessionID());
	permissionRequest.put("project_uuid", projectUUID);
	Data permissions = Communicator.communicate(permissionRequest);

	String permissionsStatus = permissions.get("status", String.class);
	if (permissionsStatus.equals("ACCESS_DENIED")) {
	    return;
	}

	String[] editors = permissions.get("editors", String[].class);
	String owner = permissions.get("owner", String.class);

	boolean canEdit = false;
	if (Communicator.getUsername().equals(owner)) {
	    canEdit = true;
	} else if (Arrays.asList(editors).contains(Communicator.getUsername())) {
	    canEdit = true;
	}

	String status = fileContents.get("status", String.class);
	switch (status) {
	case "OK":
	    String type = fileData.get("file_type", String.class);
	    if (type.contains("ARBIT")) {
		// try {
		// //TODO write byte array to local directory
		// Desktop.getDesktop().open();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
	    } else if (type.contains("TEXT")) {
		EditTabs tabs = editPane.getEditTabs();
		if (tabs != null) {
		    String fileName = fileData.get("file_name", String.class);
		    byte[] bytes = fileContents.get("file_data", byte[].class);
		    String fileContentsString = new String(bytes);
		    UUID versionUUID = fileContents.get("version_uuid", UUID.class);
		    tabs.openTab(fileName, fileContentsString, projectUUID, fileToOpen, versionUUID, canEdit);
		}
	    }
	    break;
	case "PROJECT_NOT_FOUND":
	    JOptionPane.showConfirmDialog(null, "The project that this file is in cannot be found for some reason.\n" + "Try refreshing the list of projects (move the mouse cursor into the console and back here)" + "\nand see if it is resolved.", "Project not found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	    return;
	case "FILE_NOT_FOUND":
	    JOptionPane.showConfirmDialog(null, "The file you are trying to open cannot be found.\n" + "Try refreshing the list of projects/files (move the mouse cursor into the console and back here)" + "\nand see if it is resolved.", "Project not found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	    return;
	}
    }

    private class CreateProjectButton extends JMenuItem {
	private CreateProjectButton() {
	    super("New project");
	    addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    editPane.getEditToolbar().createProjectButtonDoClick();
		    EditorDocTree.this.refreshProjectList();
		}
	    });
	}

    }

    private class CreateFolderOnFolderButton extends JMenuItem {
	private CreateFolderOnFolderButton() {
	    super("New directory");
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    TreePath selectionPath = getSelectionPath();
		    if (selectionPath == null) {
			JOptionPane.showConfirmDialog(null, "Please select a directory to put your new directory in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    DirectoryNode selectedDir = (DirectoryNode) selectionPath.getLastPathComponent();
		    if (selectedDir == null) {
			JOptionPane.showConfirmDialog(null, "Please select a directory to put your new directory in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new directory?", "Name", JOptionPane.QUESTION_MESSAGE);
		    if (name == null) {
			return;
		    }
		    name = name.trim();
		    if (name.length() == 0) {
			return;
		    }
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }

		    Data createDirReq = new Data("new_directory");
		    createDirReq.put("session_id", Communicator.getSessionID());
		    UUID parentDirUUID = selectedDir.getDirectoryUUID();
		    createDirReq.put("parent_directory_uuid", parentDirUUID);
		    UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
		    createDirReq.put("project_uuid", projectUUID);
		    createDirReq.put("directory_name", name);

		    Data response = Communicator.communicate(createDirReq);
		    if (response.get("status", String.class).equals("OK")) {
			UUID newDirUUID = response.get("directory_uuid", UUID.class);
			((DefaultTreeModel) EditorDocTree.this.getModel()).insertNodeInto(new DirectoryNode(newDirUUID), selectedDir, selectedDir.getChildCount());
		    } else if (response.get("status", String.class).equals("DIRECTORY_NAME_INVALID")) {
			JOptionPane.showConfirmDialog(null, "The directory name is invalid. Try another name.\nThe most likely issue is that the name is conflicting with another name.", "Directory name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    } else
			JOptionPane.showConfirmDialog(null, "The directory was not created because of an error.\nTry refreshing by Alt + clicking the project tree, or try again some other time.", "Directory creation failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		    expandRow(getRowForPath(new TreePath(selectedDir.getPath())));
		}
	    });

	}
    }

    private class CreateFileOnFolderButton extends JMenuItem {
	private CreateFileOnFolderButton() {
	    super("New (source code) file");
	    addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		    DirectoryNode selectedDir = (DirectoryNode) getSelectionPath().getLastPathComponent();
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new  file?\nInclude extensions such as .java", "Name", JOptionPane.QUESTION_MESSAGE);
		    if (name == null) {
			return;
		    }
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.trim().length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }
		    name = name.trim();
		    Data createFileRequest = new Data("new_text_file");
		    UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
		    createFileRequest.put("project_uuid", projectUUID);
		    createFileRequest.put("file_name", name);
		    createFileRequest.put("session_id", Communicator.getSessionID());
		    createFileRequest.put("directory_uuid", selectedDir.getDirectoryUUID());

		    Data reply = Communicator.communicate(createFileRequest);
		    String status = reply.get("status", String.class);
		    switch (status) {
		    case "OK":
			UUID fileUUID = reply.get("file_uuid", UUID.class);
			((DefaultTreeModel) EditorDocTree.this.getModel()).insertNodeInto(new FileNode(fileUUID), selectedDir, selectedDir.getChildCount());
			break;

		    case "DIRECTORY_DOES_NOT_EXIST":
			JOptionPane.showConfirmDialog(null, "The directory you are trying to create this file in does not exist.\nTry refreshing the list of projects by Alt + clicking the projects list.", "Project cannot be found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    case "DOCUMENT_NAME_INVALID":
			JOptionPane.showConfirmDialog(null, "The file name is invalid.\nThis is typically due to a conflict with another file name.\nTry a different document name.", "Document name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    default:
			break;
		    }
		    expandRow(getRowForPath(new TreePath(selectedDir.getPath())));
		}
	    });

	}
    }

    private class PasteOnFolderButton extends JMenuItem {
	private PasteOnFolderButton() {
	    super("Paste");
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO authorize with server to put these files here
		    EditorDocTree.this.refreshProjectList();
		}
	    });
	}
    }
}