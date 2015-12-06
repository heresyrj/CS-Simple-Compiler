import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jianruan on 12/2/15.
 */
public class DataFlow {
    final private AST_to_CFG CFG;
    final private ArrayList<IRnode> nodesList;
    final private HashMap<Integer, HashSet<Integer>> adjacency;
    final private HashMap<Integer, HashSet<Integer>> reverseAdjacency;
    private HashMap<String, int[]> funcBoundaries;

    public DataFlow(AST_to_CFG CFG) {
        this.CFG = CFG;
        nodesList = CFG.getNodesList();
        adjacency = CFG.getGraph();
        reverseAdjacency = CFG.getReverseGraph();
        funcBoundaries = new HashMap<>();
        initialization();
        livenessAnalysis();
        //debug();
    }
    public HashMap<String, int[]> getFuncBoundaries () {return funcBoundaries;}
    private boolean isCmp(String op) {
        return op.contains("NE") || op.contains("EQ") || (op.contains("GE")&&!op.contains("MERGE")) || op.contains("LE")
                || op.contains("GT") || (op.contains("LT") && !op.contains("MUL"));
    }

    private boolean isFormat(String op) {
        return op.contains("LABEL") || op.contains("LINK");
    }

    private boolean isRet(String op) {
        return op.contains("RET");
    }

    private boolean isArithmatic(String op) {
        return op.contains("ADD") || op.contains("SUB") || op.contains("MULT") || op.contains("DIV");
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

    private boolean isMerge(String op) {
        return op.contains("MERGE");
    }

    private void initialization() {
        /** generate Require and Redefine var sets for each node */
        getFunctionInfo();
        for (IRnode node : nodesList) {
            transfer(node);
        }
    }

    private void transfer(IRnode node) {
        /** when generate the two SETs
         * they are independent info
         * therefore needless to follow CGF path*/
        HashSet<String> Require = new HashSet<>();
        HashSet<String> ReDefine = new HashSet<>();
        String op = node.opCode;

        if (isStackOP(op) || isIO(op)) {
            if (op.contains("PUSH") || op.contains("WRITE")) {
                if (node.result != null) addToSet(node.result, Require);
            } else {
                // POP  or READ
                if (node.result != null) addToSet(node.result, ReDefine);
            }
        } else if (isRet(op)) {
            HashSet<String> retNodeSet = new HashSet<>();
            for (String var : generalUtils.SymbolTable.keySet()) {
                if (generalUtils.SymbolTable.get(var).sym_getParentScope().getName().equals("GLOBAL")) {
                    if (!funcBoundaries.keySet().contains(var))
                        retNodeSet.add(var);
                }
            }
            node.setLiveOUT(retNodeSet);
            HashSet<String> set = new HashSet<>();
            set.add("$R");
            node.setLiveIN(set);

        } else if (isArithmatic(op)) {
            addToSet(node.operand1, Require);
            addToSet(node.operand2, Require);
            addToSet(node.result, ReDefine);
        } else if (isStore(op)) {
            addToSet(node.operand1, Require);
            addToSet(node.result, ReDefine);
        } else if (isCmp(op)) {
            addToSet(node.operand1, Require);
            addToSet(node.operand2, Require);
        } else {
            if (!(isJSR(op) || isJump(op) || isFormat(op) || isMerge(op)))
                System.out.println("Unrecognizable Opcode [" + op + "] in Class DataFlow");
        }

        node.setRequire(Require);
        node.setReDefine(ReDefine);
    }

    private void getFunctionInfo() {
        HashSet<String> allFuncs = CFG.getAllFuncs();
        for (String func : allFuncs) {
            /** Step1: find the boundaries for this func */
            int boundaries[] = new int[2];
            boundaries[0] = CFG.getFuncEnter(func);
            HashSet<Integer> funcRets = CFG.getFuncExit(func);
            int funcEnd = -1;
            for (int ret : funcRets) {
                if (ret >= funcEnd) funcEnd = ret;
            }
            boundaries[1] = funcEnd;
            funcBoundaries.put(func, boundaries);
            //System.out.println(func+" starts at "+funcBoundaries.get(func)[0]+" ends at "+funcBoundaries.get(func)[1]);
        }
    }

    private HashSet<Integer> getPredecessors(int currentNodeIndex) {
        return reverseAdjacency.get(currentNodeIndex);
    }

    private HashSet<Integer> getSuccessors(int currentNodeIndex) {
        return adjacency.get(currentNodeIndex);
    }

    private HashSet<String> union(HashSet<String> set1, HashSet<String> set2) {
        HashSet<String> unionSet = new HashSet<>();
        if(set1 != null) {
            for (String i : set1) {
                unionSet.add(i);
            }
        }
        if(set2 != null) {
            for (String j : set2) {
                unionSet.add(j);
            }
        }
        return unionSet;
    }

    private HashSet<String> subtract(HashSet<String> set1, HashSet<String> set2) {
        HashSet<String> set = (HashSet<String>) set1.clone();
        //set1 - set2
        for (String i : set1) {
            if (set2.contains(i))
                set.remove(i);
        }
        return set;
    }

    public HashSet<String> addToSet(String s, HashSet<String> set) {
        if (!(generalUtils.isInteger(s) || generalUtils.isFloat(s)))
            set.add(s);
        return set;
    }

    private void IOcalculation(IRnode node, ArrayList<IRnode> worklist) {
        /** when calculate the liveness
         * MUST follow CGF path when proceed */
        //remove Node from the list
        worklist.remove(node);

        /** for current node
         *  OUT = successor.IN;
         *  IN = OUT + Require - Redefined
         *  */
        HashSet<String> currentOUT = node.getLiveOUT();
        HashSet<String> currentIN = node.getLiveIN();
        /** 1. OUT = successor.IN */
        HashSet<String> newOUT = new HashSet<>();
        for (int successor : getSuccessors(nodesList.indexOf(node))) {
            for (String var : nodesList.get(successor).getLiveIN()) {
                if (var != null) newOUT.add(var);
            }
        }
        /** 2. IN = OUT + Require - Redefined */
        HashSet<String> newIN = union(currentOUT, node.getRequire());
        newIN = subtract(newIN, node.getReDefine());

        if (!(newIN.containsAll(currentIN) && newOUT.containsAll(currentOUT) && currentIN.containsAll(newIN) && currentOUT.containsAll(newOUT))) {
            //if either set changed, put all of its predecessors in worklist
            for (int indexPredec : getPredecessors(nodesList.indexOf(node))) {
                IRnode prede = nodesList.get(indexPredec);
                worklist.add(prede);
            }
        }
        if (!node.opCode.contains("RET")) {
            node.setLiveIN(newIN);
            node.setLiveOUT(newOUT);
        }

    }

    private void livenessAnalysis() {

        ArrayList<IRnode> worklist = (ArrayList<IRnode>) nodesList.clone();

        while(!worklist.isEmpty()) {
            IRnode node = worklist.remove(0);
            IOcalculation(node, worklist);
        }

    }

    private void debug() {
        for(IRnode thisNode: nodesList) {
            printNode(thisNode);
            printNodeLiveness(thisNode);
            System.out.print("\n");
        }
    }

    private void printNode(IRnode node) {

        System.out.print("Node " + nodesList.indexOf(node));
        System.out.print("   [" + node.opCode + " ");
        if (node.operand1 != null)
            System.out.print(node.operand1 + " ");
        else
            System.out.print("  ");
        if (node.operand2 != null)
            System.out.print(node.operand2 + " ");
        if (node.result != null)
            System.out.print(node.result + " ]   ");
        else
            System.out.print("]  ");

        System.out.print("PLUS (");
        for (String s : node.getRequire()) {
            System.out.print(s + " ");
        }
        System.out.print(") ");

        System.out.print("  MINUS (");
        for (String s : node.getReDefine()) {
            System.out.print(s + " ");
        }
        System.out.print(") ");
    }

    private void printNodeLiveness(IRnode node) {

        System.out.print("liveness {");
        for (String var : node.getLiveOUT()) {
            System.out.print(var + " ");
        }
        System.out.print("}");
    }


}
