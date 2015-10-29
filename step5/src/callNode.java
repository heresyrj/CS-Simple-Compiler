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

        String part1 = ";" + determineOperator();
        while(!arguments.isEmpty())
        {
            code = part1 + " " + arguments.remove(arguments.size()-1).temp;
            generalUtils.storeCode(code);
        }
        //System.out.println(code);

    }

    public String determineOperator() {
        String call = getCall();
        String type = getDataType(arguments.get(0));
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
