package editing;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JList;

import editing.CollabsList.CollabButton;

public class CollabsList extends JList<CollabButton> {
    public CollabsList() {
	setMinimumSize(new Dimension(5, 1));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    public void addCollab() {
	// TODO should accept a collaborator object and add it to the list,
	// maybe display a pop-up notif
    }

    public void removeCollab() {
	// TODO should accept a collab's object, removes it, shows notif
    }

    class CollabButton extends JButton {
	public CollabButton() {
	    // TODO Auto-generated constructor stub
	}
    }
}
