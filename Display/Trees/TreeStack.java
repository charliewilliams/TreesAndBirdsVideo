package Display.Trees;

import java.util.ArrayList;
import java.util.Collections;

import org.gicentre.handy.HandyPresets;
import org.gicentre.handy.HandyRenderer;

import Display.Birds.*;
import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

public class TreeStack {

	Flower.FlowerType		flowerType;
	Leaf.LeafShape			leafType;
	private ArrayList<Tree>	trees	= new ArrayList<Tree>();
	private PGraphics2D		pg;
	private PApplet			parent;
	private PVector			pos;
	private float			hue;
	private Note			n;
	private HandyRenderer	sketcher;

	TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, PVector pos) {

		this.parent = parent;
		this.pos = pos;
		this.n = n;
		flowerType = Flower.randomType();
		leafType = Leaf.randomType();
		hue = PApplet.map(n.pitch % 12.0f, 0, 12, 0, 360);
		pg = (PGraphics2D) parent.createGraphics(parent.width, parent.height, PConstants.P2D);
		// pg.pixelDensity = 2;
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.smooth(4);
		//		pg.noStroke();

		for (int i = 0; i < numChildren; i++) {
			float alpha = PApplet.map(i, 0, numChildren, 100, 20);
			trees.add(new Tree(parent, n, baseIndex + i, alpha, Util.randomf(5, 15), Util.randomf(5, 15)));
		}

		sketcher = HandyPresets.createWaterAndInk(parent); // new HandyRenderer(a);
		sketcher.setRoughness(Util.randomf(0, 2));
	}

	static int calls = 0;

	void grow(Note note) {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.grow(note).size() > 0) {
				return;
			}
		}
	}

	void addFlower() {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.addFlower(flowerType)) {
				break;
			}
		}
	}

	void addLeaf() {

		Collections.shuffle(trees);
		for (Tree t : trees) {
			if (t.addLeaf(leafType, pg)) {
				break;
			}
		}
	}

	void updateAndDraw(int millis) {

		sketcher.setGraphics(pg);
		sketcher.setSeed(0);

		pg.beginDraw();

		pg.background(0, 0, 0, 0);

		pg.pushMatrix();
		pg.translate(pos.x, pos.y);

		for (Tree t : trees) {
			t.jitter(millis);
			t.draw(pg, sketcher, hue);
		}

		drawDebugLabel(pg);

		pg.popMatrix();

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
		parent.image(pg, 0, 0);
	}

	private void drawDebugLabel(PGraphics2D pg) {

		pg.fill(0);
		pg.textSize(24);
		pg.text(n.pitchClass, -20, 20);
	}

	public PVector acquireLandingSite(Bird b) {

		Collections.shuffle(trees);
		for (Tree t : trees) {

			Collections.shuffle(t.branches);
			for (Branch br : t.branches) {

				if (br.hasBird == false) {

					br.hasBird = true;
					PVector absolutePos = PVector.add(pos, br.end);

					// Someday I want to know enough trig to know why this fudge factor is needed:
					absolutePos.x += 100;
					absolutePos.y += 50;

					//					if (n.pitchClass == "C#") {
					//						PApplet.println("Pos:", pos, "branch origin:", br.origin, "end:", br.end, "angle:", br.angle, "length:", br.length, "draw dot at:", absolutePos);
					//					}
					//					absolutePos = PVector.add(absolutePos, br.origin);
					//					PApplet.println("Got spot", absolutePos, "for bird at", b.pos());
					return absolutePos;
				}
			}
		}

		PVector fallback = pos.copy();
		fallback.x += 100;
		fallback.y += 50;

		return fallback;
	}
}
