package editing;

import gui.FlowClient;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class DocTree extends JTree {

    DocTreeModel model;
    JScrollPane scrollView;
    EditTabs editTabs;

    public DocTree(EditTabs editTabs) {
	this.editTabs = editTabs;
	setMinimumSize(new Dimension(100, 0));
	setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	setBorder(FlowClient.EMPTY_BORDER);
	scrollView = new JScrollPane(this);
	model = new DocTreeModel();
	setModel(model);
	getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	addTreeSelectionListener(new DocTreeSelectionListener());
    }

    private class DocTreeModel extends DefaultTreeModel {
	private DocTreeModel() {
	    super(new DefaultMutableTreeNode("Workspace"));
	}

    }

    private class DocTreeSelectionListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) DocTree.this
		    .getSelectionPath().getLastPathComponent();

	    if (node.isLeaf()) {
		// TODO get the link to the file, then open a new tab in the
		// editPane for the file
	    }
	}

    }

    public JScrollPane getScrollable() {
	return scrollView;
    }

    private void addProject() {
	// TODO Should actually accept a param for a project object
    }

    private void addFile() {
	// TODO should accept a file object and a project object that it goes
	// under
    }

    private void removeProject() {
	// TODO accepts a project object, shows a warning, then deletes it from
	// the tree (actual deleting it from the account and HDD isn't done
	// here). Also closes all tabs that may have a file in there open
    }

    private void removeFile() {
	// TODO accepts a file object and project object, shows a warning, then
	// deletes file from tree. Closes the tab that may have the file open
    }
}