/**
 * Created by jianruan on 11/20/15.
 */
public class ASTNode_FUNCTION_Pre extends ASTnode {
    public ASTNode_FUNCTION_Pre(String funcName) {
        super("FUNCTION_Pre",funcName);
        CodeAndResult();
    }
    public void CodeAndResult() {
        code.add(";LABEL "+getValue());
        code.add(";LINK");
    }
}
