package Display.Trees;

import java.util.Random;
import Util.Util;
import processing.core.*;

public class Leaf {

	PVector	pos;
	PShape	shape;
	float	angle;
	float	hue, sat, bri, alp;
	boolean	isFalling	= false;
	float	groundY;
	float	fallSpeed;

	enum LeafShape {
		circle, square, star, crescent;
	}

	public static LeafShape randomType() {

		final Random rnd = new Random();
		LeafShape types[] = LeafShape.values();
		return types[rnd.nextInt(types.length)];
	}

	void createShape(PGraphics pg, LeafShape ls) {

		// POINT, LINE, TRIANGLE, QUAD, RECT, ELLIPSE, ARC, BOX, SPHERE
		shape = pg.createShape();
		shape.beginShape();
		shape.noStroke();
		shape.colorMode(PConstants.HSB, 360, 100, 100, 100);
		shape.fill(hue, sat, bri, alp);

		switch (ls) {
		case circle:
			float diam = 8;
			for (int i = 0; i <= 360; i += 4) {
				float x = (float) (diam * Math.sin(Math.toRadians(i)));
				float y = (float) (diam / 2 * Math.cos(Math.toRadians(i)));
				shape.vertex(x, y);
			}
			break;

		case square:
			float side = 7;
			shape.vertex(0, 0);
			shape.vertex(0, side);
			shape.vertex(side, side);
			shape.vertex(side, 0);
			break;

		case star:
			shape.scale(0.15f);
			shape.vertex(0, -50);
			shape.vertex(14, -20);
			shape.vertex(47, -15);
			shape.vertex(23, 7);
			shape.vertex(29, 40);
			shape.vertex(0, 25);
			shape.vertex(-29, 40);
			shape.vertex(-23, 7);
			shape.vertex(-47, -15);
			shape.vertex(-14, -20);
			break;

		case crescent:
			float size = 8f;
			int step = 4;

			for (int i = 0; i <= 180; i += step) {
				float x = (float) (size * Math.sin(Math.toRadians(i)));
				float y = (float) (size * Math.cos(Math.toRadians(i)));
				shape.vertex(x, y);
			}

			// Interior part of moon curve
			float max = 180;
			float min = -45;
			float half = 67.5f;
			float small = 0.4f;

			for (int i = (int) max; i >= (int) half; i -= step) {
				float mult = PApplet.map(i, max, half, 1, small);
				float x = (float) (mult * size * Math.sin(Math.toRadians(i)));
				float y = (float) (mult * size * Math.cos(Math.toRadians(i)));
				shape.vertex(x, y);
			}
			for (int i = (int) half; i >= (int) min; i -= step) {
				float mult = PApplet.map(i, half, min, small, 1);
				float x = (float) (mult * size * Math.sin(Math.toRadians(i)));
				float y = (float) (mult * size * Math.cos(Math.toRadians(i)));
				shape.vertex(x, y);
			}
			break;
		}

		shape.endShape(PConstants.CLOSE);
	}

	Leaf(LeafShape ls, PVector pos, float hue, PGraphics pg) {

		this.pos = pos;
		this.hue = hue;
		this.sat = Util.randomf(70, 100);
		this.bri = Util.randomf(70, 100);
		this.alp = Util.randomf(20, 70);
		angle = (float) Util.random(0, Math.PI * 2);
		groundY = pos.y + 100;
		fallSpeed = Util.randomf(0.5f, 1);
		createShape(pg, ls);
	}

	void draw(PGraphics pg, float size) {

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(angle);
		pg.shape(shape);
		pg.popMatrix();
	}

	void fallTick() {

		if (!isFalling || pos.y >= groundY) {
			return;
		}

		pos.y += fallSpeed;

		// TODO maybe doing a sine-based sway or something
	}
}
