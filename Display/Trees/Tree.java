package Display.Trees;

import java.util.ArrayList;

import org.gicentre.handy.HandyRenderer;

import Model.Note;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics2D;

public class Tree {

	float				alpha;
	float				leafSize	= 10, flowerSize = 10;
	Branch				root;
	ArrayList<Branch>	branches	= new ArrayList<Branch>();
	PApplet				parent;
	long				seed, seedStride;

	Tree(PApplet parent, long seed, long seedStride, Note n, float alpha, float flowerSize, float leafSize) {

		this.parent = parent;
		this.seed = seed;
		this.seedStride = seedStride;
		this.alpha = alpha; // gives fake depth
		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
	}

	ArrayList<Branch> grow(Note n) {

		if (root == null) {
			root = new Branch(parent, seed, seedStride, 80, flowerSize, leafSize, alpha);
			branches.add(root);
			return new ArrayList<Branch>();
		}

		ArrayList<Branch> newChildren = root.grow(n);
		branches.addAll(newChildren);
		return newChildren;
	}

	boolean addFlower(Flower.FlowerType flowerType) {

		if (root == null) {
			return false;
		}
		return root.addFlower(flowerType);
	}

	public boolean addLeaf(Leaf.LeafShape leafType, PGraphics pg) {

		if (root == null) {
			return false;
		}
		return root.addLeaf(leafType, pg);
	}

	public boolean dropLeaf() {

		if (root == null) {
			return false;
		}
		return root.dropLeaf();
	}

	public boolean dropFlower() {

		if (root == null) {
			return false;
		}
		return root.dropFlower();
	}

	public void turnLeafColorTick(int millis) {

		if (root == null) {
			return;
		}
		root.turnLeafColorTick(millis);
	}

	void growLeaves() {
		leafSize += 0.1;
	}

	void growFlowers() {
		flowerSize += 0.1;
	}

	void renderTrees(PGraphics2D pg_trees, HandyRenderer sketcher) {

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
}
