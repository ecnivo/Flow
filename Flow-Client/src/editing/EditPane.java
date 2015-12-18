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

		EditConsole editConsole = new EditConsole();
		CollabsList collabsList = new CollabsList();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				editConsole, collabsList);
		this.add(splitPane, BorderLayout.EAST);
	}
}
