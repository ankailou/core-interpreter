CORE Interpreter
================
 
 * Author:      Ankai Lou
 * Project:     Interpreter for the CORE Language
 * Class:       CSE 3341 MWF 1:50-2:45pm (Spring 2015)
 * Language(s): Java

## Table of Contents
1. [Package Contents](#package contents)
2. [Compilation & Running the Project](#compilation & running the project)
3. [Special Issues during Compilation & Running](#special issues during compilation & running)
4. [Interpreter Design](#interpreter design)
5. [Testing the Core Interpreter](#testing the core interpreter)
6. [Resources](#resources)

## Package Contents

 * /src
    * Main.java:        interpreter: scanner > parser > printer > executor;
    * Scanner.java:     generate token stream; run tokenizer for raw input;
    * Tokenizer.java:   extract input from file; sends input stream to scanner;
    * Parser.java:      generate parse tree; class definitions for node types;
    * Printer.java:     output printing the program; generated from parse tree;
    * Executor.java:    run program and generate output of program given input;
    * makefile          makefile to compile and clean up project

## Compilation & Running the Project

 * The program and date file must be self-supplied and placed in the /src
 * When in the same directory as the README.txt, execute the command:
    cd src

 * Compile all the .java files in the package contents with the command:
    make

 * Run the interpreter on a program file with a data file via tha command:
    java Main <program-file> <data-file>

 * Command line argument usage:
    <program-file>  file containing program code
    <data-file>     file containing input data for the program

 * Lastly, to remove all .class files, execute the command:
    make clean

 * Ensure <program-file> and <data-file> are files in the current directory;
 * Ensure the run command has two arguments corresponding to valid files;

 * As of 2/08/2015, the initial version of the tokenizer is finished;
 * As of 2/09/2015, the initial version of the scanner & parser finished
 * As of 2/11/2015, the initial version of the printer & executor finished;
 * As of 2/13/2015, the Core interpreter has passed all applied test cases;
 * The printer & executor are called in the Main class - uncomment to see;

## Special Issues during Compilation & Running

 * Note: this project requires a JDK version of 1.7.0 or above to compile;
 * Note: the default Java -version for the OSU student linux server is 1.7.0;
 * Note: this interpreter will terminate as soon as an error is encountered;

 * As of 2/13/2015, the Core Interpreter checks for the following errors:
    * Scanner Errors:
        * SCANNER_ERROR token:    illegal character or identifier parsed;
        * Scanner.getConst():     unexpected token; NumberFormatException;
        * Scanner.getID():        unexpected token;
    * Parser Errors:
        * Expected statement;     unexpected token; not a valid statement;
        * Expected boolean op;    unexpected token; not a valid operator;
        * Expected comparison op; unexpected token; not a valid operator
        * Scanner.match(String);  Scanner.currentToken() != argument String;
    * Executor Errors:
        * execASSIGN();           undeclared variable;
        * execIDLIST();           repeated variable declaration;
        * setVarByInput();        undeclared variable; out of input data;
        * outputVar();            undeclared variable; uninstantiated variable;
        * getValueById();         undeclared variable; uninstantiated variable;
        * updateDataList();       NumberFormatException;

## Interpreter Design

 This CORE interpreter uses recursive descent to generate the parse tree for the 
 program and execute. For more detailed documentation for a specific method, field or class, consult
 the .java file where said method, field or class is located for the source.

### Main Class & General Structure

The main class found in Main.class is responsible for running/connecting
the main components of the interpreter, i.e.: the scanner class, parser
class, printer class, and executor class. Here is the order of tasks:

* Call Scanner.java to generate a stream of parsable tokens;
* Print all internally generated tokens - 2/24/2015 submission only;
* Call Parser.java to parse the tokens; Return the root to the parse tree;
* Call Printer.java to generate a well-formatted output of the program;
* Call Executor.java to execute the program via the parse tree and data;

* Command Line Argument 0 - the program file - sent to the Scanner;
* Command Line Argument 1 - the input data file - sent to the Executor;

### Scanner & Tokenizer

* API for the Scanner class:
     * class Scanner
        * String ILLEGAL
        * String[] KEYWORD
        * void begin(String program)
        * String currentToken()
        * void nextToken()
        * void resetTokenStream()
        * void match(String token)
        * String getID()
        * int getConst()
        * String getParsableToken(String token)
        * Boolean containsIllegalChar(String token)
        * void printTokens()

* API for the Tokenizer class:
     * class Tokenizer
        * String WHITESPACE
        * String SPECIAL_SYMBOL
        * String SYMBOL
        * List<String> TOKENS
        * int TRACKER
        * void begin(String program)
        * String currentToken()
        * void nextToken()
        * void resetTokenStream()
        * void tokenize(List<String> lines)

* The Scanner passes a program tokenizer the program file name;
* The Tokenizer extracts program code from the file by line as raw input;
* The Tokenizer maintains a private List<String> TOKEN for the tokens;
* The Scanner has 3 methods that call similar methods in the Tokenizer;
    * currentToken()        read the current token in the stream;
    * nextToken()           advance the token stream by one place;
    * resetTokenStream()    reset the token stream to the beginning;
* The Scanner maps input from the Tokenizer to tokens for the Parser;
* The Scanner is the only class that interfaces with the Tokenizer;
* The Scanner interfaces with the Main class to print the internal tokens;
* The Scanner interfaces with the Parser class to generate a parse tree;

### Interface from Scanner to Parser

The Parser class requires one sequential pass through the tokens to
generate a parse tree for the program. The Parser uses 5 of the 6 static
methods in the Scanner Class to retrieve and advance the token stream,
confirm a token, and extract an identifier or constant value from a token:

* Static Methods in Scanner Class:
    * Scanner.nextToken();
    * Scanner.currentToken();
    * Scanner.match(String token);
    * Scanner.getID();
    * Scanner.getConst();

### Parser & Building the Parse Tree

The Parser class API consists of one method: getParseTree(). This method
begins the recursive descent through the parse tree node classes also in
the Parser.java file. Each node in the parse tree build is an object of
one of the parse tree node classes:

* Parse Tree Node Classes in Parser.java:
     * PROG getParseTree()
     * class PROG
     * class DECL_SEQ
     * class DECL
     * class ID_LIST
     * class STMT_SEQ
     * class STMT
     * class ASSIGN
     * class IF
     * class LOOP
     * class IN
     * class OUT
     * class COND
     * class CMPR
     * class CMPR_OP
     * class EXPR
     * class TERM
     * class FACTOR
     * class CASE
     * class CASES
     * class INT_LIST

The Parser returns the top-level PROG node to the Main class as the tree.
The API for each parse tree node class consists of private fields, getter
methods for said private members, and a parse method for recursive parsing.
The fields and getter methods correspond to the Core language grammar in
the Languages & Grammars and Recursive Descent slides and Homework 2.

### Printer & Pretty Printing the Tree

The Printer class API consists of several methods to recursively print a
well-formatted program using the parse tree generated by the parser. The
methods in the API are self-explanatory and also use recursive descent.

* API for the Printer class:
     * void prettyPrint(PROG parseTree)
     * void printPROG(PROG prog)
     * void printDECLSEQ(DECL_SEQ declSeq)
     * void printDECL(DECL decl)
     * void printIDLIST(ID_LIST idList)
     * void printSTMTSEQ(STMT_SEQ stmtSeq, int indent)
     * void printSTMT(STMT stmt, int indent)
     * void printASSIGN(ASSIGN assignStmt)
     * void printIF(IF ifStmt, int indent)
     * void printLOOP(LOOP loopStmt, int indent)
     * void printIN(IN inputStmt)
     * void printOUT(OUT outputStmt)
     * void printCOND(COND cond)
     * void printCMPR(CMPR cmpr)
     * void printCMPROP(CMPR_OP cmprOp)
     * void printEXPR(EXPR expr)
     * void printTERM(TERM term)
     * void printFACTOR(FACTOR factor)
     * void printCASE(CASE case, int indent)
     * void printCASES(CASES cases, int indent)
     * void printINTLIST(INT_LIST intList)
     * void indent(int times)

### Executor & Program Execution

The Executor class API has one public method execute() to begin execution
of the program code. To support this end, the API is divided into four
subsets: 1) methods for executing each parse tree node type; 2) helper
methods for getting, instantiating, and outputting program variables;
3) methods for extracting integer data tokens from a file; 4) private
members for storing the id-value pairs and input data tokens;

* API for the Executor class:
     * HashMap<String,Integer> VARIABLES
     * List<Integer> DATA
     * void execute(PROG parseTree, String data)
     * void execPROG(PROG prog)
     * void execDECLSEQ(DECL_SEQ declSeq)
     * void execDECL(DECL decl)
     * void execIDLIST(ID_LIST idList)
     * void execSTMTSEQ(STMT_SEQ stmtSeq)
     * void execSTMT(STMT stmt)
     * void execASSIGN(ASSIGN assignStmt)
     * void execIF(IF ifStmt)
     * void execLOOP(LOOP loopStmt)
     * void execIN(IN inputStmt)
     * void execOUT(OUT outputStmt)
     * Boolean execCOND(COND cond)
     * Boolean execCMPR(CMPR cmpr)
     * int execEXPR(EXPR expr)
     * int execTERM(TERM term)
     * int execFACTOR(FACTOR factor)
     * void execCASE(CASE caseStmt)
     * void execCASES(CASES cases, String id, int value)
     * Boolean execINTLIST(INT_LIST intList, int value)
     * void getData(String data)
     * void updateDataList(List<String> lines)
     * void setVarByInput(ID_LIST idList)
     * void outputVar(ID_LIST idList)
     * void getVarById(String id)

## Testing the CORE Interpreter

 * The interpreter passed all of the weak test cases provided by Professor Bond.
 * The interpreter passed all of the component tests for each kind of statement
   including CASE, CASES, INT_LIST, IF, IF-ELSE, and various kinds of EXPR.
 * The interpreter passed all tests for the printer including generic test cases
   as well as special cases: e.g. IF-ELSE nested in CASE and CASE nested in LOOP
   and various other combinations of statements and expressions.
 * The executor was tested along side the printer for success cases as both used
   a similar kind of recursive descent to print and execute the code.
 * All of the weak failure test cases provided by Professor Bond were passed.
 * All of the error-checking in the Special Issues during Compilation & Running
   section were tested and the interpreter passed all of the given cases.
 * Note: the testing did not account for error-checking beyond those previously
   specified in this README. Potential bugs include any errors not taken into
   account by the implementer - which would therefore not have been implemented.

## Resources

 * Ideas for the interpreter - specifically the parser and executor - were taken
   from the Recursive Descent slides on the CSE 3341 page of Prof. Bond's site.
 * References to the Core grammar in the Languages & Grammars slides were used
   to confirm the validity of the parser, printer, and executor implementations.
 * The Oracle Java 7 API (link: http://docs.oracle.com/javase/7/docs/api/) was
   used/referenced  to resolve issues during the implementation process.