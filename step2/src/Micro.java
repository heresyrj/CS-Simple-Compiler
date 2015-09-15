import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;


public class Micro {

    MicroLexer lexer;
    TokenStream tokenStream;
    MicroParser parser;
    MyErrorHandler errorHandler;
    ParseTree tree;

    public Micro (String filePath) throws IOException, RecognitionException
    {
        //PART A: LEXER
        //Lexer splits input into tokens
        CharStream text =  new ANTLRFileStream(filePath);
        this.lexer = new MicroLexer(text);

        //PART B: TokenStream
        this.tokenStream = new CommonTokenStream(this.lexer);

        //PART C: PARSER
        //Parser generates abstract syntax tree
        this.parser = new MicroParser(this.tokenStream);

        //PART D: ErrorHandler
        this.errorHandler = new MyErrorHandler();
        this.parser.setErrorHandler(this.errorHandler);//assume parser class throws RecognitionException

        //PART D: Start to parse
        this.tree = parser.program();//refer to ANTLR4 Book
    }


    public static void main(String[] args) throws IOException, RecognitionException
    {

        Micro newTest = new Micro(args[0]);
        //program will exit here if encounters any error

        String treeString = newTest.tree.toStringTree(newTest.parser);
        //System.out.println(treeString);

        if (!treeString.isEmpty())
        {
            System.out.println("Accepted");
        }

    }
}