package Display.Birds;
import processing.core.*;
import Model.*;
import java.util.*;
import processing.opengl.*;

public class Flock {

	int baseHue;
	// TODO more flock-specific stuff about tweaking speed, behavior, appearance
	// private class array of sizes or whatever
	
	ArrayList<Bird> birds = new ArrayList<Bird>();

	Flock(Note n) {

		// Set baseHue from note.pitch % 12
		baseHue = 360 / ((n.pitch % 12) + 1);
	}

	void addBird(PVector stage, PVector pos) {
		
		birds.add(new Bird(stage, pos));
	}

	void update(PGraphics2D pg) {

		for (Bird b: birds) {
			
			// TODO call an Update based on what's happening in the song - call flock(), flee(), enter(), leave() etc
			b.hue = baseHue;
			b.run(birds, pg);
//			PApplet.println(b.pos);
		}
	}
}
