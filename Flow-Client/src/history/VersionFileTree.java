
package history;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import shared.FileTree;

/**
 * A special file tree for version history management
 * 
 * @author Vince
 *
 */
public class VersionFileTree extends FileTree {

	/**
	 * Creates a new VersionFileTree
	 * 
	 * @param versionViewer
	 *        the associated VersionViewer to open the file in
	 */
	public VersionFileTree(VersionViewer versionViewer) {
		super();

		addMouseListener(new MouseAdapter() {

			/**
			 * When something is selected, it will open its version history information in the
			 * second window.
			 */
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
