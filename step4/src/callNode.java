/**
 * Created by jianruan on 10/15/15.
 */
public class callNode extends ASTnode {

    private ASTnode arguement;
    String result;

    public callNode(String call, ASTnode node) {
        super("CALL", call);
        arguement = node;
        CodeAndResult();
    }
    public String getCall() {return getValue();}
    @Override
    public void CodeAndResult() {

        code = ";" + determineOperator() + " " + arguement.temp;
        //System.out.println(code);
        generalUtils.storeCode(code);
    }

    public String determineOperator() {
        String type = getDataType(arguement);
        if (type.contains("INT")) return "WRITEI";
        else return "WRITEF";
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
