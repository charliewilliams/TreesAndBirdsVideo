package Model;
//import static java.lang.Math.pow;

public class Note {
	
	Float startMillis;
	boolean isActive;
	public boolean isSustaining;
	int noteNumber;
	public int pitch;
	private String name;
	public String pitchClass;
	public float velocity; // normalized 0-1
	float duration; // seconds
//	private float freq;
	public int channel;
	
	public Note(int num) {
		this(0f, num, 0f, 0f, 16);
	}

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
	}
	
	public boolean isBlackKey() {
		return pitchClass.length() > 1;
	}

	void end() {
		isActive = false;
	}

	String description() {
		return name + " " + pitch + " " + velocity + " active: " + isActive;
	}

	/* Utility */

	private String noteNameFromNumber(int num, boolean withOctave) {
		int  nNote = num % 12;
		int  nOctave = num / 12;
		return withOctave ? noteNames[nNote] + (nOctave - 1) : noteNames[nNote];
	}

//	private float midiToFreq(int note) {
//		return (float) ((pow(2, ((note - 69) / 12.0f))) * 440.0f);
//	}
	
	private static String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
}
