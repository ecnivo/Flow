package editing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EditPane extends JPanel {

	public EditPane() {
		this.setLayout(new BorderLayout(0, 0));

		EditArea editorPane = new EditArea();
		this.add(editorPane, BorderLayout.CENTER);

		DocTree tree = new DocTree();
		this.add(tree, BorderLayout.WEST);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		EditConsole editConsole = new EditConsole();
		splitPane.add(editConsole);
		this.add(splitPane, BorderLayout.EAST);
	}
}
