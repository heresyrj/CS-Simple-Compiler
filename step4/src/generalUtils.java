import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {
    private static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 0;
    static int labelCounter = 0;
    static ArrayList<String> varNameSpace = new ArrayList<>();
    static ArrayList<String> codeLabelSpace = new ArrayList<>();
    static Stack<String> exprStack = new Stack<>();



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
    public static void convertExpr2Stack(char[] symbols) {


    }


}
