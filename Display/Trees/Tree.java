package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

public class Tree {

	private PApplet parent;
	int idx;
	PVector pos;
	static float zVariance = 100;
	ArrayList<Branch> branches = new ArrayList<Branch>();
	
	Tree(PApplet parent, Note n, boolean isPitchClass) {
		
		this.parent = parent;
		
		pos = new PVector(parent.width / 13.0f, 0, n.velocity * zVariance);
		this.idx = isPitchClass ? n.pitch % 12 : n.pitch;
	}
	
	void grow() {
		
		
	}
	
	void update() {
		
	}
	
	void draw() {
		
	}
}
