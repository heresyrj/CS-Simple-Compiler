/**
 * Created by jianruan on 9/19/15.
 */
public class strSymbol extends Symbol {

    private String value;

    public strSymbol(String name, String value, Scope scope) {
        super("STRING", name, scope);
        this.value = value;
    }

    public String sym_getStr() {
        return value;
    }
}
