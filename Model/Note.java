package Model;
//import static java.lang.Math.pow;

public class Note extends Event {

	Note(Float startMillis, int num, Float vel, Float dur, int chan) {

		super(startMillis);
		isActive = false;
		noteNumber = num;
		pitch = num;
		velocity = vel;
		duration = dur;
//		freq = midiToFreq(num);
		name = noteNameFromNumber(num);
		channel = chan;
	}

	void end() {
		isActive = false;
	}

	String description() {
		return name + " " + pitch + " " + velocity + " active: " + isActive;
	}

	/* Boring property boilerplate stuff */
	boolean isActive;
	public boolean isSustaining;
	int noteNumber;
	public int pitch;
	private String name;
	public float velocity; // normalized 0-1
	float duration; // seconds
//	private float freq;
	public int channel;

	/* Utility */

	private String noteNameFromNumber(int num) {
		int  nNote = num % 12;
		int  nOctave = num / 12;
		return noteNames[nNote] + (nOctave - 1);
	}

//	private float midiToFreq(int note) {
//		return (float) ((pow(2, ((note - 69) / 12.0f))) * 440.0f);
//	}
	
	private static String[] noteNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};
}
