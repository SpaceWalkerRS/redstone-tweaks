package redstonetweaks.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextFormatting {
	
	public static final String DEFAULT_LINE_BREAK_SEPARATOR = "\n";
	public static final int DEFAULT_LINE_LENGTH = 75;
	public static final int DEFAULT_LINE_WIDTH = 100;
	
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
	
	public static String[] getAsLines(String text, int lineLength) {
		return getAsLines(text, DEFAULT_LINE_BREAK_SEPARATOR, lineLength);
	}
	
	public static String[] getAsLines(String text, String separator, int lineLength) {
		return insertLineBreaks(text, separator, lineLength).split(separator);
	}
	
	public static String insertLineBreaks(TextRenderer font, String text, String separator, int lineWidth) {
		int start = 0;
		int end = 1;
		
		while (end < text.length()) {
			String subString = text.substring(start, end);
			
			if (font.getWidth(subString) < lineWidth) {
				end++;
			} else {
				while (text.charAt(end) != ' ') {
					end--;
				}
				
				text = text.substring(0, end) + separator + text.substring(end + 1, text.length());
				
				start = end + separator.length() + 1;
				end = start + 1;
			}
		}
		
		return text;
	}
	
	public static String[] getAsLines(TextRenderer font, String text) {
		return getAsLines(font, text, DEFAULT_LINE_BREAK_SEPARATOR, DEFAULT_LINE_WIDTH);
	}
	
	public static String[] getAsLines(TextRenderer font, String text, int lineWidth) {
		return getAsLines(font, text, DEFAULT_LINE_BREAK_SEPARATOR, lineWidth);
	}
	
	public static String[] getAsLines(TextRenderer font, String text, String separator, int lineWidth) {
		return insertLineBreaks(font, text, separator, lineWidth).split(separator);
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
