package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyPresets;
import org.gicentre.handy.HandyRenderer;

import Display.Glow;
import Display.Birds.Bird;
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
	private boolean			renderGlow;
	private PVector			pos;
	//	private float			hue;
	private Note			n;
	private HandyRenderer	sketcher;
	private long			seed		= (long) Util.random(0, 10000000);
	private long			seedStride	= (long) Util.random(0, 10000000);

	TreeStack(int numChildren, PApplet parent, Note n, PVector pos, boolean renderGlow, long seed, long seedStride) {

		this.parent = parent;
		this.renderGlow = renderGlow;
		this.pos = pos;
		this.n = n;
		
		if (seed > 0) {
			this.seed = seed;
		}
		if (seedStride > 0) {
			this.seedStride = seedStride;
		}
		flowerType = Flower.randomType();
		leafType = Leaf.randomType();
		//		hue = PApplet.map(n.pitch % 12.0f, 0, 12, 0, 360);

		pg_trees = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
//		pg_trees.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_trees.smooth(4);

		pg_leaves = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_leaves.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_leaves.smooth(8);

		pg_glow = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		//		pg_glow.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_glow.smooth(8);

		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 255, 50);
			trees.add(new Tree(parent, seed, seedStride, n, alpha, Util.randomf(5, 15), Util.randomf(5, 15)));
		}

		sketcher = HandyPresets.createWaterAndInk(parent); // new HandyRenderer(a);
		sketcher.setRoughness(Util.randomf(0, 1.5f));
		sketcher.setGraphics(pg_trees);
		
		PApplet.println(n.pitchClass + ": " + this.seed + "+" + this.seedStride);
	}

	void grow(Note note) {

		int iterations = note.isRare() ? 10 : 1;
		for (int i = 0; i < iterations; i++) {
			ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
			Collections.shuffle(ts);
			for (Tree t : ts) {

				if (t.grow(note).size() > 0) {
					return;
				}
			}
		}
	}

	void addFlower() {

		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.addFlower(flowerType)) {
				return;
			}
		}
	}

	void addLeaf() {

		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.addLeaf(leafType, pg_leaves)) {
				return;
			}
		}
	}

	public boolean dropLeaf() {

		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.dropLeaf()) {
				return true;
			}
		}
		return false;
	}

	public boolean dropFlower() {

		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.dropFlower()) {
				return true;
			}
		}
		return false;
	}
	
	public void turnLeafColorTick(int millis) {
		
		for (Tree t : trees) {
			t.turnLeafColorTick(millis);
		}
	}

	private void preparePGraphics(PGraphics2D p) {

		p.beginDraw();
		p.clear();
		p.pushMatrix();
		p.translate(pos.x, pos.y);

	}

	private void finalizePGraphics(PGraphics2D p) {

		p.popMatrix();
		p.endDraw();
	}

	void updateRender(int millis) {

		sketcher.setSeed(0);

		preparePGraphics(pg_trees);
		{
			for (Tree t : trees) {
				t.jitter(millis);
				t.renderTrees(pg_trees, sketcher);
			}
		}
		drawDebugLabel(pg_trees);
		finalizePGraphics(pg_trees);

		preparePGraphics(pg_leaves);
		{
			for (Tree t : trees) {
				t.renderLeaves(pg_leaves);
			}
		}
		finalizePGraphics(pg_leaves);

		if (!renderGlow) {
			return;
		}
		preparePGraphics(pg_glow);
		{
			for (Tree t : trees) {
				t.renderGlow(pg_glow);
			}
		}
		finalizePGraphics(pg_glow);
	}

	void drawBack() {
		parent.blendMode(PConstants.BLEND);
		parent.image(pg_trees, 0, 0);

		//				pg_trees.save("tmp/pg-trees-" + n.pitchClass + "-" + parent.frameCount + ".png");
	}

	void drawLeaves() {
		parent.blendMode(PConstants.BLEND);
		parent.image(pg_leaves, 0, 0);

		//				pg_leaves.save("tmp/pg-leaves-" + n.pitchClass + "-" + parent.frameCount + ".png");			
	}

	void drawGlow() {

		if (!renderGlow) {
			return;
		}

		Glow.render(pg_glow);

		//		pg_glow.tint(12);

		parent.blendMode(PConstants.ADD);
		parent.image(pg_glow, 0, 0);

		//		pg_glow.save("tmp/pg-glow-" + n.pitchClass + "-" + parent.frameCount + ".png");
	}

	void glowRoot() {
		for (Tree t : trees) {
			t.root.glow();
		}
	}

	void drawDebugLabel(PGraphics2D pg) {

		pg.fill(0);
		pg.textSize(24);
		pg.text(n.pitchClass, -20, 20);
		pg.textSize(10);
		pg.text(seed + "+" + seedStride, -20, 40);
	}

	public PVector acquireLandingSite(Bird b) {

		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {

			ArrayList<Branch> bs = (ArrayList<Branch>) t.branches.clone();
			Collections.shuffle(bs);
			for (Branch br : bs) {

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

		float xNoise = 80;
		float yNoise = 50;

		return fallback.add(new PVector(Util.randomf(-xNoise, xNoise), Util.randomf(0, yNoise)));
	}
}
