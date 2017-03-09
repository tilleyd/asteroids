import java.awt.*;

/* The Asteroid class creates a randomly generated polygon as it's sprite. */

public class Asteroid extends Entity {

	public static final int LARGE = 128;
	public static final int MEDIUM = 64;
	public static final int SMALL = 32;
	public static final Color COLOR = new Color(110, 55, 32);
	private final int MIN_POINTS = 16;
	private final int MAX_POINTS = 32;
	private final float MIN_SPEED = 64.0f;
	private final float MAX_SPEED = 128.0f;
	private final float MAX_DIRECTION_OFFSET = 25.0f;

	private int size;
	private int numPoints;
	private float direction;
	private float[][] points;

	public Asteroid() {
		float randomX = (float)(Math.random() * wrapWidth);
		float randomY = (float)(Math.random() * wrapHeight);
		direction = (float)(Math.random() * 360.0f);
		setPosition(randomX, randomY);
		float randomSpeed = (float)(Math.random() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED);
		setXSpeed((float)(randomSpeed * Math.cos(Math.toRadians(direction))));
		setYSpeed((float)(randomSpeed * Math.sin(Math.toRadians(direction))));
		size = LARGE;
		setSize(size, size);
		randomizeLook();
	}

	public Asteroid(Asteroid prev) {
		int prevSize = prev.getSize();
		if (prevSize == LARGE) {
			size = MEDIUM;
		} else {
			size = SMALL;
		}
		setPosition(prev.getX() + prevSize / 2 - size / 2, prev.getY() + prevSize / 2 - size / 2);
		float dirOffset = (float)(Math.random() * MAX_DIRECTION_OFFSET * 2) - MAX_DIRECTION_OFFSET;
		float prevXSpeed = prev.getXSpeed();
		float prevYSpeed = prev.getYSpeed();
		float prevSpeed = (float)Math.sqrt(prevXSpeed * prevXSpeed + prevYSpeed * prevYSpeed);
		direction = (prev.getDirection() + dirOffset) % 360.0f;
		setXSpeed((float)(prevSpeed * Math.cos(Math.toRadians(direction))));
		setYSpeed((float)(prevSpeed * Math.sin(Math.toRadians(direction))));
		setSize(size, size);
		randomizeLook();
	}

	public int getSize() {
		return size;
	}

	private void randomizeLook() {
		numPoints = (int)(Math.random() * (MAX_POINTS - MIN_POINTS) + MIN_POINTS);
		points = new float[numPoints][2];
		int turn = 360 / numPoints;
		float maxPointOffset = size / (SMALL / 2);
		for (int i = 0, angle = 0; i < numPoints; i++, angle += turn) {
			float offset = (float)Math.random() * (2 * maxPointOffset) - maxPointOffset;
			float length = size / 2 + offset;
			points[i][0] = length * (float)Math.cos(Math.toRadians(angle));
			points[i][1] = length * (float)Math.sin(Math.toRadians(angle));
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(COLOR);
		Polygon poly = new Polygon();
		for (float[] point : points) {
			poly.addPoint((int)(x + point[0] + size / 2), (int)(y + (int)point[1] + size / 2));
		}
		g.fillPolygon(poly);
	}

	@Override
	public boolean contains(float x1, float y1) {
		float xMid = x + size / 2;
		float yMid = y + size / 2;
		float xDiff = x1 - xMid;
		float yDiff = y1 - yMid;
		float distance = (float)Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
		return (distance <= size / 2);
	}

	public float getDirection() {
		return direction;
	}

}