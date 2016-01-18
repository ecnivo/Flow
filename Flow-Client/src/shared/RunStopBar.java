
package shared;

import compiler.CompilableText;
import compiler.FlowCompiler;
import gui.FlowClient;
import message.Data;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/**
 * A toolbar with run and stop buttons
 *
 * @author Vince
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

	private void getFiles(UUID currentDirectoryUUID, ArrayList<CompilableText> outTexts, String currentPath, boolean ignoreFirst) {
		Data dirInfoRequest = new Data("directory_info");
		dirInfoRequest.put("session_id", Communicator.getSessionID());
		dirInfoRequest.put("directory_uuid", currentDirectoryUUID);
		Data dirInfo = Communicator.communicate(dirInfoRequest);
		String directoryName = dirInfo.get("directory_name", String.class);
		if (ignoreFirst)
			currentPath = Paths.get(currentPath, directoryName).toString();

		for (UUID childFileUUID : dirInfo.get("child_files", UUID[].class)) {
			Data fileDataRequest = new Data("file_info");
			fileDataRequest.put("file_uuid", childFileUUID);
			fileDataRequest.put("session_id", Communicator.getSessionID());
			Data fileData = Communicator.communicate(fileDataRequest);

			String fileName = fileData.get("file_name", String.class);
			String extension = fileName.substring(Math.min(fileName.length(), fileName.lastIndexOf('.') + 1));
			if (extension.equals("java")) {
				Data fileRequest = new Data("file_request");
				fileRequest.put("file_uuid", childFileUUID);
				fileRequest.put("session_id", Communicator.getSessionID());
				Data file = Communicator.communicate(fileRequest);

				String text = new String(file.get("file_data", byte[].class));
				CompilableText compilableText = new CompilableText(text, currentPath, fileName);
				outTexts.add(compilableText);
			}
		}

		for (UUID childDirUUID : dirInfo.get("child_directories", UUID[].class)) {
			getFiles(childDirUUID, outTexts, currentPath, true);
		}
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

	private Process	activeProcess;

	/**
	 * Runs a file
	 *
	 * @author Vince Ou
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
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// When the button is pressed
					if (editTabs == null) {
						return;
					}
					if (activeProcess != null || (activeProcess != null ? activeProcess.isAlive() : false)) {
						console.addOutput("A process is still running!\n");
					} else {
						ArrayList<CompilableText> textFiles = new ArrayList<>();
						try {
							getFiles(((EditArea) ((JScrollPane) editTabs.getSelectedComponent()).getViewport().getView()).getProjectUUID(), textFiles, "", false);
						} catch (NullPointerException e11) {
							return;
						}
						FlowCompiler flowCompiler = new FlowCompiler(textFiles.toArray(new CompilableText[0]));
						try {
							java.util.List<Diagnostic<? extends JavaFileObject>> errors = flowCompiler.build();
							if (errors != null && errors.size() > 0) {
								for (Diagnostic<? extends JavaFileObject> diagnostic : errors) {
									console.addOutput(String.format("error on line %d in %s: %s\n", diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(Locale.getDefault())));
								}
							} else {

								activeProcess = flowCompiler.execute();
								new Thread() {

									public void run() {
										console.addOutput("Running " + textFiles.get(0).getName() + "\n");
										try {
											SequenceInputStream s = new SequenceInputStream(activeProcess.getInputStream(), activeProcess.getErrorStream());
											InputStreamReader isr = new InputStreamReader(s);
											console.setActiveOutputStream(activeProcess.getOutputStream());
											int c;
											while ((c = isr.read()) != -1 && activeProcess != null) {
												console.addOutput(((char) c) + "");
											}
											isr.close();
											activeProcess.getOutputStream().close();
										} catch (IOException e) {
											e.printStackTrace();
										}
										console.addOutput("==================== STOPPED ====================\n");
										activeProcess = null;
									}
								}.start();
							}

						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showConfirmDialog(null, "Code failed to compile or run for some reason. Make sure you have the appropriate JDK installed", "Compiling failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			});
		}
	}

	/**
	 * Button to stop code execution
	 *
	 * @author Vince
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
					if (activeProcess != null) {
						activeProcess.destroyForcibly();
						activeProcess = null;
						console.setActiveOutputStream(null);
					}
				}
			});
		}
	}
}
