package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

public class Tree {
	
	int idx;
	float alpha;
	float leafSize = 0, flowerSize = 0;
	PVector pos;
	Branch root;
	ArrayList<Branch> branches = new ArrayList<Branch>();

	Tree(PApplet parent, Note n, int idx, float alpha) {

		this.idx = idx; // Global idx for physics
		this.alpha = alpha; // gives fake depth
		
		// Branch(PApplet parent, PVector origin, float bSize, float theta, float depth, float noiseOffset, boolean isEnd)
		root = new Branch(parent, 100);
	}

	boolean grow() {
		return root.grow();
	}
	
	boolean addFlower(Flower.Type flowerType) {
		return root.addFlower(flowerType);
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
