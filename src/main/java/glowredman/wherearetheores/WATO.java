package glowredman.wherearetheores;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import glowredman.wherearetheores.proxy.CommonProxy;

@Mod(acceptedMinecraftVersions = "1.7.10", dependencies = "requiredAfter:NotEnoughItems;", modid = WATO.MODID, name = "Where Are The Ores?", version = WATO.VERSION)
public class WATO {
	
	public static final String MODID = "wherearetheores";
	public static final String VERSION = "@version@";
	
	@Instance
	public static WATO instance;
	
	@SidedProxy(clientSide = "glowredman.wherearetheores.proxy.ClientProxy", serverSide = "glowredman.wherearetheores.proxy.CommonProxy")
	public static CommonProxy proxy;

}
