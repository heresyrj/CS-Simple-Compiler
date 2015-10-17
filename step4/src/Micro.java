import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Micro {

    MicroLexer lexer;
    TokenStream tokenStream;
    MicroParser parser;
    MyErrorHandler errorHandler;
    mySTbuilder listener;
    myASTbuilder listener1;
    ParseTree tree;//ParseTree is essentially an interface.
    ParserRuleContext prc;//ParserRuleContext implemented the ParseTree interface and other interfaces as well
    Scope wrapperScope;

    public Micro(String filePath) throws IOException, RecognitionException {
        //PART A: LEXER
        //Lexer splits input into tokens
        CharStream text = new ANTLRFileStream(filePath);
        lexer = new MicroLexer(text);

        //PART B: TokenStream
        tokenStream = new CommonTokenStream(lexer);

        //PART C: PARSER
        //Parser generates abstract syntax tree
        parser = new MicroParser(tokenStream);

        //PART D: ErrorHandler
        errorHandler = new MyErrorHandler();
        parser.setErrorHandler(errorHandler);//assume parser class throws RecognitionException


        //PART E: add listener to for parsing
        parser.setBuildParseTree(true);//check API for explanation
        wrapperScope = new Scope("wrapper", null);
        listener = new mySTbuilder(wrapperScope);
        listener1 = new myASTbuilder();
        parser.addParseListener(listener);//associate the listener with parser
        parser.addParseListener(listener1);

        //PART E: Start to parse and get tree
        prc = parser.program();//refer to ANTLR4 Book p239
        tree = prc;
    }


    public static void main(String[] args) throws Exception
    {
        //program will exit here if encounters any error, handled in my ErrorHandler
        Micro newTest = new Micro(args[0]);

        //

        //print symbol table
        //newTest.wrapperScope.printSymbols();
    }
}