package debug;

import gui.FlowClient;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;

import debug.VariablesList.VariablePanel;

public class VariablesList extends JList<VariablePanel> {
    public VariablesList() {
	setPreferredSize(new Dimension(400, 275));
	setMinimumSize(new Dimension(0, 50));
	setBorder(FlowClient.EMPTY_BORDER);
	// TODO Auto-generated constructor stub
    }

    class VariablePanel extends JPanel {
	public VariablePanel() {
	    setBorder(FlowClient.EMPTY_BORDER);
	    // TODO Auto-generated constructor stub
	}
    }
}
