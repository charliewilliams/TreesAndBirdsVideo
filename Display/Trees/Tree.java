package Display.Trees;
import Model.Note;
import Util.Util;
import processing.core.*;
import java.util.*;

public class Tree {
	
	int idx, z;
	float noiseOffset;
	float leafSize = 0, flowerSize = 0;
	PVector pos;
	Branch root;
	ArrayList<Branch> branches = new ArrayList<Branch>();

	Tree(PApplet parent, Note n, int idx, int z) {

		this.idx = idx;
		this.z = z;
		noiseOffset = Util.random(1000, 2000);
		
		// Branch(PApplet parent, PVector origin, float bSize, float theta, float depth, float noiseOffset, boolean isEnd)
		PVector origin = new PVector();
		PVector end = new PVector(0, -100);
		root = new Branch(parent, origin, end);
	}

	void grow(boolean leaves, boolean flowers) {
		
		if (leaves) {
			leafSize += 0.1;
		} else if (flowers) {
			flowerSize += 0.1;
		} else {
			root.grow();	
		}
	}
	
	boolean addFlower(Flower.Type flowerType) {
		return root.addFlower(flowerType);
	}

	void draw(PGraphics pg) {
		root.draw(pg);
	}
}
