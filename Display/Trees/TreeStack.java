package Display.Trees;
import java.util.*;
import Model.Note;
import processing.core.*;

public class TreeStack {

	Flower.Type flowerType;

	private ArrayList<Tree> trees = new ArrayList<Tree>();
	private PGraphics pg;
	private PApplet parent;
	private PVector pos;
	private float hue, circleAlpha = 0, circleDiam = 0;
	private float maxAlpha = 100, maxDiam = 200;

	TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, PVector pos) {

		this.parent = parent;
		this.pos = pos;
		hue = PApplet.map(n.pitch % 12.0f, 0, 12, 0, 100);
		pg = parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg.pixelDensity = 2;
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.noStroke();

		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 100, 20);
			trees.add(new Tree(parent, n, baseIndex + i, alpha));
		}
	}

	static int calls = 0;

	void grow(Note note) {

		circleAlpha = PApplet.map(note.velocity, 0, 0.5f, maxAlpha/2, maxAlpha);
		circleDiam = PApplet.map(note.velocity, 0, 0.5f, maxDiam * 0.67f, maxDiam);

		Collections.shuffle(trees);
		for (Tree t: trees) {
			if (t.grow()) {
				break;
			}
		}
	}

	void addFlower() {

		for (Tree t: trees) {
			if (t.addFlower(flowerType)) {
				break;
			}
		}
	}

	void jitter() {

		for (Tree t: trees) {
			t.jitter();
		}
	}

	void draw() {

		pg.beginDraw();

		pg.background(0, 0, 0, 0);

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);

		// Background circle
//		if (circleAlpha > 0) {
//
//			pg.stroke(hue, 100, 30, circleAlpha * 0.8f);
//			pg.strokeWeight(0.5f);
//			pg.fill(hue, 100, 90, circleAlpha * 0.2f);
//			pg.ellipse(0, -100, circleDiam, circleDiam); 
//
//			circleAlpha *= 0.98;
//			circleDiam *= 0.999;
//		}

		for (Tree t: trees) {
			t.draw(pg, hue);
		}

		pg.popMatrix();

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
		parent.image(pg, 0, 0);
	}
}
