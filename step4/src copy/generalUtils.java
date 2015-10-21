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

    /*****************************************************************************
     * IRnode Generation
     * */
    private static class IRNode {
        public String opCode;
        public String operand1;
        public String operand2;
        public String result;
    }

    static ArrayList<IRNode> nodeListIR = new ArrayList<>();
    public static void buidIRNode() {
        for (String line : codeAggregete) {
            String[] splitline = line.split(" ");
            IRNode newnode = new IRNode();
            int len = splitline[0].length();
            newnode.opCode = splitline[0].substring(1, len);
            if (splitline[0].contains("STORE")) {
                newnode.operand1 = splitline[1];
                newnode.result = splitline[2];
            } else if (splitline[0].contains("WRITE")) {
                newnode.result = splitline[1];
            } else {
                newnode.operand1 = splitline[1];
                newnode.operand2 = splitline[2];
                newnode.result = splitline[3];
            }
            nodeListIR.add(newnode);
        }
    }

    /***********************************************************************
     * TinyNode Generation
     * */
    private static class tinyNode {
        String opCode;
        String operand1;
        String operand2;
    }

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();
    public static void generateTinyAssembly() {

        SymbolTable.keySet().stream().filter(varname -> !(varname.equals("temp"))).forEach(varname -> {
            tinyNode newnode = new tinyNode();
            newnode.opCode = "var";
            newnode.operand1 = varname;
            newnode.operand2 = "";

            nodeListTiny.add(newnode);
        });

        for (IRNode aNodeListIR : nodeListIR) {
            tinyNode newnode = new tinyNode();
            switch (aNodeListIR.opCode) {
                case "STOREI":
                case "STOREF":
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);
                    break;
                case "WRITEI":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writei";
                    newnode.operand2 = aNodeListIR.result;
                    nodeListTiny.add(newnode);
                    break;
                case "WRITEF":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writer";
                    newnode.operand2 = aNodeListIR.result;
                    nodeListTiny.add(newnode);
                    break;
                case "ADDI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "addi";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "ADDF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "addr";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "SUBI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "subi";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "SUBF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "subr";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "MULTI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "muli";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "MULTF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "mulr";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "DIVI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "divi";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "DIVF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$T")) {
                        newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        newnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "divr";
                    if (aNodeListIR.operand2.startsWith("$T")) {
                        tempnode.operand1 = String.format("r%s", aNodeListIR.operand2.substring(2));
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$T")) {
                        tempnode.operand2 = String.format("r%s", aNodeListIR.result.substring(2));
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
            }
        }
    }

    public static void compile() {

        buidIRNode();
        generateTinyAssembly();

        System.out.println(";IR code");
        int size = codeAggregete.size();
        int i = 0;
        while(i < size) {
            System.out.println(codeAggregete.get(i));
            i++;
        }
        System.out.println(";tiny code");
        for (tinyNode aNodeListTiny : nodeListTiny) {
            System.out.println(aNodeListTiny.opCode + " " + aNodeListTiny.operand1 + " " + aNodeListTiny.operand2);
        }
        System.out.println("sys halt");
    }

}
