package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyRenderer;

import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Branch {

	boolean						hasBird				= false, finished = false, isRoot = false;
	PVector						origin, end;
	Leaf						leaf;
	Flower						flower;
	float						flowerSize, leafSize;
	private ArrayList<Branch>	children			= new ArrayList<Branch>();
	private int					numberOfDescendants	= 0;

	private PApplet	parent;
	float			diam, angle, length;
	// private float initialLength, finalLength;
	private int		depth;
	private int		numberOfParents;
	private int		maxChildren	= 2;
	private float	circleAlpha	= 0, circleDiam = 0;
	private float	maxAlpha	= 100;

	static private float	piOver2		= (float) (Math.PI / 2.0);
	static private float	piOver5		= (float) (Math.PI / 5.0);
	static private float	piOver15	= (float) (Math.PI / 15.0);

	// Root
	Branch(PApplet parent, float length, float flowerSize, float leafSize) {

		this.parent = parent;
		this.origin = new PVector();
		isRoot = true;
		numberOfParents = 0;
		circleAlpha = maxAlpha;
		circleDiam = 0;

		// initialLength = length / 4.0f;
		// finalLength = length;
		this.length = length; // initialLength;
		end = new PVector(0, -length);
		angle = PVector.angleBetween(end, origin);

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
	}

	// Normal branch
	Branch(PApplet parent, PVector origin, PVector end, int depth, float flowerSize, float leafSize,
			int numberOfParents) {

		this.parent = parent;
		this.origin = origin;
		this.end = end;
		this.depth = depth;
		this.numberOfParents = numberOfParents;
		length = PVector.dist(origin, end);
		angle = PVector.angleBetween(end, origin);
		circleAlpha = maxAlpha;
		circleDiam = 0;

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;
	}

	ArrayList<Branch> grow(Note n) {

		ArrayList<Branch> newChildren = new ArrayList<Branch>();

		if (finished) {
			Collections.shuffle(children);
			for (Branch b : children) {

				newChildren = b.grow(n);
				if (newChildren.size() > 0) {

					numberOfDescendants += newChildren.size();

					return newChildren;
				}
			}
			return newChildren;
		}

		// 0-3 children

		// 90% chance of first child
		//		if (Util.random(0, 1) < 0.9 || isRoot) {
		newChildren.add(makeChild());
		//		}
		// 40% chance of 2nd child
		//		if (Util.random(0, 1) < 0.4 || isRoot) {
		//			newChildren.add(makeChild());
		//		}
		// 10% chance of 3rd child
		if (Util.random(0, 1) < 0.1) {
			newChildren.add(makeChild());
		}

		children.addAll(newChildren);
		finished = children.size() >= maxChildren;
		numberOfDescendants += newChildren.size();

		return newChildren;
	}

	ArrayList<Float> existingAngles = new ArrayList<Float>();

	Branch makeChild() {

		PVector dir = PVector.sub(end, origin);
		dir.rotate(suitableRangomAngle(existingAngles));
		dir.mult(Util.randomf(0.5f, 0.7f));
		PVector newEnd = PVector.add(end, dir);

		return new Branch(parent, end, newEnd, depth + 1, flowerSize, leafSize, ++numberOfParents);
	}

	void draw(PGraphics pg, HandyRenderer sketcher, float hue, float alpha) {

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

		// Draw the basic line for our branch
		pg.strokeWeight(PApplet.map(numberOfDescendants, 100, 0, 10, 0.5f));
		pg.stroke(hue, 100, 50, alpha);
		pg.fill(hue, 100, 50, alpha);

		sketcher.line(origin.x, origin.y, end.x, end.y);
//		pg.ellipse(end.x, end.y, 4, 4);

		for (Branch child : children) {
			child.draw(pg, sketcher, hue, alpha * 0.9f);
		}

		if (leaf != null) {
			leaf.draw(pg, leafSize);
		}

		if (flower != null) {
			flower.draw(pg, flowerSize);
		}
	}

	//	static float jitMag = 1;

	public void jitter(int millis) {

		if (!finished) {

			end.x += Math.sin((float) millis / 1000) / 20f;
			end.y += Math.sin((float) millis / 2134) / 25f;

			//			end.x += Util.random(-jitMag, jitMag);
			//			end.y += Util.random(-jitMag, jitMag);
		}

		for (Branch child : children) {
			child.jitter(millis);
		}
	}

	private float suitableRangomAngle(ArrayList<Float> existingAngles) {

		Float angle = 0f;

		float maxAngle = PApplet.map(numberOfParents, 0, 5, piOver2, piOver5);

		boolean found = false;
		while (!found) {

			// take a random angle in range
			angle = Util.randomf(piOver15, maxAngle);
			if (Util.coinToss() || (!existingAngles.isEmpty() && existingAngles.get(0) > 0)) {
				angle *= -1;
			}

			// but not too close to an existing angle
			for (Float existing : existingAngles) {

				float minAngleDelta = piOver15;
				if (Math.abs(existing - angle) < minAngleDelta) {
					continue;
				}
			}
			found = true;
		}

		existingAngles.add(angle);
		// TODO push down according to whether we're left or right of centre?
		return angle;
	}

	boolean addFlower(Flower.Type flowerType) {
		return addFlower(flowerType, end);
	}

	boolean addFlower(Flower.Type flowerType, PVector pos) {

		if (flower != null) {
			return false;
		}

		for (Branch child : children) {
			if (child.addFlower(flowerType)) {
				return true;
			}
		}

		flower = new Flower(flowerType, pos);
		return true;
	}

	boolean addLeaf() {
		return addLeaf(end);
	}

	boolean addLeaf(PVector pos) {

		if (leaf != null) {
			return false;
		}

		for (Branch child : children) {
			if (child.addLeaf()) {
				return true;
			}
		}

		leaf = new Leaf(pos, 0, 100);
		return true;
	}
}
