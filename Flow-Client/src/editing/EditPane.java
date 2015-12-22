package editing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import flow_debug_commons.DocTree;
import flow_debug_commons.EditArea;
import flow_debug_commons.GenericConsole;
import flow_debug_commons.NavBar;

public class EditPane extends JPanel {

    private EditArea editArea;
    private DocTree tree;

    public EditPane(NavBar navBar) {
	this.setLayout(new BorderLayout());

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	JSplitPane rightSide = new JSplitPane();
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	mainSplit.setRightComponent(rightSide);
	
	rightSide.setLeftComponent(new GenericConsole());
	rightSide.setRightComponent(new CollabsList());

	JSplitPane leftSide = new JSplitPane();
	mainSplit.setLeftComponent(leftSide);

	editArea = new EditArea();
	leftSide.setRightComponent(editArea);

	JSplitPane treeAndButtons = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	treeAndButtons.setEnabled(false);
	JPanel buttonPanel = new JPanel();
	buttonPanel.add(navBar);
	buttonPanel.add(new EditorToolbar());
	treeAndButtons.setLeftComponent(buttonPanel);
	tree = new DocTree(editArea);
	treeAndButtons.setRightComponent(tree.getScrollable());
	leftSide.setLeftComponent(treeAndButtons);
    }

    public EditArea getEditArea() {
	return editArea;
    }

    public DocTree getDocTree() {
	return tree;
    }
}
