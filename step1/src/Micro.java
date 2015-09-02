import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import org.antlr.v4.runtime.*;


public class Micro {

    public static HashMap<Integer, String> tokenNameLookup = new HashMap<Integer, String>();

    public static void formLookup () throws FileNotFoundException
    {
        Scanner tokenScan = new Scanner(new File("./build/MicroLexer.tokens"));
        while (tokenScan.hasNextLine())
        {
            String line = tokenScan.nextLine();
            int splitPoint = line.indexOf('=');
            String typeName = line.substring(0, splitPoint);//this is token type name
            int key = Integer.parseInt((line.substring(splitPoint + 1)));//this is int value of that type
            tokenNameLookup.put(key, typeName);
        }


    }

    public static void main(String[] args) throws Exception
    {
        tokenNameLookup.clear();
        formLookup();//forming the lookup table for token type
        
        //get file name
        String currentFile = args[0];

        //get string stream for test
        CharStream text =  new ANTLRFileStream(currentFile);
        MicroLexer lexer = new MicroLexer(text);

        while (true)
        {
            Token token = lexer.nextToken();
            if (token.getType() == MicroLexer.EOF) break;


            String tokenType = tokenNameLookup.get(token.getType());
            String tokenValue = token.getText();

            if (!tokenType.equals("FORMAT") && !tokenType.equals("STRINGLITERAL") && !tokenType.equals("COMMENT"))//surpress irrelevat tokens
            {
                if (tokenValue.contains(" "))
                {
                    System.out.print("Token Type: " + tokenType + "\n");
                    System.out.print("Value: \" \" \n");
                }
                else
                {
                    System.out.print("Token Type: " + tokenType + "\n");
                    System.out.print("Value: " + tokenValue + "\n");
                }

            }
            if(tokenType.equals("STRINGLITERAL")) 
            {
                System.out.print("Token Type: " + tokenType + "\n");
                System.out.print("Value: " + tokenValue + "\n");
            }

        }
    }
}