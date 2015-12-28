package history;

import gui.FlowClient;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class VersionViewer extends JPanel {

    private static ImageIcon first;
    private static ImageIcon middle;
    private static ImageIcon last;
    
    private static final int ICON_SIZE = 42;

    private JScrollPane scrolling;

    public VersionViewer() {
	// TODO should actually accept a flowfile or something as a parameter to
	// get a list of its history
	setMinimumSize(new Dimension(25, 0));
	setBorder(FlowClient.EMPTY_BORDER);
	scrolling = new JScrollPane(this);
	scrolling
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scrolling.getVerticalScrollBar().setUnitIncrement(
		FlowClient.SCROLL_SPEED);
    }

    class VersionItem extends JPanel {

	public VersionItem() {
	    setBorder(FlowClient.EMPTY_BORDER);
	    // TODO should actually be init with a particular version
	}
    }

    public JScrollPane getScrolling() {
	return scrolling;
    }

    public static void initImages() {
	try {
	    first = new ImageIcon(ImageIO.read(new File(
		    "images/firstVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	    middle = new ImageIcon(ImageIO.read(new File(
		    "images/middleVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	    last = new ImageIcon(
		    ImageIO.read(new File("images/lastVersion.png")).getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
