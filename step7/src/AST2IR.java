import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jianruan on 10/25/15.
 */
public class AST2IR {

    private ArrayList<String> IRcodes;
    protected HashMap<String, Symbol> SymbolTable = generalUtils.SymbolTable;
    protected HashMap<String, HashMap> directoryLookup = generalUtils.directoryLookup;
    protected HashMap<String, HashMap<String, ArrayList<String>>> funcSymbolTable = new HashMap<String, HashMap<String, ArrayList<String>>>();
    protected HashMap<String, ArrayList<Integer>> funcVarsLookup = new HashMap<String, ArrayList<Integer>>();
    protected ArrayList<IR> nodeListIR;

    public AST2IR(ArrayList<String> codeAggregete) {
        IRcodes = codeAggregete;
        nodeListIR = new ArrayList<>();
        buidIRNode();
    }


    public void buidIRNode() {
        for (String scope : directoryLookup.keySet()) {
            HashMap<String, Symbol> map = directoryLookup.get(scope);
            HashMap<String, ArrayList<String>> tempSymTable = new HashMap<String, ArrayList<String>>();
            if(!(scope.equals("GLOBAL"))) {
                int numl = 0;
                int nump = 0;
                for(String varname : map.keySet()) {
                    Symbol_FUCNTION fs = (Symbol_FUCNTION) SymbolTable.get(scope);
                    String type = fs.getFuncVarType(varname);
                    boolean tmp = fs.getLocalOrPara(varname);
                    String lp = null;
                    if(tmp) {
                        lp = "PARA";
                        nump++;
                    } else {
                        lp = "LOCAL";
                        numl++;
                    }
                    ArrayList<String> templist = new ArrayList<String>();
                    templist.add(type);
                    templist.add(lp);
                    tempSymTable.put(varname, templist);
                }
                funcSymbolTable.put(scope, tempSymTable);
                ArrayList<Integer> templist2 = new ArrayList<Integer>();
                templist2.add(numl);
                templist2.add(nump);
                funcVarsLookup.put(scope, templist2);
            }
        }

        for (String line : IRcodes) {
            String[] splitline = line.split(" ");
            IR newnode = new IR();
            int len = splitline[0].length();
            newnode.opCode = splitline[0].substring(1, len);
            if (splitline[0].contains("STORE")) {
                newnode.operand1 = splitline[1];
                newnode.result = splitline[2];
            } else if (splitline[0].contains("WRITE") || splitline[0].contains("LABEL") || splitline[0].contains("JUMP") || splitline[0].contains("READ") || splitline[0].contains("JSR")) {
                newnode.result = splitline[1];
            } else if (splitline[0].contains("PUSH") || splitline[0].contains("POP") || splitline[0].contains("RET") || splitline[0].contains("LINK")) {
                if(splitline.length > 1) {
                    newnode.result = splitline[1];
                }
            }
            else {
                newnode.operand1 = splitline[1];
                newnode.operand2 = splitline[2];
                newnode.result = splitline[3];
            }
            nodeListIR.add(newnode);
        }
    }

}
