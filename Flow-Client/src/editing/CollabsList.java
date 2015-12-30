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
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import shared.EditArea;
import shared.EditTabs;
import shared.FlowPermission;
import struct.FlowProject;
import struct.User;

@SuppressWarnings("serial")
public class CollabsList extends JPanel {
    // TODO when switching projects, clear search box and doClick on the search
    // box
    private JPanel searchPane;
    private JTextField searchBox;
    private JButton searchButton;
    private EditPane editPane;
    private JPanel userListPanel;
    private static final String SEARCHBOX_TEXT = "Search...";
    private static final int USER_ICON_SIZE = 55;
    private static final Font USERNAME_FONT = new Font("TW Cen MT", Font.BOLD,
	    20);
    private static final Border TEXT_ENTRY_BORDER = BorderFactory
	    .createLineBorder(new Color(0xB1ADFF), 2);
    private static final Border ICON_ENTRY_BORDER = BorderFactory
	    .createLineBorder(new Color(255, 128, 128), 2);
    private FlowPermission myPermission;
    private UUID currProjectUUID;

    public CollabsList(FlowPermission myPermission, EditPane editPane) {
	this.editPane = editPane;
	currProjectUUID = null;
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
		if (FlowClient.NETWORK) {
		    refreshUserList();
		    if (!(searchBox.getText().equals(SEARCHBOX_TEXT) || searchBox
			    .getText().trim().equals(""))) {
			// TODO make a search for a user
		    }
		}
	    }
	});
	searchPane.add(searchButton, BorderLayout.EAST);
	add(searchPane, BorderLayout.NORTH);

	userListPanel = new JPanel(new GridLayout(0, 1, 2, 3));
	userListPanel.setMaximumSize(new Dimension((int) Math
		.floor(CollabsList.this.getSize().getWidth()),
		Integer.MAX_VALUE));
	userListPanel.addMouseListener(new MouseListener() {

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
		refreshUserList();
	    }

	    @Override
	    public void mouseClicked(MouseEvent arg0) {
		// nothing
	    }
	});
	JScrollPane userListScroll = new JScrollPane(userListPanel);
	userListScroll.getVerticalScrollBar().setUnitIncrement(
		FlowClient.SCROLL_SPEED);
	userListScroll
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	userListScroll
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	add(userListScroll, BorderLayout.CENTER);
    }

    public void refreshUserList() {
	FlowProject active = editPane.getTree().getActiveProject();
	if (!currProjectUUID.equals(active.getProjectUUID())) {
	    currProjectUUID = active.getProjectUUID();
	    userListPanel.removeAll();

	    userListPanel.add(new UserInfo(active.getOwner(),
		    new FlowPermission(FlowPermission.OWNER)));

	    Iterator<User> editorIterator = active.getEditors().iterator();
	    while (editorIterator.hasNext()) {
		userListPanel.add(new UserInfo(editorIterator.next(),
			new FlowPermission(FlowPermission.EDIT)));
	    }

	    Iterator<User> viewerIterator = active.getViewers().iterator();
	    while (viewerIterator.hasNext()) {
		userListPanel.add(new UserInfo(viewerIterator.next(),
			new FlowPermission(FlowPermission.VIEW)));
	    }
	}
    }

    class UserInfo extends JPanel {

	private FlowPermission userPermission;

	private JPanel simpleView;
	private JPanel permissionsView;

	private JLabel permissionLabel;
	private ButtonGroup permissionGroup;

	private JRadioButton[] permissionSelectors = new JRadioButton[4];

	public UserInfo(User user, FlowPermission permission) {
	    userPermission = permission;

	    setMaximumSize(new Dimension((int) Math.floor(CollabsList.this
		    .getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(CollabsList.this
		    .getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setLayout(new BorderLayout(2, 0));
	    JLabel icon = new JLabel(user.getAvatar());
	    icon.setPreferredSize(new Dimension(USER_ICON_SIZE, USER_ICON_SIZE));
	    icon.setMinimumSize(new Dimension(USER_ICON_SIZE, USER_ICON_SIZE));
	    add(icon, BorderLayout.WEST);
	    JPanel switcher = new JPanel(new CardLayout(0, 0));
	    switcher.setOpaque(false);
	    add(switcher, BorderLayout.CENTER);

	    simpleView = new JPanel(new BorderLayout(0, 1));
	    simpleView.setMaximumSize(new Dimension((int) Math
		    .floor(CollabsList.this.getSize().getWidth() * .9), 80));
	    JLabel name = new JLabel(user.getUsername()) {
		public void paintComponent(Graphics g) {
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
			    RenderingHints.VALUE_RENDER_QUALITY);
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
	    JLabel name2 = new JLabel(user.getUsername());
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
		permissionSelectors[permLevel] = new JRadioButton(
			new FlowPermission(permLevel).toString());
		permissionSelectors[permLevel]
			.addActionListener(new PermissionRadioButtonListener(
				permLevel));
		permissionSelectors[permLevel]
			.addMouseListener(new ButtonHighlightListener());
		permissionSelectors[permLevel].setOpaque(false);
		permissionGroup.add(permissionSelectors[permLevel]);
		permissionPanel.add(permissionSelectors[permLevel]);
	    }

	    JButton saveButton = new JButton("Save");
	    saveButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		    // TODO formally send a change in permissions to server
		    ((CardLayout) switcher.getLayout())
			    .show(switcher, "simple");
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
			((CardLayout) switcher.getLayout()).show(switcher,
				"permissions");
		    permissionSelectors[userPermission.getPermissionLevel()]
			    .setSelected(true);
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
