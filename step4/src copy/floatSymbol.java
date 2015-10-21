/**
 * Created by jianruan on 9/20/15.
 */
public class floatSymbol extends Symbol {

    private float value;

    public floatSymbol(String name, Scope scope) {
        super("FLOAT", name, scope);
    }

    public floatSymbol(String name, String value, Scope scope) {
        super("FLOAT", name, scope);
        this.value = Float.valueOf(value);
    }

    public float sym_getFloat() {
        return value;
    }
    public void sym_setValue(float f) { value = f;}

}
