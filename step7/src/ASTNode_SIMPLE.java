/**
 * Created by jianruan on 10/15/15.
 */
public class ASTNode_Simple extends ASTNode {

    /**
        The node stores Either a Var or an Int or a Float
      */
    private String belong = null;//tell if its parameter or local vars or not

    public ASTNode_Simple(String type, String value, String belong)
    {
        //store "VAR" or "INT" or "FLOAT"
        super(type, value);
        this.belong = belong;
        CodeAndResult();
    }

    public String getBelong() {return belong;}

    @Override
    public void CodeAndResult() {
        //For simple Node the code is it self
        if(getType().equals("INT") || getType().equals("FLOAT")) {
            temp = generalUtils.generateGlobalName();
            if(getType().equals("INT")) {
                code = ";STOREI ";
            } else {
                code = ";STOREF ";
            }
            code =  code + getValue() +" "+ temp;
            //System.out.println(code);
            String[] constVar = {getType(),temp};
            generalUtils.constStack.push(constVar);

            generalUtils.storeCode(code);
        }
        else {

            if(belong == null || belong.equals("NOT")) temp = getValue();
            else {
                String var = getValue();
                String current = generalUtils.getCurrentScope();
                Symbol_Func func = (Symbol_Func) generalUtils.SymbolTable.get(current);
                temp = func.getFuncVarLabel(var);
            }
            code = null;
        }
    }

}
