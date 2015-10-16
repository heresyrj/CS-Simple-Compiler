package AST;

/**
 * Created by jianruan on 10/15/15.
 */
public class assignNode extends ASTnode {

    private ASTnode leftNode;
    private ASTnode rightNode;
    String result;

    public assignNode (ASTnode left, ASTnode right) {
        super("ASSIGN", ":=");

    }

    @Override
    public void CodeAndResult() {

    }
}
