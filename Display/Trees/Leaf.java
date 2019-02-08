package Display.Trees;
import processing.core.*;

public class Leaf {

	PVector pos;
	int color;
	float alpha;
	
	Leaf(PVector pos, int color, float alpha) {
	
		this.pos = pos;
		this.color = color;
		this.alpha = alpha;
	}

	void draw(PGraphics pg, float size) {
		
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.fill(color, alpha);
		pg.ellipse(0, 0, size, size);
		pg.popMatrix();
	}
	
	void fallTick() {
		
		// TODO leaves fall until they reach the floor
		// maybe doing a sine-based sway or something
	}
}
