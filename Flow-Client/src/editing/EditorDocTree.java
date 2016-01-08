package editing;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import message.Data;
import shared.Communicator;
import shared.DocTree;
import shared.EditTabs;
import struct.ArbitraryDocument;
import struct.FlowDocument;
import struct.FlowFile;
import struct.FlowProject;
import struct.TextDocument;

@SuppressWarnings("serial")
public class EditorDocTree extends DocTree {

    private DirectoryNode activeDirectory;
    private FileNode activeFile;
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
		// TODO send request to server to delete the activedir
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
		clipboard = activeFile.getFile();
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
		// TODO tell server to delete this file
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
		    setActiveProject(((ProjectNode) selected).getProject());
		    setActiveDirectory((ProjectNode) selected);
		    if (e.getButton() == MouseEvent.BUTTON3) {
			projectPopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof DirectoryNode) {
		    setActiveProject(((FlowProject) ((DirectoryNode) selected).getDirectory().getRootDirectory()));
		    setActiveDirectory((DirectoryNode) selected);
		    if (e.getButton() == MouseEvent.BUTTON3) {
			dirPopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof FileNode) {
		    FileNode fileNode = (FileNode) selected;
		    setActiveProject((FlowProject) fileNode.getFile().getParentDirectory().getRootDirectory());
		    setActiveDirectory((DirectoryNode) ((FileNode) selected).getParent());
		    setActiveFile(fileNode);
		    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			openFile(fileNode.getFile());
		    } else if (e.getButton() == MouseEvent.BUTTON3) {
			filePopup.show(EditorDocTree.this, x, y);
		    }
		} else if (selected instanceof DefaultMutableTreeNode && e.getButton() == MouseEvent.BUTTON3) {
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
		    // TODO add a joptionpane to ask for name
		    // TODO ask server to add a new directory here
		    EditorDocTree.this.refreshProjectList();
		}
	    });

	}
    }

    private class CreateFileOnFolderButton extends JMenuItem {
	private CreateFileOnFolderButton() {
	    super("New file");
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    // TODO ask server to add a document here
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

    private void setActiveDirectory(DirectoryNode newActive) {
	activeDirectory = newActive;
    }

    private void setActiveFile(FileNode newActive) {
	activeFile = newActive;
    }
}