package redstonetweaks.util;

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
}
