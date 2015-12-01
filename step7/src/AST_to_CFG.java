import java.util.*;

/**
 * Created by jianruan on 11/29/15.
 */
public class AST_to_CFG {
    //set to final to avoid any change
    //to preserve the order
    final private ArrayList<IRnode> nodes;
    //private ArrayList<IRnode> leaderNodes; //--> turns out to be unnecessary once new mark filed is added to IRnode, preserve to debug
    private HashMap<Integer, Integer> adjacency;

    public AST_to_CFG (ArrayList<IRnode> nodes) {
        this.nodes = nodes;
        //leaderNodes = new ArrayList<>();
        //simply treat every single statement as a block
        adjacency = new HashMap<>();
        generateLeaders();
        generateInOut();
    }
    private boolean isCmp(String op) {
        return op.contains("NE") || op.contains("EQ") || op.contains("GE") || op.contains("LE")
                || op.contains("GT") || (op.contains("LT") && !op.contains("MUL"));
    }

    private void generateLeaders () {
        ArrayList<Integer> leaders = new ArrayList<>();
        IRnode previous;
        IRnode current = null;

        for (int i = 0; i < nodes.size(); i++)
        {
            previous = current;
            current = nodes.get(i);
            //case 1: if previous is null, the current statement is the first in program
            //case 2: identify explicit targets, the statements with "Label"
            //case 3: identify implicit targets, the first statements after conditional jump
            boolean cond0 = (previous == null);
            boolean cond1 = false;
            boolean cond2 = false;
            if (!cond0) {
                cond1 = current.opCode.contains("LABEL");
                cond2 = isCmp(previous.opCode);
            }
            if(cond0 || cond1 || cond2 ) { leaders.add(i);}
        }

        Collections.sort(leaders);
        for(Integer index : leaders)
        {
            IRnode node = nodes.get(index);
            node.isleader = true;
            //leaderNodes.add(node);
        }

    }

    private void generateInOut() {
        /**
         * check if all leaders are labeled in origNodes
         * for debug
         * **/
//        System.out.println("\nLeaders :");
//        for(IRnode node : nodes) {
//            if(node.isleader) {
//                System.out.println(nodes.indexOf(node)+1+": "+node.opCode +" "+ node.operand1 +" " + node.result);
//            }
//        }


        /** Analysis for Function Calls.
         * "JSR someFunc"---index i
         *  IN = { i-1 } ---> only 1 element
         *  OUT = { first leader node after "someFunc" label} --> only one element
         *
         *  and
         *
         *  for [ first leader node after "someFunc" label ]
         *  IN = { i } --> may be multiple
         *  OUT = { index of itself + 1 } --> only 1 elements
         *
         *  also
         *
         *  for [the RET node in the func ]
         *  IN = { index of itself - 1 }
         *  OUT = {i + 1};
         * */

        //find all labels
        HashSet<String> labels = new HashSet<>();
        for (IRnode node : nodes) {
            String op = node.opCode;
            if (op.equals("LABEL"))
                labels.add(node.result);
        }

        //find occurences of all labels
        HashMap<String, HashSet<Integer>> labels2Indexes = new HashMap<>();
        //for each label find all locations of it
        for (String label : labels)
        {
            HashSet<Integer> indexes = new HashSet<>();
            for (IRnode node : nodes) {
                String current = node.result;
                if (current != null)
                {
                    if(current.equals(label))
                    {
                        int index = nodes.indexOf(node);
                        indexes.add(index);
                    }
                }
            }
            labels2Indexes.put(label, indexes);
        }

        /** There are two passes, their operations may overlap, but it doesn't hurt */
        int entryPoint = -1;
        int exitPoint = -1;
        HashSet<String> funcLabels = new HashSet<>();

        /** PASS 1: handle all the nodes involving labels **/
        //this pass takes care of function calls and explicit/implicit targets of conditional jump
        for (String label : labels)
        {
            HashSet<Integer> occurs = labels2Indexes.get(label);
            for (int occur : occurs)
            {
                IRnode node = nodes.get(occur);
                //"LABEL label_x" are destinations
                //"JUMP/JSR label_x" / "NE/LE xx xx xx label_x" are sources

                /** If occurrences are destinations */
                if (node.opCode.equals("LABEL"))
                { //-----> not covering Comparison Operation and JSR/JUMP
                    int des = nodes.indexOf(node);
                    HashSet<Integer> sources = (HashSet<Integer>)occurs.clone();
                    sources.remove(des);

                    /** If Destinations are functions */
                    //this part takes care of RET of functions
                    //by finding the first RET after the func label
                    //and add all nodes following the sources of the function to its {OUT} -->case1
                    //also need to add the RET to {IN} of those nodes-->case2
                    if(!label.contains("label"))//--> if function
                    {
                        /**these lines below are ->Irrelevant<- to other operations in the outer most iteration
                         * just to facilitate Pass 2 */
                        /***/funcLabels.add(label);
                        /***/if(label.equals("main")) { entryPoint = des;} //->store entryPoint
                        /*************************************************************************************/

                        for(int addr_first_ret = des; ;addr_first_ret++) {
                            IRnode firstRetNode= nodes.get(addr_first_ret);
                            if(firstRetNode.opCode.contains("RET")) {

                                /**this line is ->Irrelevant<- to other operations in the outer most iteration
                                 * just to facilitate Pass 2 */
                                /***/if(label.equals("main")) { exitPoint = addr_first_ret;} //-> store exitPoint
                                /*****************************************************************************/

                                for(int src : sources) {
                                    //case1
                                    firstRetNode.addToOUT(src+1);//+1 to get the following node
                                    //case2
                                    IRnode followingNode = nodes.get(src+1);
                                    followingNode.addToIN(addr_first_ret);
                                }
                                break;
                            }
                        }
                    }

                    /** For any destination node in general */
                    IRnode des_node = nodes.get(des);
                    for (int source : sources) {
                        //add "sources" into {IN} of des node
                        des_node.addToIN(source);
                        //add "des" into {OUT} of every source node
                        IRnode source_node = nodes.get(source);
                        /** this line handles part of the sources OUTs
                         * all the source need add des as one of its {OUT}   <------------------------------
                         * but it's only OK for unconditional jump                                         |
                         * situations when source is conditional jumps handles below in "else" part        |
                         * */
                        source_node.addToOUT(des); //---> explicit targets
                    }
                        /**                                                                                |
                         * */
                }
                else
                {
                    /**                                                                                    |
                     * If occurrences are sources and conditional jumps *  <--------------------------------
                     */
                    if(isCmp(node.opCode)) {
                        //add follow node into {OUT} of cmp node
                        int indexOfCmp = nodes.indexOf(node);
                        node.addToOUT(indexOfCmp+1);//the following node of cmp node is implicit targets
                        //add cmp node into {IN} of follow node
                        IRnode implicit_node = nodes.get(indexOfCmp+1);
                        implicit_node.addToIN(indexOfCmp);
                    }

                }

            }
        }

        /** PASS 2: handle all the nodes in general **/

        /** Step 1: Validate entry point, aka "main" */
        if(entryPoint == -1) {
            System.out.println("main function is not set");
            System.exit(-1);
        }
        //for debug
        System.out.println("main is at line: " + (entryPoint+1) );
        System.out.println("exit is at line: " + (exitPoint+1) );//---> not necessary is the last line

        /** So far,
         *  1. IN and OUT for all functions are taken cared
         *  2. IN and OUT for other JUMPs are taken cared
         *  3. Entry point for the program is Found
         *
         *  Need to do,
         *  1. add IN and OUT for nodes within functions ---> all function labels are found
         *  2. Particularly, IN for Label main is NULL
         *                   OUT for the RET belongs to main is NULL
         * */


        /** For each function, find the begin and end
         *  add IN and OUT for inner nodes
         *  then take care of begin and end nodes
         *  in a case by case manner (main or regular func)
         * **/
        int funcBegin = -1;
        int funcEnd = -1;
        for (String func : funcLabels) {

            //get Begin and End point for current function
            HashSet<Integer> occurs = labels2Indexes.get(func);
            for(int occur :occurs) {
                if (nodes.get(occur).opCode.contains("LABEL")) {
                    funcBegin = occur;
                    break;
                }
            }
            for(int addr_ret = funcBegin+1; ; addr_ret++) {
                if(nodes.get(addr_ret).opCode.contains("RET")){
                    funcEnd = addr_ret;
                    /** WRONG!!!!!
                     *  fail in recursive functions
                     *  the RET right before next function label
                     *  is the end point for the function
                     *
                     *  also, check previous steps see
                     *  if IN and OUT of jumps are correct
                     ***/
                    break;
                }
            }

            //for debug
            System.out.println("Function "+func+": Begins at "+(funcBegin+1)+" and Ends at "+(funcEnd+1));

        }


    }


}
