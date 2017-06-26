package Display;
import processing.core.*;
//import java.util.*;

public class SceneManager {

	private static SceneManager m;
	private PApplet parent;
	int[] backgroundColors = new int[6];
	PImage bg;
	float cameraZ = 600;

	public SceneManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		
		m = this;
		this.parent = parent;
		
		bg = parent.loadImage("paper.jpg");
		
		backgroundColors[0] = parent.color(39, 5, 100); // paper beige
		backgroundColors[1] = parent.color(204, 100, 25); // night blue //color(306, 100, 25); // maroon
		backgroundColors[2] = parent.color(0, 0, 0); // black //color(204, 100, 25); // night blue
		backgroundColors[3] = parent.color(0, 0, 0); // black
		backgroundColors[4] = parent.color(0, 0, 0);
		backgroundColors[5] = parent.color(0, 0, 0);
	}

	public static SceneManager instance() {
		return m;
	}
	
	public void update(int millis) {

		parent.background(0, 0, 100);
		
		parent.camera(parent.width/2, parent.height/2, cameraZ, parent.width/2, parent.height/2, 0, 0, 1, 0);
	}
}
