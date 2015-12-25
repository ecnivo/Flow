package editing;

import gui.FlowClient;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditArea extends JEditorPane {
    private JScrollPane scrolling;

    protected EditArea(File file, boolean editable, EditTabs tabs) {
	scrolling = new JScrollPane(EditArea.this);
	setBorder(FlowClient.EMPTY_BORDER);
	setEditable(editable);
	addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent e) {
		// nothing
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {
		    tabs.removeTabAt(tabs.getSelectedIndex());
		}
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {
		// nothing
	    }
	});
	getDocument().addDocumentListener(new EditAreaListener());
	// TODO make things work
    }

    public JScrollPane getScrollPane() {
	return scrolling;
    }

    private class EditAreaListener implements DocumentListener {
	@Override
	public void changedUpdate(DocumentEvent e) {
	    // useless
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
	    // TODO find a way to get the actual chars that got changed
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	    System.out.println("something deleted");
	}
    }
}