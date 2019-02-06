package Display.Birds;
import processing.core.*;
import processing.opengl.*;
import java.util.*;

import Display.Trees.TreeManager;
import Model.Note;

public class BirdManager {

	private static BirdManager m;
	private PApplet parent;
	private Random r = new Random(0);
	private Flock[] flocks = new Flock[12];
	ArrayList<Bird> allBirds = new ArrayList<Bird>();
	private PGraphics2D pg;
	private PVector stage;
	private static PVector offScreenArea = new PVector(100, 50);
	
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

		pg = (PGraphics2D) parent.createGraphics((int)stage.x, (int)stage.y, PConstants.P2D);
//		pg.pixelDensity = 2;
		pg.noStroke();
		pg.rectMode(PConstants.CENTER);
		pg.colorMode(PConstants.HSB, 360, 100, 100, 100);
		pg.smooth(8);
	}

	public static BirdManager instance() {
		return m;
	}

	public void addNote(Note n, boolean fromRight) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect
		Flock f = flocks[n.pitch % 12];

		if (f == null) {
			f = new Flock(n, TreeManager.instance().treeStackFor(n));
			flocks[n.pitch % 12] = f;
		}

		float posX = fromRight ? stage.x - offScreenArea.x : offScreenArea.x;
		float posY = r.nextFloat() * stage.y * 0.3333f;
		PVector pos = new PVector(posX, posY);

		allBirds.add(f.addBird(stage, pos));
	}

	public void updateAndDraw() {

		pg.beginDraw();
		pg.background(0, 0, 0, 0);

		for (Flock f: flocks) {
			if (f != null) {
				f.update(pg, allBirds);
			}
		}

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
//		parent.blendMode(PConstants.DILATE);
		
//		parent.image(pg, -offScreenArea.x, -offScreenArea.y, stage.x, stage.y);
		parent.image(pg, 0, 0, parent.width, parent.height);
	}
}
