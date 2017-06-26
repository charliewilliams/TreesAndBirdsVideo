package Model;
import java.util.*;
import processing.core.*;
import processing.data.*;

public class JSONReader {

	// Convert from npm midi output json to a more useful in-memory format

	int currentTimestampIndex = 0;
	Float[] timestamps;
	//Key is timestamp in seconds
	HashMap<Float, Note> notes = new HashMap<Float, Note>();
	HashMap<Float, Pedal> pedals = new HashMap<Float, Pedal>();
	HashMap<Float, Ictus> structure = new HashMap<Float, Ictus>();

	public JSONReader(PApplet parent, String name) {

		JSONObject json = parent.loadJSONObject(name);
		JSONArray tracks = json.getJSONArray("tracks");

		for (int i = 0; i < tracks.size(); i++) {

			JSONObject track = tracks.getJSONObject(i);

			String channelStr = track.getString("name");
			if (channelStr == null || channelStr.length() == 0) {
				continue;
			}

			Integer channel = Integer.valueOf(channelStr);

			JSONArray notesJSON = track.getJSONArray("notes");

			// if it's channel 16, make an Ictus instead of a Note
			if (channel == 16) {
				buildIctusFrom(notesJSON, parent);
				continue;
			}

			// Otherwise pull each note's JSON, create a Note object, and add it to the "notes" hashmap
			for (int j = 0; j < notesJSON.size(); j++) {

				JSONObject noteJSON = notesJSON.getJSONObject(j);

				Integer noteNumber = noteJSON.getInt("midi");
				Float velocity = noteJSON.getFloat("velocity");
				Float duration = noteJSON.getFloat("duration");
				Float timestamp = noteJSON.getFloat("time");

				Note note = new Note(timestamp * 1000, noteNumber, velocity, duration, channel);
				notes.put(timestamp, note);
			}

			// read any control 64 info and make a Pedal object
			JSONObject control = track.getJSONObject("controlChanges");
			JSONArray sustainsJSON = control.getJSONArray("64");

			if (sustainsJSON == null) {
				continue;
			}

			for (int j = 0; j < sustainsJSON.size(); j++) {

				JSONObject sustainJSON = sustainsJSON.getJSONObject(j);

				Float timestamp = sustainJSON.getFloat("time");
				Float value = sustainJSON.getFloat("value");
				Pedal pedal = new Pedal(value);
				pedals.put(timestamp, pedal);
			}
		}

		buildSortedArrayOfAllTimestamps();
	}

	private void buildIctusFrom(JSONArray notesJSON, PApplet parent) {

		for (int i = 0; i < notesJSON.size(); i++) {

			JSONObject noteJSON = notesJSON.getJSONObject(i);

			Integer noteNumber = noteJSON.getInt("midi");
			Float timestamp = noteJSON.getFloat("time");

			switch (noteNumber) {
			case 88:
			case 100:
				structure.put(timestamp, Ictus.PHRASE);
				break;
			case 71:
				structure.put(timestamp, Ictus.BAR);
				break;
			case 64:
				structure.put(timestamp, Ictus.BEAT);
				break;
			default:
				PApplet.println("ERROR: ", noteNumber);
				parent.exit();
			}
		}
	}

	//Check to see if anything in the hashmap has a smaller key than millis(), 
	//if so, trigger those events & remove from the hashmap
	private void buildSortedArrayOfAllTimestamps() {

		int count = notes.size() + pedals.size() + structure.size();

		timestamps = new Float[count];

		int i = 0;
		for (Float noteTime : notes.keySet()) {
			timestamps[i] = noteTime;
			i++;
		}
		for (Float pedTime : pedals.keySet()) {
			timestamps[i] = pedTime;
			i++;
		}
		for (Float tickTime : structure.keySet()) {
			timestamps[i] = tickTime;
			i++;
		}

		Arrays.sort(timestamps);
	}
}