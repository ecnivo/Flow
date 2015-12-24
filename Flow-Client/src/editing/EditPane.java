package editing;

import gui.GenericConsole;
import gui.NavBar;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EditPane extends JPanel {

    private EditTabs editTabs;
    private DocTree tree;

    private static final int RIGHT_SIDE_WIDTH = 300;

    public EditPane(PanelManager manager) {
	this.setLayout(new BorderLayout());

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setMinimumSize(new Dimension(0, 0));
	mainSplit.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	mainSplit.setResizeWeight(0.9);
	JSplitPane rightSide = new JSplitPane();
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	rightSide.setMinimumSize(new Dimension(5, 0));
	rightSide.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	rightSide.setResizeWeight(0.5);
	rightSide.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
	mainSplit.setRightComponent(rightSide);

	GenericConsole genericConsole = new GenericConsole();
	genericConsole.getScroll().setPreferredSize(
		new Dimension(RIGHT_SIDE_WIDTH, 500));
	rightSide.setLeftComponent(genericConsole.getScroll());
	CollabsList collabsList = new CollabsList();
	collabsList.getScroll().setPreferredSize(
		new Dimension(RIGHT_SIDE_WIDTH, 225));
	rightSide.setRightComponent(collabsList.getScroll());

	JSplitPane leftSide = new JSplitPane();
	leftSide.setMinimumSize(new Dimension(390, 0));
	leftSide.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	mainSplit.setLeftComponent(leftSide);

	editTabs = new EditTabs(true);
	leftSide.setRightComponent(editTabs);

	JSplitPane treeAndButtons = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	treeAndButtons.setMinimumSize(new Dimension(340, 0));
	treeAndButtons.setEnabled(false);
	JPanel buttonPanel = new JPanel(new FlowLayout());
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.EDIT);
	buttonPanel.add(navBar);
	buttonPanel.add(new EditorToolbar());
	treeAndButtons.setLeftComponent(buttonPanel);
	tree = new DocTree(editTabs);
	treeAndButtons.setRightComponent(tree.getScrollable());
	leftSide.setLeftComponent(treeAndButtons);
    }

    public EditTabs getEditTabs() {
	return editTabs;
    }

    public DocTree getDocTree() {
	return tree;
    }
}
