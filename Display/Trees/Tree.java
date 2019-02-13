package Display.Trees;
import java.util.ArrayList;

import org.gicentre.handy.HandyRenderer;

import Model.Note;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphics2D;

public class Tree {
	
	int idx;
	float alpha;
	float leafSize = 10, flowerSize = 10;
	Branch root;
	ArrayList<Branch> branches = new ArrayList<Branch>();

	Tree(PApplet parent, Note n, int idx, float alpha, float flowerSize, float leafSize) {

		this.idx = idx; // Global idx for physics
		this.alpha = alpha; // gives fake depth
		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
		
		// Branch(PApplet parent, PVector origin, float bSize, float theta, float depth, float noiseOffset, boolean isEnd)
		root = new Branch(parent, 80, flowerSize, leafSize);
		branches.add(root);
	}

	ArrayList<Branch> grow(Note n) {
		
		ArrayList<Branch> newChildren = root.grow(n);
		branches.addAll(newChildren);
		return newChildren;
	}
	
	boolean addFlower(Flower.FlowerType flowerType) {
		return root.addFlower(flowerType);
	}
	
	public boolean addLeaf(Leaf.LeafShape leafType, PGraphics pg) {
		return root.addLeaf(leafType, pg);
	}
	
	public boolean dropLeaf() {
		return root.dropLeaf();
	}
	
	void growLeaves() {
		leafSize += 0.1;
	}
	
	void growFlowers() {
		flowerSize += 0.1;
	}

	void draw(PGraphics pg, PGraphics pg_front, PGraphics2D pg_glow, HandyRenderer sketcher, float hue) {
		root.draw(pg, pg_front, pg_glow, sketcher, hue, alpha);
	}
	
	public void jitter(int millis) {
		root.jitter(millis);
	}
}
