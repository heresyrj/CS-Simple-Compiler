import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {
    protected static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 1;
    static int labelCounter = 1;
    static Stack<String> varNameSpace = new Stack<>();
    static Stack<String> codeLabelSpace = new Stack<>();
    static ArrayList<String> codeAggregete = new ArrayList<>();


    public static void addSymboltoTable(String varName,Symbol symbol)
    {
        SymbolTable.put(varName, symbol);
    }

    public static void addLineToCodeAggregate (String line) {codeAggregete.add(line);}

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
        boolean isCmp = s.equals("<") || s.equals(">") || s.equals("=") || s.equals("!=")
                || s.equals("<=") || s.equals(">=");
        boolean isCall = s.equals("WRITE") || s.equals("READ");
        if (isCall) return s;
        else if (isOp) return "OP";
        else if (isCmp) return "CMP";
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

        String name = "label" + labelCounter++;
        codeLabelSpace.push(name);
        return name;
    }
    public static String getRecentCodeLabel() {
        return codeLabelSpace.pop();
    }
    public static void storeCode(String code) {
        codeAggregete.add(code);
    }


    /***********************************************************************
     * AST tree generation
     * */
    private static String label4cmp;//this will be updated everytime for or ifelse occurs
    public static void setlabel4Cmp(String label) { label4cmp = label;}
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
            } else if (type.equals("CMP")) {
                ASTnode right = builderStack.pop();
                ASTnode left = builderStack.pop();
                cmpNode node = new cmpNode(current, left, right, label4cmp);
                execQueue.add(node);

            } else if (type.equals("WRITE")){
                ASTnode argument = builderStack.pop();
                ASTnode node = new callNode("WRITE", argument);
                execQueue.add(node);
            } else if (type.equals("READ")){
                //step1: create ASTnode for the var that will hold value that "READ" get
                ASTgenerator(expr);
                //step2: create "READ" ASTnode
                ASTnode argument = builderStack.pop();
                ASTnode node = new callNode("READ", argument);
                execQueue.add(node);
            } else {
                System.out.println("ERR: unrecognizable symbol \""+type+ "\" in expr");
            }
        }

    }


    public static void compile() {

        System.out.println(";IR code");
        for(String line : codeAggregete) {
            System.out.println(line);
        }

        System.out.println(";tiny code");

    }

}
