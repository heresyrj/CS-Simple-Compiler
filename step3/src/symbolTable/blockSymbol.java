package symbolTable;

/**
 * Created by jianruan on 9/20/15.
 */
public class blockSymbol extends Symbol {

    private Scope scope;

    public blockSymbol(String name, Scope parent, Scope scope) {
        super("BLOCK", name, scope);
    }


}
