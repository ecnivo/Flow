package shared;

import gui.FlowClient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import message.Data;

@SuppressWarnings("serial")
public abstract class DocTree extends JTree {

    private JScrollPane scrollView;

    private final static int TREE_ICON_SIZE = 16;

    private UUID[] usersProjectsUUIDs;

    public DocTree() {
	// TODO
	// UIManager.put("Tree.closedIcon", icon);
	// UIManager.put("Tree.openIcon", icon);
	// UIManager.put("Tree.leafIcon", icon);
	setMinimumSize(new Dimension(100, 0));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrollView = new JScrollPane(this);
	setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Workspace")));
	getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.isAltDown())
		    refreshProjectList();
	    }
	});
	addTreeSelectionListener(new TreeSelectionListener() {

	    @Override
	    public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();
		if (treePath == null)
		    return;

		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) treePath.getLastPathComponent();

		if (((DefaultMutableTreeNode) selected).getChildCount() == 0 && !(selected instanceof ProjectNode) && !(selected instanceof DirectoryNode) && !(selected instanceof FileNode)) {
		    refreshProjectList();

		}
	    }
	});
	setCellRenderer(new FlowNodeRenderer());
	addTreeWillExpandListener(new TreeWillExpandListener() {

	    @Override
	    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		// nothing
	    }

	    @Override
	    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		if (event.getPath().getPathCount() == 1) {
		    throw new ExpandVetoException(event);
		}
	    }
	});
    }

    public JScrollPane getScrollable() {
	return scrollView;
    }

    public void refreshProjectList() {
	if (!FlowClient.NETWORK) {
	    return;
	} else {
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) getModel()).getRoot();
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
		    ((DefaultTreeModel) getModel()).removeNodeFromParent((MutableTreeNode) root.getChildAt(i));
		}
	    }
	    ((DefaultTreeModel) getModel()).nodeChanged(root);
	    revalidate();
	    repaint();
	}
    }

    private void createProjectNode(UUID projectUUID) {
	if (FlowClient.NETWORK) {
	    Data fileListRequest = new Data("project_info");
	    System.out.println("create project node");
	    fileListRequest.put("project_uuid", projectUUID);
	    fileListRequest.put("session_id", Communicator.getSessionID());
	    Data project = Communicator.communicate(fileListRequest);

	    ProjectNode newProjectNode = new ProjectNode(projectUUID, project.get("project_name", String.class));
	    ((DefaultTreeModel) getModel()).insertNodeInto(newProjectNode, (DefaultMutableTreeNode) getModel().getRoot(), ((DefaultMutableTreeNode) getModel().getRoot()).getChildCount());

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
	    for (UUID childDirUUID : childDirs) {
		Data childDirNameRequest = new Data("directory_info");
		childDirNameRequest.put("session_id", Communicator.getSessionID());
		childDirNameRequest.put("directory_uuid", childDirUUID);
		String childDirName = Communicator.communicate(childDirNameRequest).get("directory_name", String.class);

		DirectoryNode subDirNode = new DirectoryNode(childDirUUID, childDirName);
		((DefaultTreeModel) getModel()).insertNodeInto(subDirNode, localDir, localDir.getChildCount());
		createProjectFileNodes(childDirUUID, subDirNode);
	    }
	}

	// adds files
	UUID[] childFiles = remoteDir.get("child_files", UUID[].class);
	if (childFiles.length > 0) {
	    for (UUID childFileUUID : childFiles) {
		((DefaultTreeModel) getModel()).insertNodeInto(new FileNode(childFileUUID), localDir, localDir.getChildCount());
	    }
	}
	
	expandRow(0);
    }

    public void reloadProjectFiles(ProjectNode projectNode) {
	// TODO see if you can combine these two into one loop

	// Makes an array of its children nodes
	DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[projectNode.getChildCount()];
	for (int i = 0; i < projectNode.getChildCount(); i++) {
	    children[i] = (DefaultMutableTreeNode) projectNode.getChildAt(i);
	}
	for (DefaultMutableTreeNode child : children) {
	    ((DefaultTreeModel) getModel()).removeNodeFromParent(child);
	}

	// Gets the project from the server
	Data projectReload = new Data("directory_info");
	projectReload.put("directory_uuid", projectNode.getProjectUUID());
	Data reloadedProject = Communicator.communicate(projectReload);
	if (reloadedProject == null) {
	    JOptionPane.showConfirmDialog(null, "The project couldn't be found.\nTry refreshing the project list by Alt + clicking.", "Project retrieval error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	    return;
	}

	reloadProjectFilesRecursively(reloadedProject, projectNode);
	((DefaultTreeModel) getModel()).nodeChanged(projectNode);
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
		((DefaultTreeModel) getModel()).removeNodeFromParent(localFileNode);
		localFiles.remove(localFileNode);
	    }
	}

	UUID[] remoteChildDirs = remoteParentDir.get("child_directories", UUID[].class);
	for (DirectoryNode localDirNode : localDirs) {
	    int idx = indexOfArray(remoteChildDirs, localDirNode.getDirectoryUUID());
	    if (idx == -1) {
		((DefaultTreeModel) getModel()).removeNodeFromParent(localDirNode);
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
		((DefaultTreeModel) getModel()).insertNodeInto(newFileNode, localNode, localNode.getChildCount());
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
		((DefaultTreeModel) getModel()).insertNodeInto(newDirNode, localNode, localNode.getChildCount());
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

	private DirectoryNode(UUID dir, String name) {
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
	private String type;

	public FileNode(UUID fileUUID) {
	    this.file = fileUUID;

	    Data fileDataRequest = new Data("file_info");
	    fileDataRequest.put("file_uuid", fileUUID);
	    fileDataRequest.put("session_id", Communicator.getSessionID());
	    Data fileData = Communicator.communicate(fileDataRequest);

	    name = fileData.get("file_name", String.class);
	    type = fileData.get("file_type", String.class);
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

	public String getType() {
	    return type;
	}
    }

    private class FlowNodeRenderer implements TreeCellRenderer {

	private ImageIcon workspaceIcon;
	private ImageIcon projectIcon;
	private ImageIcon directoryIcon;
	private ImageIcon textDocumentIcon;
	private ImageIcon arbitraryFileIcon;

	private JLabel label;

	public FlowNodeRenderer() {
	    this.label = new JLabel();
	    try {
		workspaceIcon = new ImageIcon(ImageIO.read(new File("images/workspace.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
		projectIcon = new ImageIcon(ImageIO.read(new File("images/icon.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
		directoryIcon = new ImageIcon(ImageIO.read(new File("images/folder.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
		textDocumentIcon = new ImageIcon(ImageIO.read(new File("images/textDoc.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
		arbitraryFileIcon = new ImageIcon(ImageIO.read(new File("images/arbitDoc.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	    Object node = (DefaultMutableTreeNode) value;
	    if (node instanceof ProjectNode) {
		ProjectNode projectNode = (ProjectNode) node;
		label.setText(projectNode.getName());
		label.setIcon(projectIcon);
	    } else if (node instanceof DirectoryNode) {
		DirectoryNode dirNode = (DirectoryNode) node;
		label.setText(dirNode.getName());
		label.setIcon(directoryIcon);
	    } else if (node instanceof FileNode) {
		FileNode fileNode = (FileNode) node;
		label.setText(fileNode.getName());
		if (fileNode.getType().equals("TEXT_DOCUMENT")) {
		    label.setIcon(textDocumentIcon);
		} else if (fileNode.getType().equals("ARBITRARY_DOCUMENT")) {
		    label.setIcon(arbitraryFileIcon);
		}
	    } else {
		label.setText(node.toString());
		label.setIcon(workspaceIcon);
	    }
	    if (selected) {
		label.setBackground(new Color(118, 118, 118, 120));
		label.setOpaque(true);
	    } else {
		label.setBackground(Color.WHITE);
		label.setOpaque(false);
	    }
	    return label;
	}
    }
}