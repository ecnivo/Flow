package editing;

import gui.Communicator;
import gui.FlowClient;
import history.VersionViewer;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import message.Data;
import struct.ArbitraryDocument;
import struct.FlowDocument;
import struct.FlowFile;
import struct.FlowProject;
import struct.User;

public class DocTree extends JTree {

    private DocTreeModel model;
    private JScrollPane scrollView;
    private EditTabs editTabs;
    private VersionViewer versionViewer;

    private FlowProject[] usersProjects;
    private static FlowProject activeProject;

    public DocTree(EditTabs editTabs) {
	// TODO right click menus: projects (properties), folders
	// (new-copy-cut-paste-delete), files
	// (new-copy-cut-paste-delete-properties)
	this.editTabs = editTabs;
	init();
    }

    public DocTree(VersionViewer versionViewer) {
	init();
	this.versionViewer = versionViewer;
    }

    private void init() {
	// UIManager.put("Tree.closedIcon", icon);
	// UIManager.put("Tree.openIcon", icon);
	// UIManager.put("Tree.leafIcon", icon);
	setMinimumSize(new Dimension(100, 0));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrollView = new JScrollPane(this);
	model = new DocTreeModel();
	setModel(model);
	getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	addTreeSelectionListener(new TreeSelectionListener() {

	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) DocTree.this
			.getSelectionPath().getLastPathComponent();
		if (node instanceof FileNode || node instanceof FolderNode) {

		} else if (node instanceof ProjectNode) {
		    activeProject = ((ProjectNode) node).getProject();
		}
	    }
	});
	addMouseListener(new MouseListener() {

	    @Override
	    public void mouseReleased(MouseEvent arg0) {
		// nothing
	    }

	    @Override
	    public void mousePressed(MouseEvent arg0) {
		// nothing
	    }

	    @Override
	    public void mouseExited(MouseEvent arg0) {
		// nothing
	    }

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
		    // TODO right click menu
		} else if (e.getButton() == MouseEvent.BUTTON1) {
		    if (selected instanceof FileNode && e.getClickCount() == 2) {
			if (FlowClient.NETWORK) {
			    FlowFile fileToOpen = ((FileNode) selected)
				    .getFile();

			    Data fileRequest = new Data("file_request");
			    fileRequest.put("project_uuid",
				    activeProject.getProjectUUID());
			    fileRequest.put("file_uuid",
				    fileToOpen.getFileUUID());
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
				} else {
				    if (editTabs != null) {
					editTabs.openTab(document, true);
				    } else if (versionViewer != null) {
					versionViewer.setFile(document);
				    }
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
		    } else if (selected instanceof ProjectNode) {
			ProjectNode projectNode = (ProjectNode) selected;
			activeProject = projectNode.getProject();
			loadProjectFiles(projectNode);
			System.out.println("loading project files");
		    }
		}
	    }
	});
	updateProjectList();
    }

    private class DocTreeModel extends DefaultTreeModel {
	private DocTreeModel() {
	    super(new DefaultMutableTreeNode("Workspace"));
	}
    }

    public JScrollPane getScrollable() {
	return scrollView;
    }

    private void updateProjectList() {
	if (FlowClient.NETWORK) {
	    usersProjects = Communicator.communicate(new Data("list_projects"))
		    .get("projects", FlowProject[].class);
	}

	DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	root.removeAllChildren();
	// model.reload();

	FlowProject example = new FlowProject("1234", new User("abc"));
	FlowProject example2 = new FlowProject("12345678", new User("abc"));
	usersProjects = new FlowProject[2];
	usersProjects[0] = example;
	usersProjects[1] = example2;

	for (int i = 0; i < usersProjects.length; i++) {
	    root.add(new ProjectNode(usersProjects[i], i));
	}
    }

    private void loadProjectFiles(ProjectNode projectNode) {
	if (FlowClient.NETWORK) {
	    Data fileListRequest = new Data("list_project_files");
	    fileListRequest.put("project_uuid", projectNode.getProject()
		    .getProjectUUID());
	    fileListRequest.put("session_id", Communicator.getSessionID());
	    FlowFile[] projectFiles = Communicator.communicate(fileListRequest)
		    .get("files", FlowFile[].class);

	    for (FlowFile flowFile : projectFiles) {
		String[] path = flowFile.getRemotePath().split("\\");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) model
			.getRoot();
		for (int i = 1; i < path.length - 1; i++) {
		    FolderNode compare = new FolderNode(path[i]);
		    if (node.getIndex(compare) == -1) {
			node.add(compare);
		    }
		}
		node.add(new FileNode(flowFile));
	    }
	}
    }

    public static FlowProject getActiveProject() {
	return activeProject;
    }

    private class ProjectNode extends DefaultMutableTreeNode {
	private FlowProject project;
	private int index;

	public ProjectNode(FlowProject project, int index) {
	    super(project);
	    this.project = project;
	    this.index = index;
	}

	public FlowProject getProject() {
	    return project;
	}

	public int getTreeIndex() {
	    return index;
	}
    }

    private class FolderNode extends DefaultMutableTreeNode {
	public FolderNode(String folderName) {
	    super(folderName);

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