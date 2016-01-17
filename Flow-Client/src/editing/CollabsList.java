
package editing;

import gui.FlowClient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import message.Data;
import shared.Communicator;
import shared.EditArea;
import shared.FileTree.ProjectNode;
import shared.FlowPermission;

/**
 * The list of collaborators; appears on the right side during the editing view
 * 
 * @author Vince Ou
 */
@SuppressWarnings("serial")
public class CollabsList extends JPanel {

	// Assorted fields for the Components needed
	private JPanel				searchPane;
	private JTextField			searchBox;
	private JButton				searchButton;
	private EditPane			editPane;
	private JPanel				userList;

	// Currently-open-project tracking
	private UUID				activeProjectUUID;
	private FlowPermission		myPermission;

	// Constants for styling
	private static final String	SEARCHBOX_TEXT	= "Search...";
	private static final Font	USERNAME_FONT	= new Font("TW Cen MT", Font.BOLD, 20);

	// private static final int USER_ICON_SIZE = 55;
	// private static final Border ICON_ENTRY_BORDER =
	// BorderFactory.createLineBorder(new Color(255, 128, 128), 2);

	/**
	 * Creates a new pane for collaborators
	 * 
	 * @param editPane
	 *        the parent pane that this list is on
	 */
	public CollabsList(EditPane editPane) {
		this.editPane = editPane;
		// Sets the swing layouts
		setMinimumSize(new Dimension(5, 1));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setBorder(FlowClient.EMPTY_BORDER);
		setLayout(new BorderLayout(0, 0));

		// Creates the top half (searching for new users)
		searchPane = new JPanel(new BorderLayout(0, 0));
		searchPane.add(new JLabel("Type a username to seach for them"), BorderLayout.NORTH);

		// Entry for user name
		searchBox = new JTextField();
		searchBox.setText(SEARCHBOX_TEXT);
		searchBox.setForeground(Color.GRAY);
		searchBox.addFocusListener(new FocusListener() {

			/**
			 * Sets text to the default text when focus lost, and
			 * 
			 * @override
			 */
			public void focusLost(FocusEvent e) {
				if (searchBox.getText().trim().equals("")) {
					searchBox.setText(SEARCHBOX_TEXT);
					searchBox.setForeground(Color.GRAY);
				}
			}

			/**
			 * Clears the text box when clicked on
			 * 
			 * @override
			 */
			public void focusGained(FocusEvent e) {
				if (searchBox.getText().equals(SEARCHBOX_TEXT) || searchBox.getText().trim().equals("")) {
					searchBox.setText("");
					searchBox.setForeground(Color.BLACK);
				}
			}
		});
		// Adds shortcuts
		searchBox.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// nothing
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// nothing
			}

			/**
			 * Does a search when [ENTER] is pressed
			 * 
			 * @override
			 */
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					searchButton.doClick();
				}
			}
		});
		searchPane.add(searchBox, BorderLayout.CENTER);

		// Click-to-refresh or to search for + add new user
		searchButton = new JButton();
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Debug
				// Gets the text, checks it, and queries the server to
				// change the permission from NONE to VIEW (see
				// shared.FlowPermission for more details on permissions)
				String query = searchBox.getText();
				if (query != null && !query.equals(SEARCHBOX_TEXT) && !query.trim().equals("")) {
					// Creates the data package
					Data collabMod = new Data("project_modify");
					collabMod.put("session_id", Communicator.getSessionID());
					collabMod.put("project_modify_type", "MODIFY_COLLABORATOR");
					collabMod.put("project_uuid", activeProjectUUID);
					collabMod.put("username", query);
					collabMod.put("access_level", (byte) 1);

					// Sends it to server
					Data response = Communicator.communicate(collabMod);
					switch (response.get("status", String.class)) {
					// Is changed server side, refresh at the end
						case "OK":
							break;

						// No changes server side, because the requested user
						// does not exist.
						// This doubles as a check for the existence of such a
						// user.
						case "USERNAME_DOES_NOT_EXIST":
							JOptionPane.showConfirmDialog(null, "This user does not exist.\nPlease double check your entry, and make sure that your case is correct.", "Cannot find user", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;

						// The currently open user does not have the required
						// permissions change collaborators. However, this
						// should be handled in CollabsList.UserInfo
						case "ACCESS_LEVEL_INVALID":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions to add this user as a viewer.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							break;

						case "ACCESS_DENIED":
							JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							break;

						default:
							break;
					}
				}
				// Resets the search box and refreshes the list of users
				searchBox.setText(SEARCHBOX_TEXT);
				refreshUserList();
			}
		});
		// Sets the search button icon
		try {
			searchButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/addCollab.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE - 3, FlowClient.BUTTON_ICON_SIZE - 3, Image.SCALE_SMOOTH)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Adds the button to the search pane, and the search pane to the
		// collabs list
		searchPane.add(searchButton, BorderLayout.EAST);
		add(searchPane, BorderLayout.NORTH);

		// Creates the bottom half of the Collabss List, the list of current
		// collaborators to the FlowProject
		userList = new JPanel(new GridLayout(0, 1, 2, 3));
		userList.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth()), Integer.MAX_VALUE));
		// Makes it scrollable
		JScrollPane userListScroll = new JScrollPane(userList);
		userListScroll.getVerticalScrollBar().setUnitIncrement(FlowClient.SCROLL_SPEED);
		userListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		userListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(userListScroll, BorderLayout.CENTER);
	}

	/**
	 * Regenerates the list of users currently in the FlowProject
	 */
	public void refreshUserList() {
		// Requests the server for the current list of collaborators
		// (owner/viewers/editors)
		Data getProject = new Data("project_info");
		getProject.put("session_id", Communicator.getSessionID());
		// Gets the "active project" (see getActiveProjectUUID())
		try {
			activeProjectUUID = getActiveProjectUUID();
		} catch (NoActiveProjectException e) {
			e.printStackTrace();
		}
		getProject.put("project_uuid", activeProjectUUID);

		// Asks server
		Data activeProject = Communicator.communicate(getProject);

		if (activeProject.get("status", String.class).equals("ACCESS_DENIED")) {
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Clears the list
		userList.removeAll();

		// Creates the owner first, with a check if the owner is this particular
		// client's user, then it will set the current permission (for use
		// later)
		String ownerName = activeProject.get("owner", String.class);
		if (ownerName.equals(Communicator.getUsername())) {
			myPermission = new FlowPermission(FlowPermission.OWNER);
		}

		// Lists the editors
		String[] editors = activeProject.get("editors", String[].class);
		if (Arrays.asList(editors).contains(Communicator.getUsername())) {
			myPermission = new FlowPermission(FlowPermission.EDIT);
		}

		// Lists the viewers
		String[] viewers = activeProject.get("viewers", String[].class);
		if (Arrays.asList(viewers).contains(Communicator.getUsername())) {
			myPermission = new FlowPermission(FlowPermission.VIEW);
		}

		userList.add(new UserInfo(ownerName, new FlowPermission(FlowPermission.OWNER)));
		for (String editor : editors) {
			userList.add(new UserInfo(editor, new FlowPermission(FlowPermission.EDIT)));
		}
		for (String viewer : viewers) {
			userList.add(new UserInfo(viewer, new FlowPermission(FlowPermission.VIEW)));
		}

		// Refresh
		userList.revalidate();
		userList.repaint();

		// Aesthetics.
		searchBox.setText(SEARCHBOX_TEXT);
		searchBox.setForeground(Color.black);
	}

	/**
	 * Gets the "active" project's UUID
	 * 
	 * @return the 'active' project's UUID, with priority to the currently open
	 *         tab, and then to the selected project in the documents tree
	 * @throws NoActiveProjectException
	 *         When there is no active project (no tabs open, no selection
	 *         in the documents tree)
	 */
	private UUID getActiveProjectUUID() throws NoActiveProjectException {
		// Tries to get the currently open tab
		JScrollPane selectedScrollPane = (JScrollPane) editPane.getEditTabs().getSelectedComponent();
		if (selectedScrollPane == null) {
			// If there is no tab open, tries the doc-tree
			TreePath treePath = editPane.getFileTree().getSelectionPath();
			if (treePath == null || treePath.getPath().length <= 1)
				// If neither of those apply, then throws a
				// NoActiveProjectException
				throw new NoActiveProjectException();
			// Returns the selected project
			return ((ProjectNode) treePath.getPath()[1]).getProjectUUID();
		}
		// Returns the open tab
		UUID activeProjectUUID = ((EditArea) selectedScrollPane.getViewport().getView()).getProjectUUID();
		return activeProjectUUID;
	}

	/**
	 * Exception to be thrown when there is no active project; So I don't have
	 * to do null checking
	 * 
	 * @author Vince Ou
	 *
	 */
	@SuppressWarnings("serial")
	class NoActiveProjectException extends Exception {

		public NoActiveProjectException() {
			JOptionPane.showConfirmDialog(null, "You first need to select a project you want to modify.\nThis can be done by either opening a file from the project and having its tab open, or\nselecting a project and keeping it open in the documents tree.", "No project open", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * A single collaborator's information
	 * 
	 * @author Vince Ou
	 *
	 */
	class UserInfo extends JPanel {

		// This user's permission level
		private FlowPermission	userPermission;

		// The two different "views"
		private JPanel			simpleView;
		private JPanel			permissionsView;

		// Other swing Components
		private JLabel			permissionLabel;
		private ButtonGroup		permissionGroup;
		private JRadioButton[]	permissionSelectors	= new JRadioButton[4];

		/**
		 * Creates a new UserInfo panel
		 * 
		 * @param user
		 *        the user's name
		 * @param permission
		 *        the level of permission that this user hase
		 */
		public UserInfo(String user, FlowPermission permission) {
			// Sets up.
			this.userPermission = permission;

			// Sets up necessary swing attributes
			setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
			setPreferredSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
			setMinimumSize(new Dimension(5, 5));

			// Sets the layout for the entire JPanel
			setLayout(new BorderLayout(2, 0));
			// JLabel icon = new JLabel(user.getAvatar());
			// icon.setPreferredSize(new Dimension(USER_ICON_SIZE,
			// USER_ICON_SIZE));
			// icon.setMinimumSize(new Dimension(USER_ICON_SIZE,
			// USER_ICON_SIZE));
			// add(icon, BorderLayout.WEST);

			// The JPanel is basically a CardLayout that switches between its
			// two main layouts
			JPanel switcher = new JPanel(new CardLayout(0, 0));
			switcher.setOpaque(false);
			add(switcher, BorderLayout.CENTER);

			// The simple view lists the user name and their permission, and
			// that's it
			simpleView = new JPanel(new BorderLayout(0, 1));
			// Sets up the size and enables anti-aliasing
			simpleView.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
			JLabel name = new JLabel(user) {

				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					super.paintComponent(g2);
				}
			};
			// Set aesthetics and all that fun stuff
			// Adds it to the simple view panel
			name.setFont(USERNAME_FONT);
			permissionLabel = new JLabel(userPermission.toString());
			simpleView.add(name, BorderLayout.NORTH);
			name.setBorder(FlowClient.EMPTY_BORDER);
			simpleView.add(permissionLabel, BorderLayout.CENTER);
			permissionLabel.setBorder(FlowClient.EMPTY_BORDER);
			simpleView.setOpaque(false);
			switcher.add(simpleView, "simple");

			// The permissions view lets the user edit this collaborator's
			// permissions
			permissionsView = new JPanel(new BorderLayout(0, 0));
			JLabel name2 = new JLabel(user);
			name2.setFont(USERNAME_FONT);
			permissionsView.add(name2, BorderLayout.NORTH);
			// ButtonGroup is used to manage the radio buttons (see later)
			permissionGroup = new ButtonGroup();
			JPanel permissionPanel = new JPanel();
			permissionPanel.setLayout(new GridLayout(2, 2, 1, 1));
			permissionPanel.setOpaque(false);
			permissionsView.add(permissionPanel, BorderLayout.CENTER);
			permissionsView.setOpaque(false);
			switcher.add(permissionsView, "permissions");

			// Sets according to the permission level
			setBackground(userPermission.getPermissionColor());

			// Creates the radio buttons. Will not create any "OWNER" buttons
			// for non-owners of the project
			byte limit = FlowPermission.EDIT;
			if (myPermission.getPermissionLevel() >= FlowPermission.OWNER) {
				limit = FlowPermission.OWNER;
			}
			for (byte permLevel = 0; permLevel <= limit; permLevel++) {
				permissionSelectors[permLevel] = new JRadioButton(new FlowPermission(permLevel).toString());
				permissionSelectors[permLevel].addActionListener(new PermissionRadioButtonListener(permLevel));
				permissionSelectors[permLevel].setOpaque(false);
				permissionGroup.add(permissionSelectors[permLevel]);
				permissionPanel.add(permissionSelectors[permLevel]);
			}

			// Used to save the selected permission level for that user
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Takes the highest permission selected
					byte changePermission = FlowPermission.NONE;
					for (byte level = 0; level < permissionSelectors.length; level++) {
						if (permissionSelectors[level].isSelected()) {
							changePermission = level;
							break;
						}
					}

					// Creates a data packet to send to the server
					Data changePerm = new Data("project_modify");
					changePerm.put("project_modify_type", "MODIFY_COLLABORATOR");
					UUID projectUUID = activeProjectUUID;
					changePerm.put("project_uuid", projectUUID);
					changePerm.put("session_id", Communicator.getSessionID());
					changePerm.put("username", user);
					changePerm.put("access_level", changePermission);
					// If not cleared by the server, fail the changes
					if (!Communicator.communicate(changePerm).get("status", String.class).equals("OK")) {
						JOptionPane.showConfirmDialog(null, "Either this project does not exist,\n" + "the user does not exist,\n" + "the access level is invalid,\n" + "or you do not have the access to change permissions.\n\n" + "Try refreshing the list of projects by moving your mouse cursor to the documents tree\n"
								+ "and back into this list of users and try again.", "Project out of sync", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					}

					// Switch back to the simple layout and refresh the list
					((CardLayout) switcher.getLayout()).show(switcher, "simple");
					updateFields();
					CollabsList.this.refreshUserList();
				}
			});
			permissionPanel.add(saveButton);

			// Enables switching to the permissions pane
			switcher.addMouseListener(new MouseListener() {

				/**
				 * When you click on it, it will shade the tile darker for a cool effect
				 */
				@Override
				public void mouseReleased(MouseEvent e) {
					setBackground(userPermission.getPermissionColor());
				}

				@Override
				public void mousePressed(MouseEvent e) {
					setBackground(new Color(0xB1ADFF));
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				/**
				 * For a particular tile, the user must be either an EDITOR or
				 * OWNER, but if the tile is an OWNER tile, it does not "flip"
				 * to the permissions pane. This prevents the user from deleting
				 * the owner of the project.
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					if (myPermission.getPermissionLevel() >= FlowPermission.EDIT && userPermission.getPermissionLevel() < FlowPermission.OWNER) {
						((CardLayout) switcher.getLayout()).show(switcher, "permissions");
						permissionSelectors[userPermission.getPermissionLevel()].setSelected(true);
					}
				}
			});
		}

		/**
		 * Updates the current field of the tile
		 */
		private void updateFields() {
			// Updates the colour
			setBackground(userPermission.getPermissionColor());
			// Updates the text
			permissionLabel.setText(userPermission.toString());
			// Tells swing to refresh
			revalidate();
		}

		/**
		 * Special listener for the PermissionRadioButtons
		 * 
		 * @author Vince Ou
		 *
		 */
		class PermissionRadioButtonListener implements ActionListener {

			// The radio button's permission level
			private byte	permLevel;

			/**
			 * Creates a new PermissionRadioButtonListener
			 * 
			 * @param permLevel
			 *        the permission level of the button
			 */
			public PermissionRadioButtonListener(byte permLevel) {
				this.permLevel = permLevel;
			}

			/**
			 * Sets the user's permission to the one that is selected
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				userPermission.setPermission(permLevel);
			}
		}
	}
}
