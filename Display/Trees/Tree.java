package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

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
		root = new Branch(parent, 100, flowerSize, leafSize);
		branches.add(root);
	}

	ArrayList<Branch> grow(Note n) {
		
		ArrayList<Branch> newChildren = root.grow(n);
		branches.addAll(newChildren);
		return newChildren;
	}
	
	boolean addFlower(Flower.Type flowerType) {
		return root.addFlower(flowerType);
	}
	
	boolean addLeaf() {
		return root.addLeaf();
	}
	
	void growLeaves() {
		leafSize += 0.1;
	}
	
	void growFlowers() {
		flowerSize += 0.1;
	}

	void draw(PGraphics pg, float hue) {
		root.draw(pg, hue, alpha);
	}
	
	void jitter() {
		root.jitter();
	}
}
