package Display.Trees;

import Util.*;
import processing.core.*;
import java.util.*;

import Model.Note;

public class Branch {

	boolean hasBird = false, finished = false, isRoot = false;
	private PVector origin;
	PVector end;
	Leaf leaf;
	Flower flower;
	float flowerSize, leafSize;
	private ArrayList<Branch> children = new ArrayList<Branch>();
	private ArrayList<BranchBump> displayBumps = new ArrayList<BranchBump>();

	private PApplet parent;
	private float diam, angle, length;
	// private float initialLength, finalLength;
	private int depth;
	// private int childDepth;
	private float circleAlpha = 0, circleDiam = 0;
	private float maxAlpha = 100;

	static private float piOver5 = (float) (Math.PI / 5.0);
	static private float piOver15 = (float) (Math.PI / 15.0);

	// Root
	Branch(PApplet parent, float length, float flowerSize, float leafSize) {

		this.parent = parent;
		this.origin = new PVector();
		isRoot = true;

		// initialLength = length / 4.0f;
		// finalLength = length;
		this.length = length; // initialLength;
		end = new PVector(0, -length);
		angle = PVector.angleBetween(end, origin);

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
		// makeBumps();
	}

	// Normal branch
	Branch(PApplet parent, PVector origin, PVector end, int depth, float flowerSize, float leafSize) {

		this.parent = parent;
		this.origin = origin;
		this.end = end;
		this.depth = depth;
		length = PVector.dist(origin, end);
		angle = PVector.angleBetween(end, origin);

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
		// makeBumps();
	}

	private float bumpDistance = 4.0f;

	private void makeBumps() {

		int numBumps = (int) (length / bumpDistance);
		float noiseSeed = Util.randomf(0, 1000);

		for (int i = 0; i < numBumps; i++) {
			BranchBump b = new BranchBump(parent.noise(noiseSeed + ((float) i / 5.0f)) * length / 5.0f);
			displayBumps.add(b);
		}
	}

	// static private int maxDepthForMutableLength = 3;
	// static private int childDepthOverWhichParentLengthGrows = 3;

	// private void updateLengthAndEndpoint() {
	//
	// if (depth <= maxDepthForMutableLength && childDepth <=
	// childDepthOverWhichParentLengthGrows) {
	//
	// PVector dir = PVector.sub(end, origin);
	// dir.setMag(PApplet.map(childDepth /
	// (float)childDepthOverWhichParentLengthGrows, 0, 1, initialLength,
	// finalLength));
	// end = PVector.add(origin, dir);
	// length = PVector.dist(origin, end);
	// }
	// }

	ArrayList<Branch> grow(Note n) {

		ArrayList<Branch> newChildren = new ArrayList<Branch>();

		if (finished) {
			Collections.shuffle(children);
			for (Branch b : children) {

				newChildren = b.grow(n);
				if (newChildren.size() > 0) {

					// updateLengthAndEndpoint();
					// childDepth++;

					return newChildren;
				}
				;
			}
			return newChildren;
		}

		// 0-3 children

		// 90% chance of first child
		if (Util.random(0, 1) > 0.1 || isRoot) {
			newChildren.add(makeChild());
		}
		// 90% chance of 2nd child
		if (Util.random(0, 1) > 0.4 || isRoot) {
			newChildren.add(makeChild());
		}
		// 10% chance of 3rd child
		if (Util.random(0, 1) > 0.9) {
			newChildren.add(makeChild());
		}

		finished = true;
		circleAlpha = maxAlpha;
		circleDiam = 0;
		// childDepth++;

		children.addAll(newChildren);

		return newChildren;
	}

	Branch makeChild() {

		PVector dir = PVector.sub(end, origin);
		dir.rotate(suitableRangomAngle());
		dir.mult(Util.randomf(0.5f, 0.7f));
		PVector newEnd = PVector.add(end, dir);

		return new Branch(parent, end, newEnd, depth + 1, flowerSize, leafSize);
	}

	private float suitableRangomAngle() {

		float angle = Util.randomf(piOver15, piOver5);
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

			for (Branch child : children) {
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

	void draw(PGraphics pg, float hue, float alpha) {

		// Background circle
		if (circleAlpha > 0) {

			pg.stroke(hue, 100, 90, circleAlpha * 0.8f);
			pg.strokeWeight(0.5f);
			pg.fill(hue, 20, 100, circleAlpha * 0.2f);
			pg.ellipse(end.x, end.y, circleDiam, circleDiam);

			circleAlpha *= 0.99;

			if (circleDiam < length) {
				circleDiam += length / 50;
			} else {
				circleDiam *= 1.005;
			}
		}

		// Draw the basic line for our branch (debug)
		if (hasBird) {
			pg.stroke(0, 100, 100, 100);
			pg.fill(0, 100, 100, 100);
		} else {
			pg.stroke(hue, 100, 50, alpha);
			pg.fill(hue, 100, 50, alpha);
		}

		pg.line(origin.x, origin.y, end.x, end.y);
		pg.ellipse(end.x, end.y, 4, 4);

		// Draw the nice texture-y bumps over it
		pg.pushMatrix();
		{
			pg.translate(origin.x, origin.y);
			pg.rotate(angle);
			for (int i = 0; i < displayBumps.size(); i++) {
				BranchBump b = displayBumps.get(i);
				float pct = (float) i / (float) displayBumps.size();
				pg.noStroke();
				pg.fill(0, 0, 0, alpha);
				pg.ellipse(0, -pct * length, b.diam, b.diam);
			}
		}
		pg.popMatrix();

		// Draw a tip if we don't have children
		if (isTip()) {

			pg.pushMatrix();
			{
				pg.translate(0, length);
				pg.quad(0, -diam / 2, 2 * diam, -diam / 6, 2 * diam, diam / 6, 0, diam / 2);
			}
			pg.popMatrix();
		}
		// Draw children if we have them
		else {

			for (Branch child : children) {
				child.draw(pg, hue, alpha * 0.9f);
			}
		}
		// Draw a leaf and/or flower if necessary
		if (leaf != null) {
			leaf.draw(pg, leafSize);
		}

		if (flower != null) {
			flower.draw(pg, flowerSize);
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
}
