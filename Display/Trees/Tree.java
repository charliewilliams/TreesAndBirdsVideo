package Display.Trees;

import java.util.ArrayList;
import java.util.Random;

import org.gicentre.handy.HandyRenderer;

import Model.Note;
import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics2D;

public class Tree {

	float				alpha;
	float				leafSize	= 10, flowerSize = 10;
	Branch				root;
	ArrayList<Branch>	branches	= new ArrayList<Branch>();
	ArrayList<Leaf>		allLeaves	= new ArrayList<Leaf>();
	ArrayList<Flower>	allFlowers	= new ArrayList<Flower>();
	PApplet				parent;
	long				seed, seedStride;
	Random				rand		= new Random();

	Tree(PApplet parent, long seed, long seedStride, Note n, float alpha, float flowerSize, float leafSize) {

		this.parent = parent;
		this.seed = seed;
		this.seedStride = seedStride;
		this.alpha = alpha; // gives fake depth
		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
		rand.setSeed(seed);
	}

	ArrayList<Branch> grow(Note n, boolean propagateGlow) {

		if (root == null) {
			root = new Branch(parent, seed + seedStride, seedStride, 80, alpha);
			branches.add(root);
			return new ArrayList<Branch>();
		}

		ArrayList<Branch> newChildren = root.grow(n, propagateGlow);
		branches.addAll(newChildren);
		return newChildren;
	}

	boolean addFlower(Flower.FlowerType flowerType) {

		if (root == null) {
			return false;
		}
		Flower f = root.addFlower(flowerType);
		if (f == null) {
			return false;
		}
		allFlowers.add(f);
		return true;
	}

	public boolean addLeaf(Leaf.LeafShape leafType, PGraphics pg, PGraphics pg_glow) {

		if (root == null) {
			return false;
		}
		Leaf l = root.addLeaf(leafType, pg, pg_glow);
		if (l == null) {
			return false;
		}
		allLeaves.add(l);
		return true;
	}

	public boolean dropLeaf() {

		if (root == null) {
			return false;
		}
		return root.dropLeaf();
	}

	public boolean dropFlower() {

		for (Flower f : allFlowers) {
			f.isFalling = true;
		}
		return true;
	}

	public void turnLeafColorTick(int millis) {

//		Section s = Section.forMillis(millis);
		ArrayList<Leaf> leavesToTurn;

		// For the first x/y of bigReturn, turn only half the leaves
//		if (s == Section.bigReturn && millis < s.startTime() + s.length() / 3) {
//
//			leavesToTurn = new ArrayList<Leaf>((ArrayList<Leaf>) allLeaves.clone());
//			Collections.shuffle(leavesToTurn);
//			leavesToTurn = new ArrayList<Leaf>(leavesToTurn.subList(0, allLeaves.size() / 2));
//			
//		} else {
			leavesToTurn = allLeaves;
//		}

		for (Leaf l : leavesToTurn) {
			l.turnColorTick();
		}
	}

	public void dropAllLeaves() {

		for (Leaf l : allLeaves) {
			l.hue = l.fallHue;
			l.isFalling = true;
		}
	}

	public void dropAllFlowers() {

		for (Flower f : allFlowers) {
			f.isFalling = true;
		}
	}

	void renderTrees(PGraphicsJava2D pg_trees, HandyRenderer sketcher) {

		if (root == null) {
			return;
		}
		root.renderBranch(pg_trees, sketcher);
	}

	void renderLeaves(PGraphics pg_leaves) {

		if (root == null) {
			return;
		}
		root.renderLeaves(pg_leaves);
	}

	void renderGlow(PGraphics2D pg_glow) {

		if (root == null) {
			return;
		}
		root.renderGlow(pg_glow);
	}

	public void jitter(int millis) {

		if (root == null) {
			return;
		}
		root.jitter(millis);
	}

	public float trunkWeight() {

		if (root == null) {
			return 0;
		}
		return root.strokeWeight();
	}
}
