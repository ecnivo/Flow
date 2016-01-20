
package history;

import gui.FlowClient;
import gui.PanelManager;
import shared.EditTabs;
import shared.NavBar;

import javax.swing.*;
import java.awt.*;

/**
 * The panel that manages all of version history
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class HistoryPane extends JSplitPane {

	private final VersionFileTree tree;

	/**
	 * Creates a new HistoryPane
	 * @param manager the associated PanelManager
	 */
	public HistoryPane(PanelManager manager) {
		// Sets up the jsplitpane
		setResizeWeight(0.2);
		setBorder(FlowClient.EMPTY_BORDER);
		setContinuousLayout(true);

		// Left side is the documents tree and the history for a particular file
		JPanel leftSide = new JPanel(new BorderLayout(0, 0));
		leftSide.setBorder(FlowClient.EMPTY_BORDER);
		Dimension leftSize = new Dimension(200, 32);
		leftSide.setMinimumSize(leftSize);
		leftSide.setPreferredSize(leftSize);
		setLeftComponent(leftSide);
		NavBar navBar = new NavBar(manager);
		navBar.disableButton(NavBar.HISTORY);
		leftSide.add(navBar, BorderLayout.NORTH);

		// The left side is also split again, and is added
		JSplitPane treeAndVersion = new JSplitPane();
		treeAndVersion.setContinuousLayout(true);
		treeAndVersion.setBorder(FlowClient.EMPTY_BORDER);
		treeAndVersion.setResizeWeight(0.4);
		leftSide.add(treeAndVersion, BorderLayout.CENTER);

		VersionViewer versionViewer = new VersionViewer(this);
		treeAndVersion.setRightComponent(versionViewer);

		tree = new VersionFileTree(versionViewer);
		treeAndVersion.setLeftComponent(tree.getScrollable());

	}

	/**
	 * Adds the editTabs into the client
	 * @param editTabs the editTabs to add
	 */
	public void addEditTabs(EditTabs editTabs) {
		setRightComponent(editTabs);
	}

	/**
	 * Gets the documents tree
	 * @return the document tree
	 */
	public VersionFileTree getTree() {
		return tree;
	}

	/**
	 * Gets the editTabs
	 * @return the editTabs
	 */
	public EditTabs getEditTabs() {
		if (getRightComponent() instanceof EditTabs) {
			return (EditTabs) getRightComponent();
		}
		return null;
	}
}
