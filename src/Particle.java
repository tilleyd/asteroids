import java.awt.*;

/* Particles can be spawned in large numbers to create effects. */

public class Particle extends Entity {

	private Color color;
	private int size;
	private double lifeTime;
	private boolean hasLifeTime = false;

	public Particle(float x, float y, int size) {
		setPosition(x, y);
		setSize(size, size);
		this.size = size;
		hasLifeTime = false;
	}

	public void randomizeColor() {
		int r = (int)(Math.random() * 128 + 128);
		int g = (int)(Math.random() * 128 + 128);
		int b = (int)(Math.random() * 128 + 128);
		color = new Color(r, g, b);
	}

	public void decreaseTime(double period) {
		if (hasLifeTime && lifeTime > 0.0) {
			lifeTime -= period;
		}
	}

	public boolean hasTime() {
		return (!hasLifeTime || lifeTime > 0.0);
	}

	public void setLifetime(double time) {
		hasLifeTime = true;
		lifeTime = time;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.drawOval((int)x - size / 2, (int)y - size / 2, size, size);
	}

	public void setColor(Color c) {
		color = c;
	}

}