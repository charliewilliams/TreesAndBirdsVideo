package Display;
import java.util.*;

public enum ChannelMapping {
	TreeGrowth(1), TreeGrowth2(2),
	Bird(3),
	Bird2(4),
	TreeChangeBass(5),
	TreeChangeMelody(6),
	Click(16);

	int channel;

	ChannelMapping(int channel) {
		this.channel = channel;
	}
	
	private static final Map<Integer, ChannelMapping> intToTypeMap = new HashMap<Integer, ChannelMapping>();
	static {
	    for (ChannelMapping type : ChannelMapping.values()) {
	        intToTypeMap.put(type.channel, type);
	    }
	}

	public static ChannelMapping fromInt(int i) {
		ChannelMapping type = intToTypeMap.get(Integer.valueOf(i));
	    if (type == null) {
	        return ChannelMapping.Click;
	    }
	    return type;
	}
}
