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

		int i = treeIndexForNote(n);
		TreeStack pitchClassTreeStack = pitchClassTrees[i];

		if (pitchClassTreeStack == null) {

			int numChildren = 1; //(int) Util.random(3, 8);

			pitchClassTrees[i] = new TreeStack(numChildren, parent, n, idx, treePositionForNote(n));

			idx += numChildren;
		}

		return pitchClassTrees[i];
	}

	public int treeIndexForNote(Note n) {
		//		// Offset so that E is the lowest pitch
		//		int offset = 8;
		// Offset so that A is the lowest pitch
		int offset = 3;
		return (n.pitch + offset) % 12;
	}

	public PVector treePositionForNote(Note n) {

		int i = treeIndexForNote(n);
		float eachTreeSpace = parent.width / (pitchClassTrees.length + 2);
		float yOffset = n.isBlackKey() ? 55 : 0;
		return new PVector(eachTreeSpace * (i + 2), parent.height * 0.8f + Util.randomf(-10, 10) - yOffset);
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

	public void addLeafOrFlower(Note n, boolean shouldBeLeaf) {

		if (shouldBeLeaf) {
			treeStackFor(n).addLeaf();
		} else {
			treeStackFor(n).addFlower();
		}
	}
	
	public void dropLeaf(Note n) {
		
		int tries = 0;
		int maxTries = 50;
		
		while (true) {			
			if (treeStackFor(n).dropLeaf(n)) {
				return;
			}
			n = new Note((int)Util.random(0, 12));
			if (tries++ > maxTries) {
				return;
			}
		}
	}

	public void updateRender(int millis) {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.updateRender(millis);
			}
		}
	}
	
	public void drawTrees() {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.drawBack();
			}
		}
	}
	
	public void drawOverlay() {

		for (int i = 0; i < pitchClassTrees.length; i++) {

			TreeStack stack = pitchClassTrees[i];
			if (stack != null) {
				stack.drawFront();
			}
		}
	}
}
