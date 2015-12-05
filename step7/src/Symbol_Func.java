import java.util.HashMap;

/**
 * Created by jianruan on 9/19/15.
 */
public class Symbol_Func extends Symbol {

    private Scope funcScope;
    private String returnType;
    private HashMap<String, funcVar> locals;

    public Symbol_Func(String name, Scope parent) {
        super("FUNCTION", name, parent);
        funcScope = new Scope("FUNCTION", parent);
        locals = new HashMap<>();
    }

    private class funcVar {
        public Symbol symbol;
        public String type;
        public String label;
    }

    public String getFuncVarLabel(String var) {
        return locals.get(var).label;
    }
    public String getFuncVarType(String var) {
        return locals.get(var).type;
    }

    public void localSymbolTable () {
        GeneralUtils.paraCounter = 1;
        GeneralUtils.localCounter = 1;

        for(Symbol s : funcScope.getSymbolList()) {
            String key = s.sym_getName();
            funcVar newVar = new funcVar();
            newVar.symbol = s;
            newVar.type = GeneralUtils.getVarType(key);
            if(getLocalOrPara(key)) {
                newVar.label = GeneralUtils.generateParaName();
            } else {
                newVar.label = GeneralUtils.generateLocalName();
            }
            locals.put(key, newVar);
        }
        String funcName = sym_getName();
        GeneralUtils.directoryLookup.put(funcName, locals);
    }

    public boolean isLocal (String var) {
        for (Symbol s :funcScope.getSymbolList()) {
            if (s.sym_getName().equals(var)) return true;
        }
        return false;
    }
    public String getLocalType(String var) {
        for (Symbol s :funcScope.getSymbolList()) {
            if (s.sym_getName().equals(var)) return s.sym_getType();
        }
        return "ERR";
    }
    public boolean getLocalOrPara(String var)
    {

        for(Symbol s :funcScope.getSymbolList()){
            if(s.sym_getName().equals(var)) {
                if(s instanceof Symbol_Int) {
                    return ((Symbol_Int)s).isPara();
                }
                if(s instanceof Symbol_Float) {
                    return ((Symbol_Float)s).isPara();
                }
            }
        }
        return false;
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
