package utils;
import symbolTable.Symbol;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {
    private static HashMap<String, Symbol> SymbolTable;

    static int varCounter = 0;
    static int labelCounter = 0;
    static ArrayList<String> varNameSpace;
    static ArrayList<String> codeLabelSpace;

    public generalUtils() {
        varNameSpace = new ArrayList<>();
        codeLabelSpace = new ArrayList<>();
        SymbolTable = new HashMap<>();
    }


    public static void addSymboltoTable(String varName,Symbol symbol)
    {
        SymbolTable.put(varName, symbol);
    }

    /**Validation utils*/
    public static boolean checkExist(String varName)
    {
        return SymbolTable.containsKey(varName);
    }
    public static String getVarType (String varName)
    {
        return SymbolTable.get(varName).sym_getType();
    }

    /*
    public static boolean checkTypesConsistent(ASTnode left, ASTnode right)
    {
        boolean bool1 = left.getType().equals("VAR");
        boolean bool2 = right.getType().equals("VAR");

        if (bool1&&bool2)
        {
            return getVarType(left.getValue()).equals(getVarType(right.getValue()));

        } else if (bool1&&(!bool2)) {


        }  else if ((!bool1)&&(!bool2)){

        } else    {

        }


        boolean bool312 = left.getValue().contains(".");
        boolean bool2312= right.getValue().contains(".");
        boolean A = bool1&&bool2; //both float
        boolean B = (!bool1) && (!bool2); //both int
        //true if they are all int or all float
        return A || B;
    }
    */


    /**Global namespaces*/
    public static String generateVarName() {

        String name = "var" + varCounter;
        varNameSpace.add(name);
        return name;
    }

    public static String generateCodeLabel() {

        String name = "label" + labelCounter;
        codeLabelSpace.add(name);
        return name;
    }

    /**Code generation utils*/
    public static String concatSimpleCode(String left, String middle, String right)
    {
        return left + " " + middle + " " + right;
    }




}
