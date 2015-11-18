import java.util.ArrayList;

/**
 * Created by jianruan on 11/17/15.
 */
public class funcNode extends ASTnode {
    ArrayList<String> paraMeters;
    public funcNode (String funcName,ArrayList<String> paraMeters) {
        super("FUNCTION",funcName);
        this.paraMeters = paraMeters;
        CodeAndResult();
    }

    opNode node;
    public funcNode (String funcName, ASTnode node) {
        super("FUNCTION",funcName);
        this.node = (opNode) node;
        CodeAndResult2();
    }
    @Override
    public String getType() {
        funcSymbol fs = (funcSymbol) generalUtils.SymbolTable.get(getValue());
        return fs.getReturnType();
    }

    @Override
    public void CodeAndResult() {

        int numOfPara = paraMeters.size();
        generalUtils.storeCode(";PUSH");

        String scope = generalUtils.getCurrentScope();
        for (String paraMeter : paraMeters) {
            funcSymbol func = (funcSymbol) generalUtils.SymbolTable.get(scope);
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
