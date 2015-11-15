/**
 * Created by jianruan on 9/19/15.
 */
public abstract class Symbol {
    /**
     * Normal symbol only contains the vars and methods listed below
     * but Function, Program, and Block has their own scope and thus their own symbollist
     */

    private String sym_name;
    private String sym_type;
    private Scope sym_parentScope;

    public Symbol(String type, String name, Scope scope) {
        sym_type = type;
        sym_name = name;
        sym_parentScope = scope;
    }

    public String sym_getType() {
        return sym_type;
    }

    public String sym_getName() {
        return sym_name;
    }

    public void sym_setName(String name) {
        sym_name = name;
    }

    public Scope sym_getParentScope() {
        return sym_parentScope;
    }


}
