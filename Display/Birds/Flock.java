package Display.Birds;

import processing.core.*;
import Model.*;
import Util.Util;

import java.util.*;

import Display.Birds.Bird.State;
import Display.Trees.TreeStack;
import processing.opengl.*;

public class Flock {

	private Note note;
	private TreeStack treeStack;
	int baseHue;
	float baseSize;
	double flapSpeed;
	// TODO more flock-specific stuff about tweaking speed, behavior, appearance
	// private class array of sizes or whatever

	ArrayList<Bird> birds = new ArrayList<Bird>();

	Flock(Note n, TreeStack t) {

		note = n;
		treeStack = t;
		// Set baseHue from note.pitch % 12
		baseHue = 360 / ((note.pitch % 12) + 1);
		baseSize = Util.randomf(2, 10);
		flapSpeed = PApplet.map(baseSize, 2f, 10f, 0.5f, 0.01f);// Util.random(0.05, 0.5);
	}

	Bird addBird(PVector stage, PVector pos) {

		Bird newB = new Bird(note, stage, pos, flapSpeed);
		newB.hue = baseHue + Util.randomf(-5, 5);
		newB.size = baseSize + Util.randomf(-0.5f, 0.5f);
		birds.add(newB);
		return newB;
	}

	void update(PGraphics2D pg, ArrayList<Bird> allBirds) {

		for (Bird b : birds) {

			// TODO call an Update based on what's happening in the song - call
			// flock(), flee(), enter(), leave() etc
			b.hue = baseHue;
			b.run(allBirds, birds, pg);
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

	void flyAway() {

		for (Bird b : birds) {

			b.landingSite = null;
			b.state = State.flying;
		}
	}
}
