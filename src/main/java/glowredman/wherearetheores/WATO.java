package glowredman.wherearetheores;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import glowredman.wherearetheores.proxy.CommonProxy;

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

}
