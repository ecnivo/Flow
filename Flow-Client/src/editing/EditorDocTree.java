package editing;

import gui.FlowClient;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

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

    public EditorDocTree(EditPane editPane) {
	super();
	this.editPane = editPane;

	JPopupMenu workspacePopup = new JPopupMenu();
	JPopupMenu projectPopup = new JPopupMenu();
	JPopupMenu dirPopup = new JPopupMenu();
	JPopupMenu filePopup = new JPopupMenu();

	// Workspace menu
	JMenuItem createProjectButton = new JMenuItem();
	createProjectButton.setText("New project");
	createProjectButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		editPane.getEditToolbar().createProjectButtonDoClick();
		EditorDocTree.this.refreshProjectList();
	    }

	});
	workspacePopup.add(createProjectButton);

	// Projects' menu
	projectPopup.add(createProjectButton);

	JMenuItem createFolderOnFolderButton = new JMenuItem();
	createFolderOnFolderButton.setText("New folder");
	createFolderOnFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		// TODO ask server to add a new directory here
		EditorDocTree.this.refreshProjectList();
	    }
	});
	projectPopup.add(createFolderOnFolderButton);

	JMenuItem createFileOnFolderButton = new JMenuItem();
	createFileOnFolderButton.setText("New file");
	createFileOnFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO ask server to add a document here
		EditorDocTree.this.refreshProjectList();
	    }
	});

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

	JMenuItem deleteProjectLabel = new JMenuItem();
	deleteProjectLabel.setText("Deleting a project should be done through\nthe button in the editor toolbar");

	// Folders' menu
	dirPopup.add(createFolderOnFolderButton);

	dirPopup.add(createFileOnFolderButton);

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

	JMenuItem pasteOnFolderButton = new JMenuItem();
	pasteOnFolderButton.setText("Paste");
	pasteOnFolderButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		// TODO authorize with server to put these files here
		EditorDocTree.this.refreshProjectList();
	    }
	});
	dirPopup.add(pasteOnFolderButton);
	
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
		//TODO tell server to delete this  file
		EditorDocTree.this.refreshProjectList();
	    }
	});

	// TODO right click menus: project (new project-new
	// folder-properties), folders
	// (new-copy-cut-paste-rename-delete), files
	// (copy-cut-paste-rename-delete-properties)

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		Object selected = getPathForRow(getRowForLocation(e.getX(), e.getY())).getLastPathComponent();
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
		} else if (selected instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) selected).equals("Workspace")) {
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

    private void setActiveDirectory(DirectoryNode newActive) {
	activeDirectory = newActive;
    }
    
    private void setActiveFile(FileNode newActive){
	activeFile = newActive;
    }
}