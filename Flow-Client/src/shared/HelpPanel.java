package shared;

import gui.FlowClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
class HelpPanel extends JTabbedPane {

	private final static Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
	private final static Font TITLE_FONT = new Font("Tw Cen MT", Font.BOLD, 18);

	public HelpPanel() {
		setTabPlacement(JTabbedPane.TOP);
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setMinimumSize(new Dimension(150, 150));

		addTab("Introduction", "Welcome to Flow. Flow is a program where you can collaborate on code. Flow is based around \"projects\", which you can share with other users. The Flow interface consists of several sub-windows, which you can resize to your liking.");
		addTab("Files", "Your files are organized in the file tree on the left side of the screen. The \"Workspace\" contains all of your projects. Your Flow Projects have the flow icon around them, and each project can contain directories and source code files. Directories can contain other directories as well. If you want to import or export a file, you can do so with the buttons on the toolbar at the top. Double clicking a file will open a new tab in the middle, where you can edit it. Files are automatically synchronized with the server, live, as you type.");
		addTab("Sharing", "Sharing in Flow is done by project. The sharing window is at the bottom right. You must first select a project or open a file. When that is done, the sharing window will update itself with a list of users that the project is shared with. These users have four levels of permissions: None, View, Edit, or Owner. New projects that you create yourself will make you the owner. Edit permissions let you edit any file inside the project, as well as deleting or creating new files. However, editors may NOT delete the project or rename it. Viewers may see anything inside the project, but they may not modify it. Flow users with no permissions will not see the file in their file tree. If you would like to add someone to your project, simply type their username inside the search box, and click the \"Add\" button. If you would like to change the permissions for a user, you can click their user tile in the users list.");
		addTab("Executing", "Flow is built around code, and executing code is an important part of the integrated development environment. To execute code, open the tab that contains the project that you would like to run, and click the blue \"run\" button in the tool bar. To stop the execution of code, click the red square. Build path setup is done in the settings.");
	}

	private void addTab(String title, String contents) {
		HelpTab intro = new HelpTab(title);
		JLabel introTitle = new JLabel(title);
		introTitle.setFont(TITLE_FONT);
		intro.add(introTitle);
		intro.add(new CustomTextArea(contents));
		intro.revalidate();
		intro.repaint();
	}

	private class HelpTab extends JPanel {

		private final JScrollPane scrolling;
		private final ArrayList<Component> children;
		private final SpringLayout layout;
		private final static int SEP_GAP = 25;

		private HelpTab(String name) {
			// Just a bunch of settings
			setMinimumSize(new Dimension(150, 150));
			setPreferredSize(new Dimension(300,300));
			children = new ArrayList<>();
			setBorder(FlowClient.EMPTY_BORDER);
			layout = new SpringLayout();
			setLayout(layout);
			scrolling = new JScrollPane(this);
			scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			HelpPanel.this.addTab(name, scrolling);
		}

		@Override
		public Component add(Component component) {
			// Does swing layout stuff
			layout.putConstraint(SpringLayout.WEST, component, SEP_GAP, SpringLayout.WEST, HelpPanel.this);

			if (children.size() == 0) {
				layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP, SpringLayout.NORTH, HelpPanel.this);
			} else {
				layout.putConstraint(SpringLayout.NORTH, component, SEP_GAP, SpringLayout.NORTH, children.get(children.size() - 1));
			}
			super.add(component);
			children.add(component);
			return component;
		}
	}

	private class CustomTextArea extends JTextArea {

		public CustomTextArea(String text) {
			super(text);
			setMinimumSize(new Dimension(150, 150));
			setPreferredSize(new Dimension(250,300));
			setFont(CONTENT_FONT);
			setWrapStyleWord(true);
			setLineWrap(true);
			setEditable(false);
			setForeground(Color.BLACK);
			setOpaque(true);
			setFocusable(false);
		}
	}
}
