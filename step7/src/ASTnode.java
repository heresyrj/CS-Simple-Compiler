import java.util.ArrayList;

/**
 * Created by jianruan on 10/15/15.
 */
public abstract class ASTnode {

    private String value;
    private String type;
    protected ArrayList<String> code;
    protected String temp;

    public ASTnode(String type, String value) {

        this.type = type;
        this.value = value;
        code = new ArrayList<>();
        temp =null;
    }

    public String getValue() {return value;}
    public String getType() {return type;}
    public void addCodeToNode(String line) {
        generalUtils.storeCode(line);
        code.add(line);
    }
    public abstract void CodeAndResult();
}
