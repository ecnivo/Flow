package editing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Created by Vince on 2015-12-18.
 */
public class EditTabs extends JTabbedPane {

    public static final int TAB_ICON_SIZE = 16;
    private boolean editable;

    public EditTabs(boolean editable) {
	setMinimumSize(new Dimension(50, 0));
	setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	setTabPlacement(JTabbedPane.TOP);
	this.editable = editable;
    }

    public void openTab(File file) {
	// TODO change the parameter, and add checks if the tab is already open
	addTab(file.getName(), new EditArea(file).getScrollPane());
	setTabComponentAt(getTabCount() - 1, new CustomTabHeader());
    }

    class CustomTabHeader extends JPanel {

	public CustomTabHeader() {
	    super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	    setOpaque(false);

	    ImageIcon icon = null;
	    try {
		icon = new ImageIcon(ImageIO.read(new File("images/file.png"))
			.getScaledInstance(TAB_ICON_SIZE, TAB_ICON_SIZE,
				Image.SCALE_SMOOTH));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    add(new JLabel(icon));

	    // make JLabel read titles from JTabbedPane
	    JLabel label = new JLabel() {
		public String getText() {
		    int i = EditTabs.this
			    .indexOfTabComponent(CustomTabHeader.this);
		    if (i != -1) {
			return EditTabs.this.getTitleAt(i);
		    }
		    return null;
		}
	    };

	    add(label);
	    // add more space between the label and the button
	    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

	    // tab button
	    JButton button = new CloseTabButton();
	    add(button);
	    // add more space to the top of the component
	    setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

	private class CloseTabButton extends JButton implements ActionListener {
	    public CloseTabButton() {
		setPreferredSize(new Dimension(TAB_ICON_SIZE, TAB_ICON_SIZE));
		setToolTipText("Close this file");
		// Make the button looks the same for all Laf's
		setUI(new BasicButtonUI());
		// Make it transparent
		setContentAreaFilled(false);
		// No need to be focusable
		setFocusable(false);
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(false);
		// Making nice rollover effect
		addMouseListener(new CloseButtonMouseListener());
		setRolloverEnabled(true);
		// Close the proper tab by clicking the button
		addActionListener(this);
	    }

	    public void actionPerformed(ActionEvent e) {
		int i = EditTabs.this.indexOfTabComponent(CustomTabHeader.this);
		if (i != -1) {
		    EditTabs.this.remove(i);
		}
	    }

	    @Override
	    public void updateUI() {
	    }

	    // paint the cross
	    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		// shift the image for pressed buttons
		if (getModel().isPressed()) {
		    g2.translate(1, 1);
		}
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLACK);
		if (getModel().isRollover()) {
		    g2.setColor(Color.MAGENTA);
		}
		int delta = 6;
		g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
			- delta - 1);
		g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
			- delta - 1);
		g2.dispose();
	    }

	    private class CloseButtonMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    Component component = e.getComponent();
		    if (component instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) component;
			button.setBorderPainted(true);
		    }
		}

		@Override
		public void mouseExited(MouseEvent e) {
		    Component component = e.getComponent();
		    if (component instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) component;
			button.setBorderPainted(false);
		    }
		}

		@Override
		public void mousePressed(MouseEvent e) {
		    // nothing
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		    // nothing
		}
	    }
	}
    }

    
}
