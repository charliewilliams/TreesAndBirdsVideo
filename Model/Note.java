package Model;

public class Note {

	Float			startMillis;
	boolean			isActive;
	public boolean	isSustaining;
	int				noteNumber;
	public int		pitch;
	private String	name;
	public String	pitchClass;
	public float	velocity;		// normalized 0-1
	public float	duration;		// seconds
	//	private float freq;
	public int channel;

	public Note(int num) {
		this(0f, num, 0f, 0f, 16);
	}

//	static boolean[] usedNotes = new boolean[12];
	
	Note(Float startMillis, int num, Float vel, Float dur, int chan) {

		this.startMillis = startMillis;
		isActive = false;
		noteNumber = num;
		pitch = num;
		velocity = vel;
		duration = dur;
		//		freq = midiToFreq(num);
		name = noteNameFromNumber(num, true);
		pitchClass = noteNameFromNumber(num, false);
		channel = chan;
		
		//
//		int idx = num % 12;
//		boolean used = usedNotes[idx];
//		if (!used) {
//			PApplet.println("First use of", pitchClass, idx, chan, startMillis);
//			usedNotes[idx] = true;
//		}
	}

	public boolean isBlackKey() {
		return pitchClass.length() > 1;
	}

	private int isRare = -1;

	public boolean isRare() {
		
		if (isRare == 1) { return true; }
		if (isRare == 0) { return false; }
		
		// A#, C, C#, D#, F, G, G#
		// These notes appear only once in the opening few sections
		// so their trees are just sticks. Let's give them a boost!
		
		String[] rareNotes = {"Bb", "C", "C#", "D#", "F", "G", "G#"};
		
		for (int i = 0; i < rareNotes.length; i++) {
			if (pitchClass.equals(rareNotes[i])) {				
				isRare = 1;
				return true;
			} else {
//				PApplet.println(pitchClass);
			}
		}
		isRare = 0;
		return false;
	}

	void end() {
		isActive = false;
	}

	String description() {
		return name + " " + pitch + " " + velocity + " active: " + isActive;
	}

	/* Utility */

	private String noteNameFromNumber(int num, boolean withOctave) {
		int nNote = num % 12;
		int nOctave = num / 12;
		return withOctave ? noteNames[nNote] + (nOctave - 1) : noteNames[nNote];
	}

	//	private float midiToFreq(int note) {
	//		return (float) ((pow(2, ((note - 69) / 12.0f))) * 440.0f);
	//	}

	private static String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "Bb", "B" };
}
