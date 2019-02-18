package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyRenderer;

import Model.Note;
import Model.Section;
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

	private ArrayList<Branch> children = new ArrayList<Branch>();

	private PApplet	parent;
	float			diam, angle, length;
	private int		depth;
	private int		numberOfParents = 0;
	private int		numberOfDescendants	= 0;
	private int		maxChildren			= 2;
	private float	maxAlpha			= 100;
	private float	circleAlpha			= maxAlpha, circleDiam = 0;
	private float	glowAmount			= 255;
	private float	hue;

	static private float	piOver2		= (float) (Math.PI / 2.0);
	static private float	piOver5		= (float) (Math.PI / 5.0);
	static private float	piOver15	= (float) (Math.PI / 15.0);

	private PVector	driftSpeed;
	private PVector	driftMag;

	private float alpha;

	private long seed, seedStride;

	// Root
	Branch(PApplet parent, long seed, long seedStride, float length, float alpha, boolean shouldGlow) {

		this.parent = parent;
		this.seed = seed;
		this.origin = new PVector();
		this.alpha = alpha;
		isRoot = true;
		hue = Util.randomf(100, 140);
		
		if (!shouldGlow) {
			glowAmount = 0;
		}

		this.length = length;
		end = new PVector(0, -length);
		angle = PVector.angleBetween(end, origin);

		driftSpeed = new PVector(Util.randomf(800, 1200), Util.randomf(2000, 2500));
		driftMag = new PVector(Util.randomf(10, 30), Util.randomf(20, 30));
	}

	// Normal branch
	Branch(PApplet parent, long seed, long seedStride, PVector origin, PVector end, int depth, int numberOfParents, PVector driftSpeed, PVector driftMag, float hue, float alpha, boolean shouldGlow) {

		this.parent = parent;
		this.seed = seed;
		this.origin = origin;
		this.end = end;
		this.depth = depth;
		this.alpha = alpha;
		this.numberOfParents = numberOfParents;
		this.hue = hue;
		length = PVector.dist(origin, end);
		angle = PVector.angleBetween(end, origin);
		circleAlpha = maxAlpha;
		
		if (!shouldGlow) {
			glowAmount = 0;
		}

		this.driftSpeed = driftSpeed.copy().mult(0.9f);
		this.driftMag = driftMag.copy().mult(1.15f);
	}

	ArrayList<Branch> grow(Note n, boolean shouldGlow) {

		ArrayList<Branch> newChildren = new ArrayList<Branch>();

		if (finished) {
			ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();
			Collections.shuffle(cs);
			for (Branch b : cs) {

				newChildren = b.grow(n, shouldGlow);
				if (newChildren.size() > 0) {

					numberOfDescendants += newChildren.size();

					return newChildren;
				}
			}
			return newChildren;
		}

		newChildren.add(makeChild(shouldGlow));

		// 10% chance of 2nd child on this pass
		if (Util.random(0, 1) < 0.1) {
			newChildren.add(makeChild(shouldGlow));
		}

		children.addAll(newChildren);
		finished = children.size() >= maxChildren;
		numberOfDescendants += newChildren.size();

		return newChildren;
	}

	ArrayList<Float> existingAngles = new ArrayList<Float>();

	Branch makeChild(boolean shouldGlow) {

		PVector dir = PVector.sub(end, origin);
		dir.rotate(suitableRangomAngle(existingAngles));
		dir.mult(Util.randomf(0.5f, 0.7f));
		PVector newEnd = PVector.add(end, dir);

		return new Branch(parent, seed + seedStride, seedStride, end, newEnd, depth + 1, ++numberOfParents, driftSpeed, driftMag, hue, alpha * 0.95f, shouldGlow);
	}

	float strokeWeight() {
		return PApplet.map(numberOfDescendants, 100, 0, 10, 0.5f);
	}

	public void renderBranch(PGraphics2D pg_trees, HandyRenderer sketcher) {
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

		pg_trees.strokeWeight(strokeWeight());

		// sketcher takes BGRA (!)
		// so let's just give it a gray value
		int glowPlusAlpha = Util.setAlpha((int) glowAmount, alpha);
		sketcher.setStrokeColour(glowPlusAlpha);

		glowAmount *= 0.9f;

		sketcher.line(origin.x, origin.y, end.x, end.y);
		//		pg_trees.line(origin.x, origin.y, end.x, end.y);

		for (Branch child : children) {
			child.renderBranch(pg_trees, sketcher);
		}
	}

	public void renderLeaves(PGraphics pg_leaves) {

		if (leaf != null) {
			leaf.draw(parent, pg_leaves);
		}

		if (flower != null) {
			flower.draw(parent, pg_leaves);
		}

		for (Branch child : children) {
			child.renderLeaves(pg_leaves);
		}
	}

	public float renderGlow(PGraphics2D pg_glow) {

		float maxGlow = glowAmount;

		for (Branch child : children) {
			float thisGlow = child.renderGlow(pg_glow);
			if (thisGlow > maxGlow) {
				maxGlow = thisGlow;
			}
		}

		float stroke = Math.max(10, strokeWeight() * 3);
		float maxStroke = 15;
		stroke = Math.min(stroke, maxStroke);
		float weight = Util.logMapf(maxGlow, 255, 0, stroke, 0.0f);

		float color = 127;

		pg_glow.strokeWeight(weight);
		pg_glow.stroke(color);
		pg_glow.fill(color);
		pg_glow.line(origin.x, origin.y, end.x, end.y);

		return maxGlow;
	}

	public void glow() {
		glowAmount = 255;
	}

	public boolean turnLeafColorTick(int millis, boolean forceAll) {

		// Start out being unlikely for leaves to change
		// this way they don't change too quickly at the start of the section
		// but they are all changed toward the end
		// It accelerates, just like real fall
		boolean shouldTick = Util.logMapf(millis, Section.repeatedNotes.startTime(), Section.bigReturn.startTime(), 0,
				1) > Util.randomf(0, 1);

		if (!shouldTick && !forceAll) {
			return false;
		}

		int numberOfTicksPerCall = millis > Section.highMel.startTime() ? 5 : 1;
		if (forceAll) {
			numberOfTicksPerCall = 25;
		}
		int ticksCompleted = 0;
		ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();

		for (int i = 0; i < numberOfTicksPerCall; i++) {
			Collections.shuffle(cs);

			for (Branch child : cs) {
				if (child.turnLeafColorTick(millis, forceAll)) {
					ticksCompleted++;

					if (ticksCompleted >= numberOfTicksPerCall) {
						return true;
					}
				}
			}
		}
		
		if (leaf != null && leaf.turnColorTick()) {
			return true;
		}
		ticksCompleted++;
		return ticksCompleted >= numberOfTicksPerCall;
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
		return angle;
	}

	Flower addFlower(Flower.FlowerType flowerType) {
		return addFlower(flowerType, end);
	}

	Flower addFlower(Flower.FlowerType flowerType, PVector pos) {

		if (flower != null) {
			return null;
		}

		ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();
		Collections.shuffle(cs);
		for (Branch child : cs) {
			Flower f = child.addFlower(flowerType);
			if (f != null) {
				return f;
			}
		}

		if (isRoot) {
			return null;
		}
		flower = new Flower(flowerType, pos);
		return flower;
	}

	Leaf addLeaf(Leaf.LeafShape leafType, PGraphics pg) {
		return addLeaf(leafType, pg, end);
	}

	Leaf addLeaf(Leaf.LeafShape leafType, PGraphics pg, PVector pos) {

		if (leaf != null) {
			return null;
		}

		ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();
		Collections.shuffle(cs);
		for (Branch child : cs) {
			Leaf l = child.addLeaf(leafType, pg);
			if (l != null) {
				return l;
			}
		}

		if (isRoot) {
			return null;
		}
		leaf = new Leaf(leafType, pos, hue, pg);
		return leaf;
	}

	boolean dropLeaf() {

		ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();
		Collections.shuffle(cs);
		for (Branch child : cs) {
			if (child.dropLeaf()) {
				return true;
			}
		}

		if (leaf != null && !leaf.isFalling) {
			leaf.isFalling = true;
			return true;
		}

		return false;
	}

	public boolean dropFlower() {

		ArrayList<Branch> cs = (ArrayList<Branch>) children.clone();
		Collections.shuffle(cs);
		for (Branch child : cs) {
			if (child.dropFlower()) {
				return true;
			}
		}

		if (flower != null && !flower.isFalling) {
			flower.isFalling = true;
			return true;
		}

		return false;
	}
}
