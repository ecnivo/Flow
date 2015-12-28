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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class VersionViewer extends JPanel {

    private ImageIcon first;
    private ImageIcon middle;
    private ImageIcon last;

    // TODO an object here to represent a version history

    private static final int ICON_SIZE = 42;

    private JScrollPane scrolling;

    public VersionViewer() {
	// TODO should actually accept a flowfile or something as a parameter to
	// get a list of its history
	setMinimumSize(new Dimension(25, 0));
	setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new GridLayout(0, 1, 0, 0));
	scrolling = new JScrollPane(this);
	scrolling
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scrolling.getVerticalScrollBar().setUnitIncrement(
		FlowClient.SCROLL_SPEED);
	try {
	    first = new ImageIcon(ImageIO.read(
		    new File("images/firstVersion.png")).getScaledInstance(
		    ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	    middle = new ImageIcon(ImageIO.read(
		    new File("images/middleVersion.png")).getScaledInstance(
		    ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	    last = new ImageIcon(ImageIO.read(
		    new File("images/lastVersion.png")).getScaledInstance(
		    ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void updateVersions() {
	// TODO ask the server for the history of a particular file, then add
	// them as needed, but ONLY if the list of versions doesn't differ from
	// the current one stored
	removeAll();
    }

    protected void setFile() {
	// TODO this method is run when the tree is clicked for a version
	updateVersions();
    }

    class VersionItem extends JPanel {

	public VersionItem() {
	    // TODO should actually be init with a particular version
	    // TODO convert Unix time to normal and display
	    String time = "1298129";

	    setMaximumSize(new Dimension((int) Math.floor(VersionViewer.this
		    .getSize().getWidth() * .9), 80));
	    setPreferredSize(new Dimension((int) Math.floor(VersionViewer.this
		    .getSize().getWidth() * .9), 80));
	    setMinimumSize(new Dimension(5, 5));

	    setBorder(FlowClient.EMPTY_BORDER);
	    setLayout(new BorderLayout(3, 0));
	    // TODO set the icon as appropriate based on first/middle/last
	    JLabel icon = new JLabel(middle);
	    add(icon, BorderLayout.WEST);

	    JLabel changeTime = new JLabel(time);
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
		    VersionViewer.this.setBorder(BorderFactory
			    .createLineBorder(new Color(0x5C9EB4), 2));
		    updateVersions();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON1) {
			// TODO pop open a new tab and with this version for
			// user
		    }
		}
	    });
	}
    }

    public JScrollPane getScrolling() {
	return scrolling;
    }
}
