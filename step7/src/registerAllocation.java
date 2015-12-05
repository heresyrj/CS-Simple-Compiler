import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jianruan on 12/4/15.
 */
public class registerAllocation {
    /**
        For each tuple op A B C in a BB, do
        Rx = ensure(A)
        Ry = ensure(B)
        if A dead after this tuple, free(Rx)
        if B dead after this tuple, free(Ry)
        Rz = allocate(C) //could use Rx or Ry
        generate code for op
        mark Rz dirty
        At end of BB, for each dirty register
        generate code to store register into appropriate variable
     */
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

    public registerAllocation () {
        registerMapping =  new HashMap<>();
        initializeRegisters(4);
        currentNode = null;
    }

    private void initializeRegisters (int num) {
        for(int i = 0; i < num; i++) {
            registerMapping.put(("r"+i), new register("r"+i));
        }
    }

    private boolean isAlive(String var, IRnode node) {
        return node.getLiveIN().contains(var);
    }

    private String ensure (String var) {
        //whenever this method is called
        //a register name has to be returned

        //if the value has been contained in one of the registers
        //return name of that register
        for (register r : registerMapping.values()) {
            if(r.valueStored.equals("var") && !r.dirty) {
                return r.name;
            }
        }
        //otherwise, allocate a register to the var
        register reg = allocate(var);
        //return the register name
        return reg.name;
    }

    private void free (String r) {
        register reg = registerMapping.get(r);
        if (reg.dirty && isAlive(reg.valueStored, currentNode)) {
            /**generate store*/
            String codeForStore = "move " + reg.name + " " + reg.valueStored;
            /**insert the code**/
        }
        reg.valueStored = null;
        reg.dirty = false;

    }

    @Nullable
    private register getAFreeRegister () {
        for (register r : registerMapping.values()) {
            if(!r.dirty) return r;
        }
        return null;
    }

    @Nullable
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

    private register allocate (String var) {
        //to get a register

        //if there's free register
        //pick any of them
        register r = getAFreeRegister();
        if(r == null) {
            r = chooseRegToFree(currentNode);
        }
        //put value in the register object
        assert r != null;
        r.valueStored = var;
        r.dirty = true;
        //load the var into this register
        String loadNewValue = "move " + r.valueStored + " "+ r.name ;
        return r;
    }

}
