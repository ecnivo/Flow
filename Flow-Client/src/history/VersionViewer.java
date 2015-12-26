package history;

import gui.FlowClient;
import history.VersionViewer.VersionItem;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;

public class VersionViewer extends JList<VersionItem> {

    public VersionViewer() {
	setMinimumSize(new Dimension(25, 0));
	setBorder(FlowClient.EMPTY_BORDER);
	// TODO Auto-generated constructor stub
    }

    class VersionItem extends JPanel {
	public VersionItem() {
	    setBorder(FlowClient.EMPTY_BORDER);
	    // TODO should actually be init with a version timestamp or
	    // something
	}
    }
}
