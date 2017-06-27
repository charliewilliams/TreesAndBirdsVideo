package Model;
import Display.*;
import Display.Birds.BirdManager;
import Display.Trees.TreeManager;
import processing.core.*;

public class NoteManager {

	/* NoteManager's job is to know where in the song we are
	 and therefore what kind of NoteDisplay to make when handed a note */

	private PApplet parent;
	private JSONReader jsonReader;

	private int[] currentPosition = {-1, -1, -1}; // phrase, bar, beat. -1 because the first thing is a downbeat
	private int beatIndex = currentPosition.length - 1;
	private int barIndex = beatIndex - 1;
	boolean isSustaining = false;
	private int readAheadAmountMillis = 0;

	public NoteManager(PApplet parent, String fileName) {

		this.parent = parent;
		jsonReader = new JSONReader(parent, fileName);
	}
	
	public void readNotes(int millis) {
		jsonReader.readNotes(this, millis + readAheadAmountMillis);
	}

	float pctDoneCurrentPhrase() {

		float amountThroughCurrentBar = currentPosition[1] / 4.0f + currentPosition[2] / 16.0f;
		float currentSectionLength = Section.forMillis(parent.millis()).length();
		float amountThroughCurrentSection = currentPosition[0] / currentSectionLength;
		
		return amountThroughCurrentSection + amountThroughCurrentBar;
	}

	private float lastBeatMillis = 1.0f;
	private float lastBeatDuration = 1.0f;

	float pctThroughBeat() {

		float elapsed = parent.millis() - lastBeatMillis;
		return (float)Math.min(Math.max(elapsed/lastBeatDuration, 0.0f), 1.0f);
	}

	float phraseEasing() {

		float pctThroughBar = (currentPosition[beatIndex] + pctThroughBeat()) / 4.0f;
		float pct = (currentPosition[barIndex] + pctThroughBar) / 4.0f;
		return (float) Math.sin(Math.toRadians(pct * 360 / 2.0f));
	}

	// Notes on Channel 16 tell us phrase-bar-beat info, so we get those separately
	void controlEvent(Ictus ictus) {

		// First, increment whichever of section, phrase, bar, beat this note goes with
		switch (ictus) {
		case BEAT:
			
			currentPosition[beatIndex]++;

			float millis = parent.millis();
			lastBeatDuration = millis - lastBeatMillis;
			lastBeatMillis = millis;
			break;

		case BAR:
			
			currentPosition[barIndex]++;
			currentPosition[beatIndex] = 0;
			break;

		case PHRASE:
			
			currentPosition[0]++;
			currentPosition[barIndex] = 0;
			currentPosition[beatIndex] = 0;
			break;
		}
	}

	void displayForNote(Note note) {
		
		switch (ChannelMapping.fromInt(note.channel)) {
		
		case TreeGrowth:
			TreeManager.instance().addNote(note, true);
			break;
		case TreeGrowth2:
			TreeManager.instance().addNote(note, false);
			break;
			
		case Bird:
//			PApplet.println("Bird");
			BirdManager.instance().addNote(note, true);
			break;
		case Bird2:
//			PApplet.println("Bird2");
			BirdManager.instance().addNote(note, false);
			break;
			
		case TreeChangeBass:
			TreeManager.instance().addChangeNote(note, true);
			break;
		case TreeChangeMelody:
			TreeManager.instance().addChangeNote(note, false);
			break;
			
		case Click: // This will never happen
		default:
			break;
		}

	}

	public String locationString() {
		Section s = Section.forMillis(parent.millis());
		return String.format("%s: %d.%d.%d.%d", s.name(), s.ordinal(), currentPosition[0], currentPosition[1], currentPosition[2]);
	}
}
