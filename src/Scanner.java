import java.util.Arrays;

/**
 * Scanner Class for the Core Interpreter Project;
 */
public class Scanner {

	private Scanner() { }

	/***********************************************************************************************
	 ***** Private Array for Reserved Keywords & String for Illegal Characters in the Language *****
	 ***********************************************************************************************/

	private static final String ILLEGAL = "~@#$%^&_?/`.";
	private static final String[] KEYWORD = {"program", "begin", "end", "int", "input", "output", "if",
			"then", "else", "endif", "do", "enddo", "while", "endwhile", "case", "of", "OR", "AND", "EOF"};

	/***********************************************************************************************
	 ************************** Start Method for Beginning Token Stream ****************************
	 ***********************************************************************************************/

	/**
	 * Generate raw token stream from tokenizer to be converted into parsable tokens;
	 *
	 * @param program   file name of the document containing the program code
	 */
	public static void begin(String program) { Tokenizer.begin(program); }

	/***********************************************************************************************
	 ****************************** Public Methods for Parser to Use *******************************
	 ***********************************************************************************************/

	/**
	 * Getter for the token in TOKENS at the position represented by the tracker index;
	 *
	 * @return  current token in the token stream as a parsable token
	 */
	public static String currentToken() { return getParsableToken(Tokenizer.currentToken()); }

	/**
	 * Setter for the current index of the token stream; Advance the current token;
	 */
	public static void nextToken() { Tokenizer.nextToken(); }

	/**
	 * Reset tracker to 0; Only required for printing the tokens in the partial submission;
	 */
	public static void resetTokenStream() { Tokenizer.resetTokenStream(); }

	/**
	 * Check if the currentToken matches the keyword; Advance stream if true;
	 *
	 * @param keyword   token consumed by the parser
	 */
	public static void match(String keyword) {
		String token = currentToken();
		if (token.equals(keyword)) {
			nextToken();
		} else {
			System.out.println("ERROR: Expected " + keyword + ", found " + token);
			System.exit(2); // Failure Case;
		}
	}

	/**
	 * Method for parser classes - extract id name from currentToken; Advance stream is successful;
	 *
	 * @return  string value enclosed in brackets of the ID terminal token
	 */
	public static String getID() {
		String token = currentToken();
		String id = "";
		if(token.contains("ID")) {
			id = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			nextToken();
		} else {
			System.out.println("ERROR: Expected ID token, found " + token);
			System.exit(2); // Failure Case;
		}
		return id;
	}

	/**
	 * Method for parser classes - extract const value from currentToken; Advance stream is successful;
	 *
	 * @return  int value enclosed in brackets of the CONST terminal token
	 */
	public static int getConst() {
		String token = currentToken();
		String id = "";
		if(token.contains("CONST")) {
			id = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			nextToken();
		} else {
			System.out.println("ERROR: Expected CONST token, found " + token);
			System.exit(2); // Failure Case;
		}
		int value = 0;
		try {
			value = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			System.out.println("ERROR: Data token " + id + " does not match a valid integer");
			System.exit(2); // Failure Case;
		}
		return value;
	}

	/***********************************************************************************************
	 ***********************- Helper Method for Generating Parsable Tokens *************************
	 ***********************************************************************************************/

	/**
	 * Map string from literal symbol to token representation for parser;
	 *
	 * @param token     valid symbol in the Core language syntax
	 * @return          token string representation of symbol for parser
	 */
	private static String getParsableToken(String token) {
		if (containsIllegalChar(token)) return "SCANNER_ERROR[" + token + "]";
		else if (Arrays.asList(KEYWORD).contains(token)) return token.toUpperCase();
		else if (Character.isLetter(token.charAt(0))) return "ID[" + token + "]";
		else if (Character.isDigit(token.charAt(0))) return "CONST[" + token + "]";
		else if (token.equals(";")) return "SEMICOLON";
		else if (token.equals(",")) return "COMMA";
		else if (token.equals("(")) return "LEFT_PAREN";
		else if (token.equals(")")) return "RIGHT_PAREN";
		else if (token.equals("[")) return "LEFT_BRACKET";
		else if (token.equals("]")) return "RIGHT_BRACKET";
		else if (token.equals("=")) return "EQUALS";
		else if (token.equals("+")) return "PLUS";
		else if (token.equals("-")) return "MINUS";
		else if (token.equals("*")) return "TIMES";
		else if (token.equals(":")) return "COLON";
		else if (token.equals("!")) return "NOT";
		else if (token.equals("<")) return "LESS_THAN";
		else if (token.equals(">")) return "GREATER_THAN";
		else if (token.equals("|")) return "BAR";
		else if (token.equals(":=")) return "ASSIGN";
		else if (token.equals("!=")) return "NOT_EQUAL";
		else if (token.equals("<=")) return "LESS_EQUAL";
		else if (token.equals(">=")) return "GREATER_EQUAL";
		else if (token.equals("EOF")) return "EOF";
		else return "SCANNER_ERROR[" + token + "]";
	}

	/**
	 * Determines whether a token contains any illegal characters;
	 *
	 * @param token     token generated by tokenizer to evaluate
	 * @return          whether or not token contains any illegal characters
	 */
	private static Boolean containsIllegalChar(String token) {
		Boolean result = false;
		for (int i = 0; i < token.length(); i++) {
			if (ILLEGAL.contains(String.valueOf(token.charAt(i)))) result = true;
		}
		return result;
	}

	/***********************************************************************************************
	 ****************** Helper Method to Print Tokens - Partial Submission Only ********************
	 ***********************************************************************************************/

	/**
	 * Method to print out all tokens represented internally by the scanner;
	 */
	public static void printTokens() {
		String token;
		while (!(token = currentToken()).equals("EOF")) {
			if (token.contains("SCANNER_ERROR")) {
				System.out.println("\nERROR: Token " + token.substring(14, token.indexOf("]"))
						+ " did not match any valid token in the Core language");
				System.exit(2); // Failure Case;
			} else {
				System.out.print(token + " ");
			}
			nextToken();
		}
		System.out.println();
		resetTokenStream();
	}
}
