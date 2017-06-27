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
		


//				parent.camera(parent.width/2, parent.height/2, cameraZ, parent.width/2, parent.height/2, 0, 0, 1, 0);
		
		parent.directionalLight(255, 255, 255, 0, 1, -100); 
		parent.noFill();
		parent.stroke(0);
		
//		parent.beginCamera();
//		parent.camera();
//
////		parent.rotateX(parent.frameCount / 100.0f);
//		parent.rotateX(4.7f);
//		parent.rotateY(6.28347f);
//
//		parent.translate(22, 17, -201);
//		
////		parent.translate(0, 0, -800);
//		parent.endCamera();
//		
		parent.colorMode(PConstants.RGB, 255);
		
		parent.stroke(255, 50, 50);

		parent.line(0, 0, 300, 0, parent.height, 300);
		parent.line(0, 0, 900, 0, parent.height, 900);
		parent.line(0, 0, 300, parent.width, 0, 300);
		parent.line(0, 0, 900, parent.width, 0, 900);
		
		parent.stroke(50, 255, 50);

		parent.line(parent.width, 0, 300, parent.width, parent.height, 300);
		parent.line(parent.width, 0, 900, parent.width, parent.height, 900);
		parent.line(0, parent.height, 300, parent.width, parent.height, 300);
		parent.line(0, parent.height, 900, parent.width, parent.height, 900);
		
		parent.stroke(50, 50, 255);

		parent.line(0, 0, 300, 0, 0, 900);
		parent.line(0, parent.height, 300, 0, parent.height, 900);
		parent.line(parent.width, 0, 300, parent.width, 0, 900);
		parent.line(parent.width, parent.height, 300, parent.width, parent.height, 900);
		
		
		
//		parent.fill(0, 0, 0);
//		parent.translate(parent.width / 2 - 22, -100, 400);
//		parent.box(50);
	}
}
