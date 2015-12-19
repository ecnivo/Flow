package editing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EditPane extends JPanel {

    private EditArea editArea;
    private DocTree tree;

    public EditPane() {
	this.setLayout(new BorderLayout(0, 0));

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);

	JSplitPane rightSide = new JSplitPane();
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	mainSplit.setRightComponent(rightSide);

	EditConsole editConsole = new EditConsole();
	rightSide.setRightComponent(editConsole);

	CollabsList collabsList = new CollabsList();
	rightSide.setLeftComponent(collabsList);

	JSplitPane leftSide = new JSplitPane();
	mainSplit.setLeftComponent(leftSide);

	JPanel leftLeft = new JPanel(new BorderLayout());
	tree = new DocTree();
	leftLeft.add(tree, BorderLayout.CENTER);
	NavBar navBar = new NavBar();
	leftLeft.add(navBar, BorderLayout.NORTH);
	leftSide.setLeftComponent(leftLeft);

	editArea = new EditArea();
	leftSide.setRightComponent(editArea);

    }

    public EditArea getEditArea() {
	return editArea;
    }
    
    public DocTree getDocTree (){
	return tree;
    }
}
