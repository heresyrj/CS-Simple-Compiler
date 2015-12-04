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
        intializeRegisters(4);
        currentNode = null;
    }

    private void intializeRegisters (int num) {
        for(int i = 0; i < num; i++) {
            registerMapping.put(("r"+i), new register("r"+i));
        }
    }

    private boolean isAlive(String var, IRnode node) {
        return node.getLiveIN().contains(var);
    }

    private String ensure (String var) {
        for (register r : registerMapping.values()) {
            if(r.valueStored.equals("var") && !r.dirty) {
                return r.name;
            }
        }

        String reg = allocate(var);

        /**generate load from opr into r*/

        return reg;
    }

    private void free (String r) {
        register reg = registerMapping.get(r);
        if (reg.dirty && isAlive(reg.valueStored, currentNode)) {
            /**generate store*/
        }
        else {
            reg.dirty = false;
        }
    }

    private register getAFreeRegister () {
        for (register r : registerMapping.values()) {
            if(!r.dirty) {
                return r;
            }
        }
        return null;
    }

    private register chooseRegToFree(IRnode node) {
        HashSet<String> neededVars = node.getRequire();
        for(register r : registerMapping.values()) {
            if(!neededVars.contains(r.valueStored))
                return r;
        }
        return null;
    }

    private String allocate (String var) {
        register r = getAFreeRegister();
        if(r == null) {
            r = chooseRegToFree(currentNode);
        }
        r.valueStored = var;
        return r.name;
    }


}
