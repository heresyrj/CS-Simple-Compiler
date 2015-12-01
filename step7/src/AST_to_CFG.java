import java.util.*;

/**
 * Created by jianruan on 11/29/15.
 */
public class AST_to_CFG {
    //set to final to avoid any change
    //to preserve the order
    final private ArrayList<IRnode> origNodes;
    final private ArrayList<IRnode> nodes;

    private ArrayList<Integer> leaders;
    private HashMap<Integer, Integer> adjacency;

    public AST_to_CFG (ArrayList<IRnode> nodes) {
        origNodes = nodes;
        this.nodes = purifyNodes(nodes);
        leaders = new ArrayList<>();
        //simply treat every single statement as a block
        adjacency = new HashMap<>();
        generateLeaders();
    }

    //this method get rid of "LINK"
    private ArrayList<IRnode> purifyNodes (ArrayList<IRnode> nodes) {

        HashSet<IRnode> getRid = new HashSet<>();

        for (IRnode node : nodes) {
            String op = node.opCode;
            if (op.contains("LINK") || op.contains("PUSH") || op.contains("POP"))
                getRid.add(node);
        }
        for (IRnode node : getRid) {
            nodes.remove(node);
        }

        return nodes;
    }


    private void generateLeaders () {

        IRnode previous;
        IRnode current = null;

        for (int i = 0; i < nodes.size(); i++) {
            previous = current;
            current = nodes.get(i);

            //case 1: if previous is null, the current statement is the first in program
            //case 2: identify explicit targets, the first statements after label
            //case 3: identify implicit targets, the first statements after conditional jump
            boolean cond0 = (previous == null);
            boolean cond1 = false;
            boolean cond2 = false;
            boolean cond3 = false;
            if (!cond0) {
                cond1 = previous.opCode.contains("LABEL");

                cond2 = previous.opCode.contains("NE") || previous.opCode.contains("EQ") || previous.opCode.contains("GE") || previous.opCode.contains("LE")
                        || previous.opCode.contains("GT") || (previous.opCode.contains("LT") && !previous.opCode.contains("MUL")) ;// ""MULTI" contains "LT"

                cond3 = !(current.opCode.contains("LABEL") );
            }

            if(cond0 || cond1 || (cond2&&cond3) ) { leaders.add(i);}
        }

        //there are cases that multiple labels are stacked together
        //so the ones after first label will be marked as target, but they are not
        HashSet<Integer> getRid = new HashSet<>();
        for (Integer i : leaders) {
            if(nodes.get(i).opCode.contains("LABEL")) getRid.add(i);
        }
        for (Integer i : getRid) {
            leaders.remove(i);
        }

        Collections.sort(leaders);

        /*** For Debug ***/
        System.out.println("\nLeaders :");
        for(Integer index : leaders) {
            IRnode node = nodes.get(index);
            System.out.println(node.opCode +" "+ node.operand1 +" " + node.result);
        }
    }




}
