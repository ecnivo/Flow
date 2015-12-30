package editing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Testing {

    public static void main(String[] args) {
	JFrame frame = new JFrame();
	frame.setSize(250, 250);
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel hi = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		// super.

		for (int i = 0; i < 50; i++) {

		    g.setColor(new Color((int) (Math.random() * 150) + 60,
			    (int) (Math.random() * 150) + 60, (int) (Math
				    .random() * 150) + 60));

		    g.fillRect(i * 4, i * 4, 20, 20);
		}
	    }
	};
	frame.add(hi);
	hi.repaint();
    }
}
