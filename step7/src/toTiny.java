import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jianruan on 12/4/15.
 */
public class toTiny {

    /** USE method in registerAllocation to get proper registers*/
    /**
     * For each tuple op A B C in a BB, do
     * Rx = ensure(A)
     * Ry = ensure(B)
     * if A dead after this tuple, free(Rx)
     * if B dead after this tuple, free(Ry)
     * Rz = allocate(C) //could use Rx or Ry
     * generate code for op
     * mark Rz dirty
     * At end of BB, for each dirty register
     * generate code to store register into appropriate variable
     */

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();
    private regAllocToolkit toolkit;
    private ArrayList<IRnode> IRnodes;
    private HashMap<String, int[]> funcBoundaries;
    private HashMap<String, HashMap<String, String>> findMapping;
    private HashSet<String> globals;

    public toTiny(regAllocToolkit toolkit, ArrayList<IRnode> IRnodes, HashMap<String, int[]> funcBoundaries) {
        this.toolkit = toolkit;
        this.IRnodes = IRnodes;
        this.funcBoundaries = funcBoundaries;
        findMapping = new HashMap<>();
        globals = new HashSet<>();
        formingFuncMapping();
        generateTinyNodes();
        printTiny();
    }

    /**
     * return num of "$L" in a function
     */
    private int getNumOfLocalVars(String funcName) {
        return ((Symbol_Func) generalUtils.SymbolTable.get(funcName)).getNumOfLocals();
    }

    private int getNumOfParaVars(String funcName) {
        return ((Symbol_Func) generalUtils.SymbolTable.get(funcName)).getNumOfParas();
    }

    private boolean isGlobal(String var) {
        return globals.contains(var);
    }

    private void formingFuncMapping() {
        for (String func : funcBoundaries.keySet()) {
            int local = getNumOfLocalVars(func);
            int para = getNumOfParaVars(func);

            HashMap<String, String> funcVarMapping = new HashMap<>();

            for (int i = 1; i <= local; i++) {
                funcVarMapping.put(("$L" + i), ("$-" + i));
            }
            int i;
            for (i = 1; i <= para; i++) {
                funcVarMapping.put(("$P" + i), ("$" + (i + 5)));
            }
            funcVarMapping.put("$R", ("$" + (i + 5)));

            findMapping.put(func, funcVarMapping);
        }

    }

    private boolean isCmp(String op) {
        return op.contains("NE") || op.contains("EQ") || (op.contains("GE") && !op.contains("MERGE")) || op.contains("LE")
                || op.contains("GT") || (op.contains("LT") && !op.contains("MUL"));
    }

    private boolean isArithmatic(String op) {
        return op.contains("ADD") || op.contains("SUB") || op.contains("MULT") || op.contains("DIV");
    }

    private boolean isLabel(String op) {
        return op.contains("LABEL");
    }

    private boolean isLink(String op) {
        return op.contains("LINK");
    }

    private boolean isRet(String op) {
        return op.contains("RET");
    }

    private boolean isStore(String op) {
        return op.contains("STORE");
    }

    private boolean isIO(String op) {
        return op.contains("READ") || op.contains("WRITE");
    }

    private boolean isStackOP(String op) {
        return op.contains("PUSH") || op.contains("POP");
    }

    private boolean isJump(String op) {
        return op.contains("JUMP");
    }

    private boolean isJSR(String op) {
        return op.contains("JSR");
    }

    private String getCase(IRnode node) {
        String op = node.opCode;
        if (isCmp(op)) return "CM";
        else if (isArithmatic(op)) return "AR";
        else if (isLabel(op)) return "LB";
        else if (isLink(op)) return "LK";
        else if (isRet(op)) return "RT";
        else if (isStore(op)) return "ST";
        else if (isIO(op)) return "WR";
        else if (isStackOP(op)) return "PP";
        else if (isJump(op)) return "JP";
        else if (isJSR(op)) return "JR";
        else {
            System.out.println("unrecognizable case num");
            return "ER";
        }
    }

    private String getCurrentFunc(IRnode node) {
        int nodeIndex = IRnodes.indexOf(node);
        for (String func : funcBoundaries.keySet()) {
            int[] boundry = funcBoundaries.get(func);
            if (nodeIndex >= boundry[0] && nodeIndex <= boundry[1])
                return func;
        }
        return "ERR";
    }

    private void generateTinyNodes() {

        headerHandler();

        for (IRnode node : IRnodes) {
            String nodeCase = getCase(node);
            switch (nodeCase) {
                case "LB":
                    labelHandler(node);
                    break;
                case "LK":
                    linkHandler(node);
                    break;
                case "RT":
                    retHandler(node);
                    break;
                case "JP":
                    jpHandler(node);
                    break;
                case "JR":
                    jrHandler(node);
                    break;
                case "PP":
                    ppHandler(node);
                    break;
                case "ST":
                    storeHandler(node);
                    break;
                case "CM":
                    cmpHandler(node);
                    break;
                case "AR":
                    arithHandler(node);
                    break;
                case "WR":
                    wrHandler(node);
                    break;
                default:
                    break;
            }
        }

    }
    private String getNameForVar(IRnode node, String operand) {
        String result;
        if(generalUtils.isInteger(operand) || generalUtils.isFloat(operand)) {
            result = operand;
        } else if (toolkit.registerContains(operand)){
            result = toolkit.returnRegisterName(operand);
        } else if(isGlobal(operand)) {
            result = operand;
        }  else {
            result = toolkit.ensure(operand);
        }

        return result;
    }

    private String getType(String operand) {
        String type;
        if (operand.startsWith("$T")) {
            type = null;
        } else {
            type = generalUtils.getVarType(operand);
        }
        return type;
    }

    private String getMathOpCode(String op) {
        String cmd = null;
        if (op.contains("ADD")) {
            switch (op) {
                case "ADDI":
                    cmd = "addi";
                    break;
                case "ADDF":
                    cmd = "addr";
                    break;
            }
        } else if (op.contains("SUB")) {
            switch (op) {
                case "SUBI":
                    cmd = "subi";
                    break;
                case "SUBF":
                    cmd = "subr";
                    break;
            }
        } else if (op.contains("MULT")) {
            switch (op) {
                case "MULTI":
                    cmd = "muli";
                    break;
                case "MULTF":
                    cmd = "mulr";
                    break;
            }
        } else if (op.contains("DIV")) {
            switch (op) {
                case "DIVI":
                    cmd = "divi";
                    break;
                case "DIVF":
                    cmd = "divr";
                    break;
            }
        } else {
            cmd = null;
        }
        return cmd;
    }

    private void registerPush() {
        for (int i = 0; i < 4; i++) {
            addTolist(new tinyNode("push", "r" + i, ""));
        }
    }

    private void registerPop() {
        for (int i = 3; i >= 0; i--) {
            addTolist(new tinyNode("pop", "r" + i, ""));
        }
    }

    private int numOfTemporals(String func) {
        int[] boundaries = funcBoundaries.get(func);
        HashSet<String> pool = new HashSet<>();

        for (int i = boundaries[0]; i <= boundaries[1]; i++) {
            IRnode node = IRnodes.get(i);
            if (node.operand1 != null) {
                if (node.operand1.contains("$T")) pool.add(node.operand1);
            }
            if (node.operand2 != null) {
                if (node.operand2.contains("$T")) pool.add(node.operand2);
            }
            if (node.result != null) {
                if (node.result.contains("$T")) pool.add(node.result);
            }
        }

        return pool.size();
    }

    private int calculateSpcaceNeeded(String func) {
        int part1 = getNumOfLocalVars(func);
        int part2 = numOfTemporals(func);
        return part1 + part2;
    }


    private void labelHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        tinyNode label = new tinyNode("label", node.result, "");
        addTolist(label);

        toolkit.freeDead(node);
    }

    private void linkHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        String func = getCurrentFunc(node);
        String space = calculateSpcaceNeeded(func) + "";
        tinyNode link = new tinyNode("link", space, "");
        addTolist(link);

        toolkit.freeDead(node);
    }

    private void headerHandler() {

        for (Symbol s : generalUtils.SymbolTable.values()) {
            if (s.sym_getParentScope().getName().equals("GLOBAL")) {
                globals.add(s.sym_getName());
                if (s.sym_getType().equals("STRING")) {
                    Symbol_Str str = (Symbol_Str) s;
                    toTiny.nodeListTiny.add(new tinyNode("str", str.sym_getName(), str.sym_getStr()));
                } else {
                    if (s.sym_getType().contains("INT") || s.sym_getType().contains("FLOAT"))
                        toTiny.nodeListTiny.add(new tinyNode("var", s.sym_getName(), ""));
                }
            }
        }
        addTolist(new tinyNode("push", "", ""));
        registerPush();
        addTolist(new tinyNode("jsr", "main", ""));
        addTolist(new tinyNode("sys", "halt", ""));
    }

    private void retHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        tinyNode unlink = new tinyNode("unlnk", "", "");
        addTolist(unlink);
        tinyNode ret = new tinyNode("ret", "", "");
        addTolist(ret);

        toolkit.freeDead(node);
    }

    private void jpHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        tinyNode jmp = new tinyNode("jmp", node.result, "");
        addTolist(jmp);

        toolkit.freeDead(node);
    }

    private void jrHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        toolkit.saveAndReset(globals);
        registerPush();
        tinyNode jsr = new tinyNode("jsr", node.result, "");
        addTolist(jsr);
        registerPop();

        toolkit.freeDead(node);
    }

    private void ppHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        String cmd;
        if (node.opCode.contains("PUSH")) {
            cmd = "push";
        } else {
            cmd = "pop";
        }

        String target = "";
        if (node.result != null) {
            target = getNameForVar(node, node.result);
        }

        tinyNode pp = new tinyNode(cmd, target, "");
        addTolist(pp);

        toolkit.freeDead(node);
    }

    private void storeHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        String operand1, operand2;
        operand1 = getNameForVar(node, node.operand1);
        operand2 = getNameForVar(node, node.result);

        boolean cond1 = operand1.contains("$") && operand1.contains("$");
        boolean cond2 = isGlobal(operand1) && isGlobal(operand2);

        if (cond1 || cond2) {
            String extraReg = toolkit.ensure(operand1);
            tinyNode store1 = new tinyNode("move", operand1, extraReg);
            addTolist(store1);
            tinyNode store2 = new tinyNode("move", extraReg, operand2);
            addTolist(store2);
        } else {
            tinyNode store = new tinyNode("move", operand1, operand2);
            addTolist(store);
        }


        toolkit.freeDead(node);
    }

    private void cmpHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        /**step1*/
        String type1 = getType(node.operand1);
        String type2 = getType(node.operand2);
        String cmd;
        if ((type1 != null && type1.contains("INT")) || (type2 != null && type2.contains("INT"))) {
            cmd = "cmpi";
        } else if (node.operand1.startsWith("$") && node.operand2.startsWith("$")) {
            cmd = "cmpi";
        } else {
            cmd = "cmpr";
        }
        String target1 = getNameForVar(node, node.operand1);
        String target2 = getNameForVar(node, node.operand2);

        boolean cond1 = target1.contains("$") && target2.contains("$");
        boolean cond2 = isGlobal(target1) || isGlobal(target2);

        if (cond1 || cond2) {
            if(isGlobal(target1)) {
                String reg_target1 = toolkit.ensure(target1);
                addTolist(new tinyNode("move", target1, reg_target1 ));
                target1 = reg_target1;
            }
            if(isGlobal(target2)) {
                String reg_target2 = toolkit.ensure(target2);
                addTolist(new tinyNode("move", target2, reg_target2 ));
                target2 = reg_target2;
            }
            addTolist(new tinyNode(cmd, target1, target2));
        } else {
            addTolist(new tinyNode(cmd, target1, target2));
        }


        /**step2 get right jump cmd*/
        String op = node.opCode;
        String jcmd = "j" + op.toLowerCase();
        addTolist(new tinyNode(jcmd, node.result, ""));

        //toolkit.freeDead(node);
    }

    private void wrHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        String op = node.opCode;
        String cmd1 = "sys";
        String cmd2;
        if (op.contains("WRITE")) {
            switch (op) {
                case "WRITEI":
                    cmd2 = "writei";
                    break;
                case "WRITEF":
                    cmd2 = "writer";
                    break;
                case "WRITES":
                    cmd2 = "writes";
                    break;
                default:
                    cmd2 = null;
                    break;
            }
        } else {
            switch (op) {
                case "READI":
                    cmd2 = "readi";
                    break;
                case "READF":
                    cmd2 = "readr";
                    break;
                default:
                    cmd2 = null;
                    break;
            }
        }

        String target = getNameForVar(node, node.result);
        addTolist(new tinyNode(cmd1, cmd2, target));


        toolkit.freeDead(node);
    }

    private void arithHandler(IRnode node) {
        toolkit.setCurrentNode(node);

        String op = node.opCode;
        String cmd = getMathOpCode(op);

        String src1 = getNameForVar(node, node.operand1);
        String src2 = getNameForVar(node, node.operand2);
        String des = getNameForVar(node, node.result);

        /** step1: move src1 to des , des is almost always a reg*/
        addTolist(new tinyNode("move", src1, des));
        /** step2: compute */
        if(isGlobal(src2)) {
            String old = src2;
            src2 = toolkit.ensure(src2);
            addTolist(new tinyNode("move", old, src2));
        }
        addTolist(new tinyNode(cmd, src2, des));

        toolkit.freeDead(node);
    }

    public void printTiny() {

        for (tinyNode node : nodeListTiny) {
            System.out.println(node.opCode + " " + node.operand1 + " " + node.operand2);
        }
        System.out.print("end");
    }

    public static void addTolist(tinyNode tiny) {
        //System.out.println("\"" + tiny.opCode + " " + tiny.operand1 + " " + tiny.operand2 + "\"  added to list");
        nodeListTiny.add(tiny);
    }
}