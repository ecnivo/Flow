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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
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
    private JPanel userListPanel;
    private static final String SEARCHBOX_TEXT = "Search...";
    // private static final int USER_ICON_SIZE = 55;
    private static final Font USERNAME_FONT = new Font("TW Cen MT", Font.BOLD, 20);
    private static final Border TEXT_ENTRY_BORDER = BorderFactory.createLineBorder(new Color(0xB1ADFF), 2);
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
		    refreshUserList();
		    if (!(searchBox.getText().equals(SEARCHBOX_TEXT) || searchBox.getText().trim().equals(""))) {
			// TODO make a search for a user
		    }
		}
	    }
	});
	try {
	    searchButton.setIcon(new ImageIcon(ImageIO.read(new File("images/addCollab.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE - 3, FlowClient.BUTTON_ICON_SIZE - 3, Image.SCALE_SMOOTH)));
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	searchPane.add(searchButton, BorderLayout.EAST);
	add(searchPane, BorderLayout.NORTH);

	userListPanel = new JPanel(new GridLayout(0, 1, 2, 3));
	userListPanel.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth()), Integer.MAX_VALUE));
	JScrollPane userListScroll = new JScrollPane(userListPanel);
	userListScroll.getVerticalScrollBar().setUnitIncrement(FlowClient.SCROLL_SPEED);
	userListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	userListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	add(userListScroll, BorderLayout.CENTER);
    }

    public void refreshUserList() {
	TreePath activeSelection = editPane.getDocTree().getSelectionPath();
	if (activeSelection == null) {
	    return;
	}
	DefaultMutableTreeNode[] path = (DefaultMutableTreeNode[]) activeSelection.getPath();
	if (path.length < 2) {
	    return;
	}
	UUID activeProjectUUID = ((ProjectNode) path[1]).getProjectUUID();

	Data getProject = new Data("project_info");
	getProject.put("project_uuid", activeProjectUUID);
	Data activeProject = Communicator.communicate(getProject);

	userListPanel.removeAll();

	userListPanel.add(new UserInfo(activeProject.get("owner", String.class), new FlowPermission(FlowPermission.OWNER)));

	String[] editors = activeProject.get("editors", String[].class);
	for (String editor : editors) {
	    userListPanel.add(new UserInfo(editor, new FlowPermission(FlowPermission.EDIT)));
	}

	String[] viewers = activeProject.get("viewers", String[].class);
	for (String viewer : viewers) {
	    userListPanel.add(new UserInfo(viewer, new FlowPermission(FlowPermission.VIEW)));
	}

	searchBox.setText(SEARCHBOX_TEXT);
	searchBox.setForeground(Color.black);

    }

    class UserInfo extends JPanel {

	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permissionsView;

	private JLabel permissionLabel;
	private ButtonGroup permissionGroup;

	private JRadioButton[] permissionSelectors = new JRadioButton[4];

	public UserInfo(String user, FlowPermission permission) {
	    userPermission = permission;

	    setMaximumSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setLayout(new BorderLayout(2, 0));
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
	    JLabel name = new JLabel(user) {
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
	    JLabel name2 = new JLabel(user);
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
		    changePerm.put("username", user);
		    changePerm.put("access_level", changePermission);
		    if (!Communicator.communicate(changePerm).get("status", String.class).equals("OK")) {
			JOptionPane.showConfirmDialog(null, "Either this project does not exist,\n" + "the user does not exist,\n" + "the access level is invalid,\n" + "or you do not have the access to change permissions.\n\n" + "Try refreshing the list of projects by moving your mouse cursor to the documents tree\n" + "and back into this list of users and try again.", "Project out of sync", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		    }

		    ((CardLayout) switcher.getLayout()).show(switcher, "simple");
		    updateFields();
		    CollabsList.this.refreshUserList();
		}
	    });
	    saveButton.addMouseListener(new ButtonHighlightListener());
	    permissionPanel.add(saveButton);

	    switcher.addMouseListener(new MouseListener() {

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
		    setBorder(FlowClient.EMPTY_BORDER);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    setBorder(TEXT_ENTRY_BORDER);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (myPermission.canChangeCollabs())
			((CardLayout) switcher.getLayout()).show(switcher, "permissions");
		    permissionSelectors[userPermission.getPermissionLevel()].setSelected(true);
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

}
