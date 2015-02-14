import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Tokenizer Class for the Core Interpreter Project;
 */
public class Tokenizer {

	private Tokenizer() { }

	/*************************************************************************************************
	 * Private Members: Strings for Reserved Symbols; List for Generated Tokens; Token Tracker Index *
	 *************************************************************************************************/

	private static final String WHITESPACE = " \n\t\r";
	private static final String SPECIAL_SYMBOL = ":!<>";
	private static final String SYMBOL = ";,()[]=+-*|";
	private static List<String> TOKENS = new LinkedList<String>();
	private static int TRACKER = 0;

	/*************************************************************************************************
	 **************************** Start Method for Beginning Tokenization ****************************
	 *************************************************************************************************/

	/**
	 * Open program file and begin generation of the token stream;
	 *
	 * @param program   name of file containing program code
	 */
	public static void begin(String program) {
		BufferedReader reader = null;
		List<String> lines = new LinkedList<String>();
		try {
			reader = new BufferedReader(new FileReader(new File(program)));
			// Extract lines from program file for tokenization process;
			String line;
			while ((line = reader.readLine()) != null) lines.add(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException ignored) { }
		}
		tokenize(lines);
	}

	/*************************************************************************************************
	 ****************************** Public Methods for Scanner to Use ********************************
	 *************************************************************************************************/

	/**
	 * Getter for the token in TOKENS at the position represented by the TRACKER index;
	 *
	 * @return  current token in the token stream as raw input
	 */
	public static String currentToken() { return TOKENS.get(TRACKER); }

	/**
	 * Setter for the current index of the token stream; Advance the current token;
	 */
	public static void nextToken() { TRACKER++; }

	/**
	 * Reset TRACKER to 0; Only required for printing the tokens in the partial submission;
	 */
	public static void resetTokenStream() { TRACKER = 0; }

	/*************************************************************************************************
	 **************************** Helper Method for Tokenization Process *****************************
	 *************************************************************************************************/

	/**
	 * Generates a token stream from the line list of the program file; Update TOKENS;
	 *
	 * @param lines     lines of the program read from the BufferedReader
	 */
	private static void tokenize (List<String> lines) {
		for (String line : lines) {
			int i = 0, len = line.length();
			while (i < len) {
				// Ensure j - i = |next token|;
				int j = i + 1;
				// Build next token from index i;
				char first = line.charAt(i);
				if (Character.isDigit(first)) {
					// Case for integer constants
					while (j < len && Character.isDigit(line.charAt(j))) j++;
					TOKENS.add(line.substring(i, j));
				} else if (SPECIAL_SYMBOL.contains(String.valueOf(first))) {
					// Case for special symbols; Potentially proceeded by '=';
					if (i + 1 < len && line.charAt(i + 1) == '=') j++;
					TOKENS.add(line.substring(i, j));
				} else if (SYMBOL.contains(String.valueOf(first))) {
					// Case for simple symbols := singular;
					TOKENS.add(line.substring(i, j));
				} else if (!WHITESPACE.contains(String.valueOf(first))) {
					// Case for IDs, keywords, and invalid tokens;
					while (j < len && !WHITESPACE.contains(String.valueOf(line.charAt(j)))
							&& !SYMBOL.contains(String.valueOf(line.charAt(j)))
							&& !SPECIAL_SYMBOL.contains(String.valueOf(line.charAt(j)))) j++;
					TOKENS.add(line.substring(i, j));
				}
				// Update index with length of built token; Only increment for whitespace;
				i = j;
			}
		}
		// Represent end of token stream;
		TOKENS.add("EOF");
	}
}
