package Display.Trees;

import java.util.Random;

import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Leaf {

	PVector	pos;
	PShape	shape;
	float	angle;
	float	hue, sat, bri, alp;
	float	fallHue;
	float	groundY;
	float	fallSpeed;
	boolean	isFalling		= false;
	float	nominalScale	= 1;
	float	currentScale	= 0;

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

		this.pos = pos.copy();
		this.pos.x += Util.randomf(-8, 8);
		this.pos.y += Util.randomf(-8, 8);
		this.hue = hue;
		this.sat = Util.randomf(70, 100);
		this.bri = Util.randomf(70, 100);
		this.alp = Util.randomf(30, 70);
		angle = (float) Util.random(0, Math.PI * 2);
		groundY = PApplet.map(Util.randomf(0, 1), 0, 1, 30, 120);
		fallSpeed = Util.randomf(0.5f, 1);
		fallHue = Util.randomf(10, 50);
		createShape(pg, ls);
	}

	void draw(PApplet parent, PGraphics pg, float size) {

		if (currentScale < nominalScale) {
			currentScale += 0.005;
		}
		if (isFalling) {
			fallTick(parent);
		} else {
			turnColorTick();
		}

		//
		//		pg.strokeWeight(1);
		//		pg.stroke(255, 0, 0);
		//		pg.line(0, groundY, pg.width, groundY);
		//		pg.stroke(0, 255, 0);
		//		pg.line(0, 0, pg.width, 0);
		//		pg.stroke(0, 0, 255);
		//		pg.stroke(0, pos.y, pg.width, pos.y);
		//
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(angle);
		pg.scale(currentScale);
		pg.shape(shape);
		pg.popMatrix();
	}

	void turnColorTick() {

		if (hue > fallHue) {
			hue -= 0.1;
			bri *= 0.95;
			updateColor();
		}
	}

	void updateColor() {
		shape.setFill(Util.colorFrom360(fallHue, sat, bri, alp));
	}

	void fallTick(PApplet parent) {

		if (pos.y >= groundY) {

			if (alp > 0) {
				alp *= 0.99;
				sat *= 0.95;
				updateColor();
			} else {
				isFalling = false;
			}
			return;
		}

		sat *= 0.95;
		bri *= 0.95;
		pos.y += fallSpeed;
		pos.x += (parent.noise(pos.y) - 0.5) * 4;
		angle += (parent.noise(pos.y) - 0.5) * 0.25;
		fallSpeed += 0.01 * fallSpeed;

		updateColor();
	}
}
