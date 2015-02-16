/**
 * Printer Class for the Core Interpreter Project;
 */
public class Printer {

	private Printer() { }

	/*************************************************************************************************
	 ****************************** Start Method for Beginning Printing ******************************
	 *************************************************************************************************/

	/**
	 * Generate pretty print for the program represented by the parse tree;
	 *
	 * @param parseTree     root PROG node for the parse tree of the program
	 */
	public static void prettyPrint(PROG parseTree) { printPROG(parseTree); }

	/*************************************************************************************************
	 *********************************** Methods for Pretty Print ************************************
	 *************************************************************************************************/

	/**
	 * Function to pretty print a PROG element;
	 *
	 * @param prog  PROG node to pretty print
	 */
	private static void printPROG(PROG prog) {
		System.out.println("program");
		printDECLSEQ(prog.getDeclSeq());
		System.out.println("begin");
		printSTMTSEQ(prog.getStmtSeq(), 1);
		System.out.println("end");
	}

	/**
	 * Function to pretty print a DECL_SEQ element;
	 *
	 * @param declSeq   DECL_SEQ node to pretty print
	 */
	private static void printDECLSEQ(DECL_SEQ declSeq) {
		indent(1);
		printDECL(declSeq.getDecl());
		// Decision: print another DECL_SEQ;
		if (declSeq.getAltNo() == 1) {
			printDECLSEQ(declSeq.getDeclSeq());
		}
	}

	/**
	 * Function to pretty print a DECL element;
	 *
	 * @param decl  DECL node to pretty print
	 */
	private static void printDECL(DECL decl) {
		System.out.print("int ");
		printIDLIST(decl.getIdList());
		System.out.println(";");
	}

	/**
	 * Function to pretty print a ID_LIST element;
	 *
	 * @param idList   ID_LIST node to pretty print
	 */
	private static void printIDLIST(ID_LIST idList) {
		System.out.print(idList.getId());
		// Decision: print another ID_LIST;
		if (idList.getAltNo() == 1) {
			System.out.print(",");
			printIDLIST(idList.getIdList());
		}
	}

	/**
	 * Function to pretty print a STMT_SEQ element;
	 *
	 * @param stmtSeq   STMT_SEQ node to pretty print
	 * @param indent    number of times to indent block
	 */
	private static void printSTMTSEQ(STMT_SEQ stmtSeq, int indent) {
		indent(indent);
		printSTMT(stmtSeq.getStmt(), indent);
		// Decision: print another STMT_SEQ;
		if (stmtSeq.getAltNo() == 1) {
			printSTMTSEQ(stmtSeq.getStmtSeq(), indent);
		}
	}

	/**
	 * Function to pretty print a STMT element;
	 *
	 * @param stmt      STMT node to pretty print
	 * @param indent    number of times to indent block
	 */
	private static void printSTMT(STMT stmt, int indent) {
		switch (stmt.getAltNo()) {
			case 1: // ASSIGN
				printASSIGN(stmt.getAssign());
				break;
			case 2: // IF
				printIF(stmt.getIf(), indent);
				break;
			case 3: // LOOP
				printLOOP(stmt.getLoop(), indent);
				break;
			case 4: // IN
				printIN(stmt.getIn());
				break;
			case 5: // OUT
				printOUT(stmt.getOut());
				break;
			case 6: // CASE
				printCASE(stmt.getCase(), indent);
				break;
			default:
				break;
		}
		System.out.println(";");
	}

	/**
	 * Function to pretty print a ASSIGN element;
	 *
	 * @param assignStmt    ASSIGN node to pretty print
	 */
	private static void printASSIGN(ASSIGN assignStmt) {
		System.out.print(assignStmt.getLvalue() + ":=");
		printEXPR(assignStmt.getExpr());
	}

	/**
	 * Function to pretty print a IF element;
	 *
	 * @param ifStmt    IF node to pretty print
	 * @param indent    number of times to indent block
	 */
	private static void printIF(IF ifStmt, int indent) {
		System.out.print("if");
		printCOND(ifStmt.getCond());
		System.out.println("then");
		printSTMTSEQ(ifStmt.getStmtSeq(), indent + 1);
		// Decision: print ELSE clause;
		if (ifStmt.getAltNo() == 1) {
			indent(indent);
			System.out.println("else");
			printSTMTSEQ(ifStmt.getElseStmtSeq(), indent + 1);
		}
		indent(indent);
		System.out.print("endif");
	}

	/**
	 * Function to pretty print a LOOP element;
	 *
	 * @param loopStmt  LOOP node to pretty print
	 * @param indent    number of times to indent block
	 */
	private static void printLOOP(LOOP loopStmt, int indent) {
		System.out.println("do");
		printSTMTSEQ(loopStmt.getStmtSeq(), indent + 1);
		indent(indent);
		System.out.print("while");
		printCOND(loopStmt.getCond());
		System.out.print("enddo");
	}

	/**
	 * Function to pretty print a IN element;
	 *
	 * @param inputStmt     INPUT node to pretty print
	 */
	private static void printIN(IN inputStmt) {
		System.out.print("input ");
		printIDLIST(inputStmt.getIdList());
	}

	/**
	 * Function to pretty print a OUTPUT element;
	 *
	 * @param outputStmt    OUTPUT node to pretty print
	 */
	private static void printOUT(OUT outputStmt) {
		System.out.print("output ");
		printIDLIST(outputStmt.getIdList());
	}

	/**
	 * Function to pretty print a COND element;
	 *
	 * @param cond  COND node to pretty print
	 */
	private static void printCOND(COND cond) {
		switch (cond.getAltNo()) {
			case 0: // !COND;
				System.out.print("!");
				printCOND(cond.getNeg());
				break;
			case 1: // (COND op COND);
				System.out.print("(");
				printCOND(cond.getLhs());
				System.out.print(cond.getOp());
				printCOND(cond.getRhs());
				System.out.print(")");
				break;
			case 2: // CMPR;
				printCMPR(cond.getCmpr());
				break;
			default:
				break;
		}
	}

	/**
	 * Function to pretty print a ID_LIST element;
	 *
	 * @param cmpr  CMPR node to pretty print
	 */
	private static void printCMPR(CMPR cmpr) {
		System.out.print("[");
		printEXPR(cmpr.getExpr1());
		printCMPROP(cmpr.getOp());
		printEXPR(cmpr.getExpr2());
		System.out.print("]");
	}

	/**
	 * Function to pretty print a CMPR_OP element;
	 *
	 * @param cmprOp    CMPR_OP node to pretty print
	 */
	private static void printCMPROP(CMPR_OP cmprOp) {
		if (cmprOp.getOp().equals("EQUALS")) {
			System.out.print("=");
		} else if (cmprOp.getOp().equals("LESS_THAN")) {
			System.out.print("<");
		} else if (cmprOp.getOp().equals("GREATER_THAN")) {
			System.out.print(">");
		} else if (cmprOp.getOp().equals("LESS_EQUAL")) {
			System.out.print("<=");
		} else if (cmprOp.getOp().equals("GREATER_EQUAL")) {
			System.out.print(">=");
		} else if (cmprOp.getOp().equals("NOT_EQUAL")) {
			System.out.print("!=");
		}
	}

	/**
	 * Function to pretty print a EXPR element;
	 *
	 * @param expr      EXPR node to pretty print
	 */
	private static void printEXPR(EXPR expr) {
		printTERM(expr.getTerm());
		// Decision: print OP EXPR;
		if (expr.getAltNo() == 1) {
			if (expr.getOp().equals("PLUS")) {
				System.out.print("+");
			} else {
				System.out.print("-");
			}
			printEXPR(expr.getExpr());
		}
	}

	/**
	 * Function to pretty print a TERM element;
	 *
	 * @param term      TERM node to pretty print
	 */
	private static void printTERM(TERM term) {
		printFACTOR(term.getFactor());
		// Decision: print * TERM;
		if (term.getAltNo() == 1) {
			System.out.print("*");
			printTERM(term.getTerm());
		}
	}

	/**
	 * Function to pretty print a FACTOR element;
	 *
	 * @param factor    FACTOR node to pretty print
	 */
	private static void printFACTOR(FACTOR factor) {
		switch (factor.getAltNo()) {
			case 0: // CONST;
				System.out.print(factor.getValue());
				break;
			case 1: // ID;
				System.out.print(factor.getId());
				break;
			case 2: // -<FACTOR>;
				System.out.print("-");
				printFACTOR(factor.getFactor());
				break;
			case 3: // (EXPR);
				System.out.print("(");
				printEXPR(factor.getExpr());
				System.out.print(")");
				break;
			default:
				break;
		}
	}

	/**
	 * Function to pretty print a CASE element;
	 *
	 * @param case_stmt     CASE node to pretty print
	 * @param indent        number of times to indent block
	 */
	private static void printCASE(CASE case_stmt, int indent) {
		System.out.println("case " + case_stmt.getId() + " of");
		indent(indent + 1);
		printCASES(case_stmt.getCases(), indent + 1);
		indent(indent);
		System.out.print("end");
	}

	/**
	 * Function to pretty print a CASES element;
	 *
	 * @param cases     CASES node to pretty print
	 * @param indent    number of times to indent block
	 */
	private static void printCASES(CASES cases, int indent) {
		printINTLIST(cases.getIntList());
		System.out.print(":");
		printEXPR(cases.getExpr());
		// Decision: more cases or else
		System.out.println();
		indent(indent);
		if (cases.getAltNo() == 1) {
			System.out.print("|");
			printCASES(cases.getCases(), indent);
		} else {
			System.out.print("else ");
			printEXPR(cases.getElseExpr());
		}
	}

	/**
	 * Function to pretty print a INT_LIST element;
	 *
	 * @param intList   INT_LIST node to pretty print
	 */
	private static void printINTLIST(INT_LIST intList) {
		System.out.print(intList.getValue());
		// Decision: print another INT_LIST;
		if (intList.getAltNo() == 1) {
			System.out.print(",");
			printINTLIST(intList.getIntList());
		}
	}

	/*************************************************************************************************
	 ******************************** Helper Method for Print Methods ********************************
	 *************************************************************************************************/

	/**
	 * Helper method to generate two-space indents for the pretty print;
	 *
	 * @param times     integer representing number of times to indent
	 */
	private static void indent(int times) { for(int i = 0; i < times; i++) System.out.print("  "); }
}
