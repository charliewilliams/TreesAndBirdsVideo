package Display.Trees;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Leaf {

	PVector	pos;
	PShape	shape, glowShape;
	float	angle;
	float	hue, sat, bri, alp;
	float	fallHue;
	float	groundY;
	float	fallSpeed;
	boolean	isFalling		= false;
	float	nominalScale	= 1;
	float	currentScale	= 0;
	float	glowAmount		= 255;

	enum LeafShape {
		ellipse, polygon, star, crescent;
	}

	public static ArrayList<LeafShape>	usedTypes	= new ArrayList<LeafShape>();
	private static LeafShape			types[]		= LeafShape.values();

	public static LeafShape randomType(Random rnd) {

		if (usedTypes.size() == types.length) {
			usedTypes.removeAll(usedTypes);
		}

		while (true) {
			int idx = rnd.nextInt(types.length);
			LeafShape type = types[idx];

			if (usedTypes.contains(type)) {
				continue;
			}
			usedTypes.add(type);
			return type;
		}
	}

	PShape createShape(PGraphics pg, LeafShape ls) {

		// POINT, LINE, TRIANGLE, QUAD, RECT, ELLIPSE, ARC, BOX, SPHERE
		PShape shape = pg.createShape();
		shape.beginShape();

		shape.noStroke();
		shape.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		shape.fill(hue, sat, bri, alp);
		//		PApplet.println(hue, fallHue);

		switch (ls) {
		case ellipse:
			float diam = 6;
			for (int i = 0; i <= 360; i += 4) {
				float x = (float) (diam * Math.sin(Math.toRadians(i)));
				float y = (float) (diam / 2 * Math.cos(Math.toRadians(i)));
				shape.vertex(x, y);
			}
			break;

		case polygon:
			float side = 7;

			int sides = (int) Util.random(5, 7);
			float twoPi = (float) (Math.PI * 2);
			float angle = twoPi / (float) sides;

			for (float a = 0; a < twoPi; a += angle) {

				float sx = (float) (Math.cos(a) * side);
				float sy = (float) (Math.sin(a) * side);
				shape.vertex(sx, sy);
			}

			break;

		case star:
			shape.scale(0.2f);
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
			float size = 9f;
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
		
		return shape;
	}

	Leaf(LeafShape ls, PVector pos, float hue, PGraphics pg, PGraphics glow) {

		this.pos = pos.copy();
		float posDrift = 5;
		this.pos.x += Util.randomf(-posDrift, posDrift);
		this.pos.y += Util.randomf(-posDrift, posDrift);
		this.hue = hue;
		this.sat = Util.randomf(70, 100);
		this.bri = Util.randomf(70, 100);
		this.alp = Util.randomf(30, 70);
		angle = (float) Util.random(0, Math.PI * 2);
		groundY = PApplet.map(Util.randomf(0, 1), 0, 1, 30, 120);
		fallSpeed = Util.randomf(0.5f, 1);
		fallHue = Util.randomf(0, 50);
		shape = createShape(pg, ls);
		glowShape = createShape(glow, ls); // annoying that we can't copy it, oh well.
		glowShape.setFill(0xFFFFFFFF);
	}

	void draw(PApplet parent, PGraphics pg) {

		if (currentScale < nominalScale) {
			currentScale += 0.005;
		}
		if (isFalling) {
			fallTick(parent);
		}

		updateColor();

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(angle);
		pg.scale(currentScale);
		pg.shape(shape);
		pg.popMatrix();
	}

	void drawGlow(PGraphics2D pg) {
		
		if (glowAmount < 0.01) {
			glowAmount = 0;
			return;
		}
		
		updateGlowColor();

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(angle);
		pg.scale(nominalScale);
		pg.shape(glowShape);
		pg.popMatrix();

		glowAmount *= 0.9f;
	}

	float distanceToFallHue() {
		return Math.abs(hue - fallHue);
	}

	boolean turnColorTick() {

		if (distanceToFallHue() < 2) {
			return false;
		}

		float colorChangeSpeed = 0.5f;

		if (hue > fallHue) {
			hue -= colorChangeSpeed;
		} else {
			hue += colorChangeSpeed;
		}

		return true;
	}

	void updateColor() {

		int rgb = Color.HSBtoRGB(hue / 360, sat / 100, bri / 100);
		shape.setFill(Util.setAlpha(rgb, alp * 2.55f));
	}
	
	void updateGlowColor() {
		
		glowShape.setFill(Util.setAlpha(0xFFFFFFFF, glowAmount));
	}

	void fallTick(PApplet parent) {

		if (pos.y >= groundY) {

			if (alp > 0) {
				alp *= 0.95;
				sat *= 0.95;
			}
			return;
		}

		pos.y += fallSpeed;
		pos.x += (parent.noise(pos.y) - 0.5) * 4;
		angle += (parent.noise(pos.y) - 0.5) * 0.25;
		fallSpeed += 0.01 * fallSpeed;
	}
}
