/**
 * Created by jianruan on 9/19/15.
 */
public class funcSymbol extends Symbol {

    private Scope funcScope;
    private String returnType;

    public funcSymbol(String name, Scope parent) {
        super("FUNCTION", name, parent);
        funcScope = new Scope("FUNCTION", parent);
    }

    public Scope getOwnScope() {
        return funcScope;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String type) {
        returnType = type;
    }

}
