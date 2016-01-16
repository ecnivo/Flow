package shared;

import gui.FlowClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import message.Data;
import struct.TextFile;

import compiler.FlowCompiler;

@SuppressWarnings("serial")
public class RunStopBar extends JToolBar {

    private EditTabs editTabs;

    public RunStopBar(GenericConsole console) {
	setBorder(FlowClient.EMPTY_BORDER);
	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

	add(new RunButton(console));
	add(new StopButton(console));

	setFloatable(false);
	setRollover(true);
    }

    /**
     * Used to recursively iterate through the entire tree for a project getting
     * all text files so that they can be compiled
     * 
     * @param directory
     *            the directory to search for the files
     */
    private TextFile[] getFiles(UUID directory) {
	ArrayList<TextFile> out = new ArrayList<TextFile>();

	Data dirInfoRequest = new Data("directory_info");
	dirInfoRequest.put("session_id", Communicator.getSessionID());
	dirInfoRequest.put("directory_uuid", directory);
	Data dirInfo = Communicator.communicate(dirInfoRequest);

	for (UUID childFileUUID : dirInfo.get("child_files", UUID[].class)) {
	    Data documentRequest = new Data("document_request");
	    documentRequest.put("file_uuid", childFileUUID);
	    documentRequest.put("session_id", Communicator.getSessionID());
	    Data document = Communicator.communicate(documentRequest);

	    TextFile textFile = new TextFile();
	    textFile.setDocumentText(new String(document.get("file_data", byte[].class)));
	    out.add(textFile);
	}
	for (UUID childDir : dirInfo.get("child_directories", UUID[].class)) {
	    out.addAll(Arrays.asList(getFiles(childDir)));
	}

	TextFile[] outArray = new TextFile[out.size()];
	for (int i = 0; i < out.size(); i++) {
	    outArray[i] = out.get(i);
	}
	return outArray;
    }

    public void setEditTabs(EditTabs tabs) {
	editTabs = tabs;
    }

    private class RunButton extends JButton {
	public RunButton(GenericConsole console) {
	    setToolTipText("Compiles, then runs the file currently open in the editor");
	    setBorder(FlowClient.EMPTY_BORDER);
	    try {
		setIcon(new ImageIcon(ImageIO.read(new File("images/run.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    if (editTabs == null) {
			return;
		    }
		    FlowCompiler flowCompiler = new FlowCompiler(getFiles(((EditArea) editTabs.getSelectedComponent()).getProjectUUID()));
		    try {
			List<Diagnostic<? extends JavaFileObject>> errors = flowCompiler.build();
			flowCompiler.execute();
		    } catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Code failed to compile or run for some reason. Make sure you have the appropriate JDK installed", "Compiling failed", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		    }
		    System.out.println("Run button pressed");
		}
	    });
	}
    }

    private class StopButton extends JButton {

	public StopButton(GenericConsole console) {
	    setToolTipText("Stops the currently running program");
	    setBorder(FlowClient.EMPTY_BORDER);
	    try {
		setIcon(new ImageIcon(ImageIO.read(new File("images/stop.png")).getScaledInstance(FlowClient.BUTTON_ICON_SIZE, FlowClient.BUTTON_ICON_SIZE, Image.SCALE_SMOOTH)));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    setFocusable(false);
	    setBorder(FlowClient.EMPTY_BORDER);
	    addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO make it stop
		    System.out.println("Stop button pressed");
		}
	    });
	}
    }
}
