/**
 * Created by jianruan on 11/20/15.
 */
public class ASTNode_IF_Exit extends ASTnode {
    String endLabel;
    public ASTNode_IF_Exit (String endLabel) {
        super("ASTNode_IF_Exit","if_exit");
        this.endLabel = endLabel;
        CodeAndResult();
    }

    public void CodeAndResult() {
        code.add(";LABEL " + endLabel);
    }
}
