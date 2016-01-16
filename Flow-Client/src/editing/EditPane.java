package editing;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import shared.EditTabs;
import shared.FlowPermission;
import shared.GenericConsole;
import shared.NavBar;
import shared.RunStopBar;

public class EditPane extends JPanel {

    private CollabsList collabsList;
    private EditorDocTree tree;
    private JSplitPane leftSide;
    private EditorToolbar editToolbar;
    private RunStopBar runStopBar;

    private static final int RIGHT_SIDE_WIDTH = 300;
    private static final int LEFT_SIDE_WIDTH = 300;

    public EditPane(PanelManager manager) {
	this.setLayout(new BorderLayout());
	setBorder(FlowClient.EMPTY_BORDER);

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setBorder(FlowClient.EMPTY_BORDER);
	mainSplit.setMinimumSize(new Dimension(0, 0));
	mainSplit.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	mainSplit.setContinuousLayout(true);
	mainSplit.setResizeWeight(0.9);
	JSplitPane rightSide = new JSplitPane();
	rightSide.setBorder(FlowClient.EMPTY_BORDER);
	rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
	rightSide.setMinimumSize(new Dimension(5, 0));
	rightSide.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	rightSide.setResizeWeight(0.5);
	rightSide.setContinuousLayout(true);
	rightSide.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
	mainSplit.setRightComponent(rightSide);

	GenericConsole genericConsole = new GenericConsole();
	genericConsole.getScroll().setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
	rightSide.setLeftComponent(genericConsole.getScroll());

	leftSide = new JSplitPane();
	leftSide.setBorder(FlowClient.EMPTY_BORDER);
	leftSide.setContinuousLayout(true);
	leftSide.setMinimumSize(new Dimension(LEFT_SIDE_WIDTH, 0));
	leftSide.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	mainSplit.setLeftComponent(leftSide);

	collabsList = new CollabsList(new FlowPermission(FlowPermission.OWNER), this);
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
	editToolbar = new EditorToolbar(this);
	buttonPanel.add(editToolbar);
	runStopBar = new RunStopBar(genericConsole);
	buttonPanel.add(runStopBar);
	treeAndButtons.setLeftComponent(buttonPanel);

	tree = new EditorDocTree(this);
	treeAndButtons.setRightComponent(tree);
	leftSide.setLeftComponent(treeAndButtons);
    }

    public CollabsList getCollabsList() {
	return collabsList;
    }

    public void addEditTabs(EditTabs editTabs) {
	runStopBar.setEditTabs(editTabs);
	leftSide.setRightComponent(editTabs);
    }

    public EditTabs getEditTabs() {
	if (leftSide.getRightComponent() instanceof EditTabs) {
	    return (EditTabs) leftSide.getRightComponent();
	}
	return null;
    }

    public EditorToolbar getEditToolbar() {
	return editToolbar;
    }

    public EditorDocTree getDocTree() {
	return tree;
    }
}
