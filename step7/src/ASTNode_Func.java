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
        Symbol_Func fs = (Symbol_Func) GeneralUtils.SymbolTable.get(getValue());
        return fs.getReturnType();
    }

    @Override
    public void CodeAndResult() {

        int numOfPara = paraMeters.size();
        GeneralUtils.storeCode(";PUSH");

        String scope = GeneralUtils.getCurrentScope();
        for (String paraMeter : paraMeters) {
            Symbol_Func func = (Symbol_Func) GeneralUtils.SymbolTable.get(scope);
            String temporal = func.getFuncVarLabel(paraMeter);
            GeneralUtils.storeCode(";PUSH " + temporal);
        }

        GeneralUtils.storeCode(";JSR "+ getValue());

        for(int i = 0; i < numOfPara; i++) {
            GeneralUtils.storeCode(";POP");
        }

        //generate a label for function return value
        temp = GeneralUtils.generateGlobalName();
        GeneralUtils.storeCode(";POP "+temp);

    }

    public void CodeAndResult2() {
        GeneralUtils.storeCode(";PUSH");
        GeneralUtils.storeCode(";PUSH " + node.temp);
        GeneralUtils.storeCode(";JSR "+ getValue());
        GeneralUtils.storeCode(";POP");
        temp = GeneralUtils.generateGlobalName();
        GeneralUtils.storeCode(";POP "+temp);
    }

}
