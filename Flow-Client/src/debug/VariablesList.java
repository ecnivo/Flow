package debug;

import gui.FlowClient;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VariablesList extends JPanel {
    public VariablesList() {
	setPreferredSize(new Dimension(400, 275));
	setLayout(new GridLayout(0, 1, 0, 2));
	setMinimumSize(new Dimension(0, 50));
	setBorder(FlowClient.EMPTY_BORDER);
	// TODO make this entire thing work some way or another
    }

    class VariablePanel extends JPanel {
	public VariablePanel() {
	    setBorder(FlowClient.EMPTY_BORDER);

	}
    }
}
