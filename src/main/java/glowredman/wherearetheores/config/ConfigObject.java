package glowredman.wherearetheores.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigObject {

	public String ores = new String();
	public Map<String, List<String>> dimensions = new HashMap<String, List<String>>();

	public ConfigObject(String oreDict, Map<String, List<String>> dims) {
		ores = oreDict;
		dimensions = dims;
	}

	public ConfigObject(String[] ores, Map<String, List<String>> dims) {
		for (String s : ores) {
			this.ores += s + ";";
		}
		this.ores = this.ores.substring(0, this.ores.length() - 1);
		dimensions = dims;
	}

}
