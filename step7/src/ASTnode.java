/**
 * Created by jianruan on 10/15/15.
 */
public abstract class ASTNode {

    private String value;
    private String type;
    protected String code;
    protected String temp;

    public ASTNode(String type, String value) {

        this.type = type;
        this.value = value;
        code = null;
        temp =null;
    }

    public String getValue() {return value;}
    public String getType() {return type;}
    public abstract void CodeAndResult();
}
