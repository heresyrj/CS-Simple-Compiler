import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


/**
 * Created by jianruan on 10/14/15.
 */
public class generalUtils {
    private static HashMap<String, Symbol> SymbolTable = new HashMap<>();

    static int varCounter = 0;
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

    static ArrayList<IRNode> nodeListIR = new ArrayList<>();
    public static void buidIRNode() {
        for(int i = 0; i < codeAggregete.size(); i++) {
            String line = codeAggregete.get(i);
            String[] splitline = line.split(" ");
            IRNode newnode = new IRNode();
            int len = splitline[0].length();
            newnode.opCode = splitline[0].substring(1, len);
            if(splitline[0].contains("STORE")) {
                newnode.operand1 = splitline[1];
                newnode.result = splitline[2];
            }  
            else if(splitline[0].contains("WRITE")) { 
                newnode.result = splitline[1];
            } 
            else {
                newnode.operand1 = splitline[1];
                newnode.operand2 = splitline[2];
                newnode.result = splitline[3];
            }
            nodeListIR.add(newnode);
        }
    }

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();
    public static void generateTinyAssembly() {

        for(String varname: SymbolTable.keySet()) {
            if(!(varname.equals("temp"))) {
                tinyNode newnode = new tinyNode();
                newnode.opCode = "var";
                newnode.operand1 = varname;
                newnode.operand2 = "";

                nodeListTiny.add(newnode);
            }
            
        }


        for(int i = 0; i < nodeListIR.size(); i++) {
            tinyNode newnode = new tinyNode();
            IRNode node = nodeListIR.get(i);
            if(node.opCode.equals("STOREI") || node.opCode.equals("STOREF")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);
            }
            else if(node.opCode.equals("WRITEI")) {
                newnode.opCode = "sys";
                newnode.operand1 = "writei";
                newnode.operand2 = node.result;
                nodeListTiny.add(newnode);
            }
            else if(node.opCode.equals("WRITEF")) {
                newnode.opCode = "sys";
                newnode.operand1 = "writer";
                newnode.operand2 = node.result;
                nodeListTiny.add(newnode);
            }
            else if(node.opCode.equals("ADDI")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "addi";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("ADDF")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "addr";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("SUBI")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "subi";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("SUBF")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "subr";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("MULTI")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "muli";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("MULTF")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "mulr";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("DIVI")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "divi";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }

            else if(node.opCode.equals("DIVF")) {
                newnode.opCode = "move";
                if(node.operand1.startsWith("$T")) {
                    newnode.operand1 = String.format("r%s", node.operand1.substring(2));
                } else {
                    newnode.operand1 = node.operand1;
                }
                if(node.result.startsWith("$T")) {
                    newnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    newnode.operand2 = node.result;
                }
                nodeListTiny.add(newnode);

                tinyNode tempnode = new tinyNode();
                tempnode.opCode = "divr";
                if(node.operand2.startsWith("$T")) {
                    tempnode.operand1 = String.format("r%s", node.operand2.substring(2));
                } else {
                    tempnode.operand1 = node.operand2;
                }
                if(node.result.startsWith("$T")) {
                    tempnode.operand2 = String.format("r%s", node.result.substring(2));
                } else {
                    tempnode.operand2 = node.result;
                }
                nodeListTiny.add(tempnode);
            }
        }
    }

    public static void traverse() {
        for(int i = 0; i < nodeListTiny.size(); i++) {
            System.out.println(nodeListTiny.get(i).opCode + " " + nodeListTiny.get(i).operand1 + " " + nodeListTiny.get(i).operand2);
        }
        System.out.println("sys halt");

    }

}
