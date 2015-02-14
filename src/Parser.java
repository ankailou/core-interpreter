/**
 * Parser Class for the Core Interpreter Project;
 */
public class Parser {

	private Parser () { }

	/***************************************************************************************************
	 **************************** Start Method for Generating Parse Tree *******************************
	 ***************************************************************************************************/

	/**
	 * Call Scanner/Tokenizer to generate token stream; Parse token stream; Build & return parse tree;
	 *
	 * @return          root node of the program parse tree
	 */
	public static PROG getParseTree() {
		// Generate Parse Tree;
		PROG tree = new PROG(); tree.parse();
		return tree;
	}
}

/*******************************************************************************************************
 ********* Class Representation of each Parse Tree Node Type in the Core Programming Language **********
 *******************************************************************************************************/

/**
 * Class for the PROG node; Parsing and accessing child node(s) enabled;
 */
class PROG {
	private DECL_SEQ declSeq;
	private STMT_SEQ stmtSeq;

	public void parse() {
		Scanner.match("PROGRAM");
		declSeq = new DECL_SEQ(); declSeq.parse();
		Scanner.match("BEGIN");
		stmtSeq = new STMT_SEQ(); stmtSeq.parse();
		Scanner.match("END");
		// Prevent straggling code after END;
		Scanner.match("EOF");
	}

	public DECL_SEQ getDeclSeq() { return declSeq; }
	public STMT_SEQ getStmtSeq() { return stmtSeq; }
}

/**
 * Class for the DECL_SEQ node; Parsing and accessing child node(s) enabled;
 */
class DECL_SEQ {

	private int altNo = 0;      // Decision;
	private DECL decl;          // 0 ::= <decl>;
	private DECL_SEQ declSeq;   // 1 ::= <decl><declSeq>;

	public void parse() {
		decl = new DECL(); decl.parse();
		// Parse another DECL_SEQ if no BEGIN token;
		if (!Scanner.currentToken().equals("BEGIN")) {
			altNo = 1;
			declSeq = new DECL_SEQ(); declSeq.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public DECL getDecl() { return decl; }
	public DECL_SEQ getDeclSeq() { return declSeq; }
}

/**
 * Class for the DECL node; Parsing and accessing child node(s) enabled;
 */
class DECL {

	private ID_LIST idList;

	public void parse() {
		Scanner.match("INT");
		idList = new ID_LIST(); idList.parse();
		Scanner.match("SEMICOLON");
	}

	public ID_LIST getIdList() { return idList; }
}

/**
 * Class for the ID_LIST node; Parsing and accessing child node(s) enabled;
 */
class ID_LIST {

	private int altNo = 0;  // Decision;
	private String id;      // 0 ::= id;
	private ID_LIST idList; // 1 ::= id<idList>;

	public void parse() {
		id = Scanner.getID();
		// Parse another ID_LIST if COMMA token is encountered;
		if(Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			idList = new ID_LIST(); idList.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public String getId() { return id; }
	public ID_LIST getIdList() { return idList; }
}

/**
 * Class for the STMT_SEQ node; Parsing and accessing child node(s) enabled;
 */
class STMT_SEQ {

	private int altNo = 0;      // Decision;
	private STMT stmt;          // 0 ::= <stmt>;
	private STMT_SEQ stmtSeq;   // 1 ::= <stmt><stmtSeq>;

	public void parse() {
		stmt = new STMT(); stmt.parse();
		// Parse another STMT_SEQ if not the end of a PROG, IF, LOOP, CASE, or ELSE statement;
		String token = Scanner.currentToken();
		if (!token.equals("END") && !token.equals("ENDIF")
				&& !token.equals("WHILE") && !token.equals("ELSE")) {
			altNo = 1;
			stmtSeq = new STMT_SEQ(); stmtSeq.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public STMT getStmt() { return stmt; }
	public STMT_SEQ getStmtSeq() { return stmtSeq; }
}

/**
 * Class for the STMT node; Parsing and accessing child node(s) enabled;
 */
class STMT {

	private int altNo;  // Decision;
	private ASSIGN s1;  // 1 ::= <assign>;
	private IF s2;      // 2 ::= <if>;
	private LOOP s3;    // 3 ::= <loop>;
	private IN s4;      // 4 ::= <input>;
	private OUT s5;     // 5 ::= <output>;
	private CASE s6;    // 6 ::= <case>;

	public void parse() {
		String token = Scanner.currentToken();
		if (token.contains("ID")) {
			altNo = 1;
			s1 = new ASSIGN(); s1.parse();
		} else if (token.equals("IF")) {
			altNo = 2;
			s2 = new IF(); s2.parse();
		} else if (token.equals("DO")) {
			altNo = 3;
			s3 = new LOOP(); s3.parse();
		} else if (token.equals("INPUT")) {
			altNo = 4;
			s4 = new IN(); s4.parse();
		} else if (token.equals("OUTPUT")) {
			altNo = 5;
			s5 = new OUT(); s5.parse();
		} else if (token.equals("CASE")) {
			altNo = 6;
			s6 = new CASE(); s6.parse();
		} else {
			System.out.println("ERROR: Expected a statement, found " + token);
			System.exit(2);
		}
		// All statements end with SEMICOLON;
		Scanner.match("SEMICOLON");
	}

	public int getAltNo() { return altNo; }
	public ASSIGN getAssign() { return s1; }
	public IF getIf() { return s2; }
	public LOOP getLoop() { return s3; }
	public IN getIn() { return s4; }
	public OUT getOut() { return s5; }
	public CASE getCase() { return s6; }
}

/**
 * Class for the ASSIGN node; Parsing and accessing child node(s) enabled;
 */
class ASSIGN {

	private EXPR expr;
	private String lvalue;

	public void parse() {
		lvalue = Scanner.getID();
		Scanner.match("ASSIGN");
		expr = new EXPR(); expr.parse();
	}

	public EXPR getExpr() { return expr; }
	public String getLvalue() { return lvalue; }
}

/**
 * Class for the IF node; Parsing and accessing child node(s) enabled;
 */
class IF {

	private int altNo = 0;          // Decision;
	private COND cond;              // 0 ::= if <cond> then <stmtSeq>;
	private STMT_SEQ stmtSeq;       // 0 ::= if <cond> then <stmtSeq>;
	private STMT_SEQ elseStmtSeq;   // 1 ::= if <cond> then <stmtSeq> else <stmtSeq>;

	public void parse() {
		Scanner.match("IF");
		cond = new COND(); cond.parse();
		Scanner.match("THEN");
		stmtSeq = new STMT_SEQ(); stmtSeq.parse();
		// Parse ELSE statement if encountered;
		String token = Scanner.currentToken();
		if (token.equals("ELSE")) {
			altNo = 1;
			Scanner.nextToken();
			elseStmtSeq = new STMT_SEQ(); elseStmtSeq.parse();
		}
		Scanner.match("ENDIF");
	}

	public int getAltNo() { return altNo; }
	public COND getCond() { return cond; }
	public STMT_SEQ getStmtSeq() { return stmtSeq; }
	public STMT_SEQ getElseStmtSeq() { return elseStmtSeq; }
}

/**
 * Class for the LOOP node; Parsing and accessing child node(s) enabled;
 */
class LOOP {

	private STMT_SEQ stmtSeq;
	private COND cond;

	public void parse() {
		Scanner.match("DO");
		stmtSeq = new STMT_SEQ(); stmtSeq.parse();
		Scanner.match("WHILE");
		cond = new COND(); cond.parse();
		Scanner.match("ENDDO");
	}

	public STMT_SEQ getStmtSeq() { return stmtSeq; }
	public COND getCond() { return cond; }
}

/**
 * Class for the IN node; Parsing and accessing child node(s) enabled;
 */
class IN {

	private ID_LIST idList;

	public void parse() {
		Scanner.match("INPUT");
		idList = new ID_LIST(); idList.parse();
	}

	public ID_LIST getIdList() { return idList; }
}

/**
 * Class for the OUT node; Parsing and accessing child node(s) enabled;
 */
class OUT {

	private ID_LIST idList;

	public void parse() {
		Scanner.match("OUTPUT");
		idList = new ID_LIST(); idList.parse();
	}

	public ID_LIST getIdList() { return idList; }
}

/**
 * Class for the COND node; Parsing and accessing child node(s) enabled;
 */
class COND {

	private int altNo;  // Decision;
	private COND neg;   // 0 ::= !<cond>;
	private COND lhs;   // 1 ::= (<cond> op <cond>);
	private COND rhs;   // 1 ::= (<cond> op <cond>);
	private String op;  // 1 ::= (<cond> op <cond>);
	private CMPR cmpr;  // 2 ::= cmpr;

	public void parse() {
		String token = Scanner.currentToken();
		if (token.equals("NOT")) { // !<cond>;
			altNo = 0;
			Scanner.nextToken();
			neg = new COND(); neg.parse();
		} else if (token.equals("LEFT_PAREN")) { // (<cond> op <cond>);
			altNo = 1;
			Scanner.nextToken();
			lhs = new COND(); lhs.parse();
			token = Scanner.currentToken();
			if (token.equals("AND") || token.equals("OR")) {
				op = token;
				Scanner.nextToken();
			} else {
				System.out.println("ERROR: Expected a boolean operator, found " + token);
			}
			rhs = new COND(); rhs.parse();
			Scanner.match("RIGHT_PAREN");
		} else { // <cmpr>;
			altNo = 2;
			cmpr = new CMPR(); cmpr.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public CMPR getCmpr() { return cmpr; }
	public COND getNeg() { return neg; }
	public COND getLhs() { return lhs; }
	public COND getRhs() { return rhs; }
	public String getOp() { return op; }
}

/**
 * Class for the CMPR node; Parsing and accessing child node(s) enabled;
 */
class CMPR {

	private CMPR_OP op;
	private EXPR expr1;
	private EXPR expr2;

	public void parse() {
		Scanner.match("LEFT_BRACKET");
		expr1 = new EXPR(); expr1.parse();
		op = new CMPR_OP(); op.parse();
		expr2 = new EXPR(); expr2.parse();
		Scanner.match("RIGHT_BRACKET");
	}

	public CMPR_OP getOp() { return op; }
	public EXPR getExpr1() { return expr1; }
	public EXPR getExpr2() { return expr2; }
}

/**
 * Class for the CMPR_OP node; Parsing and accessing child node(s) enabled;
 */
class CMPR_OP {

	private String op;

	public void parse() {
		String token = Scanner.currentToken();
		if (token.equals("EQUALS") || token.equals("LESS_THAN") ||
				token.equals("GREATER_THAN") || token.equals("LESS_EQUAL") ||
				token.equals("GREATER_EQUAL") || token.equals("NOT_EQUAL")) {
			op = token;
		} else {
			System.out.println("ERROR: Expected a comparison operator, found " + token);
			System.exit(2); // Failure Case
		}
		Scanner.nextToken();
	}

	public String getOp() { return op; }
}

/**
 * Class for the EXPR node; Parsing and accessing child node(s) enabled;
 */
class EXPR {

	private int altNo = 0;  // Decision;
	private TERM term;      // 0 ::= <term>;
	private EXPR expr;      // 1 ::= <term> op <expr>
	private String op;      // 1 ::= <term> op <expr>

	public void parse() {
		term = new TERM(); term.parse();
		// Continue parsing if arithmetic operator is encountered;
		String token = Scanner.currentToken();
		if (token.equals("PLUS") || token.equals("MINUS")) {
			altNo = 1;
			op = token;
			Scanner.nextToken();
			expr = new EXPR(); expr.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public TERM getTerm() { return term; }
	public EXPR getExpr() { return expr; }
	public String getOp() { return op; }
}

/**
 * Class for the TERM node; Parsing and accessing child node(s) enabled;
 */
class TERM {

	private int altNo = 0;  // Decision;
	private FACTOR factor;  // 0 ::= <factor>;
	private TERM term;      // 1 ::= <factor> * <term>;

	public void parse() {
		factor = new FACTOR(); factor.parse();
		// Continue parsing TIMES operator is encountered;
		if (Scanner.currentToken().equals("TIMES")) {
			altNo = 1;
			Scanner.nextToken();
			term = new TERM(); term.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public FACTOR getFactor() { return factor; }
	public TERM getTerm() { return term; }
}

/**
 * Class for the FACTOR node; Parsing and accessing child node(s) enabled;
 */
class FACTOR {

	private int altNo;      // Decision;
	private int value;      // 0 ::= const;
	private String id;      // 1 ::= id;
	private FACTOR factor;  // 2 ::= -<factor>;
	private EXPR expr;      // 3 ::= (<expr>);

	public void parse() {
		String token = Scanner.currentToken();
		if (token.contains("CONST")) { // const;
			altNo = 0;
			value = Scanner.getConst();
		} else if (token.contains("ID")) { // id;
			altNo = 1;
			id = Scanner.getID();
		} else if (token.equals("MINUS")) { // -<factor>;
			altNo = 2;
			Scanner.nextToken();
			factor = new FACTOR(); factor.parse();
		} else if (token.equals("LEFT_PAREN")) { // (<expr>);
			altNo = 3;
			Scanner.match("LEFT_PAREN");
			expr = new EXPR(); expr.parse();
			Scanner.match("RIGHT_PAREN");
		}
	}

	public int getAltNo() { return altNo; }
	public int getValue() { return value; }
	public String getId() { return id; }
	public FACTOR getFactor() { return factor; }
	public EXPR getExpr() { return expr; }
}

/**
 * Class for the CASE node; Parsing and accessing child node(s) enabled;
 */
class CASE {

	private String id;
	private CASES cases;

	public void parse() {
		Scanner.match("CASE");
		id = Scanner.getID();
		Scanner.match("OF");
		cases = new CASES(); cases.parse();
		Scanner.match("END");
	}

	public String getId() { return id; }
	public CASES getCases() { return cases; }
}

/**
 * Class for the CASES node; Parsing and accessing child node(s) enabled;
 */
class CASES {

	private int altNo = 0;      // Decision;
	private INT_LIST intList;   // 0 ::= <intList> : <expr>;
	private EXPR expr;          // 0 ::= <intList> : <expr>;
	private EXPR elseExpr;      // 1 ::= <intList> : <expr> else <expr>;
	private CASES cases;        // 2 ::= <intList> : <expr> BAR <cases>;

	public void parse () {
		intList = new INT_LIST(); intList.parse();
		Scanner.match("COLON");
		expr = new EXPR(); expr.parse();
		// Continue parsing additional CASES or ELSE;
		String token = Scanner.currentToken();
		if (token.equals("ELSE")) {
			altNo = 1;
			Scanner.nextToken();
			elseExpr = new EXPR(); elseExpr.parse();
		} else if (token.equals("BAR")) {
			altNo = 2;
			Scanner.nextToken();
			cases = new CASES(); cases.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public INT_LIST getIntList() { return intList; }
	public EXPR getExpr() { return expr; }
	public EXPR getElseExpr() { return elseExpr; }
	public CASES getCases() { return cases; }
}

/**
 * Class for the INT_LIST node; Parsing and accessing child node(s) enabled;
 */
class INT_LIST {

	private int altNo = 0;      // Decision;
	private int value;          // 0 ::= int;
	private INT_LIST intList;   // 1 ::= int, <intList>;

	public void parse() {
		value = Scanner.getConst();
		// Parse another INT_LIST if COMMA is encountered
		if(Scanner.currentToken().equals("COMMA")) {
			altNo = 1;
			Scanner.nextToken();
			intList = new INT_LIST(); intList.parse();
		}
	}

	public int getAltNo() { return altNo; }
	public int getValue() { return value; }
	public INT_LIST getIntList() { return intList; }
}
