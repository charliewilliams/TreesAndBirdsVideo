package Display.Birds;
import processing.core.*;
import processing.opengl.*;
import java.util.*;

import Model.Note;

public class BirdManager {

	private static BirdManager m;
	private PApplet parent;
	private Flock[] flocks = new Flock[12];
	private PGraphics3D pg;
	private PVector stage;
	private static PVector offScreenArea = new PVector(100, 50);
	private Random r = new Random(0);

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
		//		stage = new PVector(parent.width + offScreenArea.x * 2, parent.height + offScreenArea.y);
		stage = new PVector(parent.width, parent.height);

		pg = (PGraphics3D) parent.createGraphics((int)stage.x, (int)stage.y, PConstants.P3D);
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
		}

				float posX = fromRight ? stage.x - offScreenArea.x : offScreenArea.x;
				float posY = r.nextFloat() * stage.y * 0.3333f;
				PVector pos = new PVector(posX, posY);

//		PVector pos = new PVector(parent.width / 2.0f, parent.height / 2.0f);

		//		PApplet.println(pos, fromRight ? "Right" : "Left");
		f.addBird(stage, pos);
	}

	public void updateAndDraw() {

		pg.beginDraw();
		pg.background(0, 0, 0, 0);

		pg.beginCamera();
		pg.camera();

		//rotateX(frameCount / 100.0);
		pg.rotateX(4.7f);
		pg.rotateY(6.28347f);

		pg.translate(22, 17, -201); // fudge to centre things
		pg.endCamera();
		pg.directionalLight(255, 255, 255, 0, 1, -100); 
		pg.noFill();
		pg.stroke(0);

		pg.line(0, 0, 300, 0, parent.height, 300);
		pg.line(0, 0, 900, 0, parent.height, 900);
		pg.line(0, 0, 300, parent.width, 0, 300);
		pg.line(0, 0, 900, parent.width, 0, 900);

		pg.line(parent.width, 0, 300, parent.width, parent.height, 300);
		pg.line(parent.width, 0, 900, parent.width, parent.height, 900);
		pg.line(0, parent.height, 300, parent.width, parent.height, 300);
		pg.line(0, parent.height, 900, parent.width, parent.height, 900);

		pg.line(0, 0, 300, 0, 0, 900);
		pg.line(0, parent.height, 300, 0, parent.height, 900);
		pg.line(parent.width, 0, 300, parent.width, 0, 900);
		pg.line(parent.width, parent.height, 300, parent.width, parent.height, 900);

		for (Flock f: flocks) {
			if (f != null) {
				f.update(pg);
			}
		}

		pg.endDraw();

		parent.blendMode(PConstants.BLEND);
		//		parent.blendMode(PConstants.DILATE);

//		parent.image(pg, 0, 0);
		parent.image(pg, -offScreenArea.x, -offScreenArea.y);
	}
}
