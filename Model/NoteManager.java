package Model;
import Display.*;
import processing.core.*;

public class NoteManager {

	/* NoteManager's job is to know where in the song we are
	 and therefore what kind of NoteDisplay to make when handed a note */

	private PApplet parent;
	private JSONReader jsonReader;
	private TreeManager treeManager;
	private BirdManager birdManager;
	// Sections: first bit, rising minor, glowing major, big return, high melodies, recap
	private int[] sectionLengths = {15, 8, 12, 5, 8, 7}; // sections are (mostly) 4-bar chunks
	int currentSection = 0;
	private static Section[] sectionVals = Section.values();
	Section section() {
		return sectionVals[currentSection];
	}
	private int[] currentPosition = {-1, -1, -1}; // phrase, bar, beat. -1 because the first thing is a downbeat
	private int beatIndex = currentPosition.length - 1;
	private int barIndex = beatIndex - 1;
	boolean isSustaining = false;
	private int readAheadAmountMillis = 500;

	public NoteManager(PApplet parent, String fileName) {

		this.parent = parent;
		jsonReader = new JSONReader(parent, fileName);
	}

	public void readNotes(int millis) {

		for (; jsonReader.currentTimestampIndex < jsonReader.timestamps.length; jsonReader.currentTimestampIndex++) {

			Float timestamp = jsonReader.timestamps[jsonReader.currentTimestampIndex];

			// Don't read beyond the present instant
			if (millis < timestamp * 1000 + readAheadAmountMillis) {
				//println(millis(), "Waiting for", timestamp);
				break; // doesn't increment currentTimestampIndex
			}

			Note note = jsonReader.notes.get(timestamp);
			if (note != null) {
				displayForNote(note);
			}
			Pedal pedal = jsonReader.pedals.get(timestamp);
			if (pedal != null) {
				isSustaining = pedal.level > 0;
			}
			Ictus beat = jsonReader.structure.get(timestamp);
			if (beat != null) {
				controlEvent(beat);
			}
			if (note == null && pedal == null && beat == null) {
				PApplet.println("ERROR", timestamp);
				parent.exit();
			}
		}
	}

	float pctDoneCurrentSection() {

		float amountThroughCurrentBar = currentPosition[1] / 4.0f + currentPosition[2] / 16.0f;
		float currentSectionLength = (float)sectionLengths[currentSection];
		float amountThroughCurrentSection = currentPosition[0] / currentSectionLength;
		//println(amountThroughCurrentSection + amountThroughCurrentBar / 16.0);
		return amountThroughCurrentSection + amountThroughCurrentBar / 16.0f;
	}

	float pctDoneCurrentPhrase() {

		float amountThroughCurrentBar = currentPosition[1] / 4.0f + currentPosition[2] / 16.0f;
		float currentSectionLength = (float)sectionLengths[currentSection];
		float amountThroughCurrentSection = currentPosition[0] / currentSectionLength;
		//println(amountThroughCurrentSection + amountThroughCurrentBar);
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

	float lastPct = 0;
	float sectionEasing() {

		// provides a smooth change through the last bar of a section to the next

		// If not in the last bar, return 0
		if (currentPosition[0] == -1 || currentPosition[0] != sectionLengths[currentSection] - 1 || currentPosition[barIndex] < 3) {
			return 0;
		}

		return (currentPosition[beatIndex] + pctThroughBeat()) / 4.0f;
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

		// Go to the next section if necessary
		if (currentPosition[0] >= sectionLengths[currentSection]) {
			currentPosition[0] = 0;
			currentSection++;
		}
	}

	void displayForNote(Note note) {
		
		switch (ChannelMapping.fromInt(note.channel)) {
		
		case TreeGrowth:
		case TreeGrowth2:
			treeManager.addNote(note);
			break;
			
		case Bird:
		case Bird2:
			birdManager.addNote(note);
			break;
			
		case TreeChangeBass:
		case TreeChangeMelody:
			treeManager.addChangeNote(note);
			break;
			
		case Click: // This will never happen
		default:
			break;
		}

	}

	public String locationString() {
		return String.format("%d.%d.%d.%d", currentSection, currentPosition[0], currentPosition[1], currentPosition[2]);
	}
}
