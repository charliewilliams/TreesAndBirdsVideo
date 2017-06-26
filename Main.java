import processing.core.*;
import processing.sound.*;
import java.util.Date;
import com.hamoid.*;
import Display.*;
import Model.*;

public class Main extends PApplet {

	boolean debug = true;

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
	int debugOffset = 180000;
	int durationMillis;

	public void settings() {

		String renderer = P3D;

		if (debug) {
			size(1280, 720, renderer);
		} else {
			size(1920, 1080, renderer); // P2D, P3D, FX2D
		}
		pixelDensity(2);
	}

	public void setup() {

		frameRate(60);
		rectMode(CENTER);
		colorMode(HSB, 360, 100, 100, 100);
		
		background(0);
		noStroke();

		// Build our singletons
		sceneManager = new SceneManager(this);
		new TreeManager(this);
		new BirdManager(this);

		if (exportVideo) {
			startVideoExport();
		}

		noteManager = new NoteManager(this, "song.json");

		file = new SoundFile(this, "mix.mp3");
		durationMillis = (int)(file.duration() * 1000);
		println(durationMillis);
		file.jump(debugOffset / 1000.0f);
		file.play();

		millisOffset += millis();
	}

	public void draw() {

		int millis = millis();

		// Place the camera
		sceneManager.update(millis);

		// Read notes from JSON in memory, tick over all manager classes
		noteManager.readNotes(millis);

		if (debug) {
			int seconds = millis / 1000;
			int minutes = seconds / 60;
			int displaySeconds = minutes > 0 ? seconds % 60 : seconds;

			String txt_fps = String.format(getClass().getName() + " | %s | %02d:%02d | %2.0f fps", noteManager.locationString(), minutes, displaySeconds, frameRate);
			surface.setTitle(txt_fps);
		}

		if (exportVideo) {
			videoExport.saveFrame();
		}

		if (millis > durationMillis) {
			endMovieAndExit();
		}
	}

	public int millis() {
		return super.millis() + debugOffset - millisOffset;
	}


	/*
	 *  Video exportin'
	 * */

	VideoExport videoExport;

	boolean exportVideo = !debug;

	void startVideoExport() {

		Date d = new Date();
		videoExport = new VideoExport(this, "exp-" + d.getTime() + ".mp4");
		videoExport.setQuality(90, 1); // Video, audio
		videoExport.setFrameRate(30);
		videoExport.setDebugging(false);
		videoExport.startMovie();
	}

	public void keyPressed() {

		if (exportVideo && key == 'q') {
			endMovieAndExit();
		}
	}

	void endMovieAndExit() {

		if (exportVideo) {
			videoExport.endMovie();
		}
		exit();
	}
}
