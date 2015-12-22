package debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.EditPane;
import flow_debug_commons.DocTree;
import flow_debug_commons.NavBar;

public class DebugPane extends JPanel {

    public DebugPane(EditPane editPane, NavBar navBar) {
	setLayout(new BorderLayout(0, 0));

	JSplitPane bothSides = new JSplitPane();
	add(bothSides, BorderLayout.CENTER);

	JSplitPane leftHalf = new JSplitPane();
	bothSides.setLeftComponent(leftHalf);

	leftHalf.setLeftComponent(editPane.getDocTree());

	leftHalf.setRightComponent(editPane);

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	bothSides.setRightComponent(rightHalf);

	DebugConsole debugConsole = new DebugConsole();
	rightHalf.setBottomComponent(debugConsole);

	JPanel rightTop = new JPanel(new BorderLayout());
	rightHalf.setBottomComponent(rightTop);

	JPanel buttonPanel = new JPanel(new FlowLayout());
	rightTop.add(buttonPanel, BorderLayout.NORTH);

	buttonPanel.add(navBar);

	DebugToolbar debugBar = new DebugToolbar();
	buttonPanel.add(debugBar);

	VariablesList variablesList = new VariablesList();
	rightTop.add(variablesList, BorderLayout.CENTER);

    }
}
