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

    private HashSet<String> Require;
    private HashSet<String> ReDefine;

    private HashSet<String> liveIN;
    private HashSet<String> liveOUT;

    public IRnode () {
        IN = new HashSet<>();
        OUT = new HashSet<>();
        liveIN = new HashSet<>();
        liveOUT = new HashSet<>();
    }

    //for the purpose of creating mergeNode in CFG
    public IRnode (HashSet<Integer> in, int out) {
        opCode = "MERGE";
        IN = in;
        OUT = new HashSet<>();
        OUT.add(out);
        liveIN = new HashSet<>();
        liveOUT = new HashSet<>();
    }

    public boolean addToIN(int instruc) {
        return IN.add(instruc);
    }
    public boolean addToOUT (int instruc) { return OUT.add(instruc);}

    public HashSet<String> getLiveIN() { return (HashSet<String>)liveIN.clone(); }
    public HashSet<String> getLiveOUT() { return (HashSet<String>)liveOUT.clone(); }
    public void setLiveIN(HashSet<String> set) {liveIN = set;}
    public void setLiveOUT(HashSet<String> set) {liveOUT = set;}

    public void setRequire (HashSet<String> set) { if(Require == null) Require = set;}
    public void setReDefine (HashSet<String> set) { if(ReDefine == null) ReDefine = set;}
    public HashSet<String> getRequire() {return Require;}
    public HashSet<String> getReDefine() {return ReDefine;}

    public HashSet<Integer> getOUT() { return OUT;}
    public HashSet<Integer> getIN() { return IN;}
    public int getINsize() {return IN.size();}
    public boolean clearIN (){ IN.clear(); return IN.isEmpty();}
    public boolean removeOUT (int index){ return OUT.remove(index);}

}
