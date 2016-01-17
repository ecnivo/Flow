
package debug;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * A toolbar with buttons for start debug, step in/over/out
 * 
 * @author Vince Ou
 *
 */
@SuppressWarnings("serial")
public class DebugToolbar extends JToolBar {

	/**
	 * Creates a new DebugToolbar
	 */
	public DebugToolbar() {
		// Swing setup
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(FlowClient.EMPTY_BORDER);

		// Adds the buttons
		addSeparator();
		add(new StartDebuggingButton());
		add(new StepInButton());
		add(new StepOverButton());
		add(new StepOutButton());

		setFloatable(false);
		setRollover(true);
	}

	/**
	 * A button that starts debugging
	 * 
	 * @author Vince Ou
	 *
	 */
	private class StartDebuggingButton extends JButton {

		/**
		 * Creates a new StartDebuggingButton
		 */
		private StartDebuggingButton() {
			setToolTipText("Start debugging the currently open tab");
			setBorder(FlowClient.EMPTY_BORDER);
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/debug.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			addActionListener(new ActionListener() {

				/**
				 * Gets the currently active code and starts the debugging process
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Do some stuff to start debugging
					System.out.println("Debug button pressed!");
				}
			});
		}
	}

	/**
	 * A button that steps into the highlighted method
	 * 
	 * @author Vince Ou
	 *
	 */
	private class StepInButton extends JButton {

		/**
		 * Creates a new StepInButton
		 */
		private StepInButton() {
			setToolTipText("Step into the current method");
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/stepIn.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * We'll see how this'll work with a debugger later
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO step in using debugger
					System.out.println("Step in button pressed");
				}
			});
		}
	}

	/**
	 * A button that steps over the highlighted method
	 * 
	 * @author Vince Ou
	 *
	 */
	private class StepOverButton extends JButton {

		/**
		 * Creates a new StepOverButton
		 */
		private StepOverButton() {
			setToolTipText("Step over the highlighted method");
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/stepOver.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * We'll see how this'll work with a debugger later
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO step over using debugger
					System.out.println("Step over button pressed");
				}
			});
		}
	}

	/**
	 * A button that steps out of the current method
	 * 
	 * @author Vince Ou
	 *
	 */
	private class StepOutButton extends JButton {

		private StepOutButton() {
			setToolTipText("Step out of (return) the current method");
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/stepOut.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * We'll see how this'll work with a debugger later
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO step out using debugger
					System.out.println("Step out button pressed");
				}
			});
		}
	}
}
