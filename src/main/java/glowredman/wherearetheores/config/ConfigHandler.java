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
import glowredman.wherearetheores.nei.WATORecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ConfigHandler {

	public static File configFile;
	public static boolean enableDebug;
	public static Map<String, Map<String, List<String>>> config = new HashMap<String, Map<String, List<String>>>();
	public static Map<String, String> possibleItems = new HashMap<String, String>();

	public static void init(FMLPreInitializationEvent event) {
		try {
			configFile = new File(event.getModConfigurationDirectory().getPath() + "/NEI", WATO.MODID + ".json");
			if (!configFile.exists()) {
				Map<String, Map<String, List<String>>> list = new HashMap<String, Map<String, List<String>>>();

				list.put("oreGold", dimension("Overworld", 9, 2, 0, 31));
				list.put("oreIron", dimension("Overworld", 9, 20, 0, 63));
				list.put("oreCoal", dimension("Overworld", 17, 20, 0, 127));
				list.put("oreLapis", dimension("Overworld", Arrays.asList("Max Vein Size: 7", "Attempts per Chunk: 1",
						"Y-Level: 0 to 30", "This Ore is normally distributed!")));
				list.put("oreDiamond", dimension("Overworld", 8, 1, 0, 15));
				list.put("oreRedstone", dimension("Overworld", 8, 8, 0, 15));
				list.put("oreEmerald", dimension("Overworld", Arrays.asList("Max Vein Size: 1",
						"Attempts per Chunk: 3 to 9", "Y-Level: 4 to 31", "This Ore generates only in Hills!")));
				list.put("oreQuartz", dimension("Nether", 14, 16, 10, 117));

				ConfigObject obj = new ConfigObject();
				obj.showDebug = false;
				obj.textOffset = 20;
				obj.lineHeight = 9;
				obj.tooltipOffsetX = 8;
				obj.tooltipOffsetY = -12;
				obj.ores = list;

				BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
				writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
				writer.close();
			}

			ConfigObject configObj = new Gson().fromJson(readFile(configFile.getPath()), ConfigObject.class);
			config = configObj.ores;
			enableDebug = configObj.showDebug;
			WATORecipeHandler.lineHeight = configObj.lineHeight;
			WATORecipeHandler.textOffset = configObj.textOffset;
			WATORecipeHandler.tooltipOffsetX = configObj.tooltipOffsetX;
			WATORecipeHandler.tooltipOffsetY = configObj.tooltipOffsetY;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void collectPossibleItems() {
		WATO.info("[WATO] Adding Information for Ores...");

		for (String ore : config.keySet()) {

			// The Entry is an Item (List)
			if (ore.startsWith("[")) {
				WATO.debug(" Identifier(s) for Item-Key \"" + ore + "\":");
				for (String item : ore.substring(1).split(";")) {
					ItemStack itemStack = WATO.findItem(item);
					if (itemStack == null)
						continue;
					String identifier = WATO.getStackName(itemStack);
					WATO.debug("   " + identifier);
					String before = possibleItems.put(identifier, ore);
					if (before != null)
						WATO.error("[WATO] The Identifier " + identifier + " (previously associated with Entry \""
								+ before + "\" has been overridden and is now associated with Entry \"" + ore + "\"!");
				}

				// The Entry is an OreDict Entry
			} else {
				WATO.debug(" Identifier(s) for OreDict-Key \"" + ore + "\":");
				for (ItemStack stack : OreDictionary.getOres(ore)) {
					String identifier = WATO.getStackName(stack);
					WATO.debug("   " + identifier);
					String before = possibleItems.put(identifier, ore);
					if (before != null) {
						WATO.error("[WATO] The Identifier " + identifier + " (previously associated with Entry \""
								+ before + "\" has been overridden and is now associated with Entry \"" + ore + "\"!");
					}
				}
			}
		}
		WATO.info("[WATO] Added Information for " + possibleItems.size() + " Ores!");
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
