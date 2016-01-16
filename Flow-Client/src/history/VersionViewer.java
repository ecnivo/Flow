package history;

import gui.FlowClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import message.Data;
import shared.Communicator;
import shared.EditTabs;

@SuppressWarnings("serial")
public class VersionViewer extends JPanel {
    private ImageIcon middle;

    private UUID fileUUID;
    private UUID projectUUID;

    private static final int ICON_SIZE = 42;

    // private FlowFile file;
    private HistoryPane historyPane;
    private JScrollPane scrolling;

    public VersionViewer(HistoryPane hp) {
	historyPane = hp;
	setBackground(Color.WHITE);
	setMinimumSize(new Dimension(25, 0));
	// setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new GridLayout(0, 1, 0, 0));
	scrolling = new JScrollPane(this);
	scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scrolling.getVerticalScrollBar().setUnitIncrement(FlowClient.SCROLL_SPEED);
	try {
	    middle = new ImageIcon(ImageIO.read(new File("images/middleVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param flowFileUUID
     */
    public void setFile(UUID flowFileUUID, UUID projectUUID) {
	fileUUID = flowFileUUID;
	this.projectUUID = projectUUID;
	updateVersions();
    }

    /**
     */
    private void updateVersions() {
	removeAll();

	Data fileInfoRequest = new Data("file_info");
	fileInfoRequest.put("file_uuid", fileUUID);
	fileInfoRequest.put("session_id", Communicator.getSessionID());
	Data fileInfo = Communicator.communicate(fileInfoRequest);
	UUID[] versions = fileInfo.get("file_versions", UUID[].class);
	boolean isText = false;
	switch (fileInfo.get("file_type", String.class)) {
	case "TEXT_DOCUMENT":
	    isText = true;
	    break;

	case "ARBITRARY_DOCUMENT":
	    isText = false;
	    break;
	}

	for (UUID versionUUID : versions) {
	    Data versionDataRequest = new Data("version_request");
	    versionDataRequest.put("file_uuid", fileUUID);
	    versionDataRequest.put("version_uuid", versionUUID);
	    versionDataRequest.put("session_id", Communicator.getSessionID());
	    byte[] versionData = Communicator.communicate(versionDataRequest).get("version_data", byte[].class);

	    Data versionInfoRequest = new Data("version_info");
	    versionInfoRequest.put("version_uuid", versionUUID);
	    versionInfoRequest.put("file_uuid", fileUUID);
	    versionInfoRequest.put("session_id", Communicator.getSessionID());
	    Date saveDate = new Date(Communicator.communicate(versionInfoRequest).get("date", Long.class).longValue());

	    VersionItem item = new VersionItem(versionData, saveDate, versionUUID, isText);

	    add(item);
	}

	revalidate();
	repaint();
    }

    class VersionItem extends JPanel {

	public VersionItem(byte[] data, Date date, UUID versionUUID, boolean isText) {
	    setMaximumSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setBorder(FlowClient.EMPTY_BORDER);
	    setLayout(new BorderLayout(3, 0));
	    JLabel icon = new JLabel(middle);
	    add(icon, BorderLayout.WEST);

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

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1) {
			if (isText) {
			    EditTabs tabs = historyPane.getEditTabs();
			    if (tabs == null)
				return;
			    String stringDate = date.toString();
			    String stringData = new String(data);
			    tabs.openTab(stringDate, stringData, projectUUID, fileUUID, versionUUID, false);
			} else
			    throw new UnsupportedOperationException();
			// TODO find a way to open past arbit files in desktop
		    }
		}
	    });
	}
    }

    public JScrollPane getScrolling() {
	return scrolling;
    }
}
