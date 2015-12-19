package debug;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.DocTree;
import editing.EditArea;
import editing.EditPane;

public class DebugPane extends JPanel {

    public DebugPane(EditPane editPane) {
	setLayout(new BorderLayout(0, 0));

	JSplitPane bothSides = new JSplitPane();
	add(bothSides, BorderLayout.CENTER);

	JSplitPane leftHalf = new JSplitPane();
	bothSides.setLeftComponent(leftHalf);

	DocTree tree = editPane.getDocTree();
	leftHalf.setLeftComponent(tree);

	EditArea editArea = editPane.getEditArea();
	leftHalf.setRightComponent(editArea);

	JSplitPane rightHalf = new JSplitPane();
	rightHalf.setOrientation(JSplitPane.VERTICAL_SPLIT);
	bothSides.setRightComponent(rightHalf);

	DebugConsole debugConsole = new DebugConsole();
	rightHalf.setRightComponent(debugConsole);

	JPanel panel = new JPanel();
	rightHalf.setLeftComponent(panel);

	DebugToolbar debugToolbar = new DebugToolbar();
	panel.add(debugToolbar, BorderLayout.NORTH);

	VariablesList variablesList = new VariablesList();
	panel.add(variablesList, BorderLayout.CENTER);

    }
}
