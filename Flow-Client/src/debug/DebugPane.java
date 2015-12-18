package debug;

import editing.EditArea;

import javax.swing.*;
import java.awt.*;

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
