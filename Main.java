import processing.core.*;
import processing.opengl.PGraphics2D;
import processing.sound.*;
import Display.*;
import Display.Birds.BirdManager;
import Display.Trees.TreeManager;
import Model.*;

public class Main extends PApplet {

	boolean	renderVideo				= false;
	boolean	renderGlow				= false;
	boolean	playMusic				= false;
	int		_frameRate				= 30;
	int		prerollMillis			= renderVideo ? 10000 : 0;
	int		moveAudioEarlierMillis	= 4800;
	int		totalFrames;

	public static void main(String[] args) {

		PApplet.main("Main");
	}

	NoteManager		noteManager;
	SceneManager	sceneManager;
	SoundFile		file;

	// VARIOUS NAMED STARTING OFFSETS - NOT TO BE USED AS SECTION DEFINITIONS!
	int	musicStart		= 10000;
	int	melodyStart		= 38000;
	int	risingMel		= 104000;
	int	repeatedNotes	= 170000;
	int	bigReturn		= 251000;
	int	highMel			= 290000;
	int	outro			= 310000;
	int	end				= 350000;

	Section section = Section.preroll;

	int millisOffset = 500;
	//	int	debugOffsetMillis	= 0;
	//		int debugOffsetMillis = melodyStart;
	//	int	debugOffsetMillis = risingMel;
		int debugOffsetMillis = repeatedNotes;
//	int debugOffsetMillis = bigReturn;
	//	int debugOffsetMillis = highMel;
	int durationMillis;

	PGraphics2D grass;

	public void settings() {

		size(1692, 720, P2D); // P2D, P3D, FX2D
		// size(2538, 1080, P2D);

	}

	public void setup() {

		randomSeed(0);
		frameRate(60);
		rectMode(CENTER);
		ellipseMode(CENTER);
		colorMode(HSB, 360, 100, 100, 100);

		background(0, 0, 51);
		noStroke();

		Glow.setupGlow(this);
		sceneManager = new SceneManager(this, debugOffsetMillis);
		new TreeManager(this);
		TreeManager.instance().renderGlow = renderGlow;
		new BirdManager(this);
		sceneManager.createGrass();

		noteManager = new NoteManager(this, "song.json");

		file = new SoundFile(this, "mix.mp3");
		durationMillis = (int) (file.duration() * 1000);

		if (playMusic) {
			file.jump((debugOffsetMillis + moveAudioEarlierMillis) / 1000.0f);
			file.play();
		}

		if (renderVideo) {
			float offsetSecs = (prerollMillis * 2 + moveAudioEarlierMillis) / 1000;
			totalFrames = (int) ((file.duration() + offsetSecs) * _frameRate);
			println("Rendering", totalFrames, "frames.");
		}

		if (debugOffsetMillis >= risingMel) {
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

		// Draw the background
		sceneManager.update(millis);

		// Read notes from JSON in memory; add to managers if there are new notes this tick
		noteManager.readNotes(millis, section);

		// Special per-section behaviour
		switch (section) {
		case preroll:
		case start:
		case melodyStart:
			break;
		case risingMel:
			BirdManager.instance().landAllBirds();
			break;
		case repeatedNotes:
			break;
		case bigReturn:
			BirdManager.instance().flyAwayAllBirds(millis);
			break;
		case highMel:
			break;
		case outro:
		case end:
			BirdManager.instance().landAllBirds();
			break;
		}

		TreeManager.instance().updateRender(millis);
		TreeManager.instance().drawTrees();
		BirdManager.instance().updateAndDraw(millis);
		TreeManager.instance().drawOverlay();

		int seconds = millis / 1000;
		int minutes = seconds / 60;
		int displaySeconds = minutes > 0 ? seconds % 60 : seconds;

		String txt_fps = String.format(" | %s | %02d:%02d | %2.0f fps", noteManager.locationString(), minutes,
				displaySeconds, frameRate);
		surface.setTitle(txt_fps);

		if (renderVideo) {
			saveFrame("temp/#####.png");

			if (frameCount > totalFrames) {
				exit();
			}
		}

		fill(0);
		if (section.length() == 0) {
			exit();
		}
		text("section " + section.ordinal() + " (" + section + ") – " + (int) (section.length() / 1000) + "s long – "
				+ (int) (section.pctDone(millis) * 100) + "% done", 40, height - 40);
	}

	void checkSection(int millis) {

		Section s = Section.forMillis(millis);

		if (section != s) {
			section = s;
		}
	}
}
