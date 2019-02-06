package Display.Trees;
import processing.core.*;
import Util.*;

public class Flower {

	Type type;
	PVector pos;

	public enum Type {
		Sakura, White;
	}

	Flower(Type flowerType, PVector pos) {
		type = flowerType;
		this.pos = pos;
	}

	void draw(PGraphics pg, float size) {

		switch (type) {
		
		case Sakura:
			
			float w = Util.randomf(155, 255);
			pg.stroke(255, w, w, Util.randomf(32, 192));
			pg.strokeWeight(Util.randomf(0, 8));
			pg.point(pos.x + Util.randomf(-2, 2), pos.y + Util.randomf(-2, 2));
			break;
			
		case White:
		default:
			break;
		}

	}
}
