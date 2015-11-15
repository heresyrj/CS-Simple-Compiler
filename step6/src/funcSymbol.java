import java.util.HashMap;

/**
 * Created by jianruan on 9/19/15.
 */
public class funcSymbol extends Symbol {

    private Scope funcScope;
    private String returnType;
    private HashMap<String, Symbol> locals;

    public funcSymbol(String name, Scope parent) {
        super("FUNCTION", name, parent);
        funcScope = new Scope("FUNCTION", parent);
        locals = new HashMap<>();
    }

    public void localSymbolTable () {
        for(Symbol s : funcScope.getSymbolList()) {
            String key = s.sym_getName();
            locals.put(key, s);
        }
    }

    public boolean isLocal (String var) {
        return locals.containsKey(var);
    }
    public String getLocalType(String var) {
        return locals.get(var).sym_getType();
    }
    public String getLocalOrPara(String var)
    {
        Symbol s = locals.get(var);
        boolean para = false;
        if(s instanceof intSymbol) {
            para = ((intSymbol)s).isPara();
        }
        if(s instanceof floatSymbol) {
            para = ((floatSymbol)s).isPara();
        }

        if(para) return "PARA";
        else return "LOCAL";

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
