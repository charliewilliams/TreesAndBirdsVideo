package Display.Trees;
import processing.core.*;

import java.util.Random;

import Util.*;

public class Flower {

	FlowerType flowerType;
	PVector pos;
	PShape shape;

	public enum FlowerType {
		Sakura, White;
	}
	
	public static FlowerType randomType() {
		
		final Random rnd = new Random(); 
		FlowerType types[] = FlowerType.values();
		return types[rnd.nextInt(types.length)];
	}

	Flower(FlowerType flowerType, PVector pos) {
		this.flowerType = flowerType;
		this.pos = pos;
	}

	void draw(PGraphics pg, float size) {

//		switch (flowerType) {
//		
//		case Sakura:
//			
//			float w = Util.randomf(155, 255);
//			pg.stroke(255, w, w, Util.randomf(32, 192));
//			pg.strokeWeight(Util.randomf(0, 8));
//			pg.point(pos.x + Util.randomf(-2, 2), pos.y + Util.randomf(-2, 2));
//			break;
//			
//		case White:
//			
//			pg.fill(255);
//			pg.ellipse(pos.x, pos.y, 5, 5);
//		default:
//			break;
//		}

	}
}
