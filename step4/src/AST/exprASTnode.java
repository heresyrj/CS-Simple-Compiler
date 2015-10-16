package AST;
import utils.*;

/**
 * Created by jianruan on 10/5/15.
 */
public class exprASTnode {
    private String opcode;
    private exprASTnode leftChild;
    private exprASTnode rightChild;
    private String temporal;
    private String code;
    private String result;//value evaluated from code

    public exprASTnode(String opcode, exprASTnode left, exprASTnode right) {
        this.opcode = opcode;
        this.leftChild = left;
        this.rightChild = right;
        this.temporal = generalUtils.generateVarName();
    }

    public void generateCode() {
        switch (opcode) {
            case "+" :
                codeForAddop();
                break;
            case "-" :
                codeForAddop();
                break;
            case "*" :
                codeForMulop();
                break;
            case "/" :
                codeForMulop();
                break;
            case ":=":
                codeForAssignop();
                break;
            default:
                System.out.println("Impossible Opcode");
        }
    }

    private void codeForAddop() {


    }

    private void codeForMulop() {

    }

    private void codeForAssignop() {

    }


}
