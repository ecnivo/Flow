
package shared;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import message.Data;
import struct.VersionText;

/**
 * A toolbar with run and stop buttons
 * 
 * @author Vince
 *
 */
@SuppressWarnings("serial")
public class RunStopBar extends JToolBar {

	private EditTabs	editTabs;

	/**
	 * Creates a new RunStopBar
	 * 
	 * @param console
	 *        the GenericConsole to run in
	 */
	public RunStopBar(GenericConsole console) {
		// Swing stuff
		setBorder(FlowClient.EMPTY_BORDER);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// Adds the buttons
		add(new RunButton(console));
		add(new StopButton(console));

		// More swing stuff
		setFloatable(false);
		setRollover(true);
	}

	/**
	 * Used to recursively iterate through the entire tree for a project getting
	 * all text files so that they can be compiled
	 * 
	 * @param projectUUID
	 *        the project to search for the files
	 */
	private VersionText[] getFiles(UUID projectUUID) {
		// Gets the list of files at this level in the directory
		ArrayList<VersionText> out = new ArrayList<VersionText>();

		// Asks the server for information
		Data dirInfoRequest = new Data("directory_info");
		dirInfoRequest.put("session_id", Communicator.getSessionID());
		dirInfoRequest.put("directory_uuid", projectUUID);
		Data dirInfo = Communicator.communicate(dirInfoRequest);

		// Goes through all the children files and adds them
		for (UUID childFileUUID : dirInfo.get("child_files", UUID[].class)) {
			// Gets information about these files
			Data fileRequest = new Data("file_request");
			fileRequest.put("file_uuid", childFileUUID);
			fileRequest.put("session_id", Communicator.getSessionID());
			Data file = Communicator.communicate(fileRequest);
			if (file == null) {
				return null;
			} else if (file.get("status", String.class).equals("ACCESS_DENIED")) {
				JOptionPane.showConfirmDialog(null, "You do not have sufficient permissions complete this operation.", "Access Denied", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				return null;
			}

			VersionText versionText = new VersionText();
			versionText.setDocumentText(new String(file.get("file_data", byte[].class)));

			// Gets the file data and adds it
			out.add(versionText);
		}
		// Recursively does this in its child directories
		for (UUID childDir : dirInfo.get("child_directories", UUID[].class)) {
			out.addAll(Arrays.asList(getFiles(childDir)));
		}

		// Converts it to an array
		VersionText[] outArray = new VersionText[out.size()];
		return out.toArray(outArray);
	}

	/**
	 * Sets edit tabs as necessary
	 * 
	 * @param tabs
	 *        the new edit tabs
	 */
	public void setEditTabs(EditTabs tabs) {
		editTabs = tabs;
	}

	/**
	 * Runs a file
	 * 
	 * @author Vince Ou
	 *
	 */
	private class RunButton extends JButton {

		/**
		 * Creates a new RunButton
		 * 
		 * @param console
		 *        the affiliated console
		 */
		public RunButton(GenericConsole console) {
			// swing stuff
			setToolTipText("Compiles, then runs the file currently open in the editor");
			setBorder(FlowClient.EMPTY_BORDER);
			// Sets icon
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/run.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// More swing
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			// addActionListener(new ActionListener() {
			//
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// // When the button is pressed
			// if (editTabs == null) {
			// return;
			// }
			// /*
			// // Create a compiler
			// FlowCompiler flowCompiler = new FlowCompiler(getFiles(((EditArea)
			// editTabs.getSelectedComponent()).getProjectUUID()));
			// // Try to build and execute it
			// // TODO make this work
			// try {
			// List<Diagnostic<? extends JavaFileObject>> errors = flowCompiler.build();
			// flowCompiler.execute();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// JOptionPane.showConfirmDialog(null,
			// "Code failed to compile or run for some reason. Make sure you have the appropriate JDK installed",
			// "Compiling failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			// return;
			// }*/
			// // System.out.println("Run button pressed");
			// }
			// }
			// });
		}
	}

	/**
	 * Button to stop code execution
	 * 
	 * @author Vince
	 *
	 */
	private class StopButton extends JButton {

		/**
		 * Creates a new StopButton
		 * 
		 * @param console
		 *        the console to stop
		 */
		public StopButton(GenericConsole console) {
			// Swing stuff (incl. setting icon)
			setToolTipText("Stops the currently running program");
			setBorder(FlowClient.EMPTY_BORDER);
			try {
				setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/stop.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setFocusable(false);
			setBorder(FlowClient.EMPTY_BORDER);
			addActionListener(new ActionListener() {

				/**
				 * Stops code execution
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO make it stop
					System.out.println("Stop button pressed");
				}
			});
		}
	}
}
