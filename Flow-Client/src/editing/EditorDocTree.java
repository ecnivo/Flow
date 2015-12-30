package editing;

import gui.FlowClient;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

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

    private EditPane editPane;

    private JPopupMenu projectPopup;
    private JPopupMenu folderPopup;
    private JPopupMenu filePopup;

    public EditorDocTree(EditPane editPane) {
	super();
	this.editPane = editPane;

	projectPopup = new JPopupMenu();
	folderPopup = new JPopupMenu();
	filePopup = new JPopupMenu();

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		Object selected = getPathForRow(
			getRowForLocation(e.getX(), e.getY()))
			.getLastPathComponent();
		if (e.getButton() == MouseEvent.BUTTON3) {
		    if (selected instanceof ProjectNode) {
			projectPopup.removeAll();
			JMenuItem newProjectButton = new JMenuItem();
			newProjectButton.setText("New project");
			newProjectButton
				.addActionListener(new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
					editPane.getEditToolbar()
						.createProjectButtonDoClick();
				    }

				});
			projectPopup.add(newProjectButton);

		    } else if (selected instanceof DirectoryNode) {

		    } else if (selected instanceof FileNode) {

		    }
		    // TODO right click menus: project (new project-new
		    // folder-properties), folders
		    // (new-copy-cut-paste-delete), files
		    // (new-copy-cut-paste-rename-delete-properties)
		} else if (e.getButton() == MouseEvent.BUTTON1) {
		    if (selected instanceof FileNode) {
			FileNode fileNode = (FileNode) selected;
			setActiveProject((FlowProject) fileNode.getFile()
				.getParentDirectory().getRootDirectory());
			if (e.getClickCount() == 2) {
			    openFile(fileNode.getFile());
			}
		    } else if (selected instanceof ProjectNode) {
			setActiveProject(((ProjectNode) selected).getProject());
		    } else if (selected instanceof DirectoryNode) {
			setActiveProject((FlowProject) ((DirectoryNode) selected)
				.getDirectory().getRootDirectory());
		    }
		}
	    }
	});
    }

    private void openFile(FlowFile fileToOpen) {
	if (FlowClient.NETWORK) {
	    UUID projectUUID = ((FlowProject) fileToOpen.getParentDirectory()
		    .getRootDirectory()).getProjectUUID();
	    Data checksumRequest = new Data("file_checksum");
	    checksumRequest.put("project_uuid", projectUUID);
	    checksumRequest.put("file_uuid", fileToOpen.getFileUUID());
	    Data csReply = Communicator.communicate(checksumRequest);
	    if (csReply.get("status", String.class).equals("OK")) {
		// TODO Get the current file checksum, and compare. If it's the
		// same,
		// then open this file, if it's not, then skip ahead to the
		// already-made block. https://goo.gl/vWWtSD
	    } else {
		JOptionPane
			.showConfirmDialog(
				null,
				"The project that this file is in cannot be found for some reason.\n"
					+ "Try refreshing the list of projects (move the mouse cursor into the console and back here)"
					+ "\nand see if it is resolved.",
				"Project not found",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE);
		return;
	    }

	    Data fileRequest = new Data("file_request");
	    fileRequest.put("project_uuid", projectUUID);
	    fileRequest.put("doc_uuid", fileToOpen.latest().getUUID());
	    Data reply = Communicator.communicate(fileRequest);
	    switch (reply.get("status", String.class)) {
	    case "OK":
		FlowDocument document = reply.get("document",
			FlowDocument.class);
		if (document instanceof ArbitraryDocument) {
		    try {
			Desktop.getDesktop().open(
				((ArbitraryDocument) document).getLocalFile());
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
		JOptionPane
			.showConfirmDialog(
				null,
				"The project that this file is in cannot be found for some reason.\n"
					+ "Try refreshing the list of projects (move the mouse cursor into the console and back here)"
					+ "\nand see if it is resolved.",
				"Project not found",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE);
		return;
	    case "FILE_NOT_FOUND":
		JOptionPane
			.showConfirmDialog(
				null,
				"The file you are trying to open cannot be found.\n"
					+ "Try refreshing the list of projects/files (move the mouse cursor into the console and back here)"
					+ "\nand see if it is resolved.",
				"Project not found",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE);
		return;
	    }
	}
    }
}