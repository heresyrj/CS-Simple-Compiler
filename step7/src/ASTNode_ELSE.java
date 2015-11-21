/**
 * Created by jianruan on 11/20/15.
 */
public class ASTNode_ELSE extends ASTnode {
    String endLabel;
    String elseLabel;

    public ASTNode_ELSE (String endLabel, String elseLabel) {
        super("ASTNode_ELSE","else");
        this.endLabel = endLabel;
        this.elseLabel = elseLabel;
        CodeAndResult();
    }

    public void CodeAndResult() {
        code.add(";JUMP " + endLabel);
        code.add(";LABEL " + elseLabel);
    }
}
