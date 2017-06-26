package Display.Birds;
import processing.core.*;
import processing.opengl.*;
import java.util.*;

import Model.Note;

public class BirdManager {

	private static BirdManager m;
	private PApplet parent;
	private Random r = new Random(0);
	private Flock[] flocks = new Flock[12];
	private PGraphics3D pg;
	private PVector stage;
	private static PVector offScreenArea = new PVector(100, 50);

	public BirdManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
		stage = new PVector(parent.width, parent.height);

		pg = (PGraphics3D) parent.createGraphics(parent.width, parent.height, PConstants.P3D);
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
			f = new Flock(n);
			flocks[n.pitch % 12] = f;

			// Debug
//			for (int i = 0; i < 500; i++) {
//				f.addBird(stage, new PVector(parent.width/2, parent.height/2, 600));
//			}
		}

		float posX = fromRight ? offScreenArea.x : stage.x + offScreenArea.x;
		float posY = r.nextFloat() * stage.y + stage.y * 0.333f;
		PVector pos = new PVector(posX, posY, 600);

		f.addBird(stage, pos);
	}

	public void updateAndDraw() {

		pg.beginDraw();
		pg.background(180, 50, 100); // TEMP

		for (Flock f: flocks) {
			if (f != null) {
				f.update(pg);
			}
		}

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
		
//		parent.camera();
		parent.image(pg, 0, 0, parent.width, parent.height);
	}
}
