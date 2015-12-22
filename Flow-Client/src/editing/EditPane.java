package editing;

import flow_debug_commons.DocTree;
import flow_debug_commons.EditArea;
import flow_debug_commons.GenericConsole;
import flow_debug_commons.NavBar;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EditPane extends JPanel {

    private EditArea editArea;
    private DocTree tree;

    public EditPane(NavBar navBar) {
	this.setLayout(new BorderLayout(0, 0));

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);

	JSplitPane rightSide = new JSplitPane();
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	mainSplit.setRightComponent(rightSide);

	GenericConsole editConsole = new GenericConsole();
	rightSide.setTopComponent(editConsole);

	CollabsList collabsList = new CollabsList();
	rightSide.setBottomComponent(collabsList);

	JSplitPane leftSide = new JSplitPane();
	mainSplit.setLeftComponent(leftSide);

	editArea = new EditArea();
	leftSide.setRightComponent(editArea);

	JPanel treeAndButtons = new JPanel(new BorderLayout());
	tree = new DocTree(editArea);
	treeAndButtons.add(tree, BorderLayout.CENTER);
	leftSide.setLeftComponent(treeAndButtons);
	
	JPanel buttonPanel = new JPanel();
	buttonPanel.add(navBar);
	buttonPanel.add(new EditToolbar());
	treeAndButtons.add(buttonPanel, BorderLayout.NORTH);

    }

    public EditArea getEditArea() {
	return editArea;
    }
    
    public DocTree getDocTree (){
	return tree;
    }
}
