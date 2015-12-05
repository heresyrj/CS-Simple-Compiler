/**
 * Created by jianruan on 11/14/15.
 */
public class ASTNode_Return extends ASTNode {
    ASTNode result;

    public ASTNode_Return(ASTNode result) {
        super("RETURN", "return");
        this.result = result;
        CodeAndResult();
    }

    public void CodeAndResult(){
        String var = result.getValue();
        boolean consVar = GeneralUtils.isInteger(var) || GeneralUtils.isFloat(var) ;

        String varType;
        if(!consVar) {
            String current = GeneralUtils.getCurrentScope();
            Symbol_Func func = (Symbol_Func) GeneralUtils.SymbolTable.get(current);

            //if var is operation
            boolean isOp = var.contains("-") || var.contains("+") || var.contains("*") || var.contains("/") ;

            if(isOp) {
                temp = result.temp;
                varType = result.getType();
            } else {
                temp = func.getFuncVarLabel(var);
                varType = func.getFuncVarType(var);
            }

        } else {
            String[] info = (GeneralUtils.getRecentConstVar());
            varType = info[0];
            temp = info[1];
        }

        String opCode = null;
        if(varType != null) {
            if(varType.contains("FLOAT")) opCode = ";STOREF";
            else opCode = ";STOREI";
//            else if (varType.contains("INT")) opCode = ";STOREI";
//            else opCode = ";STORES";
        }

        code = opCode + " "+temp+" $R";
        GeneralUtils.storeCode(code);
        code = ";RET";
        GeneralUtils.storeCode(code);
    }
}
