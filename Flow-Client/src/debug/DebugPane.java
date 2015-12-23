package debug;

import flow_debug_commons.EditTabs;
import flow_debug_commons.GenericConsole;
import flow_debug_commons.NavBar;
import gui.PanelManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DebugPane extends JPanel {

    public DebugPane(PanelManager manager) {
	setLayout(new BorderLayout());

	JSplitPane middleSplit = new JSplitPane();
	add(middleSplit, BorderLayout.CENTER);

	middleSplit.setLeftComponent(new EditTabs());

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setPreferredSize(new Dimension(420, 574));
	rightHalf.setMinimumSize(new Dimension(420, 0));
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	middleSplit.setRightComponent(rightHalf);

	GenericConsole debugConsole = new GenericConsole();
	rightHalf.setRightComponent(debugConsole);

	JPanel rightTop = new JPanel(new BorderLayout());
	rightHalf.setLeftComponent(rightTop);

	JPanel buttonPanel = new JPanel(new FlowLayout());
	rightTop.add(buttonPanel, BorderLayout.NORTH);
	NavBar navBar = new NavBar(manager);
	navBar.disableButton(NavBar.DEBUG);
	buttonPanel.add(navBar);
	DebugToolbar debugBar = new DebugToolbar();
	buttonPanel.add(debugBar);
	VariablesList variablesList = new VariablesList();
	rightTop.add(variablesList, BorderLayout.CENTER);

    }
}
