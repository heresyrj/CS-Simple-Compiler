import java.util.ArrayList;

/**
 * Created by jianruan on 11/17/15.
 */
public class ASTNode_FUNCTION extends ASTnode {
    ArrayList<String> paraMeters;
    public ASTNode_FUNCTION(String funcName, ArrayList<String> paraMeters) {
        super("FUNCTION",funcName);
        this.paraMeters = paraMeters;
        CodeAndResult();
    }

    ASTNode_OP node;
    public ASTNode_FUNCTION(String funcName, ASTnode node) {
        super("FUNCTION",funcName);
        this.node = (ASTNode_OP) node;
        CodeAndResult2();
    }
    @Override
    public String getType() {
        Symbol_FUCNTION fs = (Symbol_FUCNTION) generalUtils.SymbolTable.get(getValue());
        return fs.getReturnType();
    }

    @Override
    public void CodeAndResult() {

        int numOfPara = paraMeters.size();
        addCodeToNode(";PUSH");

        String scope = generalUtils.getCurrentScope();
        for (String paraMeter : paraMeters) {
            Symbol_FUCNTION func = (Symbol_FUCNTION) generalUtils.SymbolTable.get(scope);
            String temporal = func.getFuncVarLabel(paraMeter);
            addCodeToNode(";PUSH " + temporal);
        }

        addCodeToNode(";JSR "+ getValue());

        for(int i = 0; i < numOfPara; i++) {
            addCodeToNode(";POP");
        }

        //generate a label for function return value
        temp = generalUtils.generateGlobalName();
        addCodeToNode(";POP "+temp);

    }

    public void CodeAndResult2() {
        addCodeToNode(";PUSH");
        addCodeToNode(";PUSH " + node.temp);
        addCodeToNode(";JSR "+ getValue());
        addCodeToNode(";POP");
        temp = generalUtils.generateGlobalName();
        addCodeToNode(";POP "+temp);
    }



}
