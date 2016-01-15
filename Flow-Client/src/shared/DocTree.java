package shared;

import gui.FlowClient;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import message.Data;

@SuppressWarnings("serial")
public abstract class DocTree extends JTree {

    private DefaultTreeModel model;
    private JScrollPane scrollView;

    private UUID[] usersProjectsUUIDs;

    // private UUID activeProject;
    // private DirectoryNode activeDirectoryNode;
    // private FileNode activeFileNode;

    public DocTree() {
	// TODO
	// UIManager.put("Tree.closedIcon", icon);
	// UIManager.put("Tree.openIcon", icon);
	// UIManager.put("Tree.leafIcon", icon);
	setMinimumSize(new Dimension(100, 0));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrollView = new JScrollPane(this);
	model = new DefaultTreeModel(new DefaultMutableTreeNode("Workspace"));
	setModel(model);
	getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.isAltDown())
		    refreshProjectList();
		// else if (e.isShiftDown()) {
		// DefaultMutableTreeNode root = (DefaultMutableTreeNode)
		// model.getRoot();
		// System.out.println(root);
		// root.removeAllChildren();
		// model.reload();
		// revalidate();
		// repaint();
		// }
	    }
	});
	addTreeSelectionListener(new TreeSelectionListener() {

	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();
		if (treePath == null)
		    return;

		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) treePath.getLastPathComponent();

		// if (selected instanceof ProjectNode) {
		// setActiveProject(((ProjectNode) selected).getProject());
		// setActiveDirectoryNode((ProjectNode) selected);
		// } else if (selected instanceof DirectoryNode) {
		// setActiveProject(((FlowProject) ((DirectoryNode)
		// selected).getDirectory().getRootDirectory()));
		// setActiveDirectoryNode((DirectoryNode) selected);
		// } else if (selected instanceof FileNode) {
		// FileNode fileNode = (FileNode) selected;
		// setActiveProject((FlowProject)
		// fileNode.getFile().getParentDirectory().getRootDirectory());
		// setActiveDirectoryNode((DirectoryNode) ((FileNode)
		// selected).getParent());
		// setActiveFileNode(fileNode);
		// } else {
		if (((DefaultMutableTreeNode) selected).getChildCount() == 0 && !(selected instanceof ProjectNode) && !(selected instanceof DirectoryNode) && !(selected instanceof FileNode)) {
		    refreshProjectList();
		    // }

		}
	    }
	});
    }

    public JScrollPane getScrollable() {
	return scrollView;
    }

    // public UUID getActiveProject() {
    // return activeProject;
    // }
    //
    // public void setActiveProject(UUID newActive) {
    // activeProject = newActive;
    // }
    //
    // public FileNode getActiveFileNode() {
    // return activeFileNode;
    // }
    //
    // public void setActiveFileNode(FileNode activeFileNode) {
    // this.activeFileNode = activeFileNode;
    // }
    //
    // public DirectoryNode getActiveDirectoryNode() {
    // return activeDirectoryNode;
    // }
    //
    // public void setActiveDirectoryNode(DirectoryNode activeDirectoryNode) {
    // this.activeDirectoryNode = activeDirectoryNode;
    // }

    public void refreshProjectList() {
	if (!FlowClient.NETWORK) {
	    return;
	} else {
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	    Data projectList = new Data("list_projects");
	    projectList.put("session_id", Communicator.getSessionID());
	    Data reply = Communicator.communicate(projectList);
	    usersProjectsUUIDs = reply.get("projects", UUID[].class);
	    if (usersProjectsUUIDs == null) {
		return;
	    }

	    // Adds a new project
	    for (UUID uuid : usersProjectsUUIDs) {
		boolean projectExistsLocally = false;
		for (int i = root.getChildCount() - 1; !projectExistsLocally && i >= 0; i--) {
		    if (((ProjectNode) root.getChildAt(i)).getProjectUUID().equals(uuid)) {
			projectExistsLocally = true;
		    }
		}
		if (!projectExistsLocally) {
		    createProjectNode(uuid);
		}
	    }

	    // Deletes projects that don't exist
	    for (int i = root.getChildCount() - 1; i >= 0; i--) {
		boolean projectExistsRemotely = false;
		for (int j = 0; j < usersProjectsUUIDs.length && !projectExistsRemotely; j++) {
		    if (usersProjectsUUIDs[j].equals(((ProjectNode) root.getChildAt(i)).getProjectUUID())) {
			projectExistsRemotely = true;
		    }
		}
		if (!projectExistsRemotely) {
		    model.removeNodeFromParent((MutableTreeNode) root.getChildAt(i));
		}
	    }
	    model.reload(root);
	    revalidate();
	    repaint();
	}
    }

    private void createProjectNode(UUID projectUUID) {
	if (FlowClient.NETWORK) {
	    Data fileListRequest = new Data("project_info");
	    fileListRequest.put("project_uuid", projectUUID);
	    fileListRequest.put("session_id", Communicator.getSessionID());
	    Data project = Communicator.communicate(fileListRequest);

	    ProjectNode newProjectNode = new ProjectNode(projectUUID, project.get("project_name", String.class));
	    ((DefaultMutableTreeNode) model.getRoot()).add(newProjectNode);
	    createProjectFileNodes(projectUUID, newProjectNode);
	}
    }

    private void createProjectFileNodes(UUID remoteDirUUID, DirectoryNode localDir) {
	Data dirInfoRequest = new Data("directory_info");
	dirInfoRequest.put("session_id", Communicator.getSessionID());
	dirInfoRequest.put("directory_uuid", remoteDirUUID);
	Data remoteDir = Communicator.communicate(dirInfoRequest);

	// adds folders
	UUID[] childDirs = remoteDir.get("child_directories", UUID[].class);
	if (childDirs.length > 0) {
	    System.out.println("HI");
	    for (UUID childDirUUID : childDirs) {
		Data childDirNameRequest = new Data("directory_info");
		childDirNameRequest.put("session_id", Communicator.getSessionID());
		childDirNameRequest.put("directory_uuid", childDirUUID);
		String childDirName = Communicator.communicate(childDirNameRequest).get("directory_name", String.class);

		DirectoryNode subDirNode = new DirectoryNode(childDirUUID, childDirName);
		localDir.add(subDirNode);
		createProjectFileNodes(childDirUUID, subDirNode);
	    }
	}

	// adds files
	UUID[] childFiles = remoteDir.get("child_files", UUID[].class);
	if (childFiles.length > 0) {
	    for (UUID childFileUUID : childFiles) {
		localDir.add(new FileNode(childFileUUID));
	    }
	}
    }

    public void reloadProjectFiles(ProjectNode projectNode) {
	// TODO see if you can combine these two into one loop

	// Makes an array of its children nodes
	DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[projectNode.getChildCount()];
	for (int i = 0; i < projectNode.getChildCount(); i++) {
	    children[i] = (DefaultMutableTreeNode) projectNode.getChildAt(i);
	}
	for (DefaultMutableTreeNode child : children) {
	    model.removeNodeFromParent(child);
	}
	model.reload(projectNode);

	// Gets the project from the server
	Data projectReload = new Data("directory_info");
	projectReload.put("directory_uuid", projectNode.getProjectUUID());
	Data reloadedProject = Communicator.communicate(projectReload);
	if (reloadedProject == null) {
	    JOptionPane.showConfirmDialog(null, "The project couldn't be found.\nTry refreshing the project list by Alt + clicking.", "Project retrieval error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	    return;
	}

	reloadProjectFilesRecursively(reloadedProject, projectNode);
	model.reload();
    }

    private void reloadProjectFilesRecursively(Data remoteParentDir, DirectoryNode localNode) {
	ArrayList<DirectoryNode> localDirs = new ArrayList<DirectoryNode>();
	ArrayList<FileNode> localFiles = new ArrayList<FileNode>();

	for (int i = 0; i < localNode.getChildCount(); i++) {
	    DefaultMutableTreeNode child = (DefaultMutableTreeNode) localNode.getChildAt(i);
	    if (child instanceof FileNode) {
		localFiles.add((FileNode) child);
	    } else if (child instanceof DirectoryNode) {
		localDirs.add((DirectoryNode) child);
	    }
	}

	// Removes nodes
	UUID[] remoteChildFiles = remoteParentDir.get("child_files", UUID[].class);
	for (FileNode localFileNode : localFiles) {
	    int idx = indexOfArray(remoteChildFiles, localFileNode.getFileUUID());
	    if (idx == -1) {
		model.removeNodeFromParent(localFileNode);
		localFiles.remove(localFileNode);
	    }
	}

	UUID[] remoteChildDirs = remoteParentDir.get("child_directories", UUID[].class);
	for (DirectoryNode localDirNode : localDirs) {
	    int idx = indexOfArray(remoteChildDirs, localDirNode.getDirectoryUUID());
	    if (idx == -1) {
		model.removeNodeFromParent(localDirNode);
		localDirs.remove(localDirNode);
	    }
	}

	// Adds nodes
	for (UUID remoteFile : remoteChildFiles) {
	    boolean existsLocally = false;
	    for (FileNode fileNode : localFiles) {
		if (fileNode.getFileUUID().equals(remoteFile)) {
		    existsLocally = true;
		    break;
		}
	    }
	    if (!existsLocally) {
		FileNode newFileNode = new FileNode(remoteFile);
		localNode.add(newFileNode);
	    }
	}

	for (UUID remoteDir : remoteChildDirs) {
	    boolean existsLocally = false;
	    DirectoryNode childDirNode = null;
	    for (DirectoryNode directoryNode : localDirs) {
		if (directoryNode.getDirectoryUUID().equals(directoryNode)) {
		    existsLocally = true;
		    break;
		}
	    }
	    if (!existsLocally || childDirNode == null) {
		DirectoryNode newDirNode = new DirectoryNode(remoteDir);
		localNode.add(newDirNode);
		childDirNode = newDirNode;
	    }

	    Data remoteChildDirRequest = new Data("directory_info");
	    remoteChildDirRequest.put("directory_uuid", remoteDir);
	    remoteChildDirRequest.put("session_id", Communicator.getSessionID());
	    Data remoteChildDir = Communicator.communicate(remoteChildDirRequest);

	    reloadProjectFilesRecursively(remoteChildDir, childDirNode);
	}
    }

    private int indexOfArray(Object[] array, Object target) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i].equals(target)) {
		return i;
	    }
	}
	return -1;
    }

    public class ProjectNode extends DirectoryNode {
	private UUID project;

	public ProjectNode(UUID project, String name) {
	    super(project, name);
	    this.project = project;
	}

	public UUID getProjectUUID() {
	    return project;
	}
    }

    public class DirectoryNode extends DefaultMutableTreeNode {
	// TODO each time name gets
	// updated locally OR server side, then update the name
	private UUID directory;
	private String name;

	public DirectoryNode(UUID dir, String name) {
	    this.directory = dir;
	    this.name = name;
	}

	public DirectoryNode(UUID dirUUID) {
	    this.directory = dirUUID;
	    Data requestName = new Data("directory_info");
	    requestName.put("directory_uuid", dirUUID);
	    requestName.put("session_id", Communicator.getSessionID());
	    this.name = Communicator.communicate(requestName).get("directory_name", String.class);
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public UUID getDirectoryUUID() {
	    return directory;
	}

	public String toString() {
	    return name;
	}
    }

    public class FileNode extends DefaultMutableTreeNode {
	// TODO add a listener for file renaming changes
	private UUID file;
	private String name;

	public FileNode(UUID fileUUID) {
	    this.file = fileUUID;
	    Data fileNameRequest = new Data("file_info");
	    fileNameRequest.put("file_uuid", fileUUID);
	    fileNameRequest.put("session_id", Communicator.getSessionID());
	    name = Communicator.communicate(fileNameRequest).get("file_name", String.class);
	}

	public FileNode(UUID file, String name) {
	    this.file = file;
	    this.name = name;
	}

	public UUID getFileUUID() {
	    return file;
	}

	public String toString() {
	    return name;
	}

	public String getName() {
	    return name;
	}
    }
}