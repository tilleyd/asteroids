import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/* GameFrame extends JFrame and creates an exclusive fullscreen window
 * containing a GamePanel. */

public class GameFrame extends JFrame {

	private static final int NUM_BUFFERS = 2;

	private GameLogic game;
	private GraphicsDevice gd;
	private BufferStrategy bufferStrat;
	private int width, height;

	public GameFrame() {
		initFullscreen();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new GameLogic(this, bufferStrat, width, height);
		addKeyListener(game);
		setVisible(true);
		game.startGame();
	}

	private void initFullscreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		if (gd.isFullScreenSupported()) {
			setUndecorated(true);
			setIgnoreRepaint(true);
			setResizable(false);
			gd.setFullScreenWindow(this);
			width = getBounds().width;
			height = getBounds().height;
			setupBuffer();
		} else {
			System.out.println("Error: fullscreen not supported.");
			System.exit(1);
		}
	}

	private void setupBuffer() {
		createBufferStrategy(NUM_BUFFERS);
		bufferStrat = getBufferStrategy();
	}

}
