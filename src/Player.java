import java.awt.*;

/* The Player class contains all the movement and actions that the player is
 * capable of. */

public class Player extends Entity {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int FORWARD = 2;
	public static final int BACKWARD = 3;
	public static final Color COLOR = new Color(113, 141, 48);
	public static final Color SHIELD_COLOR = new Color(41, 168, 255);

	private final int WIDTH = 32;
	private final int HEIGHT = 32;
	private final float FRICTION = 256.0f;
	private final float ACCELERATION = 640.0f;
	private final float ANGULAR_SPEED = 360.0f;
	private final double SHIELD_LIFE_TIME = 3.0f;

	private float direction;
	private double shieldTime;
	private int xPoints[], yPoints[];
	private Font smallFont;

	public Player() {
		setSize(WIDTH, HEIGHT);
		direction = 0.0f;
		xPoints = new int[3];
		yPoints = new int[3];
		smallFont = new Font("Arial", Font.BOLD, 16);
	}

	@Override
	public void move(double period) {
		super.move(period);
		float moveDirection;
		if (xSpeed != 0.0f) {
			moveDirection = (float)Math.toDegrees(Math.atan(ySpeed / xSpeed));
			if (xSpeed < 0.0f) {
				moveDirection += 180.0f;
			} else if (ySpeed < 0.0f) {
				moveDirection += 360.0f;
			}
		} else {
			if (ySpeed < 0.0f) {
				moveDirection = 270.0f;
			} else {
				moveDirection = 90.0f;
			}
		}
		if (xSpeed != 0.0f) {
			float xFric = (float)(FRICTION * period * Math.cos(Math.toRadians(moveDirection)));
			if (Math.abs(xSpeed) - Math.abs(xFric) < 0.0f) {
				xSpeed = 0.0f;
			} else {
				xSpeed -= xFric;
			}
		}
		if (ySpeed != 0.0f) {
			float yFric = (float)(FRICTION * period * Math.sin(Math.toRadians(moveDirection)));
			if (Math.abs(ySpeed) - Math.abs(yFric) < 0.0f) {
				ySpeed = 0.0f;
			} else {
				ySpeed -= yFric;
			}
		}
	}

	public void activateShield() {
		shieldTime = SHIELD_LIFE_TIME;
	}

	public boolean hasShield() {
		return (shieldTime > 0.0);
	}

	public void decreaseTime(double period) {
		if (shieldTime > 0.0) {
			shieldTime -= period;
		}
	}

	public void turn(double period, int dir) {
		switch (dir) {
			case LEFT:
				direction -= ANGULAR_SPEED * period;
				break;
			case RIGHT:
				direction += ANGULAR_SPEED * period;
				break;
		}
		if (direction < 0.0f) {
			direction = 360.0f + direction;
		}
		direction %= 360.0f;
	}

	public void accelerate(double period, int dir) {
		switch (dir) {
			case FORWARD:
				xSpeed += (ACCELERATION * period * Math.cos(Math.toRadians(direction)));
				ySpeed += (ACCELERATION * period * Math.sin(Math.toRadians(direction)));
				break;
			case BACKWARD:
				xSpeed -= (ACCELERATION * period * Math.cos(Math.toRadians(direction)));
				ySpeed -= (ACCELERATION * period * Math.sin(Math.toRadians(direction)));
				break;
		}
	}

	public void setDirection(float dir) {
		direction = dir % 360.0f;
	}

	public float getDirection() {
		return direction;
	}

	public int[] getXPoints() {
		return xPoints;
	}

	public int[] getYPoints() {
		return yPoints;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(COLOR);
		float radWidth = WIDTH / 2;
		float radHeight = HEIGHT / 2;
		float xMid = x + radWidth;
		float yMid = y + radHeight;
		xPoints[0] = (int)(xMid + (radWidth * (float)Math.cos(Math.toRadians(direction))));
		yPoints[0] = (int)(yMid + (radHeight * (float)Math.sin(Math.toRadians(direction))));
		xPoints[1] = (int)(xMid - (radWidth * (float)Math.cos(Math.toRadians(direction + 30))));
		yPoints[1] = (int)(yMid - (radHeight * (float)Math.sin(Math.toRadians(direction + 30))));
		xPoints[2] = (int)(xMid - (radWidth * (float)Math.cos(Math.toRadians(direction - 30))));
		yPoints[2] = (int)(yMid - (radHeight * (float)Math.sin(Math.toRadians(direction - 30))));
		g.fillPolygon(xPoints, yPoints, 3);
		if (hasShield()) {
			g.setFont(smallFont);
			g.setColor(SHIELD_COLOR);
			g.drawOval((int)x, (int)y, WIDTH, HEIGHT);
			g.drawString("" + (int)Math.ceil(shieldTime), (int)x, (int)y);
		}
	}

}