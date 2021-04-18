package glowredman.wherearetheores.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigObject {
	
	public boolean showDebug = false;
	public int textOffset = 20;
	public int lineHeight = 9;
	public int tooltipOffsetX = 8;
	public int tooltipOffsetY = -12;
	public Map<String, Map<String, List<String>>> ores = new HashMap<String, Map<String,List<String>>>();

}
