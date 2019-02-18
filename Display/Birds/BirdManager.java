package Display.Birds;

import processing.core.*;
import processing.opengl.*;
import java.util.*;

import Display.Trees.TreeManager;
import Model.Note;
import Util.Util;

public class BirdManager {

	private static BirdManager	m;
	private PApplet				parent;
	private Random				r				= new Random(0);
	private Flock[]				flocks			= new Flock[12];
	ArrayList<Bird>				allBirds		= new ArrayList<Bird>();
	private PGraphics2D			pg;
	private PVector				stage;
	private static PVector		offScreenArea	= new PVector(100, 50);
	private static Random		rand			= new Random();

	public static void main(String[] args) {

		PApplet.main("BirdManager");
	}

	public BirdManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;

		// Make the stage include the offscreen areas
		stage = new PVector(parent.width + offScreenArea.x * 2, parent.height + offScreenArea.y);
		//		stage = new PVector(parent.width, parent.height);

		pg = (PGraphics2D) parent.createGraphics((int) stage.x, (int) stage.y, PConstants.P2D);
		//		pg.pixelDensity = 2;
		pg.noStroke();
		pg.rectMode(PConstants.CENTER);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.smooth(8);
	}

	public static BirdManager instance() {
		return m;
	}

	public void addNote(Note n, boolean fromRight, int millis) {
		addNote(n, fromRight, millis, 0);
	}

	public void addNote(Note n, boolean fromRight, int millis, float maxSpeed) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect
		int idx = n.pitch % 12;
		Flock f = flocks[idx];

		//C  C# D  D# E   F  F# G  G# A  Bb B
		float[] flockSizes = { 2, 2, 7, 2, 10, 2, 4, 2, 3, 5, 2, 6 }; //Util.randomf(2, 10);
		float size = flockSizes[idx];

		if (f == null) {
			f = new Flock(n, TreeManager.instance().treeStackFor(n), size, parent, rand);
			flocks[idx] = f;
		}

		float posX = fromRight ? stage.x - offScreenArea.x : offScreenArea.x;
		float posY = r.nextFloat() * stage.y * 0.3333f;
		posX += Util.randomf(-5f, 5f);
		//		posY += Util.randomf(-50f, 50f);
		PVector pos = new PVector(posX, posY);

		allBirds.add(f.addBird(stage, pos, millis, maxSpeed));
	}

	public void buildDebugBirds() {

		for (int i = 0; i < 128; i++) {

			Note n = new Note(i % 12);
			addNote(n, true, 0);

			landAllBirds();

			for (Bird b : allBirds) {
				b.debugForceLand();
			}
		}
	}

	public void updateAndDraw(int millis) {

		pg.beginDraw();
		pg.background(0, 0, 0, 0);

		for (Flock f : flocks) {
			if (f != null) {
				f.update(pg, allBirds, millis);
			}
		}

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
		//		parent.blendMode(PConstants.DILATE);

		parent.image(pg, -offScreenArea.x, -offScreenArea.y, stage.x, stage.y); // real version
		//		parent.image(pg, 0, 0, parent.width, parent.height); // put the offstage area onscreen for debugging
	}

	public void landAllBirds() {

		for (Flock f : flocks) {
			if (f != null) {
				f.land();
			}
		}
	}

	public void flyAwayAllBirds(int millis) {

		for (Flock f : flocks) {
			if (f != null) {
				f.flyAway(stage, millis);
			}
		}
	}

	public void cleanUpOffscreenBirds() {

		for (Flock f : flocks) {
			if (f != null) {
				f.cleanUpOffscreenBirds(stage);
			}
		}
	}
}
