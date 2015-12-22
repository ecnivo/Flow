package debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.EditPane;
import flow_debug_commons.GenericConsole;
import flow_debug_commons.NavBar;

public class DebugPane extends JPanel {

    public DebugPane(EditPane editPane, NavBar navBar) {
	setLayout(new BorderLayout());

	JSplitPane middleSplit = new JSplitPane();
	add(middleSplit, BorderLayout.CENTER);

	JSplitPane leftHalf = new JSplitPane();
	middleSplit.setLeftComponent(leftHalf);

	leftHalf.setLeftComponent(editPane.getDocTree());

	leftHalf.setRightComponent(editPane.getEditArea());

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	middleSplit.setRightComponent(rightHalf);

	GenericConsole debugConsole = new GenericConsole();
	rightHalf.setRightComponent(debugConsole);

	JPanel rightTop = new JPanel(new BorderLayout());
	rightHalf.setLeftComponent(rightTop);

	JPanel buttonPanel = new JPanel(new FlowLayout());
	rightTop.add(buttonPanel, BorderLayout.NORTH);

	buttonPanel.add(navBar);

	DebugToolbar debugBar = new DebugToolbar();
	buttonPanel.add(debugBar);

	VariablesList variablesList = new VariablesList();
	rightTop.add(variablesList, BorderLayout.CENTER);

    }
}
