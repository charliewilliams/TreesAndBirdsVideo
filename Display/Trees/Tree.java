package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

public class Tree {
	
	int idx, z;
	float leafSize = 0, flowerSize = 0;
	PVector pos;
	Branch root;
	ArrayList<Branch> branches = new ArrayList<Branch>();

	Tree(PApplet parent, Note n, int idx, int z) {

		this.idx = idx; // Global idx for physics
		this.z = z; // z for alpha, gives fake depth
		
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

	void draw(PGraphics pg) {
		root.draw(pg);
	}
}
