import java.awt.*;

/* The abstract Entity class from which all objects in the game should inherit. */

abstract public class Entity {

	protected static int wrapWidth, wrapHeight;

	protected float x, y;
	protected float xSpeed, ySpeed;
	protected int width, height;

	public static void setWrapDimension(int width, int height) {
		wrapWidth = width;
		wrapHeight = height;
	}

	public void move(double period) {
		x += xSpeed * period;
		y += ySpeed * period;
		if (x > wrapWidth) {
			x = 0 - width;
		} else if (x + width < 0) {
			x = wrapWidth;
		}
		if (y > wrapHeight) {
			y = 0 - height;
		} else if (y + height < 0) {
			y = wrapHeight;
		}
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	protected void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getXSpeed() {
		return xSpeed;
	}

	public float getYSpeed() {
		return ySpeed;
	}

	public void setSpeed(float xSpeed, float ySpeed) {
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}

	public void setXSpeed(float speed) {
		xSpeed = speed;
	}

	public void setYSpeed(float speed) {
		ySpeed = speed;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setXPosition(float x) {
		this.x = x;
	}

	public void setYPosition(float y) {
		this.y = y;
	}

	public boolean contains(float x1, float y1) {
		return ((x1 <= x + width && x1 >= x) && (y1 <= y + height && y1 >= y));
	}

	public abstract void draw(Graphics2D g);

}