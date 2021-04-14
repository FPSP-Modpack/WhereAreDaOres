package glowredman.wherearetheores;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import glowredman.wherearetheores.config.ConfigHandler;
import glowredman.wherearetheores.proxy.CommonProxy;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Mod(acceptedMinecraftVersions = "1.7.10", dependencies = "required-after:NotEnoughItems", modid = WATO.MODID, name = WATO.MODNAME, version = WATO.VERSION)
public class WATO {

	public static final String MODID = "wherearetheores";
	public static final String MODNAME = "Where Are The Ores?";
	public static final String VERSION = "@version@";

	@Instance
	public static WATO instance;

	@SidedProxy(clientSide = "glowredman.wherearetheores.proxy.ClientProxy", serverSide = "glowredman.wherearetheores.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	/*************
	 * UTILITIES *
	 ************/

	/**
	 * Decodes the given String to an ItemStack. Examples: "apple", "coal:1",
	 * "IC2:dust", "IC2:dust:4".
	 */
	public static ItemStack findItem(String item) {
		ItemStack ret;
		String error = "";
		String[] parts = item.split(":");
		switch (parts.length) {

		// The item is from minecraft and has no meta value.
		case 1:
			ret = GameRegistry.findItemStack("minecraft", item, 1);
			error = "Assumed Vanilla Item without Meta (like \"apple\")";
			break;
		case 2:

			// The item is from Minecraft and has a meta value.
			try {
				ret = GameRegistry.findItemStack("minecraft", parts[0], 1);
				Items.apple.setDamage(ret, Integer.parseInt(parts[1]));
				error = "Assumed Vanilla Item with Meta (like \"coal:1\")";

				// The item is not from Minecraft and has no meta value.
			} catch (Exception e) {
				ret = GameRegistry.findItemStack(parts[0], parts[1], 1);
				error = "Assumed namespaced Item without Meta (like \"IC2:dust\")";
			}
			break;

		// The item is not from Minecraft and has a meta value.
		case 3:
			ItemStack stack = GameRegistry.findItemStack(parts[0], parts[1], 1);
			if (stack == null) {
				ret = null;
			} else {
				Items.apple.setDamage(stack, Integer.parseInt(parts[2]));
				ret = stack;
			}
			error = "Assumed namespaced Item with Meta (like \"IC2:dust:4\")";
			break;
		default:
			ret = null;
		}
		if (ret == null)
			error("Unable to translate \"" + item + "\" to an ItemStack! " + error);
		return ret;
	}

	public static String getStackName(ItemStack stack) {
		UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(stack.getItem());
		return identifier.modId + ":" + identifier.name + ":" + stack.getItemDamage();
	}

	public static void fatal(String message) {
		logger.fatal(message);
	}

	public static void error(String message) {
		logger.error(message);
	}

	public static void warn(String message) {
		logger.warn(message);
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void debug(String message) {
		if (ConfigHandler.enableDebug) {
			logger.info(message);
		} else {
			logger.debug(message);
		}
	}

	public static void trace(String message) {
		logger.trace(message);
	}

}
