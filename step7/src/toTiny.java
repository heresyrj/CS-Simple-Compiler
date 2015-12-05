import java.util.ArrayList;

/**
 * Created by jianruan on 12/4/15.
 */
public class toTiny {

    /** USE method in registerAllocation to get proper registers*/

    static ArrayList<tinyNode> nodeListTiny = new ArrayList<>();
    private regAllocToolkit toolkit;


    public toTiny (regAllocToolkit toolkit) {
        this.toolkit = toolkit;
    }

    private int getNumOfLocalVars(String funcName) {
        int result = ((Symbol_Func)generalUtils.SymbolTable.get(funcName)).getNumOfLocals();
        return result;
    }

    



}
