package Display.Trees;

import Util.*;
import processing.core.*;
import java.util.*;

public class Branch {

	boolean hasBird = false, finished = false;
	private PVector origin, end;
	private Tree tree; Leaf leaf; Flower flower;
	private ArrayList<Branch> children = new ArrayList<Branch>();
	private ArrayList<BranchBump> displayBumps = new ArrayList<BranchBump>();

	private PApplet parent;
	private float diam, angle, length;

	static private float piOver5  = (float)(Math.PI / 5.0);
//	static private float piOver10 = (float)(Math.PI / 10.0);
	static private float piOver15 = (float)(Math.PI / 15.0);

	// Root
	Branch(PApplet parent, float length) { 

		this.parent = parent;
		this.origin = new PVector();
		this.length = length;
		this.end = new PVector(0, -length);
		angle = PVector.angleBetween(end, origin);
		end = new PVector(0, -length);
	}

	// Normal branch
	Branch(PApplet parent, PVector origin, PVector end) { 

		this.parent = parent;
		this.origin = origin;
		this.end = end;
		length = PVector.dist(origin, end);
		angle = PVector.angleBetween(end, origin);
	}

	boolean grow() {

		if (finished) {
			Collections.shuffle(children);
			for (Branch b: children) {
				if (b.grow()) {
					return true;
				};
			}
			return false;
		}

		finished = true;

		// 0-3 children

		// 90% chance of first child
		if (Util.random(0, 1) > 0.1) {
			children.add(makeChild());
		}
		// 90% chance of 2nd child
		if (Util.random(0, 1) > 0.4) {
			children.add(makeChild());
		}
		// 10% chance of 3rd child
		if (Util.random(0, 1) > 0.9) {
			children.add(makeChild());
		}
		
		return true;
	}

	Branch makeChild() {
		
		PVector dir = PVector.sub(end, origin);
		dir.rotate(suitableRangomAngle());
		dir.mult(Util.random(0.5f, 0.7f));
		PVector newEnd = PVector.add(end, dir);
		
		return new Branch(parent, end, newEnd);
	}
	
	private float suitableRangomAngle() {

		float angle = Util.random(piOver15, piOver5);
		if (Util.random(-1, 1) < 0) {
			angle *= -1;
		}

		// TODO push down according to whether we're left or right of centre?
		return angle;
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

	void draw(PGraphics pg, float alpha) {

		// Draw the basic line for our branch (debug)
		pg.stroke(0, 0, 0, alpha);
		pg.fill(0, 0, 0, alpha);
		pg.line(origin.x, origin.y, end.x, end.y);
		pg.ellipse(end.x, end.y, 4, 4);

		// Draw the nice texture-y bumps over it
		pg.pushMatrix(); {
			pg.rotate(angle);
			for (int i = 0; i < displayBumps.size(); i++) {
				BranchBump b = displayBumps.get(i);
				float pct = i / displayBumps.size();
				pg.ellipse(0, -pct * length, b.diam, b.diam);
			}
		} pg.popMatrix();

		// Draw a tip if we don't have children
		if (isTip()) {

			pg.pushMatrix(); {
				pg.translate(0, length);
				pg.quad(0, -diam/2, 2*diam, -diam/6, 2*diam, diam/6, 0, diam/2);
			} pg.popMatrix();
		} 
		// Draw children if we have them
		else {

			for (Branch child: children) {
				child.draw(pg, alpha * 0.9f);
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

	static float jitMag = 4;
	void jitter() {

		if (!finished) {
			end.x += Util.random(-jitMag, jitMag);
			end.y += Util.random(-jitMag, jitMag);
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
