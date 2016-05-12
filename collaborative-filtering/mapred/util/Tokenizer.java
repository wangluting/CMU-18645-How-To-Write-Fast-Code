package mapred.util;

public class Tokenizer {

	static String URL_PATTERN = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\b";
	
	public static String[] tokenize(String line) {
		return removePatterns(line).split("\\s+");
	}
	
	public static String removePatterns(String line) {
		
		String l1 = line.toLowerCase();
		
		l1 = l1.replaceAll(URL_PATTERN, ""); // Remove URLs
		
		l1 = l1.replaceAll("[:;]", " ");
		
		l1 = l1.replaceAll("\\p{Punct}(\\s+|$)", " "); // All punctuations in the back		
		
		l1 = l1.replaceAll("(^|\\s+)\\p{Punct}+(\\s+|$)", " ");
		
		l1 = l1.replaceAll("(^|\\s+)[<>,./?!$%^&*()_+-=]", " "); // All punctuations in the front, only leaving hashtags and mentions		
		
		l1 = l1.replaceAll("[^\\x00-\\x80]+", ""); // Remove non-ASCII chars, leaving only english
		
		l1 = l1.replaceAll("(^|\\s+)\\d+(\\s+|$)", " "); // Remove all pure digits
		
		l1 = l1.replaceAll("\\s+", " ").trim();
		
		return l1;
	}

}
