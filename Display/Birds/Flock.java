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
	// TODO more flock-specific stuff about tweaking speed, behavior, appearance
	// private class array of sizes or whatever

	ArrayList<Bird> birds = new ArrayList<Bird>();

	Flock(Note n, TreeStack t) {

		note = n;
		treeStack = t;
		// Set baseHue from note.pitch % 12
		baseHue = 360 / ((note.pitch % 12) + 1);
	}

	Bird addBird(PVector stage, PVector pos) {

		Bird newB = new Bird(stage, pos);
		newB.hue = baseHue + Util.random(-5, 5);
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
