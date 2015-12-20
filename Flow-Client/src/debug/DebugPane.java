package debug;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.DocTree;
import editing.EditPane;
import gui.NavBar;

public class DebugPane extends JPanel {

    public DebugPane(EditPane editPane, NavBar navBar) {
	setLayout(new BorderLayout(0, 0));

	JSplitPane bothSides = new JSplitPane();
	add(bothSides, BorderLayout.CENTER);

	JSplitPane leftHalf = new JSplitPane();
	bothSides.setLeftComponent(leftHalf);

	DocTree docTree = editPane.getDocTree();
	leftHalf.setLeftComponent(docTree);

	leftHalf.setRightComponent(editPane);

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	bothSides.setRightComponent(rightHalf);

	DebugConsole debugConsole = new DebugConsole();
	rightHalf.setRightComponent(debugConsole);

	JPanel rightTop = new JPanel();
	rightHalf.setLeftComponent(rightTop);

	JPanel buttonPanel = new JPanel();
	rightTop.add(buttonPanel);

	buttonPanel.add(navBar);

	DebugToolbar debugBar = new DebugToolbar();
	buttonPanel.add(debugBar);

	VariablesList variablesList = new VariablesList();
	rightTop.add(variablesList, BorderLayout.CENTER);

    }
}
