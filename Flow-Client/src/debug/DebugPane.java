
package debug;

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
 * A panel that holds various debugging elements
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class DebugPane extends JPanel {

	private RunStopBar	runStopBar;

	// TODO if it looks so simple, things still need to be added, such as selecting break points,
	// highlighting the line...

	private JSplitPane	mainSplit;

	/**
	 * Creates a new DebugPane
	 * 
	 * @param manager
	 *        the associated PanelManager
	 */
	public DebugPane(PanelManager manager) {
		// Swing setup
		setLayout(new BorderLayout());
		setBorder(FlowClient.EMPTY_BORDER);

		// Creates a centrel jsplitpane
		mainSplit = new JSplitPane();
		add(mainSplit, BorderLayout.CENTER);
		mainSplit.setResizeWeight(0);
		mainSplit.setContinuousLayout(true);
		mainSplit.setBorder(FlowClient.EMPTY_BORDER);

		// Sets the left half of the main to another jsplitpane
		JSplitPane leftHalf = new JSplitPane();
		leftHalf.setPreferredSize(new Dimension(310, 574));
		leftHalf.setBorder(FlowClient.EMPTY_BORDER);
		leftHalf.setMinimumSize(new Dimension(310, 0));
		leftHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftHalf.setContinuousLayout(true);
		mainSplit.setLeftComponent(leftHalf);

		// Puts the debug console on the bottom of the left half
		GenericConsole debugConsole = new GenericConsole();
		debugConsole.getScroll().setPreferredSize(new Dimension(400, 255));
		leftHalf.setRightComponent(debugConsole.getScroll());

		// The left side top contains the buttons bar and the variables list
		JPanel leftTop = new JPanel(new BorderLayout());
		leftHalf.setLeftComponent(leftTop);

		// Creates a panel for the buttons, with various buttons (navigation, run/stop, debugging)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonPanel.setMinimumSize(new Dimension(310, 0));
		buttonPanel.setBorder(FlowClient.EMPTY_BORDER);
		buttonPanel.setPreferredSize(new Dimension(310, 32));
		leftTop.add(buttonPanel, BorderLayout.NORTH);
		leftTop.setBorder(FlowClient.EMPTY_BORDER);
		NavBar navBar = new NavBar(manager);
		navBar.disableButton(NavBar.DEBUG);
		buttonPanel.add(navBar);
		runStopBar = new RunStopBar(debugConsole);
		buttonPanel.add(runStopBar);
		buttonPanel.add(new DebugToolbar());

		// Adds a variables list on the bottom half of the left sides' top half
		leftTop.add(new VariablesList(), BorderLayout.CENTER);
	}

	/**
	 * Adds the editTabs to the panel
	 * 
	 * @param editTabs
	 *        the edit tabs in use
	 */
	public void addEditTabs(EditTabs editTabs) {
		runStopBar.setEditTabs(editTabs);
		mainSplit.setRightComponent(editTabs);
	}
}
