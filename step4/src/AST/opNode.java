package AST;

/**
 * Created by jianruan on 10/15/15.
 */
public class opNode extends ASTnode {

    private ASTnode leftNode;
    private ASTnode rightNode;


    public opNode (String opcode, ASTnode left, ASTnode right) {
        super("OP", opcode);
        leftNode = left;
        rightNode =right;
    }

    public String getOpcode () {return getValue();}
    public String getResult () {return result;}

    public void CodeAndResult()
    {

        String thisOperation = getValue();

        //case 1: simplest code form
        boolean bool1 = leftNode.getType().equals("VALUE");
        boolean bool2 = rightNode.getType().equals("VALUE");
        boolean bool3 = leftNode.getType().equals("VAR");
        boolean bool4 = rightNode.getType().equals("VAR");

        String leftValue = leftNode.getValue();
        String rightValue = rightNode.getValue();

        if (bool1 && bool2) //two number
        {
            if(leftValue.contains("."))//left is a float
            {
                if(rightValue.contains("."))
                {
                    float rel = Float.parseFloat(leftValue) * Float.parseFloat(rightValue);
                    result = Float.toString(rel);
                }
            } else {//left is an int
                if(!rightValue.contains("."))
                {
                    int rel = Integer.parseInt(leftValue) * Integer.parseInt(rightValue);
                    result = Integer.toString(rel);
                }
            }
            //since it's just a number
            code = result;
        }

        if (bool3 && bool4) //two variables
        {

        }

        if( (bool1&&bool4) || (bool2&&bool3))
        {

        }



    }




}
