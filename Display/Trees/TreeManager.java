package Display.Trees;

import Model.*;
import Util.*;
import processing.core.*;

public class TreeManager {

	private static TreeManager m;

	public static TreeManager instance() {
		return m;
	}

	private int idx = 0;

	private PApplet parent;

	private TreeStack[] pitchClassTrees = new TreeStack[12];

	public TreeStack treeStackFor(Note n) {

		// Offset so that e is the lowest pitch
		int offset = 8;
		int i = (n.pitch + offset) % 12;
		TreeStack pitchClassTreeStack = pitchClassTrees[i];

		if (pitchClassTreeStack == null) {
			
			float eachTreeSpace = parent.width / (pitchClassTrees.length + 2);
			float yOffset = n.isBlackKey() ? 55 : 0;
			PVector pos = new PVector(eachTreeSpace * (i + 2), parent.height * 0.8f + Util.randomf(-10, 10) - yOffset);
			int numChildren = (int) Util.random(3, 8);

			// TreeStack(int numChildren, PApplet parent, Note n, int baseIndex, float noiseOffset)
			pitchClassTrees[i] = new TreeStack(numChildren, parent, n, idx, pos);

			idx += numChildren;
		}

		return pitchClassTrees[i];
	}

	public TreeManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
	}

	public void addNote(Note n) {

		treeStackFor(n).grow(n);
	}

	public void addChangeNote(Note n, boolean shouldBeLeaf) {
		
//		if (shouldBeLeaf) {
			treeStackFor(n).addFlower();
//		}
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
