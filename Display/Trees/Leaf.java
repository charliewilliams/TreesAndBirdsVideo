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
		
		pg.translate(pos.x, pos.y);
		pg.fill(color, alpha);
		pg.ellipse(pos.x, pos.y, size, size);
	}
}
