package Display.Birds;

import java.util.ArrayList;

import Display.Birds.Bird.State;
import Display.Trees.TreeStack;
import Model.Note;
import Util.Util;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphics2D;

import org.gicentre.handy.*;

public class Flock {

	private Note note;
	private TreeStack treeStack;
	private 
	int baseHue;
	float baseSize;
	double flapSpeed;
	private HandyRenderer sketcher;

	ArrayList<Bird> birds = new ArrayList<Bird>();

	Flock(Note n, TreeStack t, PApplet a) {

		note = n;
		treeStack = t;
		// Set baseHue from note.pitch % 12
		baseHue = 360 / ((note.pitch % 12) + 1);
		baseSize = Util.randomf(2, 10);
		flapSpeed = PApplet.map(baseSize, 2f, 10f, 0.5f, 0.01f);// Util.random(0.05, 0.5);
		sketcher = HandyPresets.createWaterAndInk(a); // new HandyRenderer(a);
		sketcher.setRoughness(Util.randomf(0, 1.5f));
		sketcher.setStrokeWeight(Util.randomf(0.15f, 0.5f));
	}

	Bird addBird(PVector stage, PVector pos, int millis) {

		Bird newB = new Bird(note, stage, pos, flapSpeed, millis);
		newB.hue = baseHue + Util.randomf(-5, 5);
		newB.size = baseSize + Util.randomf(-0.5f, 0.5f);
		birds.add(newB);
		return newB;
	}

	void update(PGraphics2D pg, ArrayList<Bird> allBirds, int millis) {

		for (Bird b : birds) {
			sketcher.setGraphics(pg);
			b.run(allBirds, birds, pg, millis, sketcher);
		}
	}

	void land() {

		for (Bird b : birds) {

			if (b.landingSite != null) {
				continue;
			}
			
			b.landingSite = treeStack.acquireLandingSite(b);
			if (b.landingSite != null) {
				b.state = State.to_land;
			}
		}
	}

	void flyAway(PVector stage, int millis) {

		for (Bird b : birds) {
			b.flyAway(stage, millis);
		}
	}
}
