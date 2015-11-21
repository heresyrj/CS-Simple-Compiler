/**
 * Created by jianruan on 11/20/15.
 */
public class ASTNode_FOR_Exit extends ASTnode{
    String goToEnd_label;
    String backToFor_label;
    public ASTNode_FOR_Exit( String backToFor_label, String goToEnd_label) {
        super("For_Exit", "for_block");
        this.goToEnd_label = goToEnd_label;
        this.backToFor_label = backToFor_label;
        CodeAndResult();
    }
    public void CodeAndResult() {
        code.add(";JUMP " + backToFor_label);
        code.add(";LABEL " + goToEnd_label);
    }

}

