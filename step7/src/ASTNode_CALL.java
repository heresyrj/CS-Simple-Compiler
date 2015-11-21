import java.util.ArrayList;

/**
 * Created by jianruan on 10/15/15.
 */
public class ASTNode_CALL extends ASTnode {

    private ArrayList<ASTnode> arguments;


    public ASTNode_CALL(String call, ArrayList<ASTnode> arguments) {
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
            String line = part1 + " " + currentValue;
            addCodeToNode(line);
        }

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
            ASTNode_OP opnode = (ASTNode_OP)node;
            return getDataType(opnode.getAnode());
        } else {
            return node.getType();
        }
    }


}
