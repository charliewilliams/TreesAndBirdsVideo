package Display.Trees;
import java.util.*;
import Util.*;
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

		for (int i = 0; i < numChildren; i++) {

			// Tree(PApplet parent, Note n, int idx, int z, float noiseOffset)
			trees.add(new Tree(parent, n, baseIndex + i, i, Util.random(1000, 2000)));
		}
	}

	void grow(boolean leaves, boolean flowers) {

		needsRedraw = true;

		for (Tree t: trees) {
			t.grow(false, false);
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

	void draw() {

		if (needsRedraw) {

			pg.beginDraw();

			pg.background(0, 0, 0, 0);

			pg.pushMatrix();
			pg.translate(pos.x, pos.y);

			// Background circle
			pg.fill(0, 100, 90, 10);
			pg.ellipse(0, 0, 200, 200); 

			for (Tree t: trees) {
				t.draw(pg);
			}

			pg.popMatrix();

			pg.endDraw();

			needsRedraw = false;
		}

		parent.blendMode(PConstants.BLEND);
		parent.image(pg, 0, 0);
	}
}
