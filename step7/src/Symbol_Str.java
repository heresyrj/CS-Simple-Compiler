/**
 * Created by jianruan on 9/19/15.
 */
public class Symbol_Str extends Symbol {

    private String value;

    public Symbol_Str(String name, String value, Scope scope) {
        super("STRING", name, scope);
        this.value = value;
    }

    public String sym_getStr() {
        return value;
    }
}
