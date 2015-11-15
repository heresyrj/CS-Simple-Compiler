/**
 * Created by jianruan on 11/14/15.
 */
public class returnNode extends ASTnode {

    public returnNode(String result) {
        super("RETURN", result);
        CodeAndResult();
    }

    public void CodeAndResult(){

        code = ";RET\n";
        generalUtils.storeCode(code);
    }
}
