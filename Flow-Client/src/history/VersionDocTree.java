package history;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import shared.DocTree;

public class VersionDocTree extends DocTree {

    private VersionViewer versionViewer;

    public VersionDocTree(VersionViewer versionViewer) {
	super();
	this.versionViewer = versionViewer;

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		Object selected = getPathForRow(
			getRowForLocation(e.getX(), e.getY()))
			.getLastPathComponent();
		if (e.getButton() == MouseEvent.BUTTON1
			&& e.getClickCount() == 2
			&& selected instanceof FileNode) {
//		    versionViewer.setFile(((FileNode) selected).getFile());
		}
	    }
	});
    }
}
