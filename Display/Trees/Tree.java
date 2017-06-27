package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

public class Tree {
	
	int idx, z;
	float noiseOffset;
	float leafSize = 0, flowerSize = 0;
	PVector pos;
	static float yVariance = 100;
	Branch root;
	ArrayList<Branch> branches = new ArrayList<Branch>();

	Tree(PApplet parent, Note n, int idx, int z, float noiseOffset) {

		this.idx = idx;
		this.z = z;
		this.noiseOffset = noiseOffset;
		
		float theta = (float) (Math.PI / 2.0f);
		
		// Branch(PApplet parent, PVector origin, float bSize, float theta, float depth, float noiseOffset, boolean isEnd)
		root = new Branch(parent, new PVector(), 70.0f, theta, 150.0f, noiseOffset, false);
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
