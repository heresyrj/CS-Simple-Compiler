import java.util.HashSet;

/**
 * Created by jianruan on 11/29/15.
 */
public class IRnode {
    public String opCode;
    public String operand1;
    public String operand2;
    public String result;
    public boolean isleader = false;
    private HashSet<Integer> IN;
    private HashSet<Integer> OUT;

    public IRnode () {
        IN = new HashSet<>();
        OUT = new HashSet<>();
    }

    public boolean addToIN(int instruc) {
        return IN.add(instruc);
    }

    public boolean addToOUT (int instruc) { return OUT.add(instruc);}
}
