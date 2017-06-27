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
	
	TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, PVector pos) {
		
		this.parent = parent;
		this.pos = pos;
		pg = parent.createGraphics(parent.width, parent.height);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		
		for (int i = 0; i < numChildren; i++) {
			
			// Tree(PApplet parent, Note n, int idx, int z, float noiseOffset)
			trees.add(new Tree(parent, n, baseIndex + i, i, Util.random(1000, 2000)));
		}
	}
	
	void grow(boolean leaves, boolean flowers) {
		
		for (Tree t: trees) {
			t.grow(false, false);
		}
	}
	
	void addFlower() {

		for (Tree t: trees) {
			if (t.addFlower(flowerType)) {
				break;
			}
		}
	}

	void draw() {

		pg.beginDraw();
		
		pg.background(0, 0, 100);
		
		pg.pushMatrix();
		pg.translate(pos.x, pos.y);
		
		// Background circle
		pg.fill(0, 0, 90);
		pg.ellipse(0, 0, 530, 530); 
		
		for (Tree t: trees) {
			t.draw(pg);
		}
		
		pg.popMatrix();
		
		pg.endDraw();
		
		parent.image(pg, 0, 0);
	}
}
