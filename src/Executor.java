import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * Executor Class for the Core Interpreter Project;
 */
public class Executor {

	private Executor() { }

	/*******************************************************************************************
	 * Private Members: Map for Program Variable Declarations & Storage; List for Input Tokens *
	 *******************************************************************************************/

	private static HashMap<String,Integer> VARIABLES = new HashMap<String,Integer>();
	private static List<Integer> DATA = new LinkedList<Integer>();

	/*******************************************************************************************
	 ************************** Start Method for Beginning Execution ***************************
	 *******************************************************************************************/

	/**
	 * Execute program represent by a parse tree using DATA from a file;
	 *
	 * @param parseTree     parse tree representing the program file
	 * @param data          name of file containing the input DATA
	 */
	public static void execute(PROG parseTree, String data) {
		// Generate input list
		getData(data);
		// Begin execution
		execPROG(parseTree);
	}

	/*******************************************************************************************
	 ***************************** Methods for Execution of Code *******************************
	 *******************************************************************************************/

	/**
	 * Function to execute a PROG node;
	 *
	 * @param prog     PROG node to execute
	 */
	private static void execPROG(PROG prog) {
		// Generate variable list;
		execDECLSEQ(prog.getDeclSeq());
		// Execute statement sequence;
		execSTMTSEQ(prog.getStmtSeq());
	}

	/**
	 * Function to execute a DECL_SEQ node;
	 *
	 * @param declSeq   DECL_SEQ node to execute
	 */
	private static void execDECLSEQ(DECL_SEQ declSeq) {
		execDECL(declSeq.getDecl());
		// Decision: execute another DECL_SEQ;
		if (declSeq.getAltNo() == 1) {
			execDECLSEQ(declSeq.getDeclSeq());
		}
	}

	/**
	 * Function to execute a DECL node;
	 *
	 * @param decl   DECL node to execute
	 */
	private static void execDECL(DECL decl) {
		execIDLIST(decl.getIdList());
	}

	/**
	 * Function to declare all identifiers in an ID_LIST node;
	 *
	 * @param idList    ID_LIST node to execute
	 */
	private static void execIDLIST(ID_LIST idList) {
		// Add new variable to list;
		String var = idList.getId();
		if (!VARIABLES.containsKey(var)) {
			VARIABLES.put(var, null);
		} else {
			System.out.println("ERROR: Variable " + var + " has already been instantiated.");
			System.exit(2); // Failure Case;
		}
		// Decision: execute another ID_LIST;
		if (idList.getAltNo() == 1) {
			execIDLIST(idList.getIdList());
		}
	}

	/**
	 * Function to execute STMT nodes in a STMT_SEQ node;
	 *
	 * @param stmtSeq   STMT_SEQ node to execute
	 */
	private static void execSTMTSEQ(STMT_SEQ stmtSeq) {
		execSTMT(stmtSeq.getStmt());
		// Decision: execute another STMT_SEQ;
		if (stmtSeq.getAltNo() == 1) {
			execSTMTSEQ(stmtSeq.getStmtSeq());
		}
	}

	/**
	 * Function to execute a STMT node;
	 *
	 * @param stmt  STMT node to execute
	 */
	private static void execSTMT(STMT stmt) {
		switch (stmt.getAltNo()) {
			case 1:
				execASSIGN(stmt.getAssign());
				break;
			case 2:
				execIF(stmt.getIf());
				break;
			case 3:
				execLOOP(stmt.getLoop());
				break;
			case 4:
				execIN(stmt.getIn());
				break;
			case 5:
				execOUT(stmt.getOut());
				break;
			case 6:
				execCASE(stmt.getCase());
				break;
			default:
				break;
		}
	}

	/**
	 * Function to execute an ASSIGN statement;
	 *
	 * @param assignStmt    ASSIGN node to execute
	 */
	private static void execASSIGN(ASSIGN assignStmt) {
		String id = assignStmt.getLvalue();
		if (VARIABLES.containsKey(id)) {
			VARIABLES.put(id, execEXPR(assignStmt.getExpr()));
		} else {
			System.out.println("ERROR: variable id " + id + " has not been declared");
			System.exit(2); // Failure Case;
		}
	}

	/**
	 * Function to execute an IF-THEN or IF-THEN-ELSE statement;
	 *
	 * @param ifStmt    IF node to execute
	 */
	private static void execIF(IF ifStmt) {
		if (execCOND(ifStmt.getCond())) {
			execSTMTSEQ(ifStmt.getStmtSeq());
		} else if (ifStmt.getAltNo() == 1) { // Potential ELSE clause;
			execSTMTSEQ(ifStmt.getElseStmtSeq());
		}
	}

	/**
	 * Function to execute a DO-WHILE LOOP;
	 *
	 * @param loopStmt  LOOP node to execute
	 */
	private static void execLOOP(LOOP loopStmt) {
		do {
			execSTMTSEQ(loopStmt.getStmtSeq());
		} while (execCOND(loopStmt.getCond()));
	}

	/**
	 * Function to execute an INPUT statement;
	 *
	 * @param inputStmt IN node to execute
	 */
	private static void execIN(IN inputStmt) {
		// Get initial idList for assignment;
		ID_LIST idList = inputStmt.getIdList();
		setVarByInput(idList);
		// Decision: input for rest of ID_LIST;
		while (idList.getAltNo() == 1) {
			idList = idList.getIdList();
			setVarByInput(idList);
		}
	}

	/**
	 * Function to execute an OUTPUT statement;
	 *
	 * @param outputStmt    OUT node to execute
	 */
	private static void execOUT(OUT outputStmt) {
		// Get initial idList for output;
		ID_LIST idList = outputStmt.getIdList();
		outputVar(idList);
		// Decision: output for rest of ID_LIST;
		while (idList.getAltNo() == 1) {
			idList = idList.getIdList();
			outputVar(idList);
		}
	}

	/**
	 * Function to evaluate a COND expression;
	 *
	 * @param cond  COND node to evaluate
	 * @return      Boolean result of evaluation
	 */
	private static Boolean execCOND(COND cond) {
		Boolean result = true;
		switch (cond.getAltNo()) {
			case 0: // !COND;
				result = !execCOND(cond.getNeg());
				break;
			case 1: // (COND op COND);
				if (cond.getOp().equals("AND")) {
					result = (execCOND(cond.getLhs()) && execCOND(cond.getRhs()));
				} else {
					result = (execCOND(cond.getLhs()) || execCOND(cond.getRhs()));
				}
				break;
			case 2: // CMPR;
				result = execCMPR(cond.getCmpr());
				break;
			default:
				break;
		}
		return result;
	}

	/**
	 * Function to evaluate a CMPR expression;
	 *
	 * @param cmpr  CMPR node to evaluate
	 * @return      Boolean result of the evaluation
	 */
	private static Boolean execCMPR(CMPR cmpr) {
		Boolean result = true;
		CMPR_OP cmprOp = cmpr.getOp();
		if (cmprOp.getOp().equals("EQUALS")) {
			result = (execEXPR(cmpr.getExpr1()) == execEXPR(cmpr.getExpr2()));
		} else if (cmprOp.getOp().equals("LESS_THAN")) {
			result = (execEXPR(cmpr.getExpr1()) < execEXPR(cmpr.getExpr2()));
		} else if (cmprOp.getOp().equals("GREATER_THAN")) {
			result = (execEXPR(cmpr.getExpr1()) > execEXPR(cmpr.getExpr2()));
		} else if (cmprOp.getOp().equals("LESS_EQUAL")) {
			result = (execEXPR(cmpr.getExpr1()) <= execEXPR(cmpr.getExpr2()));
		} else if (cmprOp.getOp().equals("GREATER_EQUAL")) {
			result = (execEXPR(cmpr.getExpr1()) >= execEXPR(cmpr.getExpr2()));
		} else if (cmprOp.getOp().equals("NOT_EQUAL")) {
			result = (execEXPR(cmpr.getExpr1()) != execEXPR(cmpr.getExpr2()));
		}
		return result;
	}

	/**********************************************************************
	 * execCMPROP() omitted - not necessary to implement a trivial method *
	 **********************************************************************/

	/**
	 * Function to evaluate an EXPR node;
	 *
	 * @param expr  EXPR node to evaluate
	 * @return      int value of the expression evaluation
	 */
	private static int execEXPR(EXPR expr) {
		int result = execTERM(expr.getTerm());
		// Decision: add/subtract second EXPR;
		if (expr.getAltNo() == 1) {
			if (expr.getOp().equals("PLUS")) {
				result += execEXPR(expr.getExpr());
			} else {
				result -= execEXPR(expr.getExpr());
			}
		}
		return result;
	}

	/**
	 * Function to evaluate a TERM node;
	 *
	 * @param term  TERM node to evaluate
	 * @return      int value of the term evaluation
	 */
	private static int execTERM(TERM term) {
		int result = execFACTOR(term.getFactor());
		// Decision: multiply second EXPR;
		if (term.getAltNo() == 1) {
			result *= execTERM(term.getTerm());
		}
		return result;
	}

	/**
	 * Function to evaluate a FACTOR node;
	 *
	 * @param factor    FACTOR node to evaluate
	 * @return          int value of the factor evaluation
	 */
	private static int execFACTOR(FACTOR factor) {
		int result = 0;
		switch (factor.getAltNo()) {
			case 0: // CONST;
				result = factor.getValue();
				break;
			case 1: // ID;
				result = getValueById(factor.getId());
				break;
			case 2: // -FACTOR;
				result = -1*execFACTOR(factor.getFactor());
				break;
			case 3: // (EXPR);
				result = execEXPR(factor.getExpr());
				break;
			default:
				break;
		}
		return result;
	}

	/**
	 * Function to execute a CASE statement;
	 *
	 * @param caseStmt  CASE node to execute;
	 */
	private static void execCASE(CASE caseStmt) {
		// Get id/value pair and pass into CASES;
		String id = caseStmt.getId();
		int value = getValueById(id);
		execCASES(caseStmt.getCases(), id, value);
	}

	/**
	 * Function to evaluate a CASES statement;
	 *
	 * @param cases     CASES node to evaluate
	 * @param id        id of variable to set
	 * @param value     value of integer to match
	 */
	private static void execCASES(CASES cases, String id, int value) {
		// Set id value to EXPR if value is found in current INT_LIST;
		if (execINTLIST(cases.getIntList(), value)) {
			VARIABLES.put(id, execEXPR(cases.getExpr()));
		} else { // execute potential ELSE clause or more CASES;
			switch (cases.getAltNo()) {
				case 1: // else expr
					VARIABLES.put(id, execEXPR(cases.getElseExpr()));
					break;
				case 2: // more CASES
					execCASES(cases.getCases(), id, value);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Determine whether a value is a member of the int list;
	 *
	 * @param intList   INT_LIST node to execute
	 * @param value     int value to match to members
	 * @return          whether or not value is contained in intList
	 */
	private static Boolean execINTLIST(INT_LIST intList, int value) {
		Boolean result = false;
		if (intList.getValue() == value) {
			result = true;
		} else if (intList.getAltNo() == 1) {
			result = execINTLIST(intList.getIntList(), value);
		}
		return result;
	}

	/*******************************************************************************************
	 ************************** Methods for Extracting Data from File **************************
	 *******************************************************************************************/

	/**
	 * Generate token list of lines from DATA file;
	 *
	 * @param data  file containing the input DATA
	 */
	private static void getData(String data) {
		BufferedReader reader = null;
		List<String> lines = new LinkedList<String>();
		try {
			reader = new BufferedReader(new FileReader(new File(data)));
			String line;
			while ((line = reader.readLine()) != null) lines.add(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ignored) { }
		}
		updateDataList(lines);
	}

	/**
	 * Update DATA list with integers from DATA lines;
	 *
	 * @param lines     lines of the DATA file read from BufferedReader
	 */
	private static void updateDataList(List<String> lines) {
		for (String line : lines) {
			int i = 0, len = line.length();
			while (i < len) {
				String token = "";
				// Ensure j - i = |next token|;
				int j = i + 1;
				// First character from character stream;
				char first = line.charAt(i);
				if (Character.isDigit(first) || first == '-') {
					while (j < len && Character.isDigit(line.charAt(j))) j++;
					token = line.substring(i, j);
				}
				// Try to parse int and add value to DATA;
				if (token.length() > 0) {
					try {
						DATA.add(Integer.parseInt(token));
					} catch (NumberFormatException e) {
						System.out.println("ERROR: Input " + token + " does not match a valid integer");
						System.exit(2);
					}
				}
				// Update index with length of built token; Only increment for whitespace;
				i = j;
			}
		}
	}

	/*******************************************************************************************
	 ***************************** Helper Methods for Exec Methods *****************************
	 *******************************************************************************************/

	/**
	 * For execIN; Set the id field of the idList to the Integer at DATA.remove(0);
	 *
	 * @param idList    node with the current ID field to set to input
	 */
	private static void setVarByInput(ID_LIST idList) {
		// Error if no more input tokens;
		if (DATA.size() > 0) {
			// Set value to first input token if id is in VARIABLES and |VARIABLES| > 0;
			String currentId = idList.getId();
			if (VARIABLES.containsKey(currentId)) {
				VARIABLES.put(currentId, DATA.remove(0));
			} else {
				System.out.println("ERROR: variable id " + currentId + " has not been declared");
				System.exit(2); // Failure Case;
			}
		} else {
			System.out.println("ERROR: no more input, cannot take input");
			System.exit(2); // Failure Case;
		}
	}

	/**
	 * For execOUT; Print value of id of idList to System.out if in VARIABLES;
	 *
	 * @param idList    node with the current ID field to output
	 */
	private static void outputVar(ID_LIST idList) {
		String currentId = idList.getId();
		if (VARIABLES.containsKey(currentId)) {
			if (VARIABLES.get(currentId) != null) {
				System.out.println(VARIABLES.get(currentId));
			} else {
				System.out.println("ERROR: variable id " + currentId + " has not been instantiated");
				System.exit(2); // Failure Case;
			}
		} else {
			System.out.print("ERROR: variable id " + currentId + " has not been declared");
			System.exit(2); // Failure Case;
		}
	}

	/**
	 * For resolving an ID to a CONST;
	 *
	 * @param id    identifier name to look up in VARIABLES
	 * @return      value associated to identifier name
	 */
	private static int getValueById(String id) {
		int result = 0;
		if (VARIABLES.containsKey(id)) {
			if (VARIABLES.get(id) != null) {
				result = VARIABLES.get(id);
			} else {
				System.out.println("ERROR: variable id " + id + " has not been instantiated");
				System.exit(2); // Failure Case;
			}
		} else {
			System.out.println("ERROR: variable id " + id + " has not been declared");
			System.exit(2); // Failure Case;
		}
		return result;
	}
}
