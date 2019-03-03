package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.gicentre.handy.HandyPresets;
import org.gicentre.handy.HandyRenderer;

import Display.Glow;
import Display.Birds.Bird;
import Model.Note;
import Util.Util;
import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class TreeStack {

	Flower.FlowerType		flowerType;
	Leaf.LeafShape			leafShape;
	ArrayList<Tree>			trees						= new ArrayList<Tree>();
	public PGraphics2D		pg_glow;
	public PGraphicsJava2D	pg_trees, pg_leaves, pg_labels;
	private PApplet			parent;
	private boolean			renderGlow;
	private PVector			pos;
	private Note			note;
	private HandyRenderer	sketcher;
	private long			seed						= (long) Util.random(0, 10000000);
	private long			seedStride					= (long) Util.random(0, 10000000);
	private float			debugLabelAlpha				= 0;
	private float			debugLabelDurationMillis	= 0;
	private PFont			labelFont;
	private float			labelXOffset				= 0;
	private float			labelXOffsetAmount			= 15;
	private float[]			labelXOffsetDirections		= { 1, 1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1 };
	private Random			rand						= new Random(0);

	TreeStack(int numChildren, PApplet parent, PFont font, Note n, PVector pos, Leaf.LeafShape leafShape,
			boolean renderGlow, long seed, long seedStride) {

		this.parent = parent;
		this.renderGlow = renderGlow;
		this.pos = pos;
		this.note = n;
		this.labelFont = font;

		if (seed > 0) {
			this.seed = seed;
		}
		if (seedStride > 0) {
			this.seedStride = seedStride;
		}
		rand.setSeed(seed);

		flowerType = Flower.randomType(rand);
		this.leafShape = leafShape;
		//		hue = PApplet.map(n.pitch % 12.0f, 0, 12, 0, 360);

		pg_trees = (PGraphicsJava2D) parent.createGraphics(parent.width, parent.height, PConstants.JAVA2D);
		pg_trees.smooth(2);

		pg_leaves = (PGraphicsJava2D) parent.createGraphics(parent.width, parent.height, PConstants.JAVA2D);
		pg_leaves.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg_leaves.smooth(8);

		pg_glow = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		pg_glow.smooth(8);

		pg_labels = (PGraphicsJava2D) parent.createGraphics(parent.width, parent.height, PConstants.JAVA2D);
		pg_labels.smooth(2);

		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 255, 50);
			trees.add(new Tree(parent, seed + seedStride * i, seedStride, n, alpha, Util.randomf(5, 15, rand),
					Util.randomf(5, 15, rand)));
		}

		sketcher = HandyPresets.createWaterAndInk(parent); // new HandyRenderer(a);
		sketcher.setRoughness(Util.randomf(0, 1.5f, rand));
		sketcher.setGraphics(pg_trees);

		labelXOffset = labelXOffsetDirections[n.pitch % 12] * labelXOffsetAmount;

		PApplet.println(n.pitchClass + ": " + this.seed + "+" + this.seedStride);
	}

	void grow(Note note, int millis, boolean propagateGlow) {

		debugLabelAlpha = 255;
		debugLabelDurationMillis = (note.velocity + note.duration) * 1000;
		debugLabelTriggerMillis = millis;

		int iterations = note.isRare() ? 10 : 1;
		for (int i = 0; i < iterations; i++) {
			ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
			Collections.shuffle(ts);
			for (Tree t : ts) {

				if (t.grow(note, propagateGlow).size() > 0) {
					return;
				}
			}
		}
	}

	void addFlower() {

		debugLabelAlpha = 255;
		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.addFlower(flowerType)) {
				return;
			}
		}
	}

	void addLeaf() {

		debugLabelAlpha = 255;
		ArrayList<Tree> ts = (ArrayList<Tree>) trees.clone();
		Collections.shuffle(ts);
		for (Tree t : ts) {
			if (t.addLeaf(leafShape, pg_leaves, pg_glow)) {
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

	public void dropAllLeaves() {

		for (Tree t : trees) {
			t.dropAllLeaves();
		}
	}

	public void dropAllFlowers() {

		for (Tree t : trees) {
			t.dropAllFlowers();
		}
	}

	private void preparePGraphics(PGraphics p) {

		p.beginDraw();
		p.clear();
		p.pushMatrix();
		p.translate(pos.x, pos.y);

	}

	private void finalizePGraphics(PGraphics p) {

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

	void drawGlow(PGraphics2D onto) {

		if (!renderGlow) {
			return;
		}

		Glow.render(pg_glow);

		onto.beginDraw();
		onto.blendMode(PConstants.ADD);
		onto.image(pg_glow, 0, 0);
		onto.endDraw();
	}

	void glowRoot() {
		for (Tree t : trees) {
			if (t.root != null) {
				t.root.glow();
			}
		}
	}

	private int debugLabelTriggerMillis;

	void drawDebugLabel(int millis) {

		if (debugLabelAlpha < 1) {
			debugLabelDurationMillis = 0;
			debugLabelTriggerMillis = 0;
			return;
		}

		pg_labels.beginDraw();
		pg_labels.clear();
		pg_labels.fill(75, debugLabelAlpha);
		pg_labels.textAlign(PConstants.CENTER);
		pg_labels.textFont(labelFont);

		float weight = 1;
		if (trees.size() > 0) {
			weight = trees.get(0).trunkWeight() / 2;

			if (labelXOffset < 0) {
				weight *= -1;
			}
		}
		pg_labels.text(note.pitchClass, pos.x + labelXOffset + weight, pos.y);
		pg_labels.endDraw();

		float millisRemaining = (debugLabelTriggerMillis + debugLabelDurationMillis) - millis;
		if (millisRemaining >= 0 && debugLabelDurationMillis > 0) {
			debugLabelAlpha = 255 * millisRemaining / debugLabelDurationMillis;
		} else {
			debugLabelAlpha *= 0.75;
		}

		parent.blendMode(PConstants.BLEND);
		parent.image(pg_labels, 0, 0);
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

		return fallback.add(new PVector(Util.randomf(-xNoise, xNoise, rand), Util.randomf(0, yNoise, rand)));
	}
}
