import java.util.ArrayList;

/**
 * Created by jianruan on 10/15/15.
 */
public class ASTNode_Call extends ASTNode {

    private ArrayList<ASTNode> arguments;
    String result;

    public ASTNode_Call(String call, ArrayList<ASTNode> arguments) {
        super("CALL", call);
        this.arguments = arguments;
        CodeAndResult();
    }
    public String getCall() {return getValue();}
    @Override
    public void CodeAndResult() {


        while(!arguments.isEmpty())
        {
            ASTNode currentNode = arguments.remove(arguments.size()-1);
            String currentValue = currentNode.temp;
            String part1 = ";" + determineOperator(currentNode);
            code = part1 + " " + currentValue;
            GeneralUtils.storeCode(code);
        }
        //System.out.println(code);

    }

    public String determineOperator(ASTNode current) {
        String call = getCall();
        String type = getDataType(current);
        if (type.contains("INT")) return call+"I";
        else if (type.contains("FLOAT")) return call+"F";
        else return call+"S";
    }

    public String getDataType(ASTNode node) {
        if (node.getType().equals("OP")) {
            ASTNode_Op opnode = (ASTNode_Op)node;
            return getDataType(opnode.getAnode());
        } else {
            return node.getType();
        }
    }


}
