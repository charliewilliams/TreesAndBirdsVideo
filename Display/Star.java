package Display;

import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Star {

	PVector pos;
	float size;
	private float currentSize;
	
	Star(PVector pos, float size) {
		
		this.pos = pos;
		this.size = size;
		this.currentSize = 0.1f;
	}
	
	public void draw(PGraphics2D pg) {
		
		if (currentSize < size) {
			currentSize *= 2;
		}
		
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.fill(0, 0, 100);
		pg.ellipse(0, 0, currentSize, currentSize);
		pg.popMatrix();
	}
}
