package Display.Trees;
//import processing.core.*;
//import Model.Note;

//public class ColonisingTree extends Tree {
//
//	static int leafCount = 100;
//	static int treeHeight = 400;
//
//	ColonisingTree(PApplet parent, Note n, boolean isPitchClass) {
//		super(parent, n, isPitchClass);
//
//		float thisTreeHeight = random(0, treeHeight) + treeHeight;
//
//		for (int i = 0; i < leafCount; i++) {
//			PVector center = new PVector(rootPosition.x, rootPosition.y - thisTreeHeight);
//			leaves.add(new Leaf(center, 350));
//		}
//
//		Branch root = new Branch(rootPosition, new PVector(0, -1));
//		branches.add(root);
//
//		Branch current = new Branch(root);
//
//		int tries = 0;
//		while (!closeEnough(current) && tries++ < 1024) {
//
//			Branch trunkExtension = new Branch(current);
//			current.finished = true;
//			branches.add(trunkExtension);
//			current = trunkExtension;
//		}
//	}
//
//}
