import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;

/* This is where all the magic happens. */

public class GameLogic extends KeyAdapter implements Runnable {

	private final int FPS = 144;
	private final int PERIOD_NS = 1000000000 / FPS;
	private final double PERIOD_S = 1.0 / FPS;
	private final int MAX_DRAWS_WITHOUT_SLEEP = 16;
	private final int MAX_FRAME_SKIPS = 12;

	private volatile boolean running;

	private int width;
	private int height;
	private JFrame parent;
	private BufferStrategy bufferStrat;
	private Graphics2D g;
	private Thread gameThread;
	private Font medFont;
	private Font largeFont;
	private FontMetrics medMetrics;
	private FontMetrics largeMetrics;
	private GameSounds sounds;

	// game variables
	private final double BULLET_DELAY = 0.2;
	private final int LEVEL_OFFSET = 3;
	private final int START_LIVES = 5;
	private final int EXPLOSION_PARTICLES = 64;
	private final int STAR_PARTICLES = 128;
	private int score;
	private int lives;
	private double bulletTime;
	private int level;
	private volatile boolean leftPressed, rightPressed, upPressed, downPressed, spacePressed;
	private boolean gamePaused, gameOver;
	private Player player;
	private LinkedList<Bullet> bullets;
	private LinkedList<Asteroid> asteroids;
	private LinkedList<Particle> particles;
	private LinkedList<Particle> stars;

	public GameLogic(JFrame par, BufferStrategy buff, int w, int h) {
		parent = par;
		bufferStrat = buff;
		width = w;
		height = h;
		medFont = new Font("Arial", Font.BOLD, 32);
		medMetrics = parent.getFontMetrics(medFont);
		largeFont = new Font("Arial", Font.BOLD, 72);
		largeMetrics = parent.getFontMetrics(largeFont);
		running = false;
	}

	public void startGame() {
		if (!running || gameThread == null) {
			gameThread = new Thread(this);
		}
		Entity.setWrapDimension(width, height);
		leftPressed = rightPressed = upPressed = downPressed = false;
		gamePaused = false;
		gameOver = false;
		player = new Player();
		resetPlayer();
		bullets = new LinkedList<>();
		asteroids = new LinkedList<>();
		particles = new LinkedList<>();
		stars = new LinkedList<>();
		// TODO add loading screen for sounds
		sounds = new GameSounds();
		level = 1;
		lives = START_LIVES;
		score = 0;
		bulletTime = BULLET_DELAY;
		populateAsteroids();
		createStars();
		sounds.playSound(GameSounds.SOUND_MUSIC, true);
		gameThread.start();
	}

	private void resetPlayer() {
		if (player != null) {
			player.setPosition(width / 2, height / 2);
			player.setDirection(0.0f);
			player.setSpeed(0.0f, 0.0f);
			player.activateShield();
		}
	}

	private void resetGame() {
		gamePaused = false;
		gameOver = false;
		resetPlayer();
		bullets.clear();
		asteroids.clear();
		particles.clear();
		stars.clear();
		level = 1;
		lives = START_LIVES;
		score = 0;
		bulletTime = BULLET_DELAY;
		populateAsteroids();
		createStars();
	}

	private void populateAsteroids() {
		asteroids.clear();
		for (int i = 0; i < level + LEVEL_OFFSET; i++) {
			asteroids.add(new Asteroid());
		}
	}

	private void createStars() {
		stars.clear();
		for (int i = 0; i < STAR_PARTICLES; i++) {
			float scale = (float)Math.random();
			Particle star = new Particle((float)Math.random() * width, (float)Math.random() * height,
					(int)(scale * 3.0f));
			star.setXSpeed(scale * 50.0f);
			star.setYSpeed(scale * 25.0f);
			star.setColor(new Color(54, 27, 68));
			stars.add(star);
		}
	}

	private void createExplosion(float x, float y, Color color) {
		for (int i = 0; i < EXPLOSION_PARTICLES; i++) {
			Particle particle = new Particle(x, y, 1);
			particle.setColor(color);
			particle.setLifetime(Math.random() * 1.0);
			particle.setXSpeed((float)Math.random() * 256.0f - 128.0f);
			particle.setYSpeed((float)Math.random() * 256.0f - 128.0f);
			particles.add(particle);
		}
	}

	public void stopGame() {
		sounds.stop();
		running = false;
	}

	@Override
	public void run() {
		running = true;
		long startTime = System.nanoTime();
		long afterTime, sleepTime;
		long oversleep = 0L;
		long timeMissed = 0L;
		int drawsWithoutSleep = 0;
		int framesSkipped;
		while (running) {
			update();
			draw();
			afterTime = System.nanoTime();
			sleepTime = PERIOD_NS - (afterTime - startTime) - oversleep;
			if (sleepTime > 0L) {
				try {
					Thread.sleep(sleepTime / 1000000);
				} catch (InterruptedException ie) {}
				oversleep = (System.nanoTime() - afterTime) - sleepTime;
			} else {
				timeMissed -= sleepTime;
				drawsWithoutSleep++;
				if (drawsWithoutSleep >= MAX_DRAWS_WITHOUT_SLEEP) {
					drawsWithoutSleep = 0;
					Thread.yield();
				}
			}
			framesSkipped = 0;
			while (timeMissed >= PERIOD_NS && framesSkipped < MAX_FRAME_SKIPS) {
				update();
				timeMissed -= PERIOD_NS;
				framesSkipped++;
			}
			startTime = System.nanoTime();
		}
		System.exit(0);
	}

	private void update() {
		if (!gamePaused) {
			for (Asteroid asteroid : asteroids) {
				asteroid.move(PERIOD_S);
			}
			for (int i = 0; i < particles.size(); i++) {
				Particle particle = particles.get(i);
				particle.decreaseTime(PERIOD_S);
				if (particle.hasTime()) {
					particle.move(PERIOD_S);
				} else {
					particles.remove(i);
					i--;
				}
			}
			for (Particle star : stars) {
				star.move(PERIOD_S);
			}
			if (!gameOver) {
				player.move(PERIOD_S);
				player.decreaseTime(PERIOD_S);
				for (int i = 0; i < bullets.size(); i++) {
					Bullet bullet = bullets.get(i);
					bullet.decreaseTime(PERIOD_S);
					if (bullet.hasTime()) {
						bullet.move(PERIOD_S);
					} else {
						bullets.remove(i);
						i--;
					}
				}
				if (leftPressed) {
					player.turn(PERIOD_S, Player.LEFT);
				}
				if (rightPressed) {
					player.turn(PERIOD_S, Player.RIGHT);
				}
				if (upPressed) {
					player.accelerate(PERIOD_S, Player.FORWARD);
				}
				if (downPressed) {
					player.accelerate(PERIOD_S, Player.BACKWARD);
				}
				if (bulletTime > 0.0) {
					bulletTime -= PERIOD_S;
				}
				if (spacePressed && bulletTime <= 0.0) {
					bullets.add(new Bullet(player));
					sounds.playSound(GameSounds.SOUND_LASER, false);
					bulletTime = BULLET_DELAY;
				} else if (!spacePressed) {
					bulletTime = 0.0;
				}
				// check bullet collisions
				for (int i = 0; i < asteroids.size(); i++) {
					Asteroid asteroid = asteroids.get(i);
					for (int j = 0; j < bullets.size(); j++) {
						Bullet bullet = bullets.get(j);
						if (asteroid.contains(bullet.getX(), bullet.getY())) {
							bullets.remove(j);
							createExplosion(bullet.getX(), bullet.getY(), Bullet.COLOR);
							createExplosion(asteroid.getX() + asteroid.getWidth() / 2,
									asteroid.getY() + asteroid.getHeight() / 2, Asteroid.COLOR);
							sounds.playSound(GameSounds.SOUND_ASTEROID, false);
							if (asteroid.getSize() != Asteroid.SMALL) {
								asteroids.add(new Asteroid(asteroid));
								asteroids.add(new Asteroid(asteroid));
							}
							score += (Asteroid.LARGE * 2) / asteroid.getSize();
							asteroids.remove(i);
							j--;
							i--;
						}
					}
				}
				// check asteroid collisions
				if (!player.hasShield()) {
					int xPoints[] = player.getXPoints();
					int yPoints[] = player.getYPoints();
					for (int i = 0; i < asteroids.size(); i++) {
						Asteroid asteroid = asteroids.get(i);
						boolean contains = false;
						int j = 0;
						while (!contains && j < 3) {
							if (asteroid.contains(xPoints[j], yPoints[j])) {
								contains = true;
								createExplosion(xPoints[j], yPoints[j], Player.COLOR);
								createExplosion(asteroid.getX() + asteroid.getWidth() / 2,
										asteroid.getY() + asteroid.getHeight() / 2, Asteroid.COLOR);
								sounds.playSound(GameSounds.SOUND_COLLISION, false);
							}
							j++;
						}
						if (contains) {
							lives--;
							if (asteroid.getSize() != Asteroid.SMALL) {
								asteroids.add(new Asteroid(asteroid));
								asteroids.add(new Asteroid(asteroid));
							}
							asteroids.remove(i);
							i--;
							resetPlayer();
							break;
						}
					}
				}
				// check for game over
				if (lives <= 0) {
					gameOver = true;
					bullets.clear();
				}
				if (!gameOver) {
					// check for empty field
					if (asteroids.size() == 0) {
						level++;
						populateAsteroids();
						resetPlayer();
						bullets.clear();
					}
				}
			} else {
				if (spacePressed) {
					resetGame();
				}
			}
		}
	}

	private void buffer(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// draw background
		g.setColor(new Color(23, 12, 26));
		g.fillRect(0, 0, width, height);
		// draw entities
		for (Particle star : stars) {
			star.draw(g);
		}
		for (Bullet bullet : bullets) {
			bullet.draw(g);
		}
		for (Asteroid asteroid : asteroids) {
			asteroid.draw(g);
		}
		if (!gameOver) {
			player.draw(g);
		}
		for (Particle particle : particles) {
			particle.draw(g);
		}
		if (!gameOver) {
			g.setFont(medFont);
			g.setColor(new Color(178, 163, 255));
			g.drawString("Score: " + score, 10, medMetrics.getHeight());
			g.drawString("Lives: " + lives, 10, medMetrics.getHeight() * 2);
			g.drawString("Level: " + level, 10, medMetrics.getHeight() * 3);
		} else {
			Rectangle2D textBound1 = largeMetrics.getStringBounds("Game Over", g);
			Rectangle2D textBound2 = medMetrics.getStringBounds("Score: " + score, g);
			Rectangle2D textBound3 = medMetrics.getStringBounds("Level: " + level, g);
			g.setFont(largeFont);
			g.setColor(new Color(178, 163, 255));
			g.drawString("Game Over", (int)(width / 2 - textBound1.getWidth() / 2), height / 2);
			g.setFont(medFont);
			g.drawString("Score: " + score, (int)(width / 2 - textBound2.getWidth() / 2),
					(int)(height / 2 + textBound1.getHeight()));
			g.drawString("Level: " + level, (int)(width / 2 - textBound3.getWidth() / 2),
					(int)(height / 2 + textBound1.getHeight() + textBound2.getHeight()));
		}
	}

	private void draw() {
		try {
			g = (Graphics2D)bufferStrat.getDrawGraphics();
			buffer(g);
			g.dispose();
			if (!bufferStrat.contentsLost()) {
				bufferStrat.show();
			} else {
				System.out.println("Warning: graphics buffer contents lost.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
			upPressed = true;
		} else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = true;
		} else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
			downPressed = true;
		} else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
			spacePressed = true;
		} else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
			stopGame();
		}
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
			upPressed = false;
		} else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = false;
		} else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
			spacePressed = false;
		} else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
			downPressed = false;
		}
	}

}
