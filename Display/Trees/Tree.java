package Display.Trees;
import Model.Note;
import processing.core.*;
import java.util.*;

/*



interface Tree {

  void grow();
  void show();
}
 * */

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

	class Param {

		//param.rand_tree_angle = new Random(0);
		//param.rand_tree_length = new Random(0);
		public Random rand_tree_angle  = new Random(1);
		public Random rand_tree_length = new Random(10);
		public Random rand_leaf        = new Random(0);

		public float ANGLE_LIMIT_RANGE = (float) (Math.PI/5.0);
		public int   DEPTH_LIMIT_MAX   = 15;
		public int   LENGTH_LIMIT_MIN  = 8;
		public int   LENGTH_LIMIT_MAX  = 120;

		public float LENGTH_MULT_BASE  = 0.76f;
		public float LENGTH_MULT_RANGE = 0.22f;

		public float LEAF_CHANCE  = 0.1f;
	}

	class Style {

		public float   BRANCH_STROKEWIDTH = 15;

		public float   LEAF_RADIUS  = 20;
		public float[] LEAF_RGBA  = {255, 128, 0, 150};
	}
}
