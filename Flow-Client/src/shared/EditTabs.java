
package shared;

import editing.EditPane;
import gui.FlowClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A framework to manage the many tabs open by the user
 * 
 * @author Vince Ou
 *
 */
public class EditTabs extends JTabbedPane {

	public static final int	TAB_LIMIT		= 25;
	public static final int	TAB_ICON_SIZE	= 16;

	/**
	 * Creates a new EditTabs
	 */
	public EditTabs() {
		// Swing stuff
		setMinimumSize(new Dimension(50, 0));
		setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		setTabPlacement(JTabbedPane.TOP);
		setBorder(FlowClient.EMPTY_BORDER);
		// Adds a listener to update the collabs list when tabs are switched
		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (getParent().getParent().getParent() instanceof EditPane) {
					EditPane editPane = (EditPane) getParent().getParent().getParent();
					editPane.getCollabsList().refreshUserList();
				}
			}
		});
	}

	/**
	 * So that it de-registers the FileChangeListener when the tab is closed
	 */
	@Override
	public void removeTabAt(int idx) {
		// Get component, de-register, do its super thing.
		EditArea component = (EditArea) ((JScrollPane) getComponentAt(idx)).getViewport().getView();
		Communicator.removeFileChangeListener(component.getFileUUID());
		super.removeTabAt(idx);
	}

	/**
	 * Creates a new tab.
	 * 
	 * @param tabName
	 *        the name of the tab
	 * @param text
	 *        the text in the tab
	 * @param projectUUID
	 *        the project's UUID
	 * @param fileUUID
	 *        the file's UUID
	 * @param versionTextUUID
	 *        the versionText's UUID
	 * @param editable
	 *        whether or not it should be editable
	 */
	public void openTab(String tabName, String text, UUID projectUUID, UUID fileUUID, UUID versionTextUUID, boolean editable) {
		int tabs = getTabCount();
		// Checks if the tab is already open, and if it is, will automatically switch to it
		for (int i = 0; i < tabs; i++) {
			if (((EditArea) ((JScrollPane) getComponentAt(i)).getViewport().getView()).getVersionTextUUID().equals(versionTextUUID)) {
				setSelectedIndex(i);
				return;
			}
		}
		// Tab limit of 25 is imposed because having more would make the UI look horrible and
		// generally bad.
		if (getTabCount() <= TAB_LIMIT) {
			// Opens the new tab
			addTab(tabName, new EditArea(text, projectUUID, fileUUID, versionTextUUID, editable, this).getScrollPane());
			int idx = getTabCount() - 1;
			setTabComponentAt(idx, new CustomTabHeader(tabName));
			// TODO tool tip should be the save date
		} else {
			JOptionPane.showConfirmDialog(null, "The limit on currently open tabs is 25.\nThe reason for doing so is to save processing power and reduce strain on your system.\nIf you need more than 25 tabs at a time, consider reorganizing your workflow.", "Too many tabs!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		}
		// // Forces an update on the collaborators' list
		// if (getParent().getParent().getParent() instanceof EditPane) {
		// EditPane editPane = (EditPane) getParent().getParent().getParent();
		// editPane.getCollabsList().refreshUserList();
		// }
	}

	/**
	 * A custom tab header for the JTabbedPane
	 * 
	 * @author Vince Ou
	 *
	 */
	class CustomTabHeader extends JPanel {

		private JPopupMenu	rightClickMenu;

		/**
		 * Creates a new custom header
		 * 
		 * @param fileName
		 *        the name of the header
		 */
		public CustomTabHeader(String fileName) {
			// Swing stuff
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setOpaque(false);

			// Sets an icon
			ImageIcon icon = null;
			try {
				icon = new ImageIcon(ImageIO.read(new File("images/file.png")).getScaledInstance(TAB_ICON_SIZE, TAB_ICON_SIZE, Image.SCALE_SMOOTH));
			} catch (IOException e) {
				e.printStackTrace();
			}
			JLabel iconLabel = new JLabel(icon);
			iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			add(iconLabel);

			// get title from its parent pane
			JLabel label = new JLabel(fileName);
			add(label);
			// padding
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

			// close tab button
			JButton button = new CloseTabButton();
			add(button);
			// padding!
			setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

			// New right click menu
			rightClickMenu = new JPopupMenu();
			// Close tab button
			JMenuItem closeTabButton = new JMenuItem();
			closeTabButton.addActionListener(new ActionListener() {

				/**
				 * Closes the tab when clicked
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Gets index, closes tab
					int i = EditTabs.this.indexOfTabComponent(CustomTabHeader.this);
					if (i != -1) {
						EditTabs.this.removeTabAt(i);
					}
				}
			});
			closeTabButton.setText("Close Tab (Ctrl+W)");
			closeTabButton.setEnabled(true);
			rightClickMenu.add(closeTabButton);

			// Close ALL tabs
			JMenuItem closeAllTabsButton = new JMenuItem();
			closeAllTabsButton.addActionListener(new ActionListener() {

				/**
				 * Closes all tabs when clicked
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					EditTabs.this.removeAll();
				}
			});
			closeAllTabsButton.setText("Close All Tabs");
			closeAllTabsButton.setEnabled(true);
			rightClickMenu.add(closeAllTabsButton);

			// Adds mouse click support
			addMouseListener(new MouseListener() {

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

				/**
				 * Mouse clicks are the important ones
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					int i = EditTabs.this.indexOfTabComponent(CustomTabHeader.this);
					if (i != -1) {
						// If it's a middle button, remove
						if (e.getButton() == MouseEvent.BUTTON2) {
							EditTabs.this.removeTabAt(i);
						}
						// If it's the left, then switch
						else if (e.getButton() == MouseEvent.BUTTON1) {
							EditTabs.this.setSelectedIndex(i);

						}
						// If it's the right, then switch to it
						else if (e.getButton() == MouseEvent.BUTTON3) {
							rightClickMenu.show(CustomTabHeader.this, e.getX(), e.getY());
						}
					}
				}
			});
		}

		/**
		 * Button to close the tab with
		 * 
		 * @author Vince Ou
		 *
		 */
		private class CloseTabButton extends JButton implements ActionListener {

			/**
			 * Creates a new CloseTabButton
			 */
			public CloseTabButton() {
				// Swing stuff
				setPreferredSize(new Dimension(TAB_ICON_SIZE, TAB_ICON_SIZE));
				setToolTipText("Close this file");
				setUI(new BasicButtonUI());
				setContentAreaFilled(false);
				setFocusable(false);
				setBorder(BorderFactory.createEtchedBorder());
				setBorderPainted(false);
				addMouseListener(new CloseButtonMouseListener());
				setRolloverEnabled(true);
				addActionListener(this);
			}

			/**
			 * Close tab when clicked
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = EditTabs.this.indexOfTabComponent(CustomTabHeader.this);
				if (i != -1) {
					// Got index, close tab
					EditTabs.this.removeTabAt(i);
				}
			}

			/**
			 * So that it doesn't change for different looks and feels.
			 */
			@Override
			public void updateUI() {
			}

			// Draw the X
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				// Shift it bit when pressed
				if (getModel().isPressed()) {
					g2.translate(1, 1);
				}
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				// Change colour for rollover
				if (getModel().isRollover()) {
					g2.setColor(Color.MAGENTA);
				}
				int delta = 6;
				// Draws lines
				g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
				g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
				g2.dispose();
			}

			/**
			 * Listener for the close tab button
			 * 
			 * @author Vince Ou
			 *
			 */
			private class CloseButtonMouseListener implements MouseListener {

				@Override
				public void mouseClicked(MouseEvent e) {
					// nothing
				}

				/**
				 * Paint borders as needed when moused in
				 */
				@Override
				public void mouseEntered(MouseEvent e) {
					Component component = e.getComponent();
					if (component instanceof AbstractButton) {
						AbstractButton button = (AbstractButton) component;
						button.setBorderPainted(true);
					}
				}

				/**
				 * Unpaint borders when mouse out.
				 */
				@Override
				public void mouseExited(MouseEvent e) {
					Component component = e.getComponent();
					if (component instanceof AbstractButton) {
						AbstractButton button = (AbstractButton) component;
						button.setBorderPainted(false);
					}
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

}
