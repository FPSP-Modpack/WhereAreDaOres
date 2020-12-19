package glowredman.wherearetheores.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import glowredman.wherearetheores.WATO;

public class ConfigHandler {

	public static File configFile;
	public static List<ConfigObject> config = new ArrayList<ConfigObject>();

	public static void init(FMLPreInitializationEvent event) {
		try {
			configFile = new File(event.getModConfigurationDirectory().getPath() + "/NEI", WATO.MODID + ".json");
			if (!configFile.exists()) {
				List<ConfigObject> list = new ArrayList<ConfigObject>();

				list.add(new ConfigObject("oreGold", dimension("Overworld", 9, 2, 0, 31)));
				list.add(new ConfigObject("oreIron", dimension("Overworld", 9, 20, 0, 63)));
				list.add(new ConfigObject("oreCoal", dimension("Overworld", 17, 20, 0, 127)));
				list.add(new ConfigObject("oreLapis", dimension("Overworld", Arrays.asList("Max Vein Size: 7",
						"Attempts per Chunk: 1", "Y-Level: 0 to 30", "This Ore is normally distributed!"))));
				list.add(new ConfigObject("oreDiamond", dimension("Overworld", 8, 1, 0, 15)));
				list.add(new ConfigObject("oreRedstone", dimension("Overworld", 8, 8, 0, 15)));
				list.add(new ConfigObject("oreEmerald", dimension("Overworld", Arrays.asList("Max Vein Size: 1",
						"Attempts per Chunk: 3 to 9", "Y-Level: 4 - 31", "This Ore generates only in Hills!"))));
				list.add(new ConfigObject("oreQuartz", dimension("Nether", 14, 16, 10, 117)));

				ConfigHolder holder = new ConfigHolder();
				holder.config = list;
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
				writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(holder));
				writer.close();
			}

			config = new Gson().fromJson(readFile(configFile.getPath()), ConfigHolder.class).config;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, List<String>> dimension(String dim, List<String> info) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		ret.put(dim, info);
		return ret;
	}

	private static Map<String, List<String>> dimension(String dim, int maxVeinSize, int tries, int minY, int maxY) {
		return dimension(dim, dimInfo(maxVeinSize, tries, minY, maxY));
	}

	private static List<String> dimInfo(int maxVeinSize, int tries, int minY, int maxY) {
		List<String> ret = new ArrayList<String>();
		ret.add("Max Vein Size: " + maxVeinSize);
		ret.add("Attempts per Chunk: " + tries);
		ret.add("Y-Level: " + minY + " to " + maxY);
		return ret;
	}

	private static String readFile(String path) throws Exception {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}

}
