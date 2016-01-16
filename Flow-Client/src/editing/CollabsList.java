package editing;

import gui.FlowClient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.tree.TreePath;

import message.Data;
import shared.Communicator;
import shared.DocTree.ProjectNode;
import shared.EditArea;
import shared.FlowPermission;

@SuppressWarnings("serial")
public class CollabsList extends JPanel {
    private JPanel searchPane;
    private JTextField searchBox;
    private JButton searchButton;
    private EditPane editPane;
    private UUID activeProject;
    private JList<UserInfo> userList;
    private DefaultListModel<UserInfo> userListModel;
    private static final String SEARCHBOX_TEXT = "Search...";
    // private static final int USER_ICON_SIZE = 55;
    // private static final Border ICON_ENTRY_BORDER =
    // BorderFactory.createLineBorder(new Color(255, 128, 128), 2);
    private FlowPermission myPermission;

    public CollabsList(FlowPermission myPermission, EditPane editPane) {
	this.editPane = editPane;
	this.myPermission = myPermission;
	setMinimumSize(new Dimension(5, 1));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new BorderLayout(0, 0));

	searchPane = new JPanel(new BorderLayout(0, 0));
	searchPane.add(new JLabel("Type a username to seach for them"), BorderLayout.NORTH);

	searchBox = new JTextField();
	searchBox.setText(SEARCHBOX_TEXT);
	searchBox.setForeground(Color.GRAY);
	searchBox.addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(FocusEvent e) {
		if (searchBox.getText().trim().equals("")) {
		    searchBox.setText(SEARCHBOX_TEXT);
		    searchBox.setForeground(Color.GRAY);
		}
	    }

	    @Override
	    public void focusGained(FocusEvent e) {
		if (searchBox.getText().equals(SEARCHBOX_TEXT) || searchBox.getText().trim().equals("")) {
		    searchBox.setText("");
		    searchBox.setForeground(Color.BLACK);
		}
	    }
	});
	searchPane.add(searchBox, BorderLayout.CENTER);

	searchButton = new JButton();
	searchButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (FlowClient.NETWORK) {
		    String query = searchBox.getText();
		    if (query != null && !query.equals(SEARCHBOX_TEXT) && !query.trim().equals("")) {
			Data collabMod = new Data("project_modify");
			collabMod.put("session_id", Communicator.getSessionID());
			collabMod.put("project_modify_type", "MODIFY_COLLABORATOR");
			collabMod.put("project_uuid", activeProject);
			collabMod.put("username", query);
			collabMod.put("access_level", (byte) 1);

			Data response = Communicator.communicate(collabMod);
			switch (response.get("status", String.class)) {
			case "OK":
			    break;

			case "USERNAME_DOES_NOT_EXIST":
			    JOptionPane.showConfirmDialog(null, "This user does not exist.\nPlease double check your entry, and make sure that your case is correct.", "Cannot find user", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			    break;

			case "ACCESS_LEVEL_INVALID":
			    JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions to add this user as a viewer.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			    break;

			default:
			    break;
			}
		    }
		}
		searchBox.setText(SEARCHBOX_TEXT);
		refreshUserList();
	    }
	});
	try {
	    searchButton.setIcon(new ImageIcon(ImageIO.read(new File("images/addCollab.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE - 3, FlowClient.BUTTON_ICON_SIZE - 3, Image.SCALE_SMOOTH)));
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	searchPane.add(searchButton, BorderLayout.EAST);
	add(searchPane, BorderLayout.NORTH);

	// userListPanel = new JPanel(new GridLayout(0, 1, 2, 3));
	userListModel = new DefaultListModel<UserInfo>();
	userList = new JList<UserInfo>(userListModel);
	userList.setCellRenderer(new UserListRenderer());
	for (MouseListener listener : userList.getMouseListeners()) {
	    userList.removeMouseListener(listener);
	}
	userList.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth()), Integer.MAX_VALUE));
	JScrollPane userListScroll = new JScrollPane(userList);
	userListScroll.getVerticalScrollBar().setUnitIncrement(FlowClient.SCROLL_SPEED);
	userListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	userListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	add(userListScroll, BorderLayout.CENTER);
    }

    public void refreshUserList() {
	Data getProject = new Data("project_info");
	getProject.put("session_id", Communicator.getSessionID());
	try {
	    activeProject = getActiveUUID();
	} catch (NoActiveProjectException e) {
	    e.printStackTrace();
	}
	getProject.put("project_uuid", activeProject);

	Data activeProject = Communicator.communicate(getProject);

	userListModel.clear();

	userListModel.addElement(new UserInfo(activeProject.get("owner", String.class), new FlowPermission(FlowPermission.OWNER)));

	String[] editors = activeProject.get("editors", String[].class);
	for (String editor : editors) {
	    userListModel.addElement(new UserInfo(editor, new FlowPermission(FlowPermission.EDIT)));
	}

	String[] viewers = activeProject.get("viewers", String[].class);
	for (String viewer : viewers) {
	    userListModel.addElement(new UserInfo(viewer, new FlowPermission(FlowPermission.VIEW)));
	}

	userList.revalidate();
	userList.repaint();

	searchBox.setText(SEARCHBOX_TEXT);
	searchBox.setForeground(Color.black);
    }

    private UUID getActiveUUID() throws NoActiveProjectException {
	JScrollPane selectedScrollPane = (JScrollPane) editPane.getEditTabs().getSelectedComponent();
	if (selectedScrollPane == null) {
	    TreePath treePath = editPane.getDocTree().getSelectionPath();
	    if (treePath == null || treePath.getPath().length < 2)
		throw new NoActiveProjectException();
	    return ((ProjectNode) treePath.getPath()[1]).getProjectUUID();
	}
	UUID activeProjectUUID = ((EditArea) selectedScrollPane.getViewport().getView()).getProjectUUID();
	return activeProjectUUID;
    }

    class NoActiveProjectException extends Exception {
	public NoActiveProjectException() {
	    JOptionPane.showConfirmDialog(null, "You first need to select a project you want to modify.\nThis can be done by either opening a file from the project and having its tab open, or\nselecting a project and keeping it open in the documents tree.", "No project open", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
	}
    }

    class UserInfo extends JPanel {

	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permissionsView;

	private JLabel permissionLabel;
	private ButtonGroup permissionGroup;

	private final Border TILE_BORDER = BorderFactory.createEmptyBorder(2, 5, 2, 5);
	private final Font USERNAME_FONT = new Font("TW Cen MT", Font.BOLD, 20);
	private final Border TEXT_ENTRY_BORDER = BorderFactory.createLineBorder(new Color(0xB1ADFF), 2);

	private JRadioButton[] permissionSelectors = new JRadioButton[4];

	public UserInfo(String userName, FlowPermission permission) {
	    userPermission = permission;

	    setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setLayout(new BorderLayout(2, 0));
	    setBorder(TILE_BORDER);
	    // JLabel icon = new JLabel(user.getAvatar());
	    // icon.setPreferredSize(new Dimension(USER_ICON_SIZE,
	    // USER_ICON_SIZE));
	    // icon.setMinimumSize(new Dimension(USER_ICON_SIZE,
	    // USER_ICON_SIZE));
	    // add(icon, BorderLayout.WEST);
	    JPanel switcher = new JPanel(new CardLayout(0, 0));
	    switcher.setOpaque(false);
	    add(switcher, BorderLayout.CENTER);

	    simpleView = new JPanel(new BorderLayout(0, 1));
	    simpleView.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    JLabel name = new JLabel(userName) {
		public void paintComponent(Graphics g) {
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		    super.paintComponent(g2);
		}
	    };
	    name.setFont(USERNAME_FONT);
	    permissionLabel = new JLabel(userPermission.toString());
	    simpleView.add(name, BorderLayout.NORTH);
	    name.setBorder(FlowClient.EMPTY_BORDER);
	    simpleView.add(permissionLabel, BorderLayout.CENTER);
	    permissionLabel.setBorder(FlowClient.EMPTY_BORDER);
	    simpleView.setOpaque(false);
	    switcher.add(simpleView, "simple");

	    permissionsView = new JPanel(new BorderLayout(0, 0));
	    JLabel name2 = new JLabel(userName);
	    name2.setFont(USERNAME_FONT);
	    permissionsView.add(name2, BorderLayout.NORTH);
	    permissionGroup = new ButtonGroup();
	    JPanel permissionPanel = new JPanel();
	    permissionPanel.setLayout(new GridLayout(2, 2, 1, 1));
	    permissionPanel.setOpaque(false);
	    permissionsView.add(permissionPanel, BorderLayout.CENTER);
	    permissionsView.setOpaque(false);
	    switcher.add(permissionsView, "permissions");

	    setBackground(userPermission.getPermissionColor());

	    for (byte permLevel = 0; permLevel < permissionSelectors.length; permLevel++) {
		permissionSelectors[permLevel] = new JRadioButton(new FlowPermission(permLevel).toString());
		permissionSelectors[permLevel].addActionListener(new PermissionRadioButtonListener(permLevel));
		permissionSelectors[permLevel].addMouseListener(new ButtonHighlightListener());
		permissionSelectors[permLevel].setOpaque(false);
		permissionGroup.add(permissionSelectors[permLevel]);
		permissionPanel.add(permissionSelectors[permLevel]);
	    }

	    JButton saveButton = new JButton("Save");
	    saveButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    byte changePermission = FlowPermission.NONE;
		    for (byte level = 0; level < permissionSelectors.length; level++) {
			if (permissionSelectors[level].isSelected()) {
			    changePermission = level;
			    break;
			}
		    }

		    Data changePerm = new Data("project_modify");
		    changePerm.put("project_modify_type", "MODIFY_COLLABORATOR");
		    UUID projectUUID = ((EditArea) editPane.getEditTabs().getSelectedComponent()).getProjectUUID();
		    changePerm.put("project_uuid", projectUUID);
		    changePerm.put("session_id", Communicator.getSessionID());
		    changePerm.put("username", userName);
		    changePerm.put("access_level", changePermission);
		    if (!Communicator.communicate(changePerm).get("status", String.class).equals("OK")) {
			JOptionPane.showConfirmDialog(null, "Either this project does not exist,\n" + "the user does not exist,\n" + "the access level is invalid,\n" + "or you do not have the access to change permissions.\n\n" + "Try refreshing the list of projects by moving your mouse cursor to the documents tree\n" + "and back into this list of users and try again.", "Project out of sync", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    }

		    ((CardLayout) switcher.getLayout()).show(switcher, "simple");
		    updateFields();
		    CollabsList.this.refreshUserList();
		    userList.revalidate();
		    userList.repaint();
		}
	    });
	    saveButton.addMouseListener(new ButtonHighlightListener());
	    permissionPanel.add(saveButton);

	    addMouseListener(new MouseListener() {

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
		    setBorder(TILE_BORDER);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    setBorder(TEXT_ENTRY_BORDER);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    System.out.println("clicked!");
		    if (myPermission.canChangeCollabs())
			((CardLayout) switcher.getLayout()).show(switcher, "permissions");
		    permissionSelectors[userPermission.getPermissionLevel()].setSelected(true);
		    userList.revalidate();
		    userList.repaint();
		}
	    });
	}

	private void updateFields() {
	    setBackground(userPermission.getPermissionColor());
	    permissionLabel.setText(userPermission.toString());
	    revalidate();
	}

	class ButtonHighlightListener implements MouseListener {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		setBorder(FlowClient.EMPTY_BORDER);
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		setBorder(TEXT_ENTRY_BORDER);
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		// nothing
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		// nothing
	    }

	}

	class PermissionRadioButtonListener implements ActionListener {

	    private byte permLevel;

	    public PermissionRadioButtonListener(byte permLevel) {
		this.permLevel = permLevel;
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
		userPermission.setPermission(permLevel);
	    }
	}
    }

    private class UserListRenderer implements ListCellRenderer<JPanel> {

	@Override
	public Component getListCellRendererComponent(JList<? extends JPanel> list, JPanel value, int index, boolean isSelected, boolean cellHasFocus) {
	    return value;
	}
    }
}
