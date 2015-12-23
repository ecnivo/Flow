package debug;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;

import debug.VariablesList.VariablePanel;

public class VariablesList extends JList<VariablePanel> {
    public VariablesList() {
	setPreferredSize(new Dimension(425,225));
	setMinimumSize(new Dimension(0,50));;
	// TODO Auto-generated constructor stub
    }

    class VariablePanel extends JPanel {
	public VariablePanel() {
	    // TODO Auto-generated constructor stub
	}
    }
}
