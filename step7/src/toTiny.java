import java.util.ArrayList;

/**
 * Created by jianruan on 12/4/15.
 */
public class toTiny {

    /** USE method in registerAllocation to get proper registers*/

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();
    private regAllocToolkit toolkit;
    private ArrayList<IRnode> IRnodes;


    public toTiny (regAllocToolkit toolkit, ArrayList<IRnode> IRnodes) {
        this.toolkit = toolkit;
        this.IRnodes = IRnodes;
    }

    /** return num of "$L" in a function */
    private int getNumOfLocalVars(String funcName) {
        return ((Symbol_Func)generalUtils.SymbolTable.get(funcName)).getNumOfLocals();
    }
    private int getCaseNum(IRnode node) {
        return 0;
    }

    private void generateTinyNodes() {

        String currentFunc = null;

        for (IRnode node : IRnodes) {
            int caseNum = getCaseNum(node);
            switch (caseNum) {

            }

        }


    }

    public void printTiny() {
        for (tinyNode node : nodeListTiny) {
            System.out.println(node.opCode + " " + node.operand1 + " " + node.operand2);
        }
        System.out.print("end");
    }





}
