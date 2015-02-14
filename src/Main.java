/**
 * Main Class for the Core Interpreter Project;
 */
public class Main {

	/**
	 * Interpretation := scan tokens => generate parse tree => print program => execute program
	 *
	 * @param args  command line arguments; args[0] is the program; args[1] is the input data;
	 */
	public static void main (String[] args) {
		try {
			// Scanner := token stream;
			Scanner.begin(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unknown option or incorrect number of arguments");
			System.exit(2);
		}
		// Print tokens - 2/24/2015 partial submission only;
		Scanner.printTokens();

		// Parser := generate parse tree;
		PROG parseTree = Parser.getParseTree();

		// Printer := print program; Uncomment to see;
		Printer.prettyPrint(parseTree);

		try {
			// Executor := generate output with input; Uncomment to see;
			Executor.execute(parseTree, args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unknown option or incorrect number of arguments");
			System.exit(2);
		}
	}
}
