package debug;

import editing.EditTabs;
import gui.FlowClient;
import gui.GenericConsole;
import gui.NavBar;
import gui.PanelManager;
import gui.RunStopBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DebugPane extends JPanel {

    public DebugPane(PanelManager manager) {
	setLayout(new BorderLayout());
	setBorder(FlowClient.EMPTY_BORDER);

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setResizeWeight(1);
	mainSplit.setBorder(FlowClient.EMPTY_BORDER);

	mainSplit.setLeftComponent(new EditTabs(true));

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setPreferredSize(new Dimension(310, 574));
	rightHalf.setBorder(FlowClient.EMPTY_BORDER);
	rightHalf.setMinimumSize(new Dimension(310, 0));
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	mainSplit.setRightComponent(rightHalf);

	GenericConsole debugConsole = new GenericConsole();
	debugConsole.getScroll().setPreferredSize(new Dimension(400, 255));
	rightHalf.setRightComponent(debugConsole.getScroll());

	JPanel rightTop = new JPanel(new BorderLayout());
	rightHalf.setLeftComponent(rightTop);

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
}
