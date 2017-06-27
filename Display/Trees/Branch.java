package Display.Trees;

import Util.*;
import processing.core.*;
import java.util.*;

public class Branch {

	boolean hasBird = false, finished = false;
	PVector origin, end;
	Tree tree; Leaf leaf; Flower flower;
	ArrayList<Branch> children = new ArrayList<Branch>();

	private float noiseOffset;
	private PApplet parent;
	private float diam, angle, length;

	static float piOver5  = (float)(Math.PI / 5.0);
	static float piOver10 = (float)(Math.PI / 10.0);
	static float piOver15 = (float)(Math.PI / 15.0);
	static float rootSize = 70;
	static float minSize = 0.6f;
	static int totalDepth = 150;

	Branch(PApplet parent, PVector origin, float length, float theta, float depth, float noiseOffset, boolean isEnd) {

		this.noiseOffset = noiseOffset + 0.01f;
		this.parent = parent;
		this.length = length;
		this.origin = origin.copy();
		angle = theta;

		float newX = (float) (origin.x + Math.cos(theta + Util.random(-piOver10, piOver10)));
		float newY = (float) (origin.y + Math.sin(theta + Util.random(-piOver10, piOver10)));
		end = new PVector(newX, newY);

		diam = PApplet.lerp(rootSize, 0.7f * rootSize, depth/totalDepth);  // gradually reduce the diameter
		diam *= PApplet.map(parent.noise(noiseOffset), 0, 1, 0.4f, 1.6f);  // multiply by noise to add variation

		if (isEnd) {
			// Leaf(PVector pos, int color, float alpha)
			leaf = new Leaf(end, 0, 100);
			finished = true;
		}
	}

	void grow() {

		if (finished) {
			return;
		}

		boolean drawleftBranch = Util.random(0, 1) > 0.1;
		boolean drawrightBranch = Util.random(0, 1) > 0.1;

		if (drawleftBranch) { 
			children.add(makeChild());
		}
		if (drawrightBranch) {
			children.add(makeChild());
		}

		if (!drawleftBranch && !drawrightBranch) {
			finished = true;
		}
	}

	Branch makeChild() {

		float newSize = Util.random(0.5f, 0.7f) * length;
		float newAngle = angle - Util.random(piOver15, piOver5);
		float newDepth = Util.random(0.6f, 0.8f) * totalDepth;
		
		return new Branch(parent, end, newSize, newAngle, newDepth, noiseOffset, length < 0.6);
	}

	boolean addFlower(Flower.Type flowerType) {
		return addFlower(flowerType, end);
	}

	boolean addFlower(Flower.Type flowerType, PVector pos) {

		if (leaf == null) {

			for (Branch child: children) {
				if (child.addFlower(flowerType)) {
					return true;
				}
			}
			return false;

		} else {

			if (flower == null) {
				flower = new Flower(flowerType, pos);
				return true;
			}
			return false;
		}
	}

	void draw(PGraphics pg) {

		pg.ellipse(origin.x, origin.y, diam, diam);

		if (leaf != null) {
			leaf.draw(pg, tree.leafSize);
		}

		if (flower != null) {
			flower.draw(pg, tree.flowerSize);
		}

		for (Branch child: children) {
			child.draw(pg);
		}

		if (isTip()) {
			
			pg.pushMatrix();
			pg.translate(end.x, end.y);
			pg.rotate(angle);
			pg.quad(0, -diam/2, 2*diam, -diam/6, 2*diam, diam/6, 0, diam/2);
			pg.popMatrix();
		}
	}

	boolean isTip() {
		return children.size() == 0;
	}

	boolean canHaveBird() {

		if (isTip()) {
			return false;
		}
		for (Branch child: children) {
			if (child.leaf != null) {
				return true;
			}
		}
		return false;
	}
}
