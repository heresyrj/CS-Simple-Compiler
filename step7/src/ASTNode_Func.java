import java.util.ArrayList;

/**
 * Created by jianruan on 11/17/15.
 */
public class ASTNode_Func extends ASTNode {
    ArrayList<String> paraMeters;
    public ASTNode_Func(String funcName, ArrayList<String> paraMeters) {
        super("FUNCTION",funcName);
        this.paraMeters = paraMeters;
        CodeAndResult();
    }

    ASTNode_Op node;
    public ASTNode_Func(String funcName, ASTNode node) {
        super("FUNCTION",funcName);
        this.node = (ASTNode_Op) node;
        CodeAndResult2();
    }
    @Override
    public String getType() {
        Symbol_Func fs = (Symbol_Func) generalUtils.SymbolTable.get(getValue());
        return fs.getReturnType();
    }

    @Override
    public void CodeAndResult() {

        int numOfPara = paraMeters.size();
        generalUtils.storeCode(";PUSH");

        String scope = generalUtils.getCurrentScope();
        for (String paraMeter : paraMeters) {
            Symbol_Func func = (Symbol_Func) generalUtils.SymbolTable.get(scope);
            String temporal = func.getFuncVarLabel(paraMeter);
            generalUtils.storeCode(";PUSH " + temporal);
        }

        generalUtils.storeCode(";JSR "+ getValue());

        for(int i = 0; i < numOfPara; i++) {
            generalUtils.storeCode(";POP");
        }

        //generate a label for function return value
        temp = generalUtils.generateGlobalName();
        generalUtils.storeCode(";POP "+temp);

    }

    public void CodeAndResult2() {
        generalUtils.storeCode(";PUSH");
        generalUtils.storeCode(";PUSH " + node.temp);
        generalUtils.storeCode(";JSR "+ getValue());
        generalUtils.storeCode(";POP");
        temp = generalUtils.generateGlobalName();
        generalUtils.storeCode(";POP "+temp);
    }

}
