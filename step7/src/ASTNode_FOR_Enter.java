/**
 * Created by jianruan on 11/20/15.
 */
public class ASTNode_FOR_Enter extends ASTnode {
    String backToFor_label;

    public ASTNode_FOR_Enter(String backToFor_label) {
        super("FOR_Enter","for_enter");
        CodeAndResult();
    }
    public void CodeAndResult() {
        code.add(";LABEL " + backToFor_label);
    }

}
