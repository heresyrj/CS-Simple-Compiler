/**
 * Created by jianruan on 9/20/15.
 */
public class programSymbol extends Symbol {

    Scope programScope;


    public programSymbol(String name, Scope parent) {
        super("PROGRAM", name, parent);
        programScope = new Scope("GLOBAL", parent);//scope name is global, parent is null;
    }

    public Scope getOwnScope() {
        return programScope;
    }


}
