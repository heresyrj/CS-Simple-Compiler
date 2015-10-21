/**
 * Created by jianruan on 9/19/15.
 */
public class intSymbol extends Symbol {

    private int value;

    public intSymbol(String name, Scope scope) {
        super("INT", name, scope);
    }

    public intSymbol(String name, String value, Scope scope) {
        super("INT", name, scope);
        this.value = Integer.valueOf(value);
    }

    public int sym_getInt() {
        return value;
    }
    public void sym_setValue(int i) { value = i;}
}
