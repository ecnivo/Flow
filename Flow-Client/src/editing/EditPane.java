package editing;

import gui.FlowClient;
import gui.FlowPermission;
import gui.GenericConsole;
import gui.NavBar;
import gui.PanelManager;
import gui.RunStopBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EditPane extends JPanel {

    private EditTabs editTabs;
    private DocTree tree;
    private CollabsList collabsList;

    private static final int RIGHT_SIDE_WIDTH = 300;
    private static final int LEFT_SIDE_WIDTH = 300;

    public EditPane(PanelManager manager) {
	this.setLayout(new BorderLayout());
	setBorder(FlowClient.EMPTY_BORDER);

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setBorder(FlowClient.EMPTY_BORDER);
	mainSplit.setMinimumSize(new Dimension(0, 0));
	mainSplit.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	mainSplit.setContinuousLayout(true);
	mainSplit.setResizeWeight(0.9);
	JSplitPane rightSide = new JSplitPane();
	rightSide.setBorder(FlowClient.EMPTY_BORDER);
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	rightSide.setMinimumSize(new Dimension(5, 0));
	rightSide.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	rightSide.setResizeWeight(0.5);
	rightSide.setContinuousLayout(true);
	rightSide.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
	mainSplit.setRightComponent(rightSide);

	GenericConsole genericConsole = new GenericConsole();
	genericConsole.getScroll().setPreferredSize(
		new Dimension(RIGHT_SIDE_WIDTH, 500));
	rightSide.setLeftComponent(genericConsole.getScroll());

	JSplitPane leftSide = new JSplitPane();
	leftSide.setBorder(FlowClient.EMPTY_BORDER);
	leftSide.setContinuousLayout(true);
	leftSide.setMinimumSize(new Dimension(LEFT_SIDE_WIDTH, 0));
	leftSide.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		Integer.MAX_VALUE));
	mainSplit.setLeftComponent(leftSide);

	editTabs = new EditTabs(this);
	leftSide.setRightComponent(editTabs);

	collabsList = new CollabsList(new FlowPermission(
		FlowPermission.OWNER), editTabs);
	collabsList.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 225));
	rightSide.setRightComponent(collabsList);

	JSplitPane treeAndButtons = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	treeAndButtons.setBorder(FlowClient.EMPTY_BORDER);
	treeAndButtons.setMinimumSize(new Dimension(LEFT_SIDE_WIDTH, 0));
	treeAndButtons.setEnabled(false);
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	buttonPanel.setBorder(FlowClient.EMPTY_BORDER);
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.EDIT);
	buttonPanel.add(navBar);
	EditorToolbar editToolbar = new EditorToolbar(this);
	buttonPanel.add(editToolbar);
	buttonPanel.add(new RunStopBar());
	treeAndButtons.setLeftComponent(buttonPanel);
	tree = new DocTree(editTabs, editToolbar);

	treeAndButtons.setRightComponent(tree.getScrollable());
	leftSide.setLeftComponent(treeAndButtons);
    }

    public EditTabs getEditTabs() {
	return editTabs;
    }

    public DocTree getDocTree() {
	return tree;
    }
    
    public CollabsList getCollabsList(){
	return collabsList;
    }
}
