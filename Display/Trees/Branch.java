package Display.Trees;

import Util.*;
import processing.core.*;
import java.util.*;

public class Branch {

	boolean hasBird = false, finished = false;
	PVector end;
	Tree tree; Leaf leaf; Flower flower;
	ArrayList<Branch> children = new ArrayList<Branch>();
	ArrayList<BranchBump> displayBumps = new ArrayList<BranchBump>();

	private PApplet parent;
	private float diam, angle, length;

	static private float piOver5  = (float)(Math.PI / 5.0);
	static private float piOver10 = (float)(Math.PI / 10.0);
	static private float piOver15 = (float)(Math.PI / 15.0);

	// Root
	Branch(PApplet parent, float length) { 

		this.parent = parent;
		this.length = length;
		angle = angle();
		end = new PVector(0, -length);
	}

	Branch(PApplet parent, PVector origin, float length) { 

		this.parent = parent;
		this.length = length;
		angle = angle();
		end = origin.copy().normalize().rotate(angle).mult(length);
	}
	
	float angle() {
		float angle = Util.random(piOver15, piOver5);
		if (Util.random(-1, 1) < 0) {
			angle *= -1;
		}
		return angle;
	}

	void grow() {

		if (finished) {
			for (Branch b: children) {
				b.grow();
			}
			return;
		}

		finished = true;

		// 0-3 children

		// 90% chance of first child
		if (Util.random(0, 1) > 0.1) {
			children.add(makeChild());
		}
		// 60% chance of 2nd child
		if (Util.random(0, 1) > 0.4) {
			children.add(makeChild());
		}
		// 10% chance of 3rd child
		if (Util.random(0, 1) > 0.9) {
			children.add(makeChild());
		}
	}

	Branch makeChild() {
		float newLength = length * Util.random(0.5f, 0.7f);
		return new Branch(parent, end, newLength);
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

		// Draw the basic line for our branch (debug)
		pg.stroke(0, 0, 0, 100);
		pg.line(0, 0, end.x, end.y);
		pg.ellipse(end.x, end.y, 4, 4);

		// Draw the nice texture-y bumps over it
		for (int i = 0; i < displayBumps.size(); i++) {
			BranchBump b = displayBumps.get(i);
			float pct = i / displayBumps.size();
			pg.ellipse(0, pct * length, b.diam, b.diam);
		}

		// Draw a tip if we don't have children
		if (isTip()) {
			
			pg.pushMatrix();
			pg.translate(0, length);
			pg.quad(0, -diam/2, 2*diam, -diam/6, 2*diam, diam/6, 0, diam/2);
			pg.popMatrix();
		} 
		// Draw children if we have them
		else {

			for (Branch child: children) {

				pg.pushMatrix();
				pg.translate(0, -length);
				pg.rotate(angle);
				child.draw(pg);
				pg.popMatrix();
			}
		}
		// Draw a leaf and/or flower if necessary
		if (leaf != null) {
			leaf.draw(pg, tree.leafSize);
		}

		if (flower != null) {
			flower.draw(pg, tree.flowerSize);
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
