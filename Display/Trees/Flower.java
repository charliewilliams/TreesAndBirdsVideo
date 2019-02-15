package Display.Trees;

import java.util.ArrayList;
import java.util.Random;

import Util.Util;
import processing.core.PGraphics;
import processing.core.PVector;

public class Flower {

	FlowerType	flowerType;
	PVector		pos;
	float		satBri	= Util.randomf(155, 255);
	float		alpha	= Util.randomf(32, 192);
	float		weight	= Util.randomf(4, 10);

	public enum FlowerType {
		Sakura, White, Yellow, Purple;
	}

	public static ArrayList<FlowerType>	usedTypes	= new ArrayList<FlowerType>();
	private static Random				rnd			= new Random();
	private static FlowerType			types[]		= FlowerType.values();

	public static FlowerType randomType() {
		
		if (usedTypes.size() == types.length) {
			usedTypes.removeAll(usedTypes);
		}

		while (true) {
			int idx = rnd.nextInt(types.length);
			FlowerType type = types[idx];
			
			if (usedTypes.contains(types)) {
				continue;
			}
			usedTypes.add(type);
			return type;
		}
	}

	Flower(FlowerType flowerType, PVector pos) {
		this.flowerType = flowerType;
		this.pos = pos;
	}

	void draw(PGraphics pg, float size) {

		pg.noStroke();

		switch (flowerType) {

		case Sakura:
			pg.fill(255, satBri, satBri, alpha);
			pg.ellipse(pos.x, pos.y, weight, weight);

			pg.fill(255, alpha * 0.67f);
			pg.ellipse(pos.x, pos.y, weight / 2, weight / 2);
			break;

		case White:
			pg.fill(255, alpha);
			pg.ellipse(pos.x, pos.y, weight, weight);

			pg.fill(255, alpha * 0.67f);
			pg.ellipse(pos.x, pos.y, weight / 2, weight / 2);
			break;

		case Yellow:
			pg.fill(207, 179, 55, alpha);
			pg.ellipse(pos.x, pos.y, weight, weight);

			pg.fill(207, 179, 55, alpha * 0.67f);
			pg.ellipse(pos.x, pos.y, weight / 2, weight / 2);

		case Purple:
			pg.fill(216, 100, 238, alpha);
			pg.ellipse(pos.x, pos.y, weight, weight);

			pg.fill(216, 100, 238, alpha * 0.67f);
			pg.ellipse(pos.x, pos.y, weight / 2, weight / 2);
			break;
		}

	}
}
