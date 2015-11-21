import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jianruan on 11/20/15.
 */
public class Tiny {


    /***********************************************************************
     * TinyNode Generation
     */
    private static class tinyNode {
        String opCode;
        String operand1;
        String operand2;
    }

    private HashMap<String, String> TempregHashMap;
    private HashMap<String, ArrayList<Integer>> funcVarsLookup;
    private HashMap<String, Symbol> SymbolTable;
    private ArrayList<IR> nodeListIR;

    public Tiny (HashMap<String, ArrayList<Integer>> funcVarsLookup, ArrayList<IR> nodeListIR ) {
        this.funcVarsLookup = funcVarsLookup;
        this.nodeListIR = nodeListIR;
        SymbolTable = generalUtils.SymbolTable;
        TempregHashMap = new HashMap<>();
        printTiny();
    }

    private int regcount = -1;
    private String prevtempreg = "$T1";

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();

    private String manageReg(String operand, String currentfunc) {
        ArrayList<Integer> vars = funcVarsLookup.get(currentfunc);
        int numl = vars.get(0);
        int nump = vars.get(1);

        if(operand.startsWith("$P")) {
            int tmp = Integer.parseInt(operand.substring(2));
            int req = 6 + nump - tmp;
            return "$"+Integer.toString(req);
        }

        if(operand.startsWith("$L")) {
            return "$-"+(operand.substring(2));
        }

        if(operand.startsWith("$T")) {
            if(TempregHashMap.containsKey(operand)) {
                return TempregHashMap.get(operand);
            } else {
                if(operand.equals(prevtempreg)) {
                    TempregHashMap.put(operand, "r"+Integer.toString(regcount));
                    return "r"+Integer.toString(regcount);

                }
                else {
                    regcount++;
                    String reg = Integer.toString(regcount);
                    prevtempreg = operand;
                    TempregHashMap.put(operand, "r"+reg);
                    return "r"+reg;
                }
            }
        }

        if(operand.startsWith("$R")) {
            int tmp = 6 + nump;
            return "$"+Integer.toString(tmp);
        }

        return operand;
    }

    public void generateTinyAssembly() {
        ArrayList<String> variables = new ArrayList<String>();

        for (String varname : SymbolTable.keySet()) {

            Symbol s = SymbolTable.get(varname);
            Scope scope = s.sym_getParentScope();
            String type = s.sym_getType();

            if ((scope.getName().equals("GLOBAL")) && (!(type.equals("FUNCTION")))) {
                tinyNode newnode = new tinyNode();
                if(type.equals("STRING")) {
                    Symbol_STRING str = (Symbol_STRING) s;
                    String value = str.sym_getStr();
                    newnode.opCode = "str";
                    newnode.operand1 = varname;
                    newnode.operand2 = value;
                } else {
                    newnode.opCode = "var";
                    newnode.operand1 = varname;
                    newnode.operand2 = "";
                }

                variables.add(varname);
                nodeListTiny.add(newnode);
            }
        }

        tinyNode newnode = new tinyNode();
        newnode.opCode = "push";
        newnode.operand1 = "";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "push";
        newnode.operand1 = "r0";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "push";
        newnode.operand1 = "r1";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "push";
        newnode.operand1 = "r2";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "push";
        newnode.operand1 = "r3";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "jsr";
        newnode.operand1 = "main";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);

        newnode = new tinyNode();
        newnode.opCode = "sys";
        newnode.operand1 = "halt";
        newnode.operand2 = "";
        nodeListTiny.add(newnode);


        int reg = 99;
        tinyNode prevnode = new tinyNode();
        String currentfunc = null;
        for (IR aNodeListIR : nodeListIR) {
            newnode = new tinyNode();
            switch (aNodeListIR.opCode) {
                case "STOREI":
                case "STOREF":
                    newnode.opCode = "move";
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.result)) {
                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);

                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = "r"+ Integer.toString(reg);
                        tempnode.operand2 = aNodeListIR.result;
                        nodeListTiny.add(tempnode);
                        reg++;
                    } else if(aNodeListIR.operand1.startsWith("$L") && aNodeListIR.result.startsWith("$R")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        regcount++;
                        newnode.operand2 = "r"+Integer.toString(regcount);
                        nodeListTiny.add(newnode);

                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = "r"+ Integer.toString(regcount);
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                        nodeListTiny.add(tempnode);
                    }
                    else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.result.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.result;
                        }
                        nodeListTiny.add(newnode);
                    }
                    break;

                case "PUSH": {
                    newnode.opCode = "push";
                    if(aNodeListIR.result != null) {
                        if (aNodeListIR.result.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.result, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.result;
                        }
                    } else {
                        newnode.operand1 = "";
                    }
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
                    break;
                }
                case "POP": {
                    newnode.opCode = "pop";
                    if(aNodeListIR.result != null) {
                        if (aNodeListIR.result.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.result, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.result;
                        }
                    } else {
                        newnode.operand1 = "";
                    }
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
                    break;
                }
                case "JSR": {
                    newnode.opCode = "push";
                    newnode.operand1 = "r0";
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);

                    tinyNode newnode2 = new tinyNode();
                    newnode2.opCode = "push";
                    newnode2.operand1 = "r1";
                    newnode2.operand2 = "";
                    nodeListTiny.add(newnode2);

                    tinyNode newnode3 = new tinyNode();
                    newnode3.opCode = "push";
                    newnode3.operand1 = "r2";
                    newnode3.operand2 = "";
                    nodeListTiny.add(newnode3);

                    tinyNode newnode4 = new tinyNode();
                    newnode4.opCode = "push";
                    newnode4.operand1 = "r3";
                    newnode4.operand2 = "";
                    nodeListTiny.add(newnode4);

                    tinyNode newnode5 = new tinyNode();
                    newnode5.opCode = "jsr";
                    newnode5.operand1 = aNodeListIR.result;
                    newnode5.operand2 = "";
                    nodeListTiny.add(newnode5);

                    tinyNode newnode6 = new tinyNode();
                    newnode6.opCode = "pop";
                    newnode6.operand1 = "r3";
                    newnode6.operand2 = "";
                    nodeListTiny.add(newnode6);

                    tinyNode newnode7 = new tinyNode();
                    newnode7.opCode = "pop";
                    newnode7.operand1 = "r2";
                    newnode7.operand2 = "";
                    nodeListTiny.add(newnode7);

                    tinyNode newnode8 = new tinyNode();
                    newnode8.opCode = "pop";
                    newnode8.operand1 = "r1";
                    newnode8.operand2 = "";
                    nodeListTiny.add(newnode8);

                    tinyNode newnode9 = new tinyNode();
                    newnode9.opCode = "pop";
                    newnode9.operand1 = "r0";
                    newnode9.operand2 = "";
                    nodeListTiny.add(newnode9);

                    break;
                }

                case "WRITEI":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writei";

                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }

                    nodeListTiny.add(newnode);
                    break;
                case "WRITEF":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writer";
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);
                    break;
                case "WRITES":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writes";
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);
                    break;
                case "ADDI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "addi";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "ADDF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "addr";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "SUBI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "subi";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "SUBF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "subr";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "MULTI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "muli";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "MULTF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "mulr";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "DIVI": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "divi";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "DIVF": {
                    newnode.opCode = "move";
                    if (aNodeListIR.operand1.startsWith("$")) {
                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                    } else {
                        newnode.operand1 = aNodeListIR.operand1;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "divr";
                    if (aNodeListIR.operand2.startsWith("$")) {
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                    } else {
                        tempnode.operand1 = aNodeListIR.operand2;
                    }
                    if (aNodeListIR.result.startsWith("$")) {
                        tempnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        tempnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "LABEL": {
                    newnode.opCode = "label";
                    newnode.operand1 = aNodeListIR.result;
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
                    prevnode = newnode;
                    break;
                }

                case "LINK": {
                    newnode.opCode = "link";
                    TempregHashMap = new HashMap<String, String>();
                    ArrayList<Integer> vars = funcVarsLookup.get(prevnode.operand1);
                    int loc = vars.get(0);
                    newnode.operand1 = Integer.toString(loc);
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
                    currentfunc = prevnode.operand1;
                    regcount++;
                    break;
                }

                case "RET\n": {
                    newnode.opCode = "unlnk";
                    newnode.operand1 = "";
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "ret";
                    tempnode.operand1 = "";
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }

                case "JUMP": {
                    newnode.opCode = "jmp";
                    newnode.operand1 = aNodeListIR.result;
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
                    break;
                }
                case "NE": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }
                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jne";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);

                    break;
                }
                case "LE": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }
                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jle";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "GE": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }

                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jge";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "GT": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }
                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jgt";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "LT": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }
                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jlt";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "EQ": {
                    String type1 = null;
                    String type2 = null;
                    if(aNodeListIR.operand1.startsWith("$T")) {
                        type1 = null;
                    } else {
                        type1 = generalUtils.getVarType(aNodeListIR.operand1);
                    }

                    if(aNodeListIR.operand2.startsWith("$T")) {
                        type2 = null;
                    } else {
                        type2 = generalUtils.getVarType(aNodeListIR.operand2);
                    }
                    if((type1 != null && type1.equals("INT")) || (type2 != null && type2.equals("INT"))) {
                        newnode.opCode = "cmpi";
                    } else if(aNodeListIR.operand1.startsWith("$") && aNodeListIR.operand2.startsWith("$")) {
                        newnode.opCode = "cmpi";
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand2;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand1;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else if(aNodeListIR.operand1.startsWith("$L") && aNodeListIR.operand2.startsWith("$L")) {
                        tinyNode tempnode = new tinyNode();
                        regcount++;
                        tempnode.opCode = "move";
                        tempnode.operand1 = manageReg(aNodeListIR.operand2, currentfunc);
                        tempnode.operand2 = "r"+Integer.toString(regcount);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        newnode.operand2 = "r"+Integer.toString(regcount);
                        nodeListTiny.add(newnode);
                        regcount++;
                    }
                    else {
                        if (aNodeListIR.operand1.startsWith("$")) {
                            newnode.operand1 = manageReg(aNodeListIR.operand1, currentfunc);
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$")) {
                            newnode.operand2 = manageReg(aNodeListIR.operand2, currentfunc);
                        } else {
                            newnode.operand2 = aNodeListIR.operand2;
                        }
                        nodeListTiny.add(newnode);
                    }

                    tinyNode tempnode = new tinyNode();
                    tempnode.opCode = "jeq";
                    tempnode.operand1 = aNodeListIR.result;
                    tempnode.operand2 = "";
                    nodeListTiny.add(tempnode);
                    break;
                }
                case "READI": {
                    newnode.opCode = "sys";
                    newnode.operand1 = "readi";
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);
                    break;
                }
                case "READF": {
                    newnode.opCode = "sys";
                    newnode.operand1 = "readr";
                    if (aNodeListIR.result.startsWith("$")) {
                        newnode.operand2 = manageReg(aNodeListIR.result, currentfunc);
                    } else {
                        newnode.operand2 = aNodeListIR.result;
                    }
                    nodeListTiny.add(newnode);
                    break;
                }
            }
        }
    }

    public void printTiny() {
        generateTinyAssembly();
        for (tinyNode aNodeListTiny : nodeListTiny) {
            System.out.println(aNodeListTiny.opCode + " " + aNodeListTiny.operand1 + " " + aNodeListTiny.operand2);
        }
        System.out.print("end");
    }
}
