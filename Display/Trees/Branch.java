package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyRenderer;

import Display.Glow;
import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class Branch {

	boolean hasBird = false, finished = false, isRoot = false;

	PVector	origin, end;
	Leaf	leaf;
	Flower	flower;
	float	flowerSize, leafSize;

	private ArrayList<Branch> children = new ArrayList<Branch>();

	private PApplet	parent;
	float			diam, angle, length;
	private int		depth;
	private int		numberOfParents;
	private int		numberOfDescendants	= 0;
	private int		maxChildren			= 2;
	private float	circleAlpha			= 0, circleDiam = 0;
	private float	maxAlpha			= 100;
	private float	glowAmount			= 255;
	private float	hue;

	static private float	piOver2		= (float) (Math.PI / 2.0);
	static private float	piOver5		= (float) (Math.PI / 5.0);
	static private float	piOver15	= (float) (Math.PI / 15.0);

	private PVector	driftSpeed;
	private PVector	driftMag;

	private long seed = (long) Util.random(0, 10000000);

	// Root
	Branch(PApplet parent, float length, float flowerSize, float leafSize) {

		this.parent = parent;
		this.origin = new PVector();
		isRoot = true;
		numberOfParents = 0;
		circleAlpha = maxAlpha;
		circleDiam = 0;
		hue = Util.randomf(100, 140);

		this.length = length;
		end = new PVector(0, -length);
		angle = PVector.angleBetween(end, origin);

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;

		driftSpeed = new PVector(Util.randomf(800, 1200), Util.randomf(2000, 2500));
		driftMag = new PVector(Util.randomf(10, 30), Util.randomf(20, 30));
	}

	// Normal branch
	Branch(PApplet parent, PVector origin, PVector end, int depth, float flowerSize, float leafSize,
			int numberOfParents, PVector driftSpeed, PVector driftMag, float hue) {

		this.parent = parent;
		this.origin = origin;
		this.end = end;
		this.depth = depth;
		this.numberOfParents = numberOfParents;
		this.hue = hue;
		length = PVector.dist(origin, end);
		angle = PVector.angleBetween(end, origin);
		circleAlpha = maxAlpha;
		circleDiam = 0;

		this.flowerSize = flowerSize;
		this.leafSize = leafSize;

		this.driftSpeed = driftSpeed.copy().mult(0.9f);
		this.driftMag = driftMag.copy().mult(1.1f);
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

		newChildren.add(makeChild());

		// 10% chance of 2nd child
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

		return new Branch(parent, end, newEnd, depth + 1, flowerSize, leafSize, ++numberOfParents, driftSpeed, driftMag,
				hue);
	}

	//	t.renderTrees(pg_trees, sketcher, hue);
	//	t.renderLeaves(pg_leaves, hue);
	//	t.renderGlow(pg_trees, pg_leaves, pg_glow, hue);
	// pg.blendMode(PConstants.NORMAL);

	public void renderTrees(PGraphics pg_trees, HandyRenderer sketcher, float hue, float alpha) {
		sketcher.setSeed(seed);

		// Background circle
		if (circleAlpha > 0) {

			pg_trees.stroke(hue, 100, 90, circleAlpha * 0.8f);
			pg_trees.strokeWeight(0.5f);
			pg_trees.fill(hue, 20, 100, circleAlpha * 0.2f);
			pg_trees.ellipse(end.x, end.y, circleDiam, circleDiam);

			circleAlpha *= 0.99;

			if (circleDiam < length) {
				circleDiam += length / 50;
			} else {
				circleDiam *= 1.005;
			}
		}

		pg_trees.strokeWeight(PApplet.map(numberOfDescendants, 100, 0, 10, 0.5f));

		// sketcher takes BGRA (!)
		// so let's just give it a gray value
//		sketcher.setStrokeColour((int) glowAmount);

		//		int color = Util.colorFrom360(0, 100, 100, alpha); // red
		//		float red = parent.red(color);
		//		float gre = parent.green(color);
		//		float blu = parent.blue(color);
		//		float alp = parent.alpha(color);
		//		int bgra = Util.bgraFrom255(red, gre, blu, alp);
		//		sketcher.setStrokeColour(bgra);

		sketcher.line(origin.x, origin.y, end.x, end.y);
		//		pg_trees.line(origin.x, origin.y, end.x, end.y);

		for (Branch child : children) {
			child.renderTrees(pg_trees, sketcher, hue, alpha * 0.9f);
		}
	}

	public void renderLeaves(PGraphics pg_leaves, float hue, float alpha) {

		if (leaf != null) {
			leaf.draw(parent, pg_leaves, leafSize);
		}

		if (flower != null) {
			flower.draw(pg_leaves, flowerSize);
		}
	}

	public void renderGlow(PGraphics pg_trees, PGraphics pg_leaves, PGraphics2D pg_glow, float hue, float alpha) {

		if (glowAmount > 0.01) {

			float mult = Util.logMapf(glowAmount, 255, 0, 10, 0);
			float radius = Util.logMapf(glowAmount, 255, 0, 1, 0);
			Glow.drawGlow(pg_trees, pg_glow, mult, radius);

			glowAmount *= 0.999f;
		}
	}

	public void jitter(int millis) {

		if (!finished) {

			end.x += Math.sin((float) millis / driftSpeed.x) / driftMag.x;
			end.y += Math.sin((float) millis / driftSpeed.y) / driftMag.y;
		}

		for (Branch child : children) {
			child.jitter(millis);
		}
	}

	private float suitableRangomAngle(ArrayList<Float> existingAngles) {

		Float angle = 0f;

		float maxAngle = isRoot ? piOver2 : PApplet.map(numberOfParents, 0, 5, piOver2, piOver5);

		boolean found = false;
		while (!found) {

			// take a random angle in range
			angle = Util.randomf(piOver15, maxAngle);
			if (Util.coinToss() || (!existingAngles.isEmpty() && existingAngles.get(0) > 0)) {
				angle *= -1;
			}

			// but not too close to an existing angle
			for (Float existing : existingAngles) {

				float minAngleDelta = isRoot ? piOver5 : piOver15;
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

	boolean addFlower(Flower.FlowerType flowerType) {
		return addFlower(flowerType, end);
	}

	boolean addFlower(Flower.FlowerType flowerType, PVector pos) {

		if (flower != null) {
			return false;
		}

		Collections.shuffle(children);
		for (Branch child : children) {
			if (child.addFlower(flowerType)) {
				return true;
			}
		}

		flower = new Flower(flowerType, pos);
		return true;
	}

	boolean addLeaf(Leaf.LeafShape leafType, PGraphics pg) {
		return addLeaf(leafType, pg, end);
	}

	boolean addLeaf(Leaf.LeafShape leafType, PGraphics pg, PVector pos) {

		if (leaf != null) {
			return false;
		}

		Collections.shuffle(children);
		for (Branch child : children) {
			if (child.addLeaf(leafType, pg)) {
				return true;
			}
		}

		leaf = new Leaf(leafType, pos, hue, pg);
		return true;
	}

	boolean dropLeaf() {

		if (leaf != null && !leaf.isFalling) {
			leaf.isFalling = true;
			return true;
		}

		Collections.shuffle(children);
		for (Branch child : children) {
			if (child.dropLeaf()) {
				return true;
			}
		}

		return false;
	}
}
