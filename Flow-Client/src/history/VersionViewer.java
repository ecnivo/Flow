
package history;

import gui.FlowClient;
import message.Data;
import shared.Communicator;
import shared.EditTabs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * A viewer for the many versions of one file
 * 
 * @author Vince
 *
 */
@SuppressWarnings("serial")
class VersionViewer extends JPanel {

	private ImageIcon			middle;

	private UUID				fileUUID;
	private UUID				projectUUID;

	private static final int	ICON_SIZE	= 42;

	// private FlowFile file;
	private final HistoryPane historyPane;
	private final JScrollPane scrolling;

	private boolean				isText;

	/**
	 * Creates a new VersionViewer
	 * 
	 * @param hp
	 *        the historyPane that this is associated with
	 */
	public VersionViewer(HistoryPane hp) {
		// init
		historyPane = hp;
		setBackground(Color.WHITE);
		setMinimumSize(new Dimension(25, 0));
		// setBorder(FlowClient.EMPTY_BORDER);
		setLayout(new GridLayout(0, 1, 0, 0));
		// Enables scrolling
		scrolling = new JScrollPane(this);
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrolling.getVerticalScrollBar().setUnitIncrement(FlowClient.SCROLL_SPEED);
		// Sets icon
		try {
			middle = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/middleVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a file based on its UUID
	 * 
	 * @param flowFileUUID
	 *        the flowFile's UUID
	 * @param projectUUID
	 *        the project's UUID
	 */
	public void setFile(UUID flowFileUUID, UUID projectUUID) {
		fileUUID = flowFileUUID;
		this.projectUUID = projectUUID;
		updateVersions();
	}

	/**
	 * Forces an update of the versions
	 */
	private void updateVersions() {
		removeAll();

		// Asks the server for the versions to update
		Data fileInfoRequest = new Data("file_info");
		fileInfoRequest.put("file_uuid", fileUUID);
		Data fileInfo = Communicator.communicate(fileInfoRequest);
		if (fileInfo.get("status", String.class).equals("ACCESS_DENIED"))
			JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		UUID[] versions = fileInfo.get("file_versions", UUID[].class);
		boolean isFileText = false;
		// Does different things based on text/arbitrary type files
		switch (fileInfo.get("file_type", String.class)) {
			case "TEXT_DOCUMENT":
				isFileText = true;
				break;

			case "ARBITRARY_DOCUMENT":
				isFileText = false;
				break;
		}
		isText = isFileText;

		// Creates a new "tile" for each version
		for (UUID versionUUID : versions) {
			// Ask the server for the version's information
			Data versionDataRequest = new Data("version_request");
			versionDataRequest.put("file_uuid", fileUUID);
			versionDataRequest.put("version_uuid", versionUUID);
			Data versionRequestResponse = Communicator.communicate(versionDataRequest);
			if (versionRequestResponse.get("status", String.class).equals("ACCESS_DENIED"))
				JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			byte[] versionData = versionRequestResponse.get("version_data", byte[].class);

			// More version information is requested
			Data versionInfoRequest = new Data("version_info");
			versionInfoRequest.put("version_uuid", versionUUID);
			versionInfoRequest.put("file_uuid", fileUUID);
			// Gets the date from a millis value
			Data versionInfo = Communicator.communicate(versionInfoRequest);
			if (versionInfo.get("status", String.class).equals("ACCESS_DENIED"))
				JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			Date saveDate = new Date(versionInfo.get("date", Long.class).longValue());

			// Creates a new VersionItem and adds it
			VersionItem item = new VersionItem(versionData, saveDate, versionUUID);

			add(item);
		}
		// Refresh
		revalidate();
		repaint();
	}

	/**
	 * Reperesents one version of a file
	 * 
	 * @author Vince Ou
	 *
	 */
	class VersionItem extends JPanel {

		/**
		 * Creates a new VersionItem
		 * 
		 * @param data
		 *        the contents of the version
		 * @param date
		 *        the date it was saved on
		 * @param versionUUID
		 *        the UUID of the version
		 */
		public VersionItem(byte[] data, Date date, UUID versionUUID) {
			// Swing setup
			setMaximumSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
			setPreferredSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
			setMinimumSize(new Dimension(5, 5));
			// Adds an icon
			setBorder(FlowClient.EMPTY_BORDER);
			setLayout(new BorderLayout(3, 0));
			JLabel icon = new JLabel(middle);
			add(icon, BorderLayout.WEST);
			// Adds the time of change
			JLabel changeTime = new JLabel(date.toString());
			add(changeTime, BorderLayout.CENTER);

			addMouseListener(new MouseListener() {

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
					VersionViewer.this.setBorder(FlowClient.EMPTY_BORDER);
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					VersionViewer.this.setBorder(BorderFactory.createLineBorder(new Color(0x5C9EB4), 2));
					// updateVersions();
				}

				/**
				 * When clicked, it will open the file up in a new tab
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (isText) {
							EditTabs tabs = historyPane.getEditTabs();
							if (tabs == null)
								return;
							String stringDate = date.toString();
							String stringData = new String(data);
							// Opens the file
							tabs.openTab(stringDate, stringData, projectUUID, fileUUID, versionUUID, false);
						} else {
							throw new UnsupportedOperationException();
							// TODO find a way to open past arbit files in desktop
						}
					}
				}
			});
		}
	}

	/**
	 * Gets the scrolling pane
	 * 
	 * @return the JScrollPane
	 */
	public JScrollPane getScrolling() {
		return scrolling;
	}
}
