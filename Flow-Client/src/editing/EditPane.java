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

	GenericConsole editConsole = new GenericConsole();
	rightSide.setLeftComponent(editConsole);

	CollabsList collabsList = new CollabsList();
	rightSide.setRightComponent(collabsList);

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
	treeAndButtons.setRightComponent(new DocTree(editArea));
	leftSide.setLeftComponent(treeAndButtons);
    }

    public EditArea getEditArea() {
	return editArea;
    }

    public DocTree getDocTree() {
	return tree;
    }
}
