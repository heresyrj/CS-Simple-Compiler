/**
 * Created by jianruan on 9/20/15.
 */
public class Symbol_Block extends Symbol {

    private Scope blockScope;

    public Symbol_Block(String name, Scope parent)
    {
        super("BLOCK", name, parent);
        blockScope = new Scope(name, parent);
    }

    public Scope getOwnScope() {
        return blockScope;
    }
}
