package editing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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
		if (confirm == JOptionPane.YES_OPTION) {
		    Data ddr = new Data("directory_modify");
		    // ddr.put("project_uuid", ((FlowProject)
		    // flowDir.getRootDirectory()).getProjectUUID());
		    DirectoryNode selectedDir = (DirectoryNode) getSelectionPath().getLastPathComponent();
		    ddr.put("directory_uuid", selectedDir.getDirectoryUUID());
		    ddr.put("parent_directory_uuid", ((DirectoryNode) selectedDir.getParent()).getDirectoryUUID());
		    ddr.put("session_id", Communicator.getSessionID());
		    ddr.put("mod_type", "DELETE");

		    String status = Communicator.communicate(ddr).get("status", String.class);
		    switch (status) {
		    case "OK":
			JOptionPane.showConfirmDialog(null, "Deletion success", "Deletion success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			break;

		    default:
			JOptionPane.showConfirmDialog(null, "Deletion failed.\nTry refreshing by Alt + clicking on the documents tree, or try again at another time.", "Failed to delete", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;
		    }
		}
		reloadProjectFiles((ProjectNode) getSelectionPath().getPath()[1]);
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
		    UUID file = selectedNode.getFileUUID();
		    Data delFileReq = new Data("file_modify");
		    // delFileReq.put("project_uuid", ((FlowProject)
		    // file.getParentDirectory().getRootDirectory()).getProjectUUID());
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
	    public void mouseClicked(MouseEvent e) {
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
	    public void mouseReleased(MouseEvent e) {
		TreePath treePath = getPathForRow(getRowForLocation(e.getX(), e.getY()));
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
			filePopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof DefaultMutableTreeNode) {
		    if (e.getButton() == MouseEvent.BUTTON3)
			workspacePopup.show(EditorDocTree.this, x, y);
		}
	    }
	});
    }

    private void openFile(UUID fileToOpen, UUID projectUUID) {
	Data fileRequest = new Data("file_info");
	fileRequest.put("file_uuid", fileToOpen);
	Data fileData = Communicator.communicate(fileRequest);

	Data documentRequest = new Data("document_request");
	documentRequest.put("session_id", Communicator.getSessionID());
	documentRequest.put("doc_uuid", fileToOpen);
	Data documentData = Communicator.communicate(documentRequest);

	switch (documentData.get("status", String.class)) {
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
		if (tabs != null)
		    tabs.openTab(fileData.get("file_name", String.class), new String(documentRequest.get("file_data", byte[].class)), projectUUID, documentRequest.get("version_uuid", UUID.class), true);
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
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new directory?", "Name", JOptionPane.QUESTION_MESSAGE).trim();
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }

		    Data createDirReq = new Data("new_directory");
		    // createDirReq.put("project_uuid", ((FlowProject)
		    // parentDir.getRootDirectory()).getProjectUUID());
		    createDirReq.put("session_id", Communicator.getSessionID());
		    // if (parentDir instanceof FlowProject)
		    // createDirReq.put("parent_directory_uuid", ((FlowProject)
		    // parentDir).getProjectUUID());
		    // else
		    // createDirReq.put("parent_directory_uuid",
		    // parentDir.getDirectoryUUID());
		    UUID parentDirUUID = ((DirectoryNode) selectedDir).getDirectoryUUID();
		    createDirReq.put("parent_directory_uuid", parentDirUUID);
		    UUID projectUUID = ((ProjectNode) ((DirectoryNode) selectedDir).getPath()[1]).getProjectUUID();
		    createDirReq.put("project_uuid", projectUUID);
		    createDirReq.put("directory_name", name);

		    Data response = Communicator.communicate(createDirReq);
		    if (response.get("status", String.class).equals("OK")) {
		    } else if (response.get("status", String.class).equals("DIRECTORY_NAME_INVALID")) {
			JOptionPane.showConfirmDialog(null, "The directory name is invalid. Try another name.\nThe most likely issue is that the name is conflicting with another name.", "Directory name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    } else
			JOptionPane.showConfirmDialog(null, "The directory was not created because of an error.\nTry refreshing by Alt + clicking the project tree, or try again some other time.", "Directory creation failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

		    reloadProjectFiles(((ProjectNode) selectedDir.getPath()[1]));
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
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new  file?\nInclude extensions such as .java", "Name", JOptionPane.QUESTION_MESSAGE).trim();
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }
		    Data createFileRequest = new Data("new_textdocument");
		    UUID projectUUID = ((ProjectNode) selectedDir.getPath()[1]).getProjectUUID();
		    createFileRequest.put("project_uuid", projectUUID);
		    // createFileRequest.put("project_uuid", ((FlowProject)
		    // getActiveDirectoryNode().getDirectoryUUID().getRootDirectory()).getProjectUUID());
		    createFileRequest.put("document_name", name);
		    createFileRequest.put("session_id", Communicator.getSessionID());
		    createFileRequest.put("directory_uuid", selectedDir.getDirectoryUUID());

		    Data reply = Communicator.communicate(createFileRequest);
		    String status = reply.get("status", String.class);
		    switch (status) {
		    case "OK":
			UUID docUUID = reply.get("file_uuid", UUID.class);
			selectedDir.add(new FileNode(docUUID));
			break;

		    case "DIRECTORY_DOES_NOT_EXIST":
			JOptionPane.showConfirmDialog(null, "The directory you are trying to create this file in does not exist.\nTry refreshing the list of projects by Alt + clicking the projects list.", "Project cannot be found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    case "DOCUMENT_NAME_INVALID":
			JOptionPane.showConfirmDialog(null, "The document name is invalid.\nThis is typically due to a conflict with another document name.\nTry a different document name.", "Document name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    default:
			break;
		    }

		    EditorDocTree.this.reloadProjectFiles((ProjectNode) selectedDir.getPath()[1]);
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