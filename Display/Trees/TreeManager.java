package Display.Trees;
import Model.*;
import Util.*;
import processing.core.*;

public class TreeManager {

	private static TreeManager m;
	public static TreeManager instance() { return m; }
	
	private PApplet parent;

	TreeStack[] pitchClassTrees = new TreeStack[12];

	public TreeManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
	}

	public void addNote(Note n, boolean b) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect

		// Two arrays of trees: one per pitch class, one per raw pitch. (We only ever show one of these at a time)
		int i = n.pitch % 12;
		TreeStack pitchClassTreeStack = pitchClassTrees[i];

		if (pitchClassTreeStack == null) {
			
			float eachTreeSpace = parent.width / (pitchClassTrees.length + 2);
			PVector pos = new PVector(eachTreeSpace * (i + 1), parent.height * 0.85f + Util.random(-20, 20));
			int numChildren = (int)Util.random(3, 8);
			
			// TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, float noiseOffset)
			pitchClassTrees[n.pitch % 12] = new TreeStack(numChildren, parent, n, i, pos);
			
		} else {
			
			pitchClassTreeStack.grow();
		}
	}

	public void addChangeNote(Note n, boolean b) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect

		// TODO
	}

	public void draw() {
		
		for (int i = 0; i < pitchClassTrees.length; i++) {
			
			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.draw();
			}
		}
	}
}
