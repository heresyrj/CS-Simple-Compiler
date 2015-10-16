package AST;

import utils.generalUtils;

import java.util.regex.Pattern;

/**
 * Created by jianruan on 10/15/15.
 */
public abstract class ASTnode {

    private String value;
    private String type;
    protected String code;
    protected String result;

    public ASTnode(String type, String value) {
        if (Pattern.matches("[a-zA-Z]+",value))
        {
            if(generalUtils.checkExist(value)) {
                System.out.println("undeclared symbol");
            }
        }
        this.type = type;
        this.value = value;
        code = null;
        result =null;
    }

    public String getValue() {return value;}
    public String getType() {return type;}
    public abstract void CodeAndResult();
}
