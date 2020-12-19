package glowredman.wherearetheores.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import glowredman.wherearetheores.config.ConfigHandler;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ConfigHandler.init(event);
	}

}
