package editing;

import gui.FlowPermission;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ShareWindow extends JFrame {
    private JPanel pane;
    private JLabel title;
    private JPanel searchPane;
    private JTextField searchBox;
    private JButton searchButton;
    private JList<UserInfo> userList;
    private JPanel findUserPane;
    private static final String SEARCHBOX_TEXT = "Search...";

    private FlowPermission myPermissions;

    public ShareWindow(FlowPermission myPermission) {
	super("Share...");
	myPermissions = myPermission;
	setVisible(true);
	pane = new JPanel(new BorderLayout(0, 0));
	add(pane);
	title = new JLabel("Share");
	pane.add(title, BorderLayout.NORTH);
	findUserPane = new JPanel(new BorderLayout(0, 5));
	pane.add(findUserPane, BorderLayout.CENTER);
	searchPane = new JPanel(new BorderLayout(0, 0));
	searchPane.add(new JLabel(
		"Type the username of the user you want to add here"),
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
	userList = new JList<UserInfo>();
	JScrollPane userListScroll = new JScrollPane(userList);
	userListScroll
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	findUserPane.add(searchPane, BorderLayout.NORTH);
	findUserPane.add(userListScroll, BorderLayout.CENTER);
    }

    class UserInfo extends JPanel {

	private String userName;
	private ImageIcon userAvatar;
	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permEdit;

	public UserInfo() {
	    // TODO accepts a UUID and gets the user's name, avatar, permission
	    // TODO don't display a UserInfo listing for themselves; do it
	    // elsewhere!
	    setLayout(new CardLayout(0, 0));

	    simpleView = new JPanel(new FlowLayout());
	    JLabel name = new JLabel(userName);
	    JLabel icon = new JLabel(userAvatar);
	    icon.setPreferredSize(new Dimension(32, 32));
	    JLabel permissionLabel = new JLabel(userPermission.toString());
	    JPanel nameAndPerm = new JPanel(new BorderLayout());
	    nameAndPerm.add(name, BorderLayout.NORTH);
	    nameAndPerm.add(permissionLabel, BorderLayout.SOUTH);
	    simpleView.add(icon);
	    simpleView.add(nameAndPerm);
	    setPreferredSize(null);
	    add(simpleView, "simple");

	    permEdit = new JPanel(new BorderLayout());
	    permEdit.add(icon, BorderLayout.WEST);
	    permEdit.add(new JLabel(userName), BorderLayout.NORTH);
	    ButtonGroup permissionGroup = new ButtonGroup();
	    JPanel permissionPanel = new JPanel();
	    permissionPanel.setLayout(new BoxLayout(permissionPanel,
		    BoxLayout.Y_AXIS));

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

	    if (myPermissions.canChangeOwner()) {
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
	    add(permissionPanel, "permissions");

	    setBackground(userPermission.getPermissionColor());
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
		    // nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (myPermissions.canChangeCollabs())
			((CardLayout) UserInfo.this.getLayout()).show(
				UserInfo.this, "permissions");
		}
	    });
	}
    }
}
