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
	boolean needsRedraw = false;

	TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, PVector pos) {

		this.parent = parent;
		this.pos = pos;
		pg = parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.noStroke();

		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 100, 20);
			trees.add(new Tree(parent, n, baseIndex + i, alpha));
		}
	}

	static int calls = 0;

	void grow() {

		needsRedraw = true;

		Collections.shuffle(trees);
		for (Tree t: trees) {
			if (t.grow()) {
				break;
			}
		}
	}

	void addFlower() {

		needsRedraw = true;

		for (Tree t: trees) {
			if (t.addFlower(flowerType)) {
				break;
			}
		}
	}
	
	void jitter() {
		
		needsRedraw = true;
		
		for (Tree t: trees) {
			t.jitter();
		}
	}

	void draw() {

		if (needsRedraw) {

			pg.beginDraw();

			pg.background(0, 0, 0, 0);

			pg.pushMatrix();
			pg.translate(pos.x, pos.y);

			// Background circle
//			pg.noStroke();
//			pg.fill(0, 100, 90, 20);
//			pg.ellipse(0, -100, 200, 200); 


			pg.popMatrix();
		for (Tree t: trees) {
			t.draw(pg, hue);
		}

			pg.endDraw();

			needsRedraw = false;
		}

		parent.blendMode(PConstants.BLEND);
		parent.image(pg, 0, 0);
	}
}
