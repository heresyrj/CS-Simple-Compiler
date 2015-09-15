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
            //System.out.println(line);
            int splitPoint = line.indexOf('=');
            //System.out.println(""+splitPoint);
            String typeName = line.substring(0, splitPoint);//this is token type name
            int key = Integer.parseInt((line.substring(splitPoint + 1)));//this is int value of that type
            tokenNameLookup.put(key, typeName);
        }


    }

    public static void main(String[] args) throws Exception
    {
        tokenNameLookup.clear();
        formLookup();//forming the lookup table for token type
        String[] fileNames = {"fibonacci", "loop", "nested", "sqrt"};
        int index = 0;

        while (index < 4) {
            //get file name
            String currentFile = fileNames[index] + ".micro";
            //get string stream for test
            CharStream text =  new ANTLRFileStream(currentFile);
            MicroLexer lexer = new MicroLexer(text);

            //setup an output fie for current file, for diffing later
            String outputFileName = fileNames[index] + "Out.txt";
            PrintWriter out = new PrintWriter(new File(outputFileName));


            while (true)
            {


                Token token = lexer.nextToken();
                if (token.getType() == MicroLexer.EOF) break;


                String tokenType = tokenNameLookup.get(token.getType());
                String tokenValue = token.getText();
                //boolean supress = tokenValue.contains("\n") || tokenValue.contains("\r") || tokenValue.contains("--");

                if (!tokenType.equals("FORMAT"))//surpress irrelevat tokens
                {
                    if (tokenValue.contains(" "))
                    {
                        out.write("Token Type: " + tokenType + "\n");
                        out.write("Value: \" \" \n");
                    }
                    else
                    {
                        out.write("Token Type: " + tokenType + "\n");
                        out.write("Value: " + tokenValue + "\n");
                    }

                }

            }

            out.close();

            index++;
        }
    }
}