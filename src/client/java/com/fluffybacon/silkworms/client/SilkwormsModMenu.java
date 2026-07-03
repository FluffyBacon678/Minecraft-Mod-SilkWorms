package com.fluffybacon.silkworms.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Optional Mod Menu integration: adds the config (gear) button on the
 * Silkworms entry. This class is only ever loaded by Mod Menu itself, so the
 * mod keeps working when Mod Menu is absent.
 */
public class SilkwormsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SilkwormsConfigScreen::new;
	}
}
