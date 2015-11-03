import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jianruan on 10/25/15.
 */
public class IRtoRawASM {
    private ArrayList<String> IRcodes;
    protected HashMap<String, Symbol> SymbolTable = generalUtils.SymbolTable;

    public IRtoRawASM(ArrayList<String> codeAggregete) {
        IRcodes = codeAggregete;
    }

    /*****************************************************************************
     * IRnode Generation
     */
    private class IRNode {
        public String opCode;
        public String operand1;
        public String operand2;
        public String result;
    }

    static ArrayList<IRNode> nodeListIR = new ArrayList<>();

    public void buidIRNode() {
        for (String line : IRcodes) {
            String[] splitline = line.split(" ");
            IRNode newnode = new IRNode();
            int len = splitline[0].length();
            newnode.opCode = splitline[0].substring(1, len);
            if (splitline[0].contains("STORE")) {
                newnode.operand1 = splitline[1];
                newnode.result = splitline[2];
            } else if (splitline[0].contains("WRITE")) {
                newnode.result = splitline[1];
            } else if (splitline[0].contains("LABEL")) {
                newnode.result = splitline[1];
            } else if (splitline[0].contains("JUMP")) {
                newnode.result = splitline[1];
            } else if (splitline[0].contains("READ")) {
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

    /***********************************************************************
     * TinyNode Generation
     */
    private static class tinyNode {
        String opCode;
        String operand1;
        String operand2;
    }

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();

    public void generateTinyAssembly() {
        ArrayList<String> variables = new ArrayList<String>();

        for (String varname : SymbolTable.keySet()) {

            Symbol s = SymbolTable.get(varname);
            Scope scope = s.sym_getParentScope();

            if (!(varname.equals("temp")) && (scope.getName().equals("GLOBAL"))) {
                tinyNode newnode = new tinyNode();
                newnode.opCode = "var";
                newnode.operand1 = varname;
                newnode.operand2 = "";

                variables.add(varname);
                nodeListTiny.add(newnode);
            }
        }

        //int counter = 0;
        int reg = 99;
        for (IRNode aNodeListIR : nodeListIR) {
            tinyNode newnode = new tinyNode();
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
                    } else {
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
                    }
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
                case "WRITES":
                    newnode.opCode = "sys";
                    newnode.operand1 = "writes";
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
                case "LABEL": {
                    newnode.opCode = "label";
                    newnode.operand1 = aNodeListIR.result;
                    newnode.operand2 = "";
                    nodeListTiny.add(newnode);
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    } else {
                        newnode.opCode = "cmpr";
                    }
                    if(variables.contains(aNodeListIR.operand1) && variables.contains(aNodeListIR.operand2)) {
                        tinyNode tempnode = new tinyNode();
                        tempnode.opCode = "move";
                        tempnode.operand1 = aNodeListIR.operand1;
                        tempnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(tempnode);

                        newnode.operand1 = aNodeListIR.operand2;
                        newnode.operand2 = "r"+ Integer.toString(reg);
                        nodeListTiny.add(newnode);
                        reg++;
                    } else {
                        if (aNodeListIR.operand1.startsWith("$T")) {
                            newnode.operand1 = String.format("r%s", aNodeListIR.operand1.substring(2));
                        } else {
                            newnode.operand1 = aNodeListIR.operand1;
                        }
                        if (aNodeListIR.operand2.startsWith("$T")) {
                            newnode.operand2 = String.format("r%s", aNodeListIR.operand2.substring(2));
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
                    newnode.operand2 = aNodeListIR.result;
                    nodeListTiny.add(newnode);
                    break;
                }
                case "READF": {
                    newnode.opCode = "sys";
                    newnode.operand1 = "readr";
                    newnode.operand2 = aNodeListIR.result;
                    nodeListTiny.add(newnode);
                    break;
                }
            }
        }
    }

    public void printTiny() {
        buidIRNode();
        generateTinyAssembly();
        for (tinyNode aNodeListTiny : nodeListTiny) {
            System.out.println(aNodeListTiny.opCode + " " + aNodeListTiny.operand1 + " " + aNodeListTiny.operand2);
        }
        System.out.println("sys halt");
    }

}
