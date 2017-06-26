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
	Tail;

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
			400000 // safety + 1
	};

	public static Section forMillis(int millis) {

		for (int i: startPointsInMillis) {
			if (millis > i && millis < startPointsInMillis[i + 1]) {
				return values[i];
			}
		}
		
		return Section.Tail;
	}
}
