package editing;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

import editing.CollabsList.CollabButton;
import gui.FlowClient;

public class CollabsList extends JList<CollabButton> {

    private JScrollPane scrolling;

    public CollabsList() {
	setMinimumSize(new Dimension(5, 1));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrolling = new JScrollPane(this);
	scrolling
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    public void addCollab() {
	// TODO should accept a collaborator object and add it to the list,
	// maybe display a pop-up notif
    }

    public void removeCollab() {
	// TODO should accept a collab's object, removes it, shows notif
    }

    public JScrollPane getScroll() {
	return scrolling;
    }

    class CollabButton extends JButton {
	public CollabButton() {
	    setBorder(FlowClient.EMPTY_BORDER);
	    // TODO Auto-generated constructor stub
	}
    }
}
