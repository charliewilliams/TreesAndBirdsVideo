package Display;
import processing.core.*;
import java.util.*;

import Model.Note;

public class BirdManager {

	private static BirdManager m;
	private PApplet parent;
	ArrayList<Object> birds = new ArrayList<Object>(); // Type to Tree

	public BirdManager(PApplet parent) {

		if (m != null) {
			// SHOUT
			throw new IllegalStateException("Already instantiated");
		}
		m = this;
		this.parent = parent;
	}

	public static BirdManager instance() {
		return m;
	}
	
	public void addNote(Note n) {
		
		
	}
}
