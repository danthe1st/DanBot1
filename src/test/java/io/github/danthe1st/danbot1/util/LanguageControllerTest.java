package io.github.danthe1st.danbot1.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.github.danthe1st.danbot1.AbstractDanBot1Test;
import io.github.danthe1st.danbot1.core.Main;

class LanguageControllerTest extends AbstractDanBot1Test{

	@Test
	public void testDefaultLocale(){
		assertEquals(Locale.ENGLISH, LanguageController.getLocale(Main.getJda().getGuildById(getGuild())));
	}

	@Test
	public void testSetLocale() {
		LanguageController.setLocale(Main.getJda().getGuildById(getGuild()), Locale.GERMAN);
		assertEquals(Locale.GERMAN, LanguageController.getLocale(Main.getJda().getGuildById(getGuild())));
		LanguageController.setLocale(Main.getJda().getGuildById(getGuild()), null);
		testDefaultLocale();
	}
	
	@Test
	public void testTextInDifferentLanguages() {
		assertEquals("User Command", LanguageController.translate(Main.getJda().getGuildById(getGuild()), "cmdType_USER"));
		LanguageController.setLocale(Main.getJda().getGuildById(getGuild()), Locale.GERMAN);
		assertEquals("Benutzerbefehl", LanguageController.translate(Main.getJda().getGuildById(getGuild()), "cmdType_USER"));
		LanguageController.setLocale(Main.getJda().getGuildById(getGuild()), null);
	}
	
	@Test
	public void testNotTranslatedText() {
		assertEquals("_test", LanguageController.translate(Main.getJda().getGuildById(getGuild()), "_test"));
	}
}
