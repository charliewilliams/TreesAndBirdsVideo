package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyPresets;
import org.gicentre.handy.HandyRenderer;

import Display.Birds.*;
import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class TreeStack {

	Flower.FlowerType		flowerType;
	Leaf.LeafShape			leafType;
	private ArrayList<Tree>	trees	= new ArrayList<Tree>();
	public PGraphics2D		pg_trees, pg_leaves, pg_glow;
	private PApplet			parent;
	private PVector			pos;
	private float			hue;
	private Note			n;
	private HandyRenderer	sketcher;

	TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, PVector pos) {

		this.parent = parent;
		this.pos = pos;
		this.n = n;
		flowerType = Flower.randomType();
		leafType = Leaf.randomType();
		hue = PApplet.map(n.pitch % 12.0f, 0, 12, 0, 360);
		
		pg_trees = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_trees.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_trees.smooth(4);

		pg_leaves = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_leaves.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_leaves.smooth(4);
		
		pg_glow = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_glow.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_glow.smooth(8);
		
		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 100, 20);
			trees.add(new Tree(parent, n, baseIndex + i, alpha, Util.randomf(5, 15), Util.randomf(5, 15)));
		}

		sketcher = HandyPresets.createWaterAndInk(parent); // new HandyRenderer(a);
		sketcher.setRoughness(Util.randomf(0, 1.5f));
		sketcher.setGraphics(pg_trees);
	}

	void grow(Note note) {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.grow(note).size() > 0) {
				return;
			}
		}
	}

	void addFlower() {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.addFlower(flowerType)) {
				return;
			}
		}
	}

	void addLeaf() {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.addLeaf(leafType, pg_trees)) {
				return;
			}
		}
	}

	public boolean dropLeaf(Note n) {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.dropLeaf()) {
				return true;
			}
		}
		return false;
	}

	void updateRender(int millis) {

		sketcher.setGraphics(pg_trees);
		sketcher.setSeed(0);

		PGraphics2D[] pgs = { pg_trees, pg_leaves, pg_glow };

		for (PGraphics2D p : pgs) {

			p.beginDraw();
			p.clear();
//			p.background(0, 0, 0, 0);
			p.pushMatrix();
			p.translate(pos.x, pos.y);
		}

		for (Tree t : trees) {
			t.jitter(millis);
			t.render(pg_trees, pg_leaves, pg_glow, sketcher, hue);
		}

		drawDebugLabel(pg_trees);

		for (PGraphics2D p : pgs) {

			p.popMatrix();
			p.updatePixels();
			p.endDraw();
		}
	}
	
	void drawBack() {
		parent.blendMode(PConstants.BLEND);
		parent.image(pg_trees, 0, 0);
	}
	
	void drawFront() {
		parent.blendMode(PConstants.NORMAL);
		parent.image(pg_leaves, 0, 0);
		parent.blendMode(PConstants.ADD);
		parent.image(pg_glow, 0, 0);
	}

	private void drawDebugLabel(PGraphics2D pg) {

		pg.fill(0);
		pg.textSize(24);
		pg.text(n.pitchClass, -20, 20);
	}

	public PVector acquireLandingSite(Bird b) {

		Collections.shuffle(trees);
		for (Tree t : trees) {

			Collections.shuffle(t.branches);
			for (Branch br : t.branches) {

				if (br.hasBird == false) {

					br.hasBird = true;
					PVector absolutePos = PVector.add(pos, br.end);

					// Someday I want to know enough trig to know why this fudge factor is needed:
					absolutePos.x += 100;
					absolutePos.y += 50;

					//					if (n.pitchClass == "C#") {
					//						PApplet.println("Pos:", pos, "branch origin:", br.origin, "end:", br.end, "angle:", br.angle, "length:", br.length, "draw dot at:", absolutePos);
					//					}
					//					absolutePos = PVector.add(absolutePos, br.origin);
					//					PApplet.println("Got spot", absolutePos, "for bird at", b.pos());
					return absolutePos;
				}
			}
		}

		PVector fallback = pos.copy();
		fallback.x += 100;
		fallback.y += 50;

		return fallback;
	}
}
