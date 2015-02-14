JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java
CLASSES = \
	Tokenizer.java \
	Scanner.java \
	Parser.java \
	Printer.java \
	Executor.java \
	Main.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
