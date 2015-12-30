package history;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.DocTree;
import editing.EditTabs;
import gui.FlowClient;
import gui.NavBar;
import gui.PanelManager;

public class HistoryPane extends JSplitPane {
    private EditTabs viewer;
    private NavBar navBar;
    private VersionViewer versionViewer;
    private DocTree docTree;

    public HistoryPane(PanelManager manager) {
	setResizeWeight(0.2);
	setBorder(FlowClient.EMPTY_BORDER);
	setContinuousLayout(true);

	JPanel leftSide = new JPanel(new BorderLayout(0, 0));
	leftSide.setBorder(FlowClient.EMPTY_BORDER);
	Dimension leftSize = new Dimension(200, 32);
	leftSide.setMinimumSize(leftSize);
	leftSide.setPreferredSize(leftSize);
	setLeftComponent(leftSide);
	navBar = new NavBar(manager);
	navBar.disableButton(NavBar.HISTORY);
	leftSide.add(navBar, BorderLayout.NORTH);

	JSplitPane treeAndVersion = new JSplitPane();
	treeAndVersion.setContinuousLayout(true);
	treeAndVersion.setBorder(FlowClient.EMPTY_BORDER);
	treeAndVersion.setResizeWeight(0.4);
	leftSide.add(treeAndVersion, BorderLayout.CENTER);

	versionViewer = new VersionViewer(this);
	treeAndVersion.setRightComponent(versionViewer);

	viewer = new EditTabs();
	setRightComponent(viewer);

	docTree = new DocTree(versionViewer);
	treeAndVersion.setLeftComponent(docTree.getScrollable());

    }
    
    public EditTabs getEditTabs(){
	return viewer;
    }
}
