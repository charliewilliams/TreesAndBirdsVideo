package Display.Trees;

import Display.Birds.Bird;
import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class TreeManager {

	// 5643821+9421841
	// to fill in as we find a great seed for each tree
	private static int[]	seeds	= { 25,     6031596, 7661790, 7468500, 7452885, 887068,  7897006, 887068,  7661790, 887068,  887068,  7661790 };
	private static int[]	strides	= { 102938, 8667501, 7109302, 7778934, 7055614, 3228859, 2543517, 3228859, 7109302, 3228859, 3228859, 7109302 };

	private static Leaf.LeafShape[] leafTypes = { Leaf.LeafShape.star, Leaf.LeafShape.polygon, Leaf.LeafShape.ellipse,
			Leaf.LeafShape.crescent, Leaf.LeafShape.polygon, Leaf.LeafShape.crescent, Leaf.LeafShape.star,
			Leaf.LeafShape.star, Leaf.LeafShape.crescent, Leaf.LeafShape.star, Leaf.LeafShape.ellipse,
			Leaf.LeafShape.star };

	private static TreeManager m;

	public static TreeManager instance() {
		return m;
	}

	private PApplet	parent;
	private PFont	labelFont;
	public boolean	renderGlow;

	private TreeStack[] pitchClassTrees = new TreeStack[12];

	public TreeStack treeStackFor(Note n) {

		int i = treeIndexForNote(n);
		TreeStack pitchClassTreeStack = pitchClassTrees[i];

		if (pitchClassTreeStack == null) {

			int numChildren = 2; //(int) Util.random(3, 8);
			pitchClassTrees[i] = new TreeStack(numChildren, parent, labelFont, n, treePositionForNote(n), leafTypes[i],
					renderGlow, seeds[i], strides[i]);
		}

		return pitchClassTrees[i];
	}

	public int treeIndexForNote(Note n) {
		//		// Offset so that E is the lowest pitch
		//		int offset = 8;
		// Offset so that A is the lowest pitch
		int offset = 3;
		return (n.pitch + offset) % 12;
	}

	public PVector treePositionForNote(Note n) {

		int i = treeIndexForNote(n);
		float eachTreeSpace = parent.width / (pitchClassTrees.length + 2);
		float yOffset = n.isBlackKey() ? 55 : 0;
		return new PVector(eachTreeSpace * (i + 2), parent.height * 0.8f + Util.randomf(-10, 10) - yOffset);
	}

	public TreeManager(PApplet parent, PFont labelFont) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
		this.labelFont = labelFont;
	}

	public void addNote(Note n, int millis) {
		addNote(n, millis, true);
	}

	public void addNote(Note n, int millis, boolean shouldGlow) {

		treeStackFor(n).grow(n, millis, shouldGlow);
	}

	public void addLeafOrFlower(Note n, boolean shouldBeLeaf) {

		if (shouldBeLeaf) {
			treeStackFor(n).addLeaf();
			treeStackFor(n).addLeaf();
		} else {
			treeStackFor(n).addFlower();
			treeStackFor(n).addFlower();
		}
	}

	public void dropLeaf(Note n) {

		int tries = 0;
		int maxTries = 50;

		while (true) {
			if (treeStackFor(n).dropLeaf()) {
				return;
			}
			n = new Note((int) Util.random(0, 12));
			if (tries++ > maxTries) {
				return;
			}
		}
	}

	public void dropFlower(Note n) {

		int tries = 0;
		int maxTries = 50;

		while (true) {
			if (treeStackFor(n).dropFlower()) {
				return;
			}
			n = new Note((int) Util.random(0, 12));
			if (tries++ > maxTries) {
				return;
			}
		}
	}

	public void turnLeafColorTick(int millis) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.turnLeafColorTick(millis);
			}
		}
	}

	public void dropAllLeaves(int millis) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.dropAllLeaves();
			}
		}
	}

	public void dropAllFlowers() {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.dropAllFlowers();
			}
		}
	}

	public void glowRoot(Note n) {
		treeStackFor(n).glowRoot();
	}

	public void updateRender(int millis) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.updateRender(millis);
			}
		}
	}

	public void drawTrees(int millis) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.drawBack();
				stack.drawDebugLabel(millis);
			}
		}
	}

	public void drawOverlay(int frameNumber) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.drawLeaves();
				stack.drawGlow(frameNumber);
			}
		}
	}

	public PVector acquireLandingSite(Bird b, Note n) {
		return treeStackFor(n).acquireLandingSite(b);
	}

	public void buildDebugLeaves() {

		for (int i = 0; i < 128; i++) {
			Note n = new Note(i % 12);
			addLeafOrFlower(n, true);
		}

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				for (Tree t : stack.trees) {
					for (Leaf l : t.allLeaves) {
						l.currentScale = 1;
					}
				}
			}
		}
	}
}
