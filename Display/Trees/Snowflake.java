package Display.Trees;

import Util.Util;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Snowflake {

	PVector	pos;
	PShape	shape;
	float	alpha		= 100;
	float	fallSpeed	= Util.randomf(0.5f, 1);
	float	angle		= (float) Util.random(-Math.PI, Math.PI);

	Snowflake(PGraphics2D pg) {

		float padding = 5;
		pos = new PVector(Util.randomf(-padding * 100, pg.width + padding), -padding);

		int sides = (int) Util.random(5, 7);
		float twoPi = (float) (Math.PI * 2);
		float angle = twoPi / (float) sides;
		
		shape = pg.createShape();

		shape.beginShape();
		shape.fill(255, alpha);
		shape.noStroke();
		float size = Util.randomf(1, 4);

		for (float a = 0; a < twoPi; a += angle) {

			float sx = (float) (Math.cos(a) * size);
			float sy = (float) (Math.sin(a) * size);
			shape.vertex(sx, sy);
		}

		shape.endShape(PShape.CLOSE);
	}

	void draw(PApplet parent, PGraphics2D pg) {

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		pg.rotate(angle);
		pg.noStroke();
		pg.shape(shape);
		pg.popMatrix();
	}

	void tick(PApplet parent) {

		if (alpha > 0) {
			alpha *= 0.995;
			shape.setFill(Util.setAlpha(0xFFFFFF, alpha));
		}

		pos.y += fallSpeed;
		pos.x += (parent.noise(pos.y) - 0.5) * 7 + 1;
		angle += parent.noise(pos.y) / 20;
		fallSpeed *= 1.01;
	}
}
