
package shared;

import gui.FlowClient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import message.Data;

/**
 * A tree for file management for a user
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public abstract class FileTree extends JTree {

	private JScrollPane			scrollView;
	private final static int	TREE_ICON_SIZE	= 16;
	private UUID[]				usersProjectsUUIDs;

	/**
	 * Creates a new FileTree
	 */
	public FileTree() {
		// Swing things
		setMinimumSize(new Dimension(100, 0));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setBorder(FlowClient.EMPTY_BORDER);
		scrollView = new JScrollPane(this);
		scrollView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Workspace")));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// nothing
			}

			@Override
			public void focusGained(FocusEvent e) {
				refresh();
			}
		});
		// Adds a refresh shortcut
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isAltDown() && e.getButton() == MouseEvent.BUTTON1) {
					refresh();
				}
			}
		});
		// Set selected and all that fun stuff
		setSelectionRow(0);
		setCellRenderer(new FlowNodeRenderer());
		// Controls the look of the file tree
		addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				refresh();
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				refresh();
				// Prevents collapse of the "workspace" node
				if (event.getPath().getPathCount() == 1) {
					throw new ExpandVetoException(event);
				}
			}
		});
	}

	/**
	 * Refreshes the file tree
	 */
	public void refresh() {
		refreshProjectList();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) getModel()).getRoot();
		for (int i = 0; i < root.getChildCount(); i++) {
			reloadProjectFiles((ProjectNode) root.getChildAt(i));
		}
		revalidate();
		repaint();
	}

	/**
	 * Gets the JScrollPane
	 * 
	 * @return the JScrollPane
	 */
	public JScrollPane getScrollable() {
		return scrollView;
	}

	/**
	 * Refreshes the LIST of projects
	 */
	public void refreshProjectList() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
		// Asks the server for list of projects
		Data projectList = new Data("list_projects");
		projectList.put("session_id", Communicator.getSessionID());
		Data reply = Communicator.communicate(projectList);
		if (reply == null) {
			return;
		} else if (reply.get("status", String.class).equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return;
		}
		usersProjectsUUIDs = reply.get("projects", UUID[].class);
		if (usersProjectsUUIDs == null) {
			return;
		}

		// Adds new projects
		for (UUID remoteProjectUUID : usersProjectsUUIDs) {
			boolean projectExistsLocally = false;
			for (int i = root.getChildCount() - 1; !projectExistsLocally && i >= 0; i--) {
				if (((ProjectNode) root.getChildAt(i)).getProjectUUID().equals(remoteProjectUUID)) {
					projectExistsLocally = true;
				}
			}
			if (!projectExistsLocally) {
				// Asks server for project's info
				Data fileListRequest = new Data("project_info");
				fileListRequest.put("project_uuid", remoteProjectUUID);
				fileListRequest.put("session_id", Communicator.getSessionID());
				Data project = Communicator.communicate(fileListRequest);
				if (project == null) {
					return;
				} else if (project.get("status", String.class).equals("ACCESS_DENIED")) {
					JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					return;
				}

				// Creates new node and adds it
				ProjectNode newProjectNode = new ProjectNode(remoteProjectUUID, project.get("project_name", String.class));
				((DefaultTreeModel) getModel()).insertNodeInto(newProjectNode, (DefaultMutableTreeNode) ((DefaultTreeModel) getModel()).getRoot(), ((DefaultMutableTreeNode) getModel().getRoot()).getChildCount());

				// Loads the project files for that new project node
				loadProjectFilesFirstTime(remoteProjectUUID, newProjectNode);
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
				// Removes the node
				((DefaultTreeModel) getModel()).removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(i));
			}
		}
		revalidate();
		repaint();

	}

	/**
	 * Creates the project's files on first load (no checking)
	 * 
	 * @param remoteDirUUID
	 *        the remote directory's UUID
	 * @param localDir
	 *        the local DirectoryNode
	 */
	private void loadProjectFilesFirstTime(UUID remoteDirUUID, DirectoryNode localDir) {
		// Asks the server for info about the directory
		Data dirInfoRequest = new Data("directory_info");
		dirInfoRequest.put("session_id", Communicator.getSessionID());
		dirInfoRequest.put("directory_uuid", remoteDirUUID);
		Data remoteDir = Communicator.communicate(dirInfoRequest);
		if (remoteDir.get("status", String.class).equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return;
		}

		// adds folders
		UUID[] childDirs = remoteDir.get("child_directories", UUID[].class);
		if (childDirs.length > 0) {
			for (UUID childDirUUID : childDirs) {
				// Creates children while asking for their info
				Data childDirNameRequest = new Data("directory_info");
				childDirNameRequest.put("session_id", Communicator.getSessionID());
				childDirNameRequest.put("directory_uuid", childDirUUID);
				String childDirName = Communicator.communicate(childDirNameRequest).get("directory_name", String.class);

				// Creates the node
				DirectoryNode subDirNode = new DirectoryNode(childDirUUID, childDirName);
				((DefaultTreeModel) getModel()).insertNodeInto(subDirNode, localDir, localDir.getChildCount());

				// Recursively does this to its children
				loadProjectFilesFirstTime(childDirUUID, subDirNode);
			}
		}

		// adds files
		UUID[] childFiles = remoteDir.get("child_files", UUID[].class);
		if (childFiles.length > 0) {
			for (UUID childFileUUID : childFiles) {
				// Creates fil nodes
				((DefaultTreeModel) getModel()).insertNodeInto(new FileNode(childFileUUID), localDir, localDir.getChildCount());
			}
		}

		expandRow(0);
	}

	/**
	 * Reloads the project's files
	 * 
	 * @param projectNode
	 *        the node to reload from
	 */
	public void reloadProjectFiles(ProjectNode projectNode) {
		// Gets the project data from the server
		Data projectReload = new Data("directory_info");
		projectReload.put("directory_uuid", projectNode.getProjectUUID());
		projectReload.put("session_id", Communicator.getSessionID());
		Data reloadedProject = Communicator.communicate(projectReload);
		if (reloadedProject == null) {
			JOptionPane.showConfirmDialog(null, "The project couldn't be found.\nTry refreshing the project list by Alt + clicking.", "Project retrieval error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		} else if (reloadedProject.get("status", String.class).equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		} else {
			return;
		}

		// Does the recursion one on the children
		reloadProjectFilesRecursively(reloadedProject, projectNode);

		if (getSelectionPath() == null) {
			setSelectionRow(0);
		}
	}

	/**
	 * Recursively reloads the project's files. Only to be called by reloadProjectFiles
	 * 
	 * @param remoteParentDir
	 *        the remote directory's Data
	 * @param localNode
	 *        the local DirectoryNode to add into
	 */
	private void reloadProjectFilesRecursively(Data remoteParentDir, DirectoryNode localNode) {
		String remoteName = remoteParentDir.get("directory_name", String.class);
		if (!remoteName.equals(localNode.toString())) {
			localNode.setName(remoteName);
		}

		// Creates a list of the localNode's child directories and files
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

		// Removes child nodes that don't exist anymore (from the node and from the arrayList)
		UUID[] remoteChildFileUUIDs = remoteParentDir.get("child_files", UUID[].class);
		for (FileNode localFileNode : localFiles) {
			int idx = Arrays.asList(remoteChildFileUUIDs).indexOf(localFileNode.getFileUUID());
			if (idx == -1) {
				((DefaultTreeModel) getModel()).removeNodeFromParent(localFileNode);
				localFiles.remove(localFileNode);
			}
		}

		UUID[] remoteChildDirUUIDs = remoteParentDir.get("child_directories", UUID[].class);
		for (DirectoryNode localDirNode : localDirs) {
			int idx = Arrays.asList(remoteChildDirUUIDs).indexOf(localDirNode.getDirectoryUUID());
			if (idx == -1) {
				((DefaultTreeModel) getModel()).removeNodeFromParent(localDirNode);
				localDirs.remove(localDirNode);
			}
		}

		// Adds new children and files from remote as needed
		remoteFiles:
		for (UUID remoteFileUUID : remoteChildFileUUIDs) {
			for (FileNode fileNode : localFiles) {
				if (fileNode.getFileUUID().equals(remoteFileUUID)) {
					Data fileNameRequest = new Data("file_info");
					fileNameRequest.put("file_uuid", remoteFileUUID);
					fileNameRequest.put("session_id", Communicator.getSessionID());
					String remoteFileName = Communicator.communicate(fileNameRequest).get("file_name", String.class);
					if (!fileNode.toString().equals(remoteFileName)) {
						fileNode.setName(remoteFileName);
					}
					continue remoteFiles;
				}
			}
			FileNode newFileNode = new FileNode(remoteFileUUID);
			((DefaultTreeModel) getModel()).insertNodeInto(newFileNode, localNode, localNode.getChildCount());
		}

		remoteDirs:
		for (UUID remoteChildDirUUID : remoteChildDirUUIDs) {
			for (DirectoryNode directoryNode : localDirs) {
				if (directoryNode.getDirectoryUUID().equals(remoteChildDirUUID)) {
					continue remoteDirs;
				}
			}
			DirectoryNode newChildNode = new DirectoryNode(remoteChildDirUUID);
			((DefaultTreeModel) getModel()).insertNodeInto(newChildNode, localNode, localNode.getChildCount());

			// Ask server for information about this new directory, and create it
			Data remoteChildDirRequest = new Data("directory_info");
			remoteChildDirRequest.put("directory_uuid", remoteChildDirUUID);
			remoteChildDirRequest.put("session_id", Communicator.getSessionID());
			Data remoteChildDir = Communicator.communicate(remoteChildDirRequest);
			if (remoteChildDir.get("status", String.class).equals("ACCESS_DENIED")) {
				JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				return;
			}

			reloadProjectFilesRecursively(remoteChildDir, newChildNode);
		}
	}

	/**
	 * A DefaultMutableTreeNode representing a Flow Project
	 * 
	 * @author Vince Ou
	 *
	 */
	public class ProjectNode extends DirectoryNode {

		/**
		 * Creates a new ProjectNode
		 * 
		 * @param projectUUID
		 *        the Project's UUID
		 * @param name
		 *        the name of the project
		 */
		public ProjectNode(UUID projectUUID, String name) {
			super(projectUUID, name);
		}

		/**
		 * Gets the project UUID
		 * 
		 * @return the projectUUID
		 */
		public UUID getProjectUUID() {
			return getDirectoryUUID();
		}
	}

	/**
	 * A DefaultMutableTreeNode representing a Flow Directory
	 * 
	 * @author Vince Ou
	 *
	 */
	public class DirectoryNode extends DefaultMutableTreeNode {

		// TODO something for directory updates?
		private UUID	directoryUUID;
		private String	name;

		/**
		 * Creates a new DirectoryNode when you have the name as well
		 * 
		 * @param dirUUID
		 *        the directory's UUID
		 * @param name
		 *        the directory's name
		 */
		private DirectoryNode(UUID dirUUID, String name) {
			this.directoryUUID = dirUUID;
			this.name = name;
		}

		/**
		 * Creates a new DirectoryNode
		 * 
		 * @param dirUUID
		 *        the directory's UUID
		 */
		public DirectoryNode(UUID dirUUID) {
			this.directoryUUID = dirUUID;
			// Requeests the server for a name
			Data requestName = new Data("directory_info");
			requestName.put("directory_uuid", dirUUID);
			requestName.put("session_id", Communicator.getSessionID());
			this.name = Communicator.communicate(requestName).get("directory_name", String.class);
		}

		/**
		 * Gets the directory's UUID
		 * 
		 * @return the directory's UUID
		 */
		public UUID getDirectoryUUID() {
			return directoryUUID;
		}

		/**
		 * Changes directory node's name
		 * 
		 * @param modifiedDirectoryName
		 *        new name
		 */
		public void setName(String modifiedDirectoryName) {
			name = modifiedDirectoryName;
		}

		/**
		 * Gets the name
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * A DefaultMutableTreeNode representing a Flow File
	 * 
	 * @author Vince
	 *
	 */
	public class FileNode extends DefaultMutableTreeNode {

		// TODO add a listener for file renaming changes

		private UUID	file;
		private String	name;
		private String	type;

		/**
		 * Creates a new FileNode
		 * 
		 * @param fileUUID
		 *        the file's UUID
		 */
		public FileNode(UUID fileUUID) {
			this.file = fileUUID;

			// Asks for the file's name
			Data fileDataRequest = new Data("file_info");
			fileDataRequest.put("file_uuid", fileUUID);
			fileDataRequest.put("session_id", Communicator.getSessionID());
			Data fileData = Communicator.communicate(fileDataRequest);

			name = fileData.get("file_name", String.class);
			type = fileData.get("file_type", String.class);
		}

		/**
		 * Gets the file's UUID
		 * 
		 * @return the file's UUID
		 */
		public UUID getFileUUID() {
			return file;
		}

		/**
		 * Gets the file's name
		 */
		public String toString() {
			return name;
		}

		/**
		 * Gets the file type
		 * 
		 * @return the type of file
		 */
		public String getType() {
			return type;
		}

		public void setName(String rename) {
			name = rename;
		}
	}

	public FileNode generateFileNode(UUID childUUID) {
		return new FileNode(childUUID);
	}

	/**
	 * Renderer for the nodes in the tree
	 * 
	 * @author Vince Ou
	 *
	 */
	private class FlowNodeRenderer implements TreeCellRenderer {

		// The different icons for each aspect
		private ImageIcon	workspaceIcon;
		private ImageIcon	projectIcon;
		private ImageIcon	directoryIcon;
		private ImageIcon	textDocumentIcon;
		private ImageIcon	arbitraryFileIcon;

		private JLabel		label;

		/**
		 * Creates a new FlowNodeRenderer
		 */
		public FlowNodeRenderer() {
			this.label = new JLabel();
			// Sets images
			try {
				workspaceIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/workspace.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
				projectIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/icon.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
				directoryIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/folder.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
				textDocumentIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/textDoc.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
				arbitraryFileIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/arbitDoc.png")).getScaledInstance(TREE_ICON_SIZE, TREE_ICON_SIZE, Image.SCALE_SMOOTH));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Paints the components
		 */
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			// Paints a different icon depending on the type of object

			// Projects
			if (node instanceof ProjectNode) {
				ProjectNode projectNode = (ProjectNode) node;
				label.setText(projectNode.toString());
				label.setIcon(projectIcon);
			}
			// Directories
			else if (node instanceof DirectoryNode) {
				DirectoryNode dirNode = (DirectoryNode) node;
				label.setText(dirNode.toString());
				label.setIcon(directoryIcon);
			}
			// Files
			else if (node instanceof FileNode) {
				FileNode fileNode = (FileNode) node;
				label.setText(fileNode.toString());
				// Different icon for arbitrary documents
				if (fileNode.getType().equals("TEXT_DOCUMENT")) {
					label.setIcon(textDocumentIcon);
				} else if (fileNode.getType().equals("ARBITRARY_DOCUMENT")) {
					label.setIcon(arbitraryFileIcon);
				}

			}
			// Workspace
			else {
				label.setText(node.toString());
				label.setIcon(workspaceIcon);
			}

			// Tint for selected
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
