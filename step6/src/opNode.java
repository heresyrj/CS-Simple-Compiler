/**
 * Created by jianruan on 10/15/15.
 */
public class opNode extends ASTnode {

    private ASTnode leftNode;
    private ASTnode rightNode;



    public opNode (String opcode, ASTnode left, ASTnode right) {
        super("OP", opcode);
        leftNode = left;
        rightNode = right;
        CodeAndResult();
    }

    public String getOpcode () {return getValue();}
    public String getTemp () {return temp;}
    public ASTnode getAnode() {return rightNode;}


    public void CodeAndResult()
    {


        String op = getOpcode();
        switch (op) {
            case ":=":
                code = ";"+determineOperator(op) + " "+ rightNode.temp +" "+ leftNode.temp;
                break;
            case "+":
                code = arithmCodeGen("+");
                break;
            case "-":
                code = arithmCodeGen("-");
                break;
            case "*":
                code = arithmCodeGen("*");
                break;
            case "/":
                code = arithmCodeGen("/");
                break;
            default:
                System.out.println("non-exit opcode");
                break;
        }
        //System.out.println(code);
        generalUtils.storeCode(code);
    }

    public String arithmCodeGen(String op) {
        temp = generalUtils.generateGlobalName();
        code = ";"+determineOperator(op) + " " + leftNode.temp + " " +rightNode.temp + " " + temp;
        return code;
    }
    public String getDataType(ASTnode node) {
        if (node.getType().equals("OP")) {
            opNode opnode = (opNode)node;
            return getDataType(opnode.rightNode);
        } else {
            return node.getType();
        }
    }
    public String determineOperator(String op) {

        String type_left = getDataType(leftNode);
        String type_right = getDataType(rightNode);
        String longer;
        String shorter;
        if (type_left.length() >= type_right.length()) {
            longer = type_left;
            shorter = type_right;
        }
        else {
            longer = type_right;
            shorter = type_left;
        }


        String type = null;
        if(longer.contains(shorter))
        {
            if(type_left.contains("INT")) type = "INT";
            if(type_left.contains("FLOAT")) type = "FLOAT";
        } else {
            System.out.println("inconsistent types in math operation.");
        }

        String operator = null;

        switch (op) {
            case ":=":
                assert type != null;
                if(type.equals("INT")) operator = "STOREI";
                if(type.equals("FLOAT")) operator = "STOREF";
                break;
            case "+":
                assert type != null;
                if(type.equals("INT")) operator = "ADDI";
                if(type.equals("FLOAT")) operator = "ADDF";
                break;
            case "-":
                assert type != null;
                if(type.equals("INT")) operator = "SUBI";
                if(type.equals("FLOAT")) operator = "SUBF";
                break;
            case "*":
                assert type != null;
                if(type.equals("INT")) operator = "MULTI";
                if(type.equals("FLOAT")) operator = "MULTF";
                break;
            case "/":
                assert type != null;
                if(type.equals("INT")) operator = "DIVI";
                if(type.equals("FLOAT")) operator = "DIVF";
                break;
            default:
                System.out.println("invalid operator occur in building AST node");
        }

        return operator;
    }




}
