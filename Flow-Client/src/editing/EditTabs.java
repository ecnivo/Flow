package editing;

import gui.FlowClient;

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Created by Vince on 2015-12-18.
 */
public class EditTabs extends JTabbedPane {

    public static final int TAB_LIMIT = 25;

    public static final int TAB_ICON_SIZE = 16;
    private boolean editable;

    public EditTabs(boolean editable) {
	setMinimumSize(new Dimension(50, 0));
	setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
	setTabPlacement(JTabbedPane.TOP);
	setBorder(FlowClient.EMPTY_BORDER);
	this.editable = editable;
	addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent e) {
		// nothing
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {
		    removeTabAt(getSelectedIndex());
		}
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {
		// nothing
	    }
	});
    }

    public void openTab(File file) {
	// TODO change the parameter, and add checks if the tab is already open
	if (getTabCount() <= TAB_LIMIT) {
	    addTab(file.getName(),
		    new EditArea(file, editable, this).getScrollPane());
	    int idx = getTabCount() - 1;
	    setTabComponentAt(idx, new CustomTabHeader(file));
	    setToolTipTextAt(idx, file.getAbsolutePath());
	}
    }

    class CustomTabHeader extends JPanel {
	
	private JPopupMenu rightClickMenu;

	public CustomTabHeader(File file) {
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
	    JLabel iconL = new JLabel(icon);
	    iconL.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
	    add(iconL);

	    // make JLabel read titles from JTabbedPane
	    JLabel label = new JLabel(file.getName());
	    add(label);
	    // add more space between the label and the button
	    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

	    // tab button
	    JButton button = new CloseTabButton();
	    add(button);
	    // add more space to the top of the component
	    setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

	    rightClickMenu = new JPopupMenu();
	    JMenuItem closeTabButton = new JMenuItem(new Action() {

	        
	        @Override
	        public void actionPerformed(ActionEvent arg0) {
	            int i = EditTabs.this.indexOfTabComponent(CustomTabHeader.this);
	            if (i != -1){
	        	EditTabs.this.removeTabAt(i);
	            }
	        }
	        
	        @Override
	        public void setEnabled(boolean b) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public void removePropertyChangeListener(PropertyChangeListener listener) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public void putValue(String key, Object value) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public boolean isEnabled() {
	    	// TODO Auto-generated method stub
	    	return false;
	        }
	        
	        @Override
	        public Object getValue(String key) {
	    	// TODO Auto-generated method stub
	    	return null;
	        }
	        
	        @Override
	        public void addPropertyChangeListener(PropertyChangeListener listener) {
	    	// TODO Auto-generated method stub
	    	
	        }
	    });
	    closeTabButton.setText("Close Tab (Ctrl+W)");
	    closeTabButton.setEnabled(true);
	    rightClickMenu.add(closeTabButton);
	    JMenuItem closeAllTabsButton = new JMenuItem(new Action() {

	        
	        @Override
	        public void actionPerformed(ActionEvent arg0) {
	            EditTabs.this.removeAll();
	        }
	        
	        @Override
	        public void setEnabled(boolean b) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public void removePropertyChangeListener(PropertyChangeListener listener) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public void putValue(String key, Object value) {
	    	// TODO Auto-generated method stub
	    	
	        }
	        
	        @Override
	        public boolean isEnabled() {
	    	// TODO Auto-generated method stub
	    	return false;
	        }
	        
	        @Override
	        public Object getValue(String key) {
	    	// TODO Auto-generated method stub
	    	return null;
	        }
	        
	        @Override
	        public void addPropertyChangeListener(PropertyChangeListener listener) {
	    	// TODO Auto-generated method stub
	    	
	        }
	    });
	    closeAllTabsButton.setText("Close All Tabs");
	    closeAllTabsButton.setEnabled(true);
	    rightClickMenu.add(closeAllTabsButton);
	    addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent e) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {
		    int i = EditTabs.this
			    .indexOfTabComponent(CustomTabHeader.this);
		    if (i != -1) {
			if (e.getButton() == MouseEvent.BUTTON2) {
			    EditTabs.this.remove(i);

			} else if (e.getButton() == MouseEvent.BUTTON1) {
			    EditTabs.this.setSelectedIndex(i);

			} else if (e.getButton() == MouseEvent.BUTTON3) {
			    rightClickMenu.show(CustomTabHeader.this, e.getX(), e.getY());
			}
		    }
		}
	    });
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
