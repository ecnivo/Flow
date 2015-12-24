package history;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import gui.NavBar;
import gui.PanelManager;
import editing.DocTree;
import editing.EditTabs;

public class HistoryPane extends JSplitPane {
    private EditTabs viewer;
    private NavBar navBar;
    private VersionViewer versionViewer;
    private DocTree docTree;

    public HistoryPane(PanelManager manager) {
	setResizeWeight(0.2);

	JPanel leftSide = new JPanel();
	setLeftComponent(leftSide);
	leftSide.setLayout(new BorderLayout(0, 0));

	navBar = new NavBar(manager);
	navBar.disableButton(NavBar.HISTORY);
	leftSide.add(navBar, BorderLayout.NORTH);

	JSplitPane treeAndVersion = new JSplitPane();
	treeAndVersion.setResizeWeight(0.4);
	leftSide.add(treeAndVersion, BorderLayout.CENTER);

	versionViewer = new VersionViewer();
	treeAndVersion.setLeftComponent(versionViewer);

	viewer = new EditTabs(false);
	setRightComponent(viewer);

	docTree = new DocTree(viewer);
	treeAndVersion.setRightComponent(docTree);

    }
}
