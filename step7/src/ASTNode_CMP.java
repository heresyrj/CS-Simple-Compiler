/**
 * Created by jianruan on 10/15/15.
 */
public class ASTNode_Cmp extends ASTNode {

    private ASTNode leftNode;
    private ASTNode rightNode;
    private String label;

    public ASTNode_Cmp(String cmpCode, ASTNode left, ASTNode right, String label) {
        super("CMP", cmpCode);
        leftNode = left;
        rightNode =right;
        this.label = label;
        CodeAndResult();
    }

    public String getCmpcode () {return getValue();}
    public String getTemp () {return label;}
    public ASTNode getAnode() {return rightNode;}

    public void CodeAndResult()
    {
        String cmp = getCmpcode();
        switch (cmp) {
            case "=":
                code = cmpCodeGen("=");
                break;
            case "!=":
                code = cmpCodeGen("!=");
                break;
            case "<":
                code = cmpCodeGen("<");
                break;
            case ">":
                code = cmpCodeGen(">");
                break;
            case "<=":
                code = cmpCodeGen("<=");
                break;
            case ">=":
                code = cmpCodeGen(">=");
                break;
            default:
                System.out.println("non-exit cmpCode");
                break;
        }
        //System.out.println(code);
        generalUtils.storeCode(code);
    }

    public String cmpCodeGen(String cmp) {
        code = ";"+determineOperator(cmp) + " " + leftNode.temp + " " +rightNode.temp + " " + label;
        return code;
    }

    public String determineOperator(String op) {

        String operator = null;
        String cmp = getCmpcode();
        /** The name is the opposite
         * because it when "=" is seen in cond,
         * what we need is  branch if NOT equal to*/
        switch (cmp) {
            case "=":
                operator = "NE";
                break;
            case "!=":
                operator = "EQ";
                break;
            case "<":
                operator = "GE";
                break;
            case ">":
                operator = "LE";
                break;
            case "<=":
                operator = "GT";
                break;
            case ">=":
                operator = "LT";
                break;
            default:
                System.out.println("invalid cmp operator occur in building AST node");
        }

        return operator;
    }

}
