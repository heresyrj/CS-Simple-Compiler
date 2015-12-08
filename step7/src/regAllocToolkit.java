import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jianruan on 12/4/15.
 */
public class regAllocToolkit {

    private HashMap<String, register> registerMapping;
    private IRnode currentNode;

    class register {
        String name;
        boolean dirty;
        String valueStored;
        register (String name) {
            this.name = name;
            dirty = false;
            valueStored = null;
        }
    }

    public regAllocToolkit() {
        registerMapping =  new HashMap<>();
        initializeRegisters(4);
        currentNode = null;
    }

    private void initializeRegisters (int num) {
        for(int i = 0; i < num; i++) {
            registerMapping.put(("r"+i), new register("r"+i));
        }
    }

    public void setCurrentNode(IRnode node) {currentNode = node;}
    public boolean registerContains(String var) {
        for(register r : registerMapping.values()) {
            if(r.valueStored != null)
                if(r.valueStored.equals(var))
                    return true;
        }
        return false;
    }
    public String returnRegisterName(String var) {
        for(register r : registerMapping.values()) {
            if(r.valueStored != null)
                if(r.valueStored.equals(var))
                    return r.name;
        }
        return null;
    }

    private boolean isAlive(String var, IRnode node) {
        if(node.getOUT() != null) {
            return node.getLiveOUT().contains(var);
        } else {
            return false;
        }

    }

    private register chooseRegToFree(IRnode node) {
        //given the node, we know its liveness
        //also easily know it's successors and predecessors
        HashSet<String> neededVars = node.getRequire();
        for(register r : registerMapping.values())
        {
            if(!neededVars.contains(r.valueStored)) return r;
        }
        return null;
    }

    private register getAFreeRegister () {
        for (register r : registerMapping.values()) {
            if(!r.dirty) return r;
        }
        return null;
    }

    /** check lecture notes for the details of the functions below */

    public String ensure (String var) {
        //whenever this method is called
        //a register name has to be returned

        //if the value has been contained in one of the registers
        //return name of that register
        for (register r : registerMapping.values()) {
            if(r.valueStored == null) {
                //for debug
                //System.out.println(r.name +" is used to store " + var);
                r.valueStored = var;
                r.dirty = true;
                return r.name;
            } else {
                if(r.valueStored.equals(var)) {
                    return r.name;
                }
            }
        }
        //otherwise, allocate a register to the var
        register reg = allocate(var);
        //return the register name
        return reg.name;
    }

    public void free (String r) {

        register reg = registerMapping.get(r);
        if (reg.dirty && isAlive(reg.valueStored, currentNode)) {
            if(!reg.valueStored.contains("$"))
            /**generate store*/
            //System.out.println(reg.name+" is dirty and stores "+reg.valueStored);
            toTiny.addTolist(new tinyNode("move ", reg.name,reg.valueStored));
            /**insert the code**/
        }
        reg.valueStored = null;
        reg.dirty = false;
    }

    private register allocate (String var) {
        //to get a register

        /** there are two cases
         * 1. is $T  --> basically means give me an free reg
         * 2. not $T
         * */

        //if there's free register
        //pick any of them
        freeDead(currentNode);
        register r = getAFreeRegister();
        if(r == null) {
            r = chooseRegToFree(currentNode);
            assert r != null;
            free(r.name);
        }

        if(!var.contains("$T")) {
            //put value in the register object
            r.valueStored = var;
            r.dirty = true;
            //load the var into this register
            toTiny.addTolist(new tinyNode("move", r.valueStored, r.name));
        }

        return r;
    }


    public void saveAndReset(HashSet<String> globals) {
        for (register r : registerMapping.values()) {

            if(globals.contains(r.valueStored) && r.dirty) {
                toTiny.addTolist(new tinyNode("move", r.name, r.valueStored));
            }
            r.valueStored = null;
            r.dirty = false;
        }
    }
    public void freeDead(IRnode node) {
        //printouts are for debug

        HashSet<String> liveness = node.getLiveOUT();
//        System.out.print("[ ");
//        for(String var : liveness) {
//            System.out.print(var+" ");
//        }
//        System.out.print(" ] are live out;\n");

        for (register r : registerMapping.values()) {
            //System.out.print(r.name + " value:" + r.valueStored+" ");
            if(r.valueStored != null) {
                if(!liveness.contains(r.valueStored)){
                    free(r.name);
                    //System.out.print("; "+r.name+" is freed; ");
                }
            }
        }
        //System.out.println();
    }

}
