
package debug;

import gui.FlowClient;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * A list of variables that can be used by the user during debugging
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class VariablesList extends JPanel {

	/**
	 * Creates a new VariablesList
	 */
	public VariablesList() {
		// Swing setup
		setPreferredSize(new Dimension(400, 275));
		setLayout(new GridLayout(0, 1, 0, 2));
		setMinimumSize(new Dimension(0, 50));
		setBorder(FlowClient.EMPTY_BORDER);
		// TODO make this entire thing work some way or another
	}

	/**
	 * Each variable is represented by one VariableTile
	 * 
	 * @author Vince Ou
	 *
	 */
	class VariableTile extends JPanel {

		/**
		 * Creates a new VariableTile
		 * 
		 */
		public VariableTile() {
			setBorder(FlowClient.EMPTY_BORDER);

		}
	}
}
