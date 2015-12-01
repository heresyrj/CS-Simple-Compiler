import java.util.HashSet;

/**
 * Created by jianruan on 11/29/15.
 */
public class IRnode {
    public String opCode;
    public String operand1;
    public String operand2;
    public String result;
    private HashSet<Integer> GEN;
    private HashSet<Integer> KILL;

    public IRnode () {
        GEN = new HashSet<>();
        KILL = new HashSet<>();
    }

    public boolean addToGEN (int instruc) {
        return GEN.add(instruc);
    }

    public boolean addToKILL (int instruc) { return KILL.add(instruc);}
}
