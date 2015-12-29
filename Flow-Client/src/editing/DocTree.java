package editing;

import gui.Communicator;
import gui.FlowClient;
import history.VersionViewer;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import message.Data;
import struct.ArbitraryDocument;
import struct.FlowDirectory;
import struct.FlowDocument;
import struct.FlowFile;
import struct.FlowProject;
import struct.TextDocument;

public class DocTree extends JTree {

    private static DefaultTreeModel model;
    private JScrollPane scrollView;
    private EditTabs editTabs;
    private VersionViewer versionViewer;
    private JPopupMenu projectPopup;
    private JPopupMenu folderPopup;
    private JPopupMenu filePopup;

    private static UUID[] usersProjectsUUIDs;
    private static FlowProject activeProject;

    public DocTree(EditTabs editTabs, EditorToolbar eToolbar) {
	this.editTabs = editTabs;
	init(eToolbar);
    }

    public DocTree(VersionViewer versionViewer) {
	init(null);
	this.versionViewer = versionViewer;
    }

    private void init(EditorToolbar eToolbar) {
	// UIManager.put("Tree.closedIcon", icon);
	// UIManager.put("Tree.openIcon", icon);
	// UIManager.put("Tree.leafIcon", icon);
	setMinimumSize(new Dimension(100, 0));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrollView = new JScrollPane(this);
	model = new DefaultTreeModel(new DefaultMutableTreeNode("Workspace"));
	setModel(model);
	getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);

	projectPopup = new JPopupMenu();
	folderPopup = new JPopupMenu();
	filePopup = new JPopupMenu();

	addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseEntered(MouseEvent arg0) {
		updateProjectList();
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
		Object selected = getPathForRow(
			getRowForLocation(e.getX(), e.getY()))
			.getLastPathComponent();
		if (e.getButton() == MouseEvent.BUTTON3) {
		    if (selected instanceof ProjectNode) {
			projectPopup.removeAll();
			if (editTabs != null) {
			    JMenuItem newProjectButton = new JMenuItem();
			    newProjectButton.setText("New project");
			    newProjectButton
				    .addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(
						ActionEvent e) {
					    eToolbar.createProjectButtonDoClick();
					}
				    });
			    projectPopup.add(newProjectButton);
			}
		    } else if (selected instanceof FolderNode) {

		    } else if (selected instanceof FileNode) {

		    }
		    // TODO right click menus: project (new project-new
		    // folder-properties), folders
		    // (new-copy-cut-paste-delete), files
		    // (new-copy-cut-paste-rename-delete-properties)
		} else if (e.getButton() == MouseEvent.BUTTON1) {
		    if (selected instanceof FileNode) {
			FileNode fileNode = (FileNode) selected;
			activeProject = (FlowProject) fileNode.getFile()
				.getParentDirectory().getRootDirectory();
			if (e.getClickCount() == 2) {
			    openFile(fileNode.getFile());
			}
		    } else if (selected instanceof ProjectNode) {
			activeProject = ((ProjectNode) selected).getProject();
		    } else if (selected instanceof FolderNode) {
			activeProject = (FlowProject) ((FolderNode) selected)
				.getFolder().getRootDirectory();
		    }
		}
	    }
	});
	updateProjectList();
    }

    public JScrollPane getScrollable() {
	return scrollView;
    }

    public static FlowProject getActiveProject() {
	return activeProject;
    }

    protected void updateProjectList() {
	if (FlowClient.NETWORK) {
	    usersProjectsUUIDs = Communicator.communicate(
		    new Data("list_projects")).get("projects", UUID[].class);
	}

	DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	// Adds a new project
	for (UUID uuid : usersProjectsUUIDs) {
	    boolean projectExistsLocally = false;
	    for (int i = root.getChildCount() - 1; !projectExistsLocally
		    && i >= 0; i--) {
		if (((ProjectNode) root.getChildAt(i)).getProject()
			.getProjectUUID().equals(uuid)) {
		    projectExistsLocally = true;
		}
	    }
	    if (!projectExistsLocally) {
		createProjectNode(uuid);
	    }
	}

	// Deletes projects that don't exist
	for (int i = root.getChildCount() - 1; i >= 0; i--) {
	    boolean projectStillExists = false;
	    for (int j = 0; j < usersProjectsUUIDs.length
		    && !projectStillExists; j++) {
		if (usersProjectsUUIDs[j].equals(((ProjectNode) root
			.getChildAt(i)).getProject().getProjectUUID())) {
		    projectStillExists = true;
		}
	    }
	    if (!projectStillExists) {
		root.remove(i);
	    }
	}
    }

    private void createProjectNode(UUID projectUUID) {
	if (FlowClient.NETWORK) {
	    Data fileListRequest = new Data("request_project");
	    fileListRequest.put("project_uuid", projectUUID);
	    fileListRequest.put("session_id", Communicator.getSessionID());
	    FlowProject project = Communicator.communicate(fileListRequest)
		    .get("project", FlowProject.class);

	    ProjectNode newProjectNode = new ProjectNode(project);
	    ((DefaultMutableTreeNode) model.getRoot()).add(newProjectNode);
	    loadProjectFiles(project, newProjectNode);
	}
    }

    private void loadProjectFiles(FlowDirectory fDir, FolderNode dir) {
	// adds folders
	if (!fDir.getDirectories().isEmpty()) {
	    for (FlowDirectory subDir : fDir.getDirectories()) {
		FolderNode subDirNode = new FolderNode(subDir);
		loadProjectFiles(subDir, subDirNode);
	    }
	}

	// adds files
	if (!fDir.getFiles().isEmpty()) {
	    for (FlowFile file : fDir.getFiles()) {
		dir.add(new FileNode(file));
	    }
	}
    }

    private void openFile(FlowFile fileToOpen) {
	if (FlowClient.NETWORK) {
	    if (versionViewer != null) {
		versionViewer.setFile(fileToOpen);
	    } else if (editTabs != null) {
		UUID projectUUID = ((FlowProject) fileToOpen
			.getParentDirectory().getRootDirectory())
			.getProjectUUID();
		Data checksumRequest = new Data("file_checksum");
		checksumRequest.put("project_uuid", projectUUID);
		checksumRequest.put("file_uuid", fileToOpen.getFileUUID());
		Data csReply = Communicator.communicate(checksumRequest);
		if (csReply.get("status", String.class).equals("OK")) {
		    // Get the current file checksum, and compare. If it's the
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
		fileRequest.put("file_uuid", fileToOpen.getFileUUID());
		Data reply = Communicator.communicate(fileRequest);
		switch (reply.get("status", String.class)) {
		case "OK":
		    FlowDocument document = reply.get("document",
			    FlowDocument.class);
		    if (document instanceof ArbitraryDocument) {
			try {
			    Desktop.getDesktop().open(
				    ((ArbitraryDocument) document)
					    .getLocalFile());
			} catch (IOException e1) {
			    e1.printStackTrace();
			}
		    } else if (document instanceof TextDocument) {
			editTabs.openTab((TextDocument) document, true);
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
				    "The file you are trying to open.\n"
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

    private class ProjectNode extends FolderNode {
	private FlowProject project;

	public ProjectNode(FlowProject project) {
	    super(project);
	    this.project = project;
	}

	public FlowProject getProject() {
	    return project;
	}
    }

    private class FolderNode extends DefaultMutableTreeNode {
	private FlowDirectory folder;

	private FolderNode(FlowDirectory folder) {
	    super(folder.toString());
	    this.folder = folder;
	}

	private FlowDirectory getFolder() {
	    return folder;
	}
    }

    private class FileNode extends DefaultMutableTreeNode {
	private FlowFile file;

	public FileNode(FlowFile file) {
	    this.file = file;
	}

	public FlowFile getFile() {
	    return file;
	}
    }
}