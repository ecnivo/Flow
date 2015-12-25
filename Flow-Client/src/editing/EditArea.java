package editing;

import gui.FlowClient;

import java.io.File;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

public class EditArea extends JEditorPane {
    private JScrollPane scrolling;

    protected EditArea(File file) {
	scrolling = new JScrollPane(EditArea.this);
	setBorder(FlowClient.EMPTY_BORDER);
	// TODO make things work
    }

    public JScrollPane getScrollPane() {
	return scrolling;
    }
}