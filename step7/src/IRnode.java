import java.util.ArrayList;

/**
 * Created by jianruan on 11/29/15.
 */
public class IRnode {
    public String opCode;
    public String operand1;
    public String operand2;
    public String result;
    public boolean isleader = false;
    private ArrayList<Integer> IN;
    private ArrayList<Integer> OUT;

    public IRnode () {
        IN = new ArrayList<>();
        OUT = new ArrayList<>();
    }

    public boolean addToGEN (int instruc) {
        return IN.add(instruc);
    }

    public boolean addToKILL (int instruc) { return OUT.add(instruc);}
}
