/**
 * Created by jianruan on 10/15/15.
 */
public class simpleNode extends ASTnode {

    /**
        The node stores Either a Var or an Int or a Float
      */
    public simpleNode (String type, String value)
    {
        //store "VAR" or "INT" or "FLOAT"
        super(type, value);
        CodeAndResult();
    }

    @Override
    public void CodeAndResult() {
        //For simple Node the code is it self
        if(getType().equals("INT") || getType().equals("FLOAT")) {
            temp = generalUtils.generateVarName();
            if(getType().equals("INT")) {
                code = ";STOREI ";
            } else {
                code = ";STOREF ";
            }
            code =  code + getValue() +" "+ temp;
            //System.out.println(code);
            generalUtils.storeCode(code);
        }
        else {
            //unnecessary to generate Temp for var
            //the code will be depending on the nearest operator
            temp = getValue();
            code = null;
        }
    }

}
