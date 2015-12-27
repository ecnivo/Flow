package editing;

import gui.FlowClient;
import gui.FlowPermission;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CollabsList extends JPanel {
    private JPanel searchPane;
    private JTextField searchBox;
    private JButton searchButton;
    private JPanel userList;
    private static final String SEARCHBOX_TEXT = "Search...";
    private static final int USER_ICON_SIZE = 55;
    private static final Font USERNAME_FONT = new Font("TW Cen MT", Font.BOLD,
	    16);
    private static final Border TEXT_ENTRY_BORDER = BorderFactory
	    .createLineBorder(new Color(0xB1ADFF), 2);
    private static final Border ICON_ENTRY_BORDER = BorderFactory
	    .createLineBorder(new Color(255, 128, 128), 2);
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
	userListScroll.getVerticalScrollBar().setUnitIncrement(12);
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

	try {
	    icon = new ImageIcon(ImageIO.read(
		    new File("D:/My Pictures/v-3.jpg")).getScaledInstance(
		    USER_ICON_SIZE, USER_ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname4", icon, new FlowPermission(
		FlowPermission.EDIT)));

	try {
	    icon = new ImageIcon(ImageIO.read(
		    new File("D:/My Pictures/v-3.jpg")).getScaledInstance(
		    USER_ICON_SIZE, USER_ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname5", icon, new FlowPermission(
		FlowPermission.EDIT)));

	try {
	    icon = new ImageIcon(ImageIO.read(
		    new File("D:/My Pictures/v-3.jpg")).getScaledInstance(
		    USER_ICON_SIZE, USER_ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	userList.add(new UserInfo("testname6", icon, new FlowPermission(
		FlowPermission.EDIT)));
    }

    /**
     * For displaying a list of users after a search has been completed.
     * 
     * @param matches
     *            the list of search results
     */
    private void updateUsers(UserInfo[] matches) {
	throw new NotImplementedException();
    }

    class UserInfo extends JPanel {

	private String userName;
	private ImageIcon userAvatar;
	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permissionsView;

	private JLabel permissionLabel;

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

	    setLayout(new BorderLayout(2, 0));
	    JLabel icon = new JLabel(userAvatar);
	    icon.setPreferredSize(new Dimension(USER_ICON_SIZE, USER_ICON_SIZE));
	    icon.setMinimumSize(new Dimension(USER_ICON_SIZE, USER_ICON_SIZE));
	    add(icon, BorderLayout.WEST);
	    JPanel switcher = new JPanel(new CardLayout(0, 0));
	    switcher.setOpaque(false);
	    add(switcher, BorderLayout.CENTER);

	    simpleView = new JPanel(new BorderLayout(0, 1));
	    simpleView.setMaximumSize(new Dimension((int) Math
		    .floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    JLabel name = new JLabel(userName);
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
	    ButtonGroup permissionGroup = new ButtonGroup();
	    JPanel permissionPanel = new JPanel();
	    permissionPanel.setLayout(new GridLayout(2, 2, 1, 1));
	    permissionPanel.setOpaque(false);
	    permissionsView.add(permissionPanel, BorderLayout.CENTER);
	    permissionsView.setOpaque(false);
	    switcher.add(permissionsView, "permissions");

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
		    setBorder(FlowClient.EMPTY_BORDER);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    setBorder(TEXT_ENTRY_BORDER);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (myPermission.canChangeCollabs())
			((CardLayout) switcher.getLayout()).show(switcher,
				"permissions");
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
		    setBorder(FlowClient.EMPTY_BORDER);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    setBorder(ICON_ENTRY_BORDER);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    // TODO Scroll the current EditArea to the cursor of this
		    // user
		}
	    });

	    JRadioButton noButton = new JRadioButton(new FlowPermission(
		    FlowPermission.NONE).toString());
	    noButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.NONE);
		}
	    });
	    noButton.addMouseListener(new ButtonHighlightListener());
	    noButton.setOpaque(false);
	    permissionGroup.add(noButton);
	    permissionPanel.add(noButton);

	    JRadioButton viewButton = new JRadioButton(new FlowPermission(
		    FlowPermission.VIEW).toString());
	    viewButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.VIEW);
		}
	    });
	    viewButton.addMouseListener(new ButtonHighlightListener());
	    viewButton.setOpaque(false);
	    permissionPanel.add(viewButton);
	    permissionGroup.add(viewButton);

	    JRadioButton editButton = new JRadioButton(new FlowPermission(
		    FlowPermission.EDIT).toString());
	    editButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    userPermission.setPermission(FlowPermission.EDIT);
		}
	    });
	    editButton.addMouseListener(new ButtonHighlightListener());
	    editButton.setOpaque(false);
	    permissionPanel.add(editButton);
	    permissionGroup.add(editButton);

	    if (myPermission.canChangeOwner()) {
		JRadioButton ownerButton = new JRadioButton(new FlowPermission(
			FlowPermission.OWNER).toString());
		ownerButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
			// TODO Show a Joptionpane to confirm, and don't forget
			// to set the current user to be an editor
			userPermission.setPermission(FlowPermission.OWNER);
		    }
		});
		ownerButton.addMouseListener(new ButtonHighlightListener());
		ownerButton.setOpaque(false);
		permissionPanel.add(ownerButton);
		permissionGroup.add(ownerButton);
	    }

	    JButton saveButton = new JButton("Save");
	    saveButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    // TODO formally send a change in editors to server
		    ((CardLayout) switcher.getLayout())
			    .show(switcher, "simple");
		    updateFields();
		}
	    });
	    saveButton.addMouseListener(new ButtonHighlightListener());
	    permissionPanel.add(saveButton);

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
    }

}
