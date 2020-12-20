package glowredman.wherearetheores.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import glowredman.wherearetheores.WATO;

public class NEIWATOConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return WATO.MODNAME;
	}

	@Override
	public String getVersion() {
		return WATO.VERSION;
	}

	@Override
	public void loadConfig() {

		API.registerRecipeHandler(new WATORecipeHandler());
		API.registerUsageHandler(new WATORecipeHandler());

	}

}
