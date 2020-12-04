package redstonetweaks.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextFormatting {
	
	private static final String DEFAULT_LINE_BREAK_SEPARATOR = "\n";
	private static final int DEFAULT_LINE_LENGTH = 75;
	
	public static String insertLineBreaks(String text) {
		return insertLineBreaks(text, DEFAULT_LINE_BREAK_SEPARATOR, DEFAULT_LINE_LENGTH);
	}
	
	public static String insertLineBreaks(String text, String separator, int lineLength) {
		int index = lineLength;
		
		while (index < text.length()) {
			while (text.charAt(index) != ' ') {
				index--;
			}
			
			text = text.substring(0, index) + separator + text.substring(index + 1, text.length());
			
			index += lineLength;
		}
		
		return text;
	}
	
	public static String[] getAsLines(String text) {
		return getAsLines(text, DEFAULT_LINE_BREAK_SEPARATOR, DEFAULT_LINE_LENGTH);
	}
	
	public static String[] getAsLines(String text, String separator, int lineLength) {
		return insertLineBreaks(text, separator, lineLength).split(separator);
	}
	
	public static String trimToLength(String text, int length) {
		int currentLength = text.length();
		return currentLength > length ? text.substring(0, length) : text;
	}
	
	public static String trimToWidth(String text, int width, TextRenderer font) {
		while (font.getWidth(text) > width && text.length() > 0) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}
	
	public static String prettyTrimToWidth(String text, int width, TextRenderer font) {
		return font.getWidth(text) > width ? trimToWidth(text, width - font.getWidth("..."), font) + "..." : text;
	}
	
	public static Text trimToWidth(Text text, int width, TextRenderer font) {
		return new TranslatableText(trimToWidth(text.asString(), width, font));
	}
	
	public static Text prettyTrimToWidth(Text text, int width, TextRenderer font) {
		return new TranslatableText(prettyTrimToWidth(text.asString(), width, font));
	}
}
