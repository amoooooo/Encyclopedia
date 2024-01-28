package aster.amo.encyclopedia;

import aster.amo.encyclopedia.client.HoverHandler;

public class Encyclopedia
{
	public static final String MOD_ID = "encyclopedia";

	public static void init() {
		HoverHandler.initVanillaExtendedHandlers();
	}
}
