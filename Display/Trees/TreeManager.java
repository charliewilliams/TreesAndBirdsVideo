package Display.Trees;
import Model.*;
import Util.*;
import processing.core.*;

public class TreeManager {

	private static TreeManager m;
	private PApplet parent;

	boolean drawingPitchClassTrees() {
		return true; // TODO query the section for the current millis to decide
	}
	TreeStack[] pitchClassTrees = new TreeStack[12];
	TreeStack[] perPitchTrees = new TreeStack[88];

	public TreeManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
	}
	

	public void setParent(PApplet p) {
		parent = p;
	}

	public static TreeManager instance() {

		return m;
	}

	public void addNote(Note n, boolean b) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect

		// Two arrays of trees: one per pitch class, one per raw pitch. (We only ever show one of these at a time)
		int i = n.pitch % 12;
		TreeStack pitchClassTreeStack = pitchClassTrees[i];

		if (pitchClassTreeStack == null) {
			
			float eachTreeSpace = parent.width / (pitchClassTrees.length + 1);
			PVector pos = new PVector(eachTreeSpace * i, parent.height * 0.75f);
			
			// TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, float noiseOffset)
			pitchClassTrees[n.pitch % 12] = new TreeStack((int)Util.random(3, 8), parent, n, i, pos);
			
		} else {
			
			pitchClassTreeStack.grow(false, false);
		}

//		Tree perPitchTree = perPitchTrees[n.channel];
//
//		if (perPitchTree == null) {
//			perPitchTrees[n.pitch] = new Tree(parent, n, false);
//		} else {
//			perPitchTree.grow(false, false);
//		}
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
