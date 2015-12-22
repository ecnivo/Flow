package flow_debug_commons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextArea;

public class GenericConsole extends JTextArea {
    public GenericConsole() {
	super("FLOW - CONSOLE");
	setWrapStyleWord(true);
	setBackground(Color.BLACK);
	setForeground(Color.WHITE);
	setFont(new Font("Consolas", Font.PLAIN, 12));
	setEditable(false);
	setMaximumSize(new Dimension(120, Integer.MAX_VALUE));
    }
}
