package Display;
import Model.*;
import processing.core.*;
import java.util.*;

public class TreeManager {

	private static TreeManager m;
	private PApplet parent;
	ArrayList<Object> trees = new ArrayList<Object>(); // Type to Tree

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
		
	
	}
	
	public void addChangeNote(Note n, boolean b) {
		
		
	}
}
