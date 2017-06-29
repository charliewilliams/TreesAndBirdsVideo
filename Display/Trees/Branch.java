package Display.Trees;

import Util.*;
import processing.core.*;
import java.util.*;

public class Branch {

	boolean hasBird = false, finished = false;
	PVector origin, end;
	Tree tree; Leaf leaf; Flower flower;
	ArrayList<Branch> children = new ArrayList<Branch>();
	ArrayList<BranchBump> displayBumps = new ArrayList<BranchBump>();

	private PApplet parent;
	private float diam, angle, length;

	static float piOver5  = (float)(Math.PI / 5.0);
	static float piOver10 = (float)(Math.PI / 10.0);
	static float piOver15 = (float)(Math.PI / 15.0);
	static float rootSize = 70;
	static float minSize = 0.6f;
	static int totalDepth = 150;

	Branch(PApplet parent, PVector origin, PVector end) { 

		this.parent = parent;
		this.origin = origin.copy();
		this.end = end.copy();
		
		angle = PVector.angleBetween(end, origin);
		length = PVector.dist(origin, end);
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

		PVector dir = PVector.sub(end, origin);
		dir.rotate(Util.random(piOver15, piOver5));
		dir.mult(Util.random(0.5f, 0.7f));
		PVector newEnd = PVector.add(end, dir);

		return new Branch(parent, end, newEnd);
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

		pg.pushMatrix();
		pg.translate(origin.x, origin.y);
		pg.rotate(angle);
		
		pg.line(origin.x, origin.y, end.x, end.y);
//		
//		for (int i = 0; i < displayBumps.size(); i++) {
//			BranchBump b = displayBumps.get(i);
//			float pct = i / displayBumps.size();
//			pg.ellipse(0, pct * length, b.diam, b.diam);
//		}

		if (isTip()) {

			pg.pushMatrix();
			pg.translate(0, length);
			pg.quad(0, -diam/2, 2*diam, -diam/6, 2*diam, diam/6, 0, diam/2);
			pg.popMatrix();

		} else {

			for (Branch child: children) {
				child.draw(pg);
			}
		}

		if (leaf != null) {
			leaf.draw(pg, tree.leafSize);
		}

		if (flower != null) {
			flower.draw(pg, tree.flowerSize);
		}

		pg.popMatrix();
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
