package editing;

import gui.FlowClient;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
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
import struct.ArbitraryDocument;
import struct.FlowDirectory;
import struct.FlowDocument;
import struct.FlowFile;
import struct.FlowProject;
import struct.TextDocument;

@SuppressWarnings("serial")
public class EditorDocTree extends DocTree {

    private EditPane editPane;
    private FlowFile clipboard;

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
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + getActiveDirectoryNode().toString() + "?", "Confirm directory deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
		    FlowDirectory flowDir = getActiveDirectoryNode().getDirectory();
		    Data ddr = new Data("directory_modify");
		    ddr.put("project_uuid", ((FlowProject) flowDir.getRootDirectory()).getProjectUUID());
		    ddr.put("directory_uuid", flowDir.getDirectoryUUID());
		    ddr.put("parent_directory_uuid", flowDir.getParent().getDirectoryUUID());
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
		EditorDocTree.this.refreshProjectList();
	    }
	});
	dirPopup.add(deleteFolderButton);

	dirPopup.add(new PasteOnFolderButton());

	// Files' menu
	JMenuItem copyFileButton = new JMenuItem();
	copyFileButton.setText("Copy");
	copyFileButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		clipboard = getActiveFileNode().getFile();
	    }
	});

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
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + getActiveFileNode().getFile().toString() + "?", "Deletion confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
		    FlowFile file = getActiveFileNode().getFile();
		    Data delFileReq = new Data("file_modify");
		    delFileReq.put("project_uuid", ((FlowProject) file.getParentDirectory().getRootDirectory()).getProjectUUID());
		    delFileReq.put("file_uuid", file.getFileUUID());
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
		EditorDocTree.this.refreshProjectList();
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
		} else if (selected instanceof FileNode) {
		    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			openFile(((FileNode) selected).getFile());
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

    private void openFile(FlowFile fileToOpen) {
	if (FlowClient.NETWORK) {
	    UUID projectUUID = ((FlowProject) fileToOpen.getParentDirectory().getRootDirectory()).getProjectUUID();
	    Data checksumRequest = new Data("file_checksum");
	    checksumRequest.put("project_uuid", projectUUID);
	    checksumRequest.put("file_uuid", fileToOpen.getFileUUID());
	    Data csReply = Communicator.communicate(checksumRequest);
	    if (csReply.get("status", String.class).equals("OK")) {
		// TODO Get the current file checksum, and compare. If it's the
		// same,
		// then open this file, if it's not, then skip ahead to the
		// already-made block.
	    } else {
		JOptionPane.showConfirmDialog(null, "The project that this file is in cannot be found for some reason.\n" + "Try refreshing the list of projects (move the mouse cursor into the console and back here)" + "\nand see if it is resolved.", "Project not found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		return;
	    }

	    Data fileRequest = new Data("file_request");
	    fileRequest.put("project_uuid", projectUUID);
	    fileRequest.put("doc_uuid", fileToOpen.latest().getUUID());
	    Data reply = Communicator.communicate(fileRequest);
	    switch (reply.get("status", String.class)) {
	    case "OK":
		FlowDocument document = reply.get("document", FlowDocument.class);
		if (document instanceof ArbitraryDocument) {
		    try {
			Desktop.getDesktop().open(((ArbitraryDocument) document).getLocalFile());
		    } catch (IOException e1) {
			e1.printStackTrace();
		    }
		} else if (document instanceof TextDocument) {
		    EditTabs tabs = editPane.getEditTabs();
		    if (tabs != null)
			tabs.openTab((TextDocument) document, true);
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
	    super("New folder");
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    if (getActiveDirectoryNode() == null) {
			JOptionPane.showConfirmDialog(null, "Please select a directory to put your new directory in first", "No selected directory", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new  directory?", "Name", JOptionPane.QUESTION_MESSAGE).trim();
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }

		    FlowDirectory parent = getActiveDirectoryNode().getDirectory();
		    System.out.println(parent);
		    Data createDirReq = new Data("new_directory");
		    createDirReq.put("project_uuid", ((FlowProject) parent.getRootDirectory()).getProjectUUID());
		    createDirReq.put("session_id", Communicator.getSessionID());
		    if (parent instanceof FlowProject)
			createDirReq.put("parent_directory_uuid", ((FlowProject) parent).getProjectUUID());
		    else
			createDirReq.put("parent_directory_uuid", parent.getDirectoryUUID());
		    createDirReq.put("name", name);

		    Data response = Communicator.communicate(createDirReq);
		    if (response.get("status", String.class).equals("OK")) {
			reloadProjectFiles(((ProjectNode) getActiveDirectoryNode().getPath()[1]));
		    } else if (response.get("status", String.class).equals("DIRECTORY_NAME_INVALID")) {
			JOptionPane.showConfirmDialog(null, "The directory name is invalid. Try another name.\nThe most likely issue is that the name is conflicting with another name.", "Directory name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    } else
			JOptionPane.showConfirmDialog(null, "The directory was not created because of an error.\nTry refreshing by Alt + clicking the project tree, or try again some other time.", "Directory creation failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
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
		    String name = JOptionPane.showInputDialog(null, "What is the name of your new  file?\nInclude extensions such as .java", "Name", JOptionPane.QUESTION_MESSAGE).trim();
		    while (CreateAccountPane.stringContains(name, CreateAccountPane.INVALID_CHARS) || name.length() < 1) {
			name = JOptionPane.showInputDialog(null, "That name is invalid.\nPlease enter an appropriate new name for this directory.\nNo characters such as: \\ / ? % * : | " + "\" < > . # & { } $ @ = ` + ", "Invalid name", JOptionPane.ERROR_MESSAGE).trim();
		    }
		    Data createFileRequest = new Data("new_textdocument");
		    createFileRequest.put("project_uuid", ((FlowProject) getActiveDirectoryNode().getDirectory().getRootDirectory()).getProjectUUID());
		    createFileRequest.put("document_name", name);
		    createFileRequest.put("session_id", Communicator.getSessionID());
		    createFileRequest.put("directory_uuid", getActiveDirectoryNode().getDirectory().getDirectoryUUID());

		    Data reply = Communicator.communicate(createFileRequest);
		    String status = reply.get("status", String.class);
		    switch (status) {
		    case "OK":
			FlowFile doc = reply.get("file", FlowFile.class);
			((DefaultTreeModel) EditorDocTree.this.getModel()).insertNodeInto(new FileNode(doc), getActiveDirectoryNode(), getActiveDirectoryNode().getChildCount());
			break;

		    case "DIRECTORY_DOES_NOT_EXIST":
			JOptionPane.showConfirmDialog(null, "The directory you are trying to create this file in does not exist.\nTry refreshing the list of projects by Alt+clicking the projects list.", "Project cannot be found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    case "DOCUMENT_NAME_INVALID":
			JOptionPane.showConfirmDialog(null, "The document name is invalid.\nThis is typically due to a conflict with another document name.\nTry a different document name.", "Document name invalid", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			break;

		    default:
			break;
		    }

		    EditorDocTree.this.refreshProjectList();
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