package debug;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import editing.EditArea;

public class DebugPane extends JPanel {
    DebugConsole debugConsole;
    DebugToolbar debugToolbar;
    VariablesList variablesList;
    EditArea editArea;

    public DebugPane() {
	debugConsole = new DebugConsole();
	debugToolbar = new DebugToolbar();
	variablesList = new VariablesList();

	this.setLayout(new BorderLayout());
	this.add(debugToolbar, BorderLayout.NORTH);
	this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, debugConsole,
		variablesList), BorderLayout.EAST);

    }
}
