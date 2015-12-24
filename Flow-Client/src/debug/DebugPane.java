package debug;

import editing.EditTabs;
import gui.GenericConsole;
import gui.NavBar;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DebugPane extends JPanel {

    public DebugPane(PanelManager manager) {
	setLayout(new BorderLayout());

	JSplitPane mainSplit = new JSplitPane();
	add(mainSplit, BorderLayout.CENTER);
	mainSplit.setResizeWeight(1);

	mainSplit.setLeftComponent(new EditTabs(true));

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setPreferredSize(new Dimension(420, 574));
	rightHalf.setMinimumSize(new Dimension(420, 0));
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	mainSplit.setRightComponent(rightHalf);

	GenericConsole debugConsole = new GenericConsole();
	debugConsole.getScroll().setPreferredSize(new Dimension(400, 255));
	rightHalf.setRightComponent(debugConsole.getScroll());

	JPanel rightTop = new JPanel(new BorderLayout());
	rightHalf.setLeftComponent(rightTop);

	JPanel buttonPanel = new JPanel(new FlowLayout());
	buttonPanel.setMinimumSize(new Dimension(200, 0));
	buttonPanel.setPreferredSize(new Dimension(200, 60));
	rightTop.add(buttonPanel, BorderLayout.NORTH);
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.DEBUG);
	buttonPanel.add(navBar);
	DebugToolbar debugBar = new DebugToolbar();
	buttonPanel.add(debugBar);
	rightTop.add(new VariablesList(), BorderLayout.CENTER);

    }
}
