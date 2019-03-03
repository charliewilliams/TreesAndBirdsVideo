package Model;

import Display.ChannelMapping;
import Display.Stars;
import Display.Birds.BirdManager;
import Display.Trees.TreeManager;
import Util.Util;
import processing.core.PApplet;

public class NoteManager {

	/*
	 * NoteManager's job is to know where in the song we are and therefore what
	 * kind of NoteDisplay to make when handed a note
	 */

	private PApplet		parent;
	private JSONReader	jsonReader;

	private int[]	currentPosition			= { -1, -1, -1 };				// phrase, bar, beat. -1 because the first thing is a downbeat
	private int		beatIndex				= currentPosition.length - 1;
	private int		barIndex				= beatIndex - 1;
	boolean			isSustaining			= false;
	private int		readAheadAmountMillis	= 0;
	private boolean	isStarRender;

	public NoteManager(PApplet parent, String fileName, boolean isStarRender) {

		this.parent = parent;
		this.isStarRender = isStarRender;
		jsonReader = new JSONReader(parent, fileName);
	}

	public void readNotes(int millis, Section section) {
		jsonReader.readNotes(this, millis + readAheadAmountMillis, section);
	}

	float pctDoneCurrentPhrase() {

		float amountThroughCurrentBar = currentPosition[1] / 4.0f + currentPosition[2] / 16.0f;
		float currentSectionLength = Section.forMillis(parent.millis()).length();
		float amountThroughCurrentSection = currentPosition[0] / currentSectionLength;

		return amountThroughCurrentSection + amountThroughCurrentBar;
	}

	private float	lastBeatMillis		= 1.0f;
	private float	lastBeatDuration	= 1.0f;

	float pctThroughBeat() {

		float elapsed = parent.millis() - lastBeatMillis;
		return (float) Math.min(Math.max(elapsed / lastBeatDuration, 0.0f), 1.0f);
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

	int		birdCount	= 0;
	boolean	fromRight	= false;

	void displayForNote(Note note, int millis, Section section) {

		switch (ChannelMapping.fromInt(note.channel)) {

		case TreeGrowth:
		case TreeGrowth2:

			// Special per-section behaviour
			switch (section) {
			case preroll:
			case start:
				TreeManager.instance().addNote(note, millis);
			case melodyStart:
				TreeManager.instance().addNote(note, millis, false);
				break;
			case risingMel:
				TreeManager.instance().addNote(note, millis, false); // false == shouldGlow
				TreeManager.instance().addLeafOrFlower(note, true); // true == isLeaf
				break;
			case repeatedNotes:
				TreeManager.instance().addNote(note, millis, false); // false == shouldGlow
				TreeManager.instance().addLeafOrFlower(note, Util.coinToss()); // leaf OR flower
				break;
			case bigReturn:
				TreeManager.instance().addNote(note, millis);
				TreeManager.instance().dropFlower(note);
				TreeManager.instance().turnLeafColorTick(millis);
				break;
			case highMel:
				TreeManager.instance().dropAllFlowers();
				TreeManager.instance().dropLeaf(note);
				TreeManager.instance().dropLeaf(note);
				TreeManager.instance().turnLeafColorTick(millis);
				break;
			case outro:
				
				TreeManager.instance().dropAllLeaves(millis);
			case end:
				if (isStarRender) {
					Stars.goOutTick();
				}
				break;
			}

			break;

		case Melody1:
		case Melody2:

			// Special per-section behaviour
			switch (section) {
			case preroll:
			case start:
			case melodyStart:
				BirdManager.instance().addNote(note, fromRight, millis);
				break;
			case risingMel:
				TreeManager.instance().addNote(note, millis, false);
				TreeManager.instance().addLeafOrFlower(note, true); // true == isLeaf
				break;
			case repeatedNotes:
				birdCount = 0;
				TreeManager.instance().addNote(note, millis, false);
				TreeManager.instance().addLeafOrFlower(note, Util.coinToss());
				break;
			case bigReturn:
				for (int i = 0; i < Util.random(5, 9); i++) {
					boolean startLandingTimer = false;
					BirdManager.instance().addNote(note, fromRight, millis, startLandingTimer);
				}
				
				BirdManager.instance().flyAwayAllBirds(note, millis);
				
				TreeManager.instance().addNote(note, millis, false);
				TreeManager.instance().dropFlower(note);
				break;
			case highMel:
				birdCount = -1;
				BirdManager.instance().getBirdsOffstage(note);
				if (isStarRender) {
					Stars.addStar(note);
				}
				TreeManager.instance().dropFlower(note);
				TreeManager.instance().dropLeaf(note);
				TreeManager.instance().dropLeaf(note);
				TreeManager.instance().dropLeaf(note);
				break;
			case outro:
			case end:
				TreeManager.instance().dropLeaf(note);
				BirdManager.instance().addNote(note, fromRight, millis, 4); // slower than normal
				break;
			}

			if (birdCount >= 11) {
				fromRight = !fromRight;
			}
			if (birdCount < 11) {
				fromRight = true;
			}
			if (birdCount < 8) {
				fromRight = false;
			}
			if (birdCount < 5) {
				fromRight = true;
			}
			if (birdCount < 2) {
				fromRight = false;
			}

			birdCount++;

			break;

		case TreeChangeBass:

			// Special per-section behaviour
			switch (section) {
			case preroll:
			case start:
				TreeManager.instance().addNote(note, millis, true);
			case melodyStart:
				TreeManager.instance().addNote(note, millis, false);
				TreeManager.instance().addLeafOrFlower(note, true); // true == isLeaf
				break;
			case repeatedNotes:
				TreeManager.instance().addNote(note, millis, false);
				TreeManager.instance().addLeafOrFlower(note, true); // true == isLeaf
				TreeManager.instance().glowRoot(note);
			case risingMel:
				break;

			case bigReturn:
			case highMel:
				TreeManager.instance().dropFlower(note);
				TreeManager.instance().glowRoot(note);
				break;

			case outro:
				TreeManager.instance().dropAllFlowers();
			case end:
				TreeManager.instance().dropLeaf(note);
				TreeManager.instance().dropLeaf(note);
				break;
			}
			break;
		case TreeChangeMelody:
			TreeManager.instance().addNote(note, millis);
			TreeManager.instance().addLeafOrFlower(note, false); // false == flower
			break;

		default:
			break;
		}

	}

	public String locationString() {
		Section s = Section.forMillis(parent.millis());
		return String.format("%s: %d.%d.%d.%d", s.name(), s.ordinal(), currentPosition[0], currentPosition[1],
				currentPosition[2]);
	}
}
