import java.util.ArrayList;

/**
 * Created by jianruan on 10/15/15.
 */
public class callNode extends ASTnode {

    private ArrayList<ASTnode> arguments;
    String result;

    public callNode(String call, ArrayList<ASTnode> arguments) {
        super("CALL", call);
        this.arguments = arguments;
        CodeAndResult();
    }
    public String getCall() {return getValue();}
    @Override
    public void CodeAndResult() {


        while(!arguments.isEmpty())
        {
            ASTnode currentNode = arguments.remove(arguments.size()-1);
            String currentValue = currentNode.temp;
            String part1 = ";" + determineOperator(currentNode);
            code = part1 + " " + currentValue;
            generalUtils.storeCode(code);
        }
        //System.out.println(code);

    }

    public String determineOperator(ASTnode current) {
        String call = getCall();
        String type = getDataType(current);
        if (type.contains("INT")) return call+"I";
        else if (type.contains("FLOAT")) return call+"F";
        else return call+"S";
    }

    public String getDataType(ASTnode node) {
        if (node.getType().equals("OP")) {
            opNode opnode = (opNode)node;
            return getDataType(opnode.getAnode());
        } else {
            return node.getType();
        }
    }


}
