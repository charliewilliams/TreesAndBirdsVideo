package Display.Trees;
import Model.*;
import processing.core.*;

public class TreeManager {

	private static TreeManager m;
	private PApplet parent;

	boolean drawingPitchClassTrees() {
		return true; // TODO query the section for the current millis to decide
	}
	Tree[] pitchClassTrees = new Tree[12];
	Tree[] perPitchTrees = new Tree[88];

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
		Tree pitchClassTree = pitchClassTrees[n.pitch % 12];

		if (pitchClassTree == null) {
			pitchClassTrees[n.pitch % 12] = new Tree(parent, n, true);
		} else {
			pitchClassTree.grow();
		}

		Tree perPitchTree = perPitchTrees[n.channel];

		if (perPitchTree == null) {
			perPitchTrees[n.pitch] = new Tree(parent, n, false);
		} else {
			perPitchTree.grow();
		}
	}

	public void addChangeNote(Note n, boolean b) {

		// Notes are added ~500ms before they sound; use `timestamp` to determine when they should take visual effect

		// TODO
	}

	public void draw() {

		for (Tree t: (drawingPitchClassTrees() ? pitchClassTrees : perPitchTrees)) {
			if (t != null) {
				t.draw();
			}
		}
	}
}
