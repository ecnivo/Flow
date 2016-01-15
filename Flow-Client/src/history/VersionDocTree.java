package history;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import shared.DocTree;

public class VersionDocTree extends DocTree {

    public VersionDocTree(VersionViewer versionViewer) {
	super();

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		Object selected = getPathForRow(getRowForLocation(e.getX(), e.getY())).getLastPathComponent();
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && selected instanceof FileNode) {
		    FileNode selectedNode = (FileNode) selected;
		    versionViewer.setFile(selectedNode.getFileUUID(), ((ProjectNode) selectedNode.getPath()[1]).getProjectUUID());
		}
	    }
	});
    }
}
