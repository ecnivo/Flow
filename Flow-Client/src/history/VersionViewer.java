package history;

import history.VersionViewer.VersionItem;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;

public class VersionViewer extends JList<VersionItem> {

    public VersionViewer() {
	setMinimumSize(new Dimension(25, 0));
	// TODO Auto-generated constructor stub
    }

    class VersionItem extends JPanel {
	public VersionItem() {
	    // TODO Auto-generated constructor stub
	}
    }
}
