package Model;

public enum Section {
	LeadIn,
	Opening,
	FirstMelody,
	FirstThirds,
	RisingMinor,
	RepeatedNotes,
	BigMelody,
	Falloff,
	HighMelody,
	Recap,
	Tail,
	FileEnd;

	private static final Section[] values = Section.values();
	private static int[] startPointsInMillis = {
			0,
			7000, // first sound
			38130, // first melody
			69830, // first thirds
			116450, // rising minor
			175012, // repeated notes 
			255250, // big melody
			282750, // falloff
			311000, // high melody
			374500, // recap
			399250, // tail
			412000 // file end
	};

	public static Section forMillis(int millis) {

		for (int i = 0; i < startPointsInMillis.length - 1; i++) {
			if (millis > i && millis < startPointsInMillis[i + 1]) {
				return values[i];
			}
		}

		return Section.Tail;
	}

	private float l = 0;
	float length() {

		if (l == 0) {

			int thisStartMillis = startPointsInMillis[this.ordinal()];
			int thisEndMillis = startPointsInMillis[this.ordinal() + 1];

			l = (float) (thisEndMillis - thisStartMillis);
		}

		return l;
	}

	float pctDone(int millis) {
		return (millis - startPointsInMillis[this.ordinal()]) / length();
	}
}
