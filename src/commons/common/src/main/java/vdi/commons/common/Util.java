package vdi.commons.common;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Helper class containing general helpful methods.
 */
public final class Util {

	/**
	 * private constructor.
	 */
	private Util() {
	}

	/**
	 * Generates a slug for a string.
	 * 
	 * @param text
	 *            the text to generate a slug of
	 * @return the slug
	 */
	public static String generateSlug(String text) {
		String nowhitespace = Pattern.compile("[\\s]").matcher(text).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}
}
