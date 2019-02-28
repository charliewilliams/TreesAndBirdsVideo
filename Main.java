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
import processing.core.PImage;
import processing.sound.SoundFile;

public class Main extends PApplet {

	boolean		renderVideo				= false;
	boolean		efficientRender			= true;
	boolean		renderStars				= false;
	boolean		renderSnow				= false;
	boolean		renderGlow				= false;
	boolean		playMusic				= false;
	boolean		isStarRender			= false;
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
//	int	debugOffsetMillis	= 0;
	//			int debugOffsetMillis = melodyStart;
	//	int debugOffsetMillis = risingMel;
	//	int debugOffsetMillis = repeatedNotes;
					int debugOffsetMillis = bigReturnMinus;
	//	int debugOffsetMillis = bigReturn;
	//	int	debugOffsetMillis	= highMel;
	//		int	debugOffsetMillis	= outro;
	int durationMillis;
	
	public static String renderer = P2D;

	public void settings() {

		size(1692, 720, renderer);
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

		file = new SoundFile(this, "mix.mp3");
		durationMillis = (int) (file.duration() * 1000);

		Glow.setupGlow(this, filter);
		new TreeManager(this, labelFont);
		TreeManager.instance().renderGlow = renderGlow;
		new BirdManager(this);
		Snow.setupSnow(this);

		if (isStarRender) {
			Stars.setupGlow(this, filter);
		}

		noteManager = new NoteManager(this, "song.json", isStarRender);

		if (playMusic && !renderVideo) {
			file.jump((debugOffsetMillis + moveAudioEarlierMillis) / 1000.0f);
			file.play();
		}

		float offsetSecs = (prerollMillis * 2 + moveAudioEarlierMillis) / 1000;
		totalFrames = (int) ((file.duration() + offsetSecs) * _frameRate);
		sceneManager = new SceneManager(this, totalFrames);

		if (renderVideo) {
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

		if (efficientRender) {
			clear();
			//			background(255);
		}
		
		if (!renderVideo) {
			background(255);
		}

		int millis = _millis();

		checkSection(millis);

		// Read notes from JSON in memory; add to managers if there are new notes this tick
		noteManager.readNotes(millis, section);

		// Draw the background
		if (!efficientRender) {
			sceneManager.update(millis);
		}

		// Special per-section behaviour
		switch (section) {
		case preroll:
		case start:
			if (renderSnow) {
				Snow.addSnowTick(millis);
			}
			break;

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
			Snow.addSnowTick(millis);
			break;

		case outro:
		case end:
			if (renderSnow) {
				Snow.addSnowTick(millis);
			}
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

		if (renderStars) {
			Stars.renderStars(millis, this, frameCount);

		}
		if (renderSnow) {
			Snow.render(frameCount);
		}

		String txt_fps = String.format("frame %d / %d (%2.0f%%) | %2.0f fps", frameCount, totalFrames, frameCount/(float)totalFrames, frameRate);
		surface.setTitle(txt_fps);

		if (renderVideo) {
			//			saveFrame("video-export/#####.png");
			saveTransparentFrame("output");
			
			clear();
			TreeManager.instance().drawGlow(this);
			
			saveTransparentFrame("glow");

			if (frameCount > totalFrames) {
				exit();
			}
		} else if (section.length() <= 1) {
			exit();
		}

		fill(0);

		if (showDebugText) {

			text("frame " + frameCount + " / " + totalFrames + ". (" + (int) (100 * frameCount / totalFrames)
					+ "%) / section " + section.ordinal() + " (" + section + ") – " + (int) (section.length() / 1000)
					+ "s long – " + (int) (section.pctDone(millis) * 100) + "% done", 40, height - 40);
		}
	}

	void saveTransparentFrame(String folder) {

		final PImage canvas = get();
		canvas.format = ARGB;

		int bg = -1;
		final int p[] = canvas.pixels;
		final int bgt = bg & ~0b000000;

		for (int i = 0; i != p.length; ++i)
			if (p[i] == bg)
				p[i] = bgt;

		canvas.updatePixels();
		canvas.save(folder + "/" + nf(frameCount, 5) + ".png");
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
