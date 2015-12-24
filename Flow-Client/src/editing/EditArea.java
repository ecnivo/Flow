package editing;

import java.awt.Component;
import java.io.File;

import javax.swing.JScrollPane;

public class EditArea extends Component {
    private JScrollPane scrolling;

    protected EditArea(File file) {
	scrolling = new JScrollPane(EditArea.this);
	// TODO make things work
    }

    public JScrollPane getScrollPane() {
	return scrolling;
    }
}