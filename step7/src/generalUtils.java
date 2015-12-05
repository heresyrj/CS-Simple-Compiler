import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by jianruan on 10/14/15.
 */
public class GeneralUtils {

    protected static HashMap<String, HashMap> directoryLookup = new HashMap<>();
    protected static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 1;
    static int labelCounter = 1;
    static ArrayList<String> codeAggregete = new ArrayList<>();

    static Stack<String> codeLabelSpace = new Stack<>();

    //store all vars which are function names
    static ArrayList<Symbol_Func> funcSyms = new ArrayList<>();

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
            if(s instanceof Symbol_Func)
            {
                funcSyms.add((Symbol_Func) s);
            }
        }
        for(Symbol_Func s : funcSyms) {
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
            for(Symbol_Func s : funcSyms) {
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
            Symbol_Func fs = (Symbol_Func) SymbolTable.get(scope);
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
    static Stack<ASTNode> builderStack = new Stack<>();
    static ArrayList<ASTNode> execQueue = new ArrayList<>();//stores all the statement AST
    public static void ASTgenerator(ArrayList<String> expr) {
        //step1 TODO: create ASTNode
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
                    ASTNode_Op node = (ASTNode_Op)builderStack.pop();
                    ASTNode newFunc = new ASTNode_Func(current, node);
                    builderStack.push(newFunc);
                } else {
                    ASTNode newFunc = new ASTNode_Func(current, paraMeters);
                    builderStack.push(newFunc);
                }

            } else if(type.contains("VAR")) {
                String isPara = localOrPara(current);
                ASTNode_Simple node = new ASTNode_Simple(type,current, isPara);
                builderStack.push(node);
            } else if (type.equals("INT")) {
                ASTNode_Simple node = new ASTNode_Simple("INT",current, null);
                builderStack.push(node);
            } else if (type.equals("FLOAT")) {
                ASTNode_Simple node = new ASTNode_Simple("FLOAT",current, null);
                builderStack.push(node);
            } else if (type.equals("OP")) {
                ASTNode right = builderStack.pop();
                ASTNode left = builderStack.pop();
                ASTNode_Op node = new ASTNode_Op(current, left, right);
                if(current.equals(":=")){
                    execQueue.add(node);
                } else {
                    builderStack.push(node);
                }
            } else if (type.equals("CMP")) {
                ASTNode right = builderStack.pop();
                ASTNode left = builderStack.pop();
                ASTNode_Cmp node = new ASTNode_Cmp(current, left, right, peekLabel());
                execQueue.add(node);

            } else if (type.equals("WRITE")){
                ArrayList<ASTNode> arguments = new ArrayList<>();
                while(!builderStack.isEmpty())
                {
                    arguments.add(builderStack.pop());
                }
                //ASTNode argument = builderStack.pop();
                ASTNode node = new ASTNode_Call("WRITE", arguments);
                execQueue.add(node);
            } else if (type.equals("READ")){
                //step1: create ASTNode for the var that will hold value that "READ" get
                ASTgenerator(expr);
                //step2: create arguments list
                ArrayList<ASTNode> arguments = new ArrayList<>();
                while(!builderStack.isEmpty())
                {
                    arguments.add(builderStack.pop());
                }
                //step3: create "READ" ASTNode;
                ASTNode node = new ASTNode_Call("READ", arguments);
                execQueue.add(node);
            } else if (type.equals("RETURN")) {
                ASTNode tobeReturned = builderStack.pop();
                ASTNode node = new ASTNode_Return(tobeReturned);
                execQueue.add(node);
            } else {
                System.out.println("ERR: unrecognizable symbol \""+type+ "\" in expr");
            }
        }

    }


    public static void compile() {

        //System.out.println(";IR code");
        for(String line : codeAggregete) {
            System.out.println(line);
        }
        //System.out.println(";tiny code");

        BuildIR converter = new BuildIR(codeAggregete);
        AST_to_CFG CFG = new AST_to_CFG(converter.getIRnodes());
        DataFlow DF = new DataFlow(CFG);
    }

}
