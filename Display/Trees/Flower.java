package Display.Trees;

import java.util.ArrayList;
import java.util.Random;

import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class Flower {

	FlowerType	flowerType;
	PVector		pos;
	float		satBri			= Util.randomf(155, 255);
	float		alpha			= Util.randomf(32, 192);
	float		weight			= Util.randomf(4, 10);
	boolean		isFalling		= false;
	float		groundY;
	float		fallSpeed;
	float		currentScale	= 0, nominalScale = 1;
	float		glowAmount		= 255;

	public enum FlowerType {
		Sakura, White, Yellow, Purple;
	}

	public static ArrayList<FlowerType>	usedTypes	= new ArrayList<FlowerType>();
	private static FlowerType			types[]		= FlowerType.values();

	public static FlowerType randomType(Random rnd) {

		if (usedTypes.size() == types.length) {
			usedTypes.removeAll(usedTypes);
		}

		while (true) {
			int idx = rnd.nextInt(types.length);
			FlowerType type = types[idx];

			if (usedTypes.contains(type)) {
				continue;
			}
			usedTypes.add(type);
			return type;
		}
	}

	Flower(FlowerType flowerType, PVector pos) {
		this.flowerType = flowerType;
		this.pos = pos.copy();
		this.pos.x += Util.random(-2, 2);
		this.pos.y += Util.random(-2, 2);
		groundY = PApplet.map(Util.randomf(0, 1), 0, 1, 30, 120);
		fallSpeed = Util.randomf(0.5f, 1);
	}

	void draw(PApplet parent, PGraphics pg, boolean forGlow) {

		if (!forGlow) {
			
			pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
			
			if (currentScale < nominalScale) {
				currentScale += 0.05f;
			}
			if (isFalling) {
				fallTick(parent);
			}
		}
		
		pg.noStroke();
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.scale(forGlow ? nominalScale : currentScale);

		switch (flowerType) {

		case Sakura:
			pg.fill(255, satBri, satBri, alpha);
			pg.ellipse(0, 0, weight, weight);

			pg.fill(255, alpha * 0.67f);
			pg.ellipse(0, 0, weight / 2, weight / 2);
			break;

		case White:
			pg.fill(255, alpha);
			pg.ellipse(0, 0, weight, weight);

			pg.fill(255, alpha * 0.67f);
			pg.ellipse(0, 0, weight / 2, weight / 2);
			break;

		case Yellow:
			pg.fill(207, 179, 55, alpha);
			pg.ellipse(0, 0, weight, weight);

			pg.fill(207, 179, 55, alpha * 0.67f);
			pg.ellipse(0, 0, weight / 2, weight / 2);

		case Purple:
			pg.fill(216, 100, 238, alpha);
			pg.ellipse(0, 0, weight, weight);

			pg.fill(216, 100, 238, alpha * 0.67f);
			pg.ellipse(0, 0, weight / 2, weight / 2);
			break;
		}
		pg.popMatrix();
		
		if (forGlow) {
			glowAmount *= 0.9f;
		}
	}

	private void fallTick(PApplet parent) {

		if (pos.y >= groundY) {

			if (alpha > 0) {
				alpha *= 0.97;
				satBri *= 0.95;
			}
			return;
		}

		if (alpha > 0) {
			alpha *= 0.97;
			satBri *= 1.05;
		}

		pos.y += fallSpeed;
		pos.x += (parent.noise(pos.y) - 0.5) * 7;
		fallSpeed += 0.008 * fallSpeed;
	}
}
