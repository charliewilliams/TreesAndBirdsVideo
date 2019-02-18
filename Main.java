import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import Display.Glow;
import Display.SceneManager;
import Display.Stars;
import Display.Birds.BirdManager;
import Display.Trees.Snow;
import Display.Trees.TreeManager;
import Model.NoteManager;
import Model.Section;
import processing.core.PApplet;
import processing.core.PFont;
import processing.sound.SoundFile;

public class Main extends PApplet {

	boolean		renderVideo				= true;
	boolean		renderGlow				= renderVideo;
	boolean		playMusic				= true;
	boolean		isStarRender			= true;
	int			_frameRate				= 30;
	int			prerollMillis			= renderVideo ? 10000 : 0;
	int			moveAudioEarlierMillis	= 4800;
	int			totalFrames;
	DwPixelFlow	context;
	DwFilter	filter;

	public static void main(String[] args) {

		PApplet.main("Main");
	}

	NoteManager		noteManager;
	SceneManager	sceneManager;
	SoundFile		file;
	PFont			labelFont;

	// VARIOUS NAMED STARTING OFFSETS - NOT TO BE USED AS SECTION DEFINITIONS!
	int	musicStart		= 10000;
	int	melodyStart		= 38000;
	int	risingMel		= 104000;
	int	repeatedNotes	= 170000;
	int	bigReturnMinus	= 240000;
	int	bigReturn		= 251000;
	int	highMel			= 290000;
	int	outro			= 365000;
	int	end				= 392000;

	Section section = Section.preroll;

	int	millisOffset		= 500;
	int	debugOffsetMillis	= 0;
	//		int debugOffsetMillis = melodyStart;
	//	int debugOffsetMillis = risingMel;
	//			int debugOffsetMillis = repeatedNotes;
	//			int debugOffsetMillis = bigReturnMinus;
	//	int debugOffsetMillis = bigReturn;
	//	int	debugOffsetMillis	= highMel;
	//		int	debugOffsetMillis	= outro;
	int durationMillis;

	public void settings() {

		size(1692, 720, P2D);
		//		 size(2538, 1080, P2D);
	}

	public void setup() {

		randomSeed(0);
		frameRate(60);
		rectMode(CENTER);
		ellipseMode(CENTER);
		colorMode(HSB, 360, 100, 100, 100);

		background(0);
		noStroke();

		context = new DwPixelFlow(this);
		filter = new DwFilter(context);

		labelFont = createFont("EBGaramond-SemiBold.ttf", 18);
		PApplet.println("FONT: " + labelFont.getName());

		Glow.setupGlow(this, filter);
		sceneManager = new SceneManager(this, debugOffsetMillis);
		new TreeManager(this, labelFont);
		TreeManager.instance().renderGlow = renderGlow;
		new BirdManager(this);
		Snow.setupSnow(this);

		if (isStarRender) {
			Stars.setupGlow(this, filter);
		}

		noteManager = new NoteManager(this, "song.json", isStarRender);

		file = new SoundFile(this, "mix.mp3");
		durationMillis = (int) (file.duration() * 1000);

		if (playMusic && !renderVideo) {
			file.jump((debugOffsetMillis + moveAudioEarlierMillis) / 1000.0f);
			file.play();
		}

		if (renderVideo) {
			float offsetSecs = (prerollMillis * 2 + moveAudioEarlierMillis) / 1000;
			totalFrames = (int) ((file.duration() + offsetSecs) * _frameRate);
			println("Rendering", totalFrames, "frames.");
		}

		if (debugOffsetMillis >= risingMel && debugOffsetMillis < outro) {
			BirdManager.instance().buildDebugBirds();
		}

		millisOffset += super.millis();
	}

	int _millis() {

		if (renderVideo) {
			// Don't base this off millis at all, you'll drop frames.
			return frameCount * 1000 / _frameRate + debugOffsetMillis - prerollMillis;
		} else {
			return millis() + debugOffsetMillis - prerollMillis;
		}
	}

	public void draw() {

		int millis = _millis();

		checkSection(millis);

		// Read notes from JSON in memory; add to managers if there are new notes this tick
		noteManager.readNotes(millis, section);

		if (isStarRender) {
			//			background(0);
			Stars.renderStars(millis, this);
		} else {
			// Draw the background
			sceneManager.update(millis);

			// Special per-section behaviour
			switch (section) {
			case preroll:
				Snow.addSnowTick();
				break;
			case start:
			case melodyStart:
				break;
			case risingMel:
				BirdManager.instance().landAllBirds();
				break;
			case repeatedNotes:
				BirdManager.instance().landAllBirds();
				break;
			case bigReturn:
				BirdManager.instance().flyAwayAllBirds(millis);
				break;
			case highMel:
				BirdManager.instance().cleanUpOffscreenBirds();
				break;
			case outro:
			case end:
				Snow.addSnowTick();
				BirdManager.instance().landAllBirds();
				break;
			}

			if (debugOffsetMillis == repeatedNotes || debugOffsetMillis == bigReturnMinus) {
				TreeManager.instance().buildDebugLeaves();
				debugOffsetMillis += 1;
			}

			TreeManager.instance().updateRender(millis);
			TreeManager.instance().drawTrees(millis);
			BirdManager.instance().updateAndDraw(millis);
			TreeManager.instance().drawOverlay();

			Snow.render();
		}

		int seconds = millis / 1000;
		int minutes = seconds / 60;
		int displaySeconds = minutes > 0 ? seconds % 60 : seconds;

		String txt_fps = String.format(" | %s | %02d:%02d | %2.0f fps", noteManager.locationString(), minutes,
				displaySeconds, frameRate);
		surface.setTitle(txt_fps);

		if (renderVideo) {
			saveFrame("video-export/#####.png");

			if (frameCount > totalFrames) {
				exit();
			}
		} else if (section.length() <= 1) {
			exit();
		}

		fill(0);

		if (showDebugText) {

			if (isStarRender) {
				println("frame " + frameCount + " / " + totalFrames + " frames. (" + (int)(100 * frameCount / totalFrames) + "%)" + millis + "ms / section " + section.ordinal() + " (" + section
						+ ") – " + (int) (section.length() / 1000) + "s long – " + (int) (section.pctDone(millis) * 100)
						+ "% done");
			} else {
				text("frame " + frameCount + " / " + totalFrames + " frames. (" + (int)(100 * frameCount / totalFrames) + "%)" + millis + "ms / section " + section.ordinal() + " (" + section
						+ ") – " + (int) (section.length() / 1000) + "s long – " + (int) (section.pctDone(millis) * 100)
						+ "% done", 40, height - 40);
			}
		}
	}

	boolean showDebugText = true;

	public void keyPressed() {
		if (key == ' ') {
			showDebugText = !showDebugText;
		} else {
			println(key);
		}
	}

	void checkSection(int millis) {

		Section s = Section.forMillis(millis);

		if (section != s) {
			section = s;
		}
	}
}
