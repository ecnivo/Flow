
package editing;

import gui.FlowClient;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import shared.EditTabs;
import shared.GenericConsole;
import shared.NavBar;
import shared.RunStopBar;

/**
 * The panel that holds all of the edit view's components
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class EditPane extends JPanel {

	// Important ones that may be referenced using getters/setters
	private CollabsList			collabsList;
	private EditorFileTree		tree;
	private JSplitPane			leftSide;
	private EditorToolbar		editToolbar;
	private RunStopBar			runStopBar;

	private static final int	RIGHT_SIDE_WIDTH	= 300;
	private static final int	LEFT_SIDE_WIDTH		= 300;

	/**
	 * Creates a new EditPane
	 * 
	 * @param manager
	 *        the PanelManager associated
	 */
	public EditPane(PanelManager manager) {
		// Swing layouts
		this.setLayout(new BorderLayout());
		setBorder(FlowClient.EMPTY_BORDER);

		// Get ready for a massive mess of nested JComponents

		//Creates a main split pane between the left and right
		JSplitPane mainSplit = new JSplitPane();
		add(mainSplit, BorderLayout.CENTER);
		mainSplit.setBorder(FlowClient.EMPTY_BORDER);
		mainSplit.setMinimumSize(new Dimension(0, 0));
		mainSplit.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainSplit.setContinuousLayout(true);
		mainSplit.setResizeWeight(0.9);
		// Sets a right side split pane
		JSplitPane rightSide = new JSplitPane();
		rightSide.setBorder(FlowClient.EMPTY_BORDER);
		rightSide.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rightSide.setMinimumSize(new Dimension(5, 0));
		rightSide.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		rightSide.setResizeWeight(0.5);
		rightSide.setContinuousLayout(true);
		rightSide.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
		mainSplit.setRightComponent(rightSide);

		// Puts the right-top component as a console
		GenericConsole genericConsole = new GenericConsole();
		genericConsole.getScroll().setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 500));
		rightSide.setLeftComponent(genericConsole.getScroll());

		// Creates a left side, which is put on the left side of the main split
		leftSide = new JSplitPane();
		leftSide.setBorder(FlowClient.EMPTY_BORDER);
		leftSide.setContinuousLayout(true);
		leftSide.setMinimumSize(new Dimension(LEFT_SIDE_WIDTH, 0));
		leftSide.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainSplit.setLeftComponent(leftSide);

		// The collaborators' list is placed on the bottom right
		collabsList = new CollabsList(this);
		collabsList.setPreferredSize(new Dimension(RIGHT_SIDE_WIDTH, 225));
		rightSide.setRightComponent(collabsList);

		// The tree and buttons pane is placed on the left half of the left pane
		JSplitPane treeAndButtons = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		treeAndButtons.setBorder(FlowClient.EMPTY_BORDER);
		treeAndButtons.setMinimumSize(new Dimension(LEFT_SIDE_WIDTH, 0));
		treeAndButtons.setEnabled(false);
		// The tree and buttons panel gets a buttons' panel, with navigation, editor tool bar, and running and stopping buttons
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

		// Places the tree on the bottom left of the left split
		tree = new EditorFileTree(this);
		treeAndButtons.setRightComponent(tree.getScrollable());
		leftSide.setLeftComponent(treeAndButtons);
	}

	/**
	 * Gets the collaborators list
	 * @return the collaborators list
	 */
	public CollabsList getCollabsList() {
		return collabsList;
	}

	/**
	 * Places editTabs in the middle when needed
	 */
	public void addEditTabs(EditTabs editTabs) {
		runStopBar.setEditTabs(editTabs);
		leftSide.setRightComponent(editTabs);
	}

	/**
	 * Gets the edit Tabs 
	 * @param editTabs returns the editTabs if applicable
	 */
	public EditTabs getEditTabs() {
		if (leftSide.getRightComponent() instanceof EditTabs) {
			return (EditTabs) leftSide.getRightComponent();
		}
		return null;
	}

	/**
	 * Gets the editor toolbar
	 * @return the editor toolbar
	 */
	public EditorToolbar getEditToolbar() {
		return editToolbar;
	}

	/**
	 * Gets the documents tree
	 * @return the editor's document tree
	 */
	public EditorFileTree getFileTree() {
		return tree;
	}
}
