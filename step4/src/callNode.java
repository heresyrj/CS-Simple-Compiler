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
        code = ";" + getCall() + " " + arguement.temp;
        System.out.println(code);
        generalUtils.storeCode(code);
    }
}
