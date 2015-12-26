package editing;

import gui.FlowClient;
import gui.FlowPermission;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class CollabsList extends JPanel {
    private JPanel searchPane;
    private JTextField searchBox;
    private JButton searchButton;
    private JPanel userList;
    private static final String SEARCHBOX_TEXT = "Search...";
    private static final int USER_ICON_SIZE = 55;
    private FlowPermission myPermission;

    public CollabsList(FlowPermission myPermission) {
	this.myPermission = myPermission;
	setMinimumSize(new Dimension(5, 1));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new BorderLayout(0, 0));

	searchPane = new JPanel(new BorderLayout(0, 0));
	searchPane.add(new JLabel("Type a username to seach for them"),
		BorderLayout.NORTH);

	searchBox = new JTextField();
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
		if (searchBox.getText().equals(SEARCHBOX_TEXT)
			|| searchBox.getText().trim().equals("")) {
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
		// TODO starts a search if the search box contents are
		// "searchboxtext" or nothing
	    }
	});
	searchPane.add(searchButton, BorderLayout.EAST);
	add(searchPane, BorderLayout.NORTH);

	userList = new JPanel(new GridLayout(0, 1, 2, 3));
	userList.setMaximumSize(new Dimension((int) Math.floor(CollabsList.this
		.getSize().getWidth()), Integer.MAX_VALUE));
	JScrollPane userListScroll = new JScrollPane(userList);
	userListScroll
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	userListScroll
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	add(userListScroll, BorderLayout.CENTER);
	updateUsers();
    }

    private void updateUsers() {
	// TODO add the current project's collaborators first, then add all
	// other users in userList
	ImageIcon icon = null;
	try {
	    icon = new ImageIcon(ImageIO.read(
		    new File("D:/My Pictures/happysad face.png"))
		    .getScaledInstance(USER_ICON_SIZE, USER_ICON_SIZE,
			    Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname", icon, new FlowPermission(
		FlowPermission.OWNER)));

	try {
	    icon = new ImageIcon(ImageIO.read(new File("D:/My Pictures/h.jpg"))
		    .getScaledInstance(USER_ICON_SIZE, USER_ICON_SIZE,
			    Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname2", icon, new FlowPermission(
		FlowPermission.EDIT)));

	try {
	    icon = new ImageIcon(ImageIO.read(
		    new File("D:/My Pictures/v-3.jpg")).getScaledInstance(
		    USER_ICON_SIZE, USER_ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname3", icon, new FlowPermission(
		FlowPermission.EDIT)));
    }

    class UserInfo extends JPanel {

	private String userName;
	private ImageIcon userAvatar;
	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permissionsView;

	public UserInfo(String username, ImageIcon avatar,
		FlowPermission permission) {
	    // TODO don't display a UserInfo listing for themselves; do it
	    // elsewhere!
	    userName = username;
	    userAvatar = avatar;
	    userPermission = permission;

	    setMaximumSize(new Dimension((int) Math.floor(CollabsList.this
		    .getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(CollabsList.this
		    .getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
	    JLabel icon = new JLabel(userAvatar);
	    icon.setPreferredSize(new Dimension(32, 32));
	    add(icon);
	    JPanel switcher = new JPanel(new CardLayout(0, 0));

	    simpleView = new JPanel(new BorderLayout(0, 0));
	    JLabel name = new JLabel(userName);
	    JLabel permissionLabel = new JLabel(userPermission.toString());
	    simpleView.add(name, BorderLayout.NORTH);
	    simpleView.add(permissionLabel, BorderLayout.SOUTH);
	    switcher.add(simpleView, "simple");

	    permissionsView = new JPanel(new BorderLayout(0, 0));
	    permissionsView.add(new JLabel(userName), BorderLayout.NORTH);
	    ButtonGroup permissionGroup = new ButtonGroup();
	    JPanel permissionPanel = new JPanel();
	    permissionPanel.setLayout(new BoxLayout(permissionPanel,
		    BoxLayout.Y_AXIS));
	    permissionsView.add(permissionPanel, BorderLayout.CENTER);
	    switcher.add(permissionPanel, "permissions");

	    setBackground(userPermission.getPermissionColor());
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
		    // nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (myPermission.canChangeCollabs())
			((CardLayout) UserInfo.this.getLayout()).show(
				UserInfo.this, "permissions");
		}
	    });
	    icon.addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mousePressed(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseExited(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    // TODO Scroll the current EditArea to the cursor of this
		    // user
		}
	    });

	    JRadioButton noButton = new JRadioButton("None (un-invite)");
	    noButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.NONE);
		}
	    });
	    permissionGroup.add(noButton);
	    permissionPanel.add(noButton);

	    JRadioButton viewButton = new JRadioButton("View only");
	    viewButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.VIEW);
		}
	    });
	    permissionPanel.add(viewButton);
	    permissionGroup.add(viewButton);

	    JRadioButton editButton = new JRadioButton("Edit");
	    editButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.EDIT);
		}
	    });
	    permissionPanel.add(editButton);
	    permissionGroup.add(editButton);

	    if (myPermission.canChangeOwner()) {
		JRadioButton ownerButton = new JRadioButton("Owner");
		ownerButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			// TODO Show a Joptionpane to confirm, and don't forget
			// to set the current user to be an editor
			userPermission.setPermission(FlowPermission.OWNER);
		    }
		});
		permissionPanel.add(ownerButton);
		permissionGroup.add(ownerButton);
	    }

	    JButton saveButton = new JButton("Save");
	    saveButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    // TODO formally send a change in editors to server
		    ((CardLayout) UserInfo.this.getLayout()).show(
			    UserInfo.this, "simple");
		}
	    });
	    permissionPanel.add(saveButton);
	}
    }
}
