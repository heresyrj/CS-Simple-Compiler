package symbolTable;

/**
 * Created by jianruan on 9/20/15.
 */
public class floatSymbol extends Symbol {

    private float value;

    public floatSymbol(String name, String value, Scope scope) {
        super("INT", name, scope);
        this.value = Float.valueOf(value);
    }

    public float sym_getFloat() {
        return value;
    }

}
