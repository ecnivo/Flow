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

@SuppressWarnings("serial")
public class DebugPane extends JPanel {
    
    //TODO if it looks so simple, things still need to be added, such as selecting break points, highlighting the line...

    private JSplitPane mainSplit;

    public DebugPane(PanelManager manager) {
	setLayout(new BorderLayout());
	setBorder(FlowClient.EMPTY_BORDER);

	mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setResizeWeight(0);
	mainSplit.setContinuousLayout(true);
	mainSplit.setBorder(FlowClient.EMPTY_BORDER);

	JSplitPane leftHalf = new JSplitPane();
	leftHalf.setPreferredSize(new Dimension(310, 574));
	leftHalf.setBorder(FlowClient.EMPTY_BORDER);
	leftHalf.setMinimumSize(new Dimension(310, 0));
	leftHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	leftHalf.setContinuousLayout(true);
	mainSplit.setLeftComponent(leftHalf);

	GenericConsole debugConsole = new GenericConsole();
	debugConsole.getScroll().setPreferredSize(new Dimension(400, 255));
	leftHalf.setRightComponent(debugConsole.getScroll());

	JPanel rightTop = new JPanel(new BorderLayout());
	leftHalf.setLeftComponent(rightTop);

	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	buttonPanel.setMinimumSize(new Dimension(310, 0));
	buttonPanel.setBorder(FlowClient.EMPTY_BORDER);
	buttonPanel.setPreferredSize(new Dimension(310, 32));
	rightTop.add(buttonPanel, BorderLayout.NORTH);
	rightTop.setBorder(FlowClient.EMPTY_BORDER);
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.DEBUG);
	buttonPanel.add(navBar);
	buttonPanel.add(new RunStopBar());
	buttonPanel.add(new DebugToolbar());
	rightTop.add(new VariablesList(), BorderLayout.CENTER);
    }

    public void addEditTabs(EditTabs editTabs) {
	mainSplit.setRightComponent(editTabs);
    }
}
