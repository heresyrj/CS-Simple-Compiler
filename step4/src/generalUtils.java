import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {
    private static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 1;
    static int labelCounter = 1;
    static Stack<String> varNameSpace = new Stack<>();
    static Stack<String> codeLabelSpace = new Stack<>();
    static ArrayList<String> codeAggregete = new ArrayList<>();


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
    public static String varORvalue (String s)
    {
        boolean isOp = s.equals(":=") || s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
        boolean isCall = s.equals("WRITE");
        if (isCall) return "WRITE";
        else if (isOp) return "OP";
        else if (isInteger(s)) return "INT";
        else if (isFloat(s)) return "FLOAT";
        else {
            if (checkExist(s)) {
                return getVarType(s)+"VAR";
            }
            else return "ERR";
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    public static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


    /**Global namespaces*/
    public static String generateVarName() {

        String name = "$T" + varCounter++;
        varNameSpace.push(name);
        return name;
    }
    public static String getRecentVarName() {
        return varNameSpace.pop();
    }

    public static String generateCodeLabel() {

        String name = "label" + labelCounter;
        codeLabelSpace.push(name);
        return name;
    }
    public static String getRecentCodeLabel() {
        return codeLabelSpace.pop();
    }
    public static void storeCode(String code) {
        codeAggregete.add(code);
    }

    /**Code generation utils
     * This is the main function to create AST and Code
     * */
    static Stack<ASTnode> builderStack = new Stack<>();
    static ArrayList<ASTnode> execQueue = new ArrayList<>();//stores all the statement AST
    public static void ASTgenerator(ArrayList<String> expr) {
        //step1 TODO: create ASTnode
        while(!expr.isEmpty())
        {
            String current = expr.remove(0);
            String type = varORvalue(current);
            if(type.contains("VAR")) {
                simpleNode node = new simpleNode(type,current);
                builderStack.push(node);
            } else if (type.equals("INT")) {
                simpleNode node = new simpleNode("INT",current);
                builderStack.push(node);
            } else if (type.equals("FLOAT")) {
                simpleNode node = new simpleNode("FLOAT",current);
                builderStack.push(node);
            } else if (type.equals("OP")) {
                ASTnode right = builderStack.pop();
                ASTnode left = builderStack.pop();
                opNode node = new opNode(current, left, right);
                if(current.equals(":=")){
                    execQueue.add(node);
                } else {
                    builderStack.push(node);
                }

            } else if (type.equals("WRITE")){
                ASTnode argument = builderStack.pop();
                ASTnode node = new callNode("WRITE", argument);
                execQueue.add(node);
            } else {
                System.out.println("ERR: unrecognizable symbol in expr");
            }
        }

    }

    public static void printIR() {
        System.out.println(";IR code");
        int size = codeAggregete.size();
        int i = 0;
        while(i < size) {
            System.out.println(codeAggregete.get(i));
            i++;
        }
        System.out.println(";tiny code");
    }

}
