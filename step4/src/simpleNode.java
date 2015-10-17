/**
 * Created by jianruan on 10/15/15.
 */
public class simpleNode extends ASTnode {

    /**
        The node stores Either a Var or an Int or a Float
      */
    public simpleNode (String type, String value)
    {
        //store "VAR" or "VALUE"
        super(type, value);
    }

    @Override
    public void CodeAndResult() {
        //For simple Node the code is it self
        code = getValue();
        if(getType().equals("VALUE"))
            result = getValue();
        else
            result = null;
    }
}