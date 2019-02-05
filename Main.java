import processing.core.*;
import processing.sound.*;
//import java.util.Date;
//import com.hamoid.*;
import Display.*;
import Display.Birds.BirdManager;
import Display.Trees.TreeManager;
import Model.*;

public class Main extends PApplet {

	boolean renderVideo = false;
	boolean renderGlow = true;
	int _frameRate = 30;
	int totalFrames;
	int prerollMillis = renderVideo ? 10000 : 2000;
	int audioMillisPreroll = 2200;

	public static void main(String[] args) {

		PApplet.main("Main");
	}

	NoteManager noteManager;
	SceneManager sceneManager;
	SoundFile file;

	// VARIOUS NAMED STARTING OFFSETS
	int musicStart = 10000;
	int melodyStart = 38000;
	int risingMel = 110000;
	int repeatedNotes = 180000;
	int bigReturn = 251000;
	int highMel = 295000;

	int millisOffset = 500;
	int debugOffsetMillis = melodyStart;
	int durationMillis;

	public void settings() {

//		pixelDensity(2);
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

		// Build our singletons
		sceneManager = new SceneManager(this);
		new TreeManager(this);
		new BirdManager(this);

		noteManager = new NoteManager(this, "song.json");

		file = new SoundFile(this, "mix.mp3");
		durationMillis = (int) (file.duration() * 1000);
		file.jump(debugOffsetMillis / 1000.0f);
		file.play();

		if (renderVideo) {
			float offsetSecs = (prerollMillis * 2 + audioMillisPreroll) / 1000;
			totalFrames = (int) ((file.duration() + offsetSecs) * _frameRate);
			println("Rendering", totalFrames, "frames.");
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

		// Place the camera, draw the background
		sceneManager.update(millis);

		// Read notes from JSON in memory; add to managers if there are new
		// notes this tick
		noteManager.readNotes(millis);

		// Update & draw trees
		TreeManager.instance().draw();

		// Update & draw birds
		BirdManager.instance().updateAndDraw();

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
	}
}
