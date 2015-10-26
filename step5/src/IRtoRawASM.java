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
     */
    private static class tinyNode {
        String opCode;
        String operand1;
        String operand2;
    }

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();

    public void generateTinyAssembly() {

        for (String varname : SymbolTable.keySet()) {
            if (!(varname.equals("temp"))) {
                tinyNode newnode = new tinyNode();
                newnode.opCode = "var";
                newnode.operand1 = varname;
                newnode.operand2 = "";

                nodeListTiny.add(newnode);
            }
        }

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

    public void printTiny() {
        buidIRNode();
        generateTinyAssembly();
        for (tinyNode aNodeListTiny : nodeListTiny) {
            System.out.println(aNodeListTiny.opCode + " " + aNodeListTiny.operand1 + " " + aNodeListTiny.operand2);
        }
        System.out.println("sys halt");
    }

}
