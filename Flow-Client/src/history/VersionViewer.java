package history;

import gui.FlowClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import shared.EditTabs;
import struct.TextDocument;

@SuppressWarnings("serial")
public class VersionViewer extends JPanel {

    private ImageIcon first;
    private ImageIcon middle;

    private static final int FIRST = 0;
    private static final int MIDDLE = 1;
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
	    first = new ImageIcon(ImageIO.read(new File("images/firstVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	    middle = new ImageIcon(ImageIO.read(new File("images/middleVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

//    /**
//     * @deprecated
//     * @param flowFile
//     */
//    public void setFile(FlowFile flowFile) {
//	file = flowFile;
//	updateVersions();
//    }
//
//    /**
//     * @deprecated
//     */
//    private void updateVersions() {
//	removeAll();
//
//	TreeSet<FlowDocument> versions = file.getVersions();
//	Iterator<FlowDocument> versionIterator = versions.iterator();
//
//	while (versionIterator.hasNext()) {
//	    FlowDocument doc = versionIterator.next();
//	    add(new VersionItem(doc, doc.getVersionDate(), 1));
//	}
//
//	revalidate();
//	repaint();
//    }

    class VersionItem extends JPanel {

	public VersionItem(UUID doc, Date date, int position) {
	    // TODO all of versioning
	    setMaximumSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(VersionViewer.this.getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setBorder(FlowClient.EMPTY_BORDER);
	    setLayout(new BorderLayout(3, 0));
	    JLabel icon = null;
	    switch (position) {
	    case FIRST:
		icon = new JLabel(first);
		break;

	    case MIDDLE:
		icon = new JLabel(middle);
		break;

	    default:
		return;
	    }
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
//		    updateVersions();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
//			if (doc instanceof TextDocument) {
//			    EditTabs tabs = historyPane.getEditTabs();
//			    if (tabs != null)
//				tabs.openTab((TextDocument) doc, false);
//			} else if (doc instanceof ArbitraryDocument)
			    try {
				Desktop.getDesktop().open(new File("blarghs"));
			    } catch (IOException e1) {
				e1.printStackTrace();
			    }
			// TODO find a way to open past arbitrary files in
			// desktop
		    }
		}
	    });
	}
    }

    public JScrollPane getScrolling() {
	return scrolling;
    }
}
