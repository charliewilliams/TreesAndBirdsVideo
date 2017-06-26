package Model;

public enum Ictus { 
	PHRASE (88), 
	BAR (71), 
	BEAT (64);

	Integer type;

	private Ictus(Integer type) {

		this.type = type;
	};
}
