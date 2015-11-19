import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {

    protected static HashMap<String, HashMap> directoryLookup = new HashMap<>();
    protected static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 1;
    static int labelCounter = 1;
    static ArrayList<String> codeAggregete = new ArrayList<>();

    static Stack<String> codeLabelSpace = new Stack<>();

    //store all vars which are function names
    static ArrayList<funcSymbol> funcSyms = new ArrayList<>();

    public static void addSymboltoTable(String varName,Symbol symbol)
    {
        SymbolTable.put(varName, symbol);
    }
    public static void organizeSymbolTable () {
        //step1: get rid of symbols that not in GLOBAL scope
        ArrayList<String> buffer = new ArrayList<>();
        for(String var : SymbolTable.keySet()) {
            if(SymbolTable.get(var).sym_getParentScope().getName().equals("FUNCTION"))
            {
                buffer.add(var);
            }
        }
        for(String var : buffer) {
            SymbolTable.remove(var);
        }
        //step2: add global vars hashtable into directory lookup
        directoryLookup.put("GLOBAL", SymbolTable);

        //step3: forming local hashtables and add to directory lookup
        for(String var : SymbolTable.keySet()) {
            Symbol s = SymbolTable.get(var);
            if(s instanceof funcSymbol)
            {
                funcSyms.add((funcSymbol) s);
            }
        }
        for(funcSymbol s : funcSyms) {
            //generate HashMaps for local scope
            s.localSymbolTable();
        }
    }

    /**Validation utils*/
    public static String getVarType (String varName)
    {
        String type;
        if (SymbolTable.containsKey(varName)) {
            type = SymbolTable.get(varName).sym_getType() + "VAR";
            return type;
        }
        else {
            for(funcSymbol s : funcSyms) {
                if(s.isLocal(varName)) {
                    type = s.getLocalType(varName) + "VAR";
                    return type;
                }
            }
            return "ERR";
        }
    }

    public static String localOrPara(String varName) {
        String result;
        String scope = getCurrentScope();
        if (SymbolTable.containsKey(varName)) result = "NOT";
        else {
            funcSymbol fs = (funcSymbol) SymbolTable.get(scope);
            if(fs.getLocalOrPara(varName)) return "PARA";
            else return "LOCAL";
        }

        return result;
    }

    public static String varORvalue (String s)
    {
        boolean isOp = s.equals(":=") || s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
        boolean isCmp = s.equals("<") || s.equals(">") || s.equals("=") || s.equals("!=")
                || s.equals("<=") || s.equals(">=");
        boolean isCall = s.equals("WRITE") || s.equals("READ");
        boolean isReturn = s.equals("RETURN");
        if (isCall || isReturn) return s;
        else if (isOp) return "OP";
        else if (isCmp) return "CMP";
        else if (isInteger(s)) return "INT";
        else if (isFloat(s)) return "FLOAT";
        else {
            return getVarType(s);
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

    /**Global namespaces && status monitor*/
    private static String pastScope = "GLOBAL";
    private static String currentScope = "GLOBAL";
    protected static int paraCounter = 1;
    protected static int localCounter = 1;
    public static void setCurrentScope(String s) {
        pastScope = currentScope;
        currentScope = s;
        varCounter = 1;

        //some fucntions doesn't explicitly return when finish
        //need to add it.
        if(!codeAggregete.isEmpty() && !codeAggregete.get(codeAggregete.size()-1).contains("RET")) {
            storeCode(";RET\n");
        }
    }
    public static String getCurrentScope() { return  currentScope; }

    public static String generateGlobalName() {
        return "$T" + varCounter++;
    }
    public static String generateParaName() {
        return "$P" + paraCounter++;
    }
    public static String generateLocalName() {
        return "$L" + localCounter++;
    }

    //when create node for int, float and string, the stack will be used
    static Stack<String[]> constStack = new Stack<>();
    public static String[] getRecentConstVar() {
        return constStack.pop();
    }

    public static String generateCodeLabel() { return "label" + labelCounter++; }
    public static void pushLabel(String label) {codeLabelSpace.push(label);}
    public static String popLabel() {
        return codeLabelSpace.pop();
    }
    public static String peekLabel() {return  codeLabelSpace.peek();}

    public static void storeCode(String code) {
        codeAggregete.add(code);
    }


    /***********************************************************************
     * AST tree generation
     * */
    static Stack<ASTnode> builderStack = new Stack<>();
    static ArrayList<ASTnode> execQueue = new ArrayList<>();//stores all the statement AST
    public static void ASTgenerator(ArrayList<String> expr) {
        //step1 TODO: create ASTnode
        while(!expr.isEmpty())
        {
            String current = expr.remove(0);
            String type = varORvalue(current);
            if (type.contains("FUNCTION")) {
                ArrayList<String> paraMeters = new ArrayList<>();
                //check if next expr exits and it's not an opCode
                //get all the var before hit an ":="
                while(!expr.get(0).equals(":=")) {
                    //then get the next var
                    String next = expr.remove(0);
                    paraMeters.add(next);
                }
                //if parameter contains operation
                boolean containsOpCode = paraMeters.contains("-") || paraMeters.contains("+") || paraMeters.contains("*") || paraMeters.contains("/") ;
                if(containsOpCode) {
                    ASTgenerator(paraMeters);
                    opNode node = (opNode)builderStack.pop();
                    ASTnode newFunc = new funcNode(current, node);
                    builderStack.push(newFunc);
                } else {
                    ASTnode newFunc = new funcNode(current, paraMeters);
                    builderStack.push(newFunc);
                }

            } else if(type.contains("VAR")) {
                String isPara = localOrPara(current);
                simpleNode node = new simpleNode(type,current, isPara);
                builderStack.push(node);
            } else if (type.equals("INT")) {
                simpleNode node = new simpleNode("INT",current, null);
                builderStack.push(node);
            } else if (type.equals("FLOAT")) {
                simpleNode node = new simpleNode("FLOAT",current, null);
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
                cmpNode node = new cmpNode(current, left, right, peekLabel());
                execQueue.add(node);

            } else if (type.equals("WRITE")){
                ArrayList<ASTnode> arguments = new ArrayList<>();
                while(!builderStack.isEmpty())
                {
                    arguments.add(builderStack.pop());
                }
                //ASTnode argument = builderStack.pop();
                ASTnode node = new callNode("WRITE", arguments);
                execQueue.add(node);
            } else if (type.equals("READ")){
                //step1: create ASTnode for the var that will hold value that "READ" get
                ASTgenerator(expr);
                //step2: create arguments list
                ArrayList<ASTnode> arguments = new ArrayList<>();
                while(!builderStack.isEmpty())
                {
                    arguments.add(builderStack.pop());
                }
                //step3: create "READ" ASTnode;
                ASTnode node = new callNode("READ", arguments);
                execQueue.add(node);
            } else if (type.equals("RETURN")) {
                ASTnode tobeReturned = builderStack.pop();
                ASTnode node = new returnNode(tobeReturned);
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

        IRtoRawASM converter = new IRtoRawASM(codeAggregete);
        converter.printTiny();

    }

}
