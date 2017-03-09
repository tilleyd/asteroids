import javax.swing.*;

/* GameFrame extends JFrame and creates an undecorated fullscreen window
 * containing a GamePanel. */

public class GameFrame extends JFrame {

	private GamePanel game;

	public GameFrame() {
		super("Asteroids indev");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint(true);
		setUndecorated(true);
		setLocation(0, 0);
		game = new GamePanel();
		add(game);
		pack();
		setVisible(true);
	}

}