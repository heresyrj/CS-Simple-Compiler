import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

/**
 * Created by jianruan on 10/16/15.
 */
public class myASTbuilder extends MicroBaseListener {

    /** every cmp in loop or if-else will require 2 labels
     * first one put before before cmp
     * second put in the cmp ;
     * At the end of for loop
     * the same first label is put in jump
     * the same second label is put after jump
     *
     * eg.
     * for(i=0; i<10; i++)
     * ====>>
     * mov i 0  <-- init
     * LabelStart   <--- firstLabel
     * cmp i 10     <--cmp
     * bge LabelEnd <--- secondLabel
     * ...
     * ...
     * add i i 1  <--- increment
     * b LabelStart  <--- firstLabel
     * LabelEnd <--- secondLabel
     * */

    @Override public void exitIf_cond(MicroParser.If_condContext ctx)
    {
        //step1, generate two labels for for cmp,
        // 1 in cmp & as else. 1 at the end of else
        String end_label = generalUtils.generateCodeLabel();
        String else_label = generalUtils.generateCodeLabel();
        generalUtils.pushLabel(end_label);
        generalUtils.pushLabel(else_label);
        //step2 set proper label for cmp code
        //generalUtils.setlabel4Cmp(else_label);
        //step3, generate code for cmp
        ArrayList<String> temp = getFlatTokenList(ctx);
        convertTokensToAST(temp);

    }

    @Override public void exitFor_cond(MicroParser.For_condContext ctx)
    {
        //step1, generate two labels for for cmp,
        // 1 before cmp, 1 in cmp
        String backToFor_label = generalUtils.generateCodeLabel();
        String goToEnd_label = generalUtils.generateCodeLabel();
        //push to stack
        generalUtils.pushLabel(backToFor_label);
        generalUtils.pushLabel(goToEnd_label);

        //step2, generate code for first Label
        String code4FirstLabel = ";LABEL " + backToFor_label;
        generalUtils.storeCode(code4FirstLabel);

        //step3, generate code for cmp
        //generalUtils.setlabel4Cmp(goToEnd_label);
        ArrayList<String> temp = getFlatTokenList(ctx);
        convertTokensToAST(temp);
    }


    /**For Loop Generation*/
    @Override public void exitInit_stmt(MicroParser.Init_stmtContext ctx)
    {
        ArrayList<String> temp = getFlatTokenList(ctx);
        convertTokensToAST(temp);

    }
    @Override public void exitFor_stmt(MicroParser.For_stmtContext ctx)
    {
        //step1, generate code for increment
        ArrayList<String> temp = getFlatTokenList(ctx.incr_stmt());
        convertTokensToAST(temp);
        //step2:pop out labels from stack with reverse order
        String goToEnd_label = generalUtils.popLabel();
        String backToFor_label = generalUtils.popLabel();
        //step3: generate code for labels
        String codeForJump = ";JUMP " + backToFor_label;
        generalUtils.storeCode(codeForJump);
        String codeForEndLoop = ";LABEL " + goToEnd_label;
        generalUtils.storeCode(codeForEndLoop);
    }

    /** IF-ELSE Statement Generation*/
    //private boolean elseExists = false;
    @Override public void enterElse_part(MicroParser.Else_partContext ctx)
    {
        String elseLabel = generalUtils.popLabel();
        String endLabel = generalUtils.popLabel();

        //generate code for label to end, and add to code
        String codeToEnd = ";JUMP " + endLabel;
        generalUtils.storeCode(codeToEnd);
        //generate code for label as ELSE, and add to code
        String codeAsElse = ";LABEL " + elseLabel;
        generalUtils.storeCode(codeAsElse);


        //push labels back
        generalUtils.pushLabel(endLabel);
        generalUtils.pushLabel(elseLabel);

    }

    @Override public void exitIf_stmt(MicroParser.If_stmtContext ctx)
    {
        //pop to clear labels used for this if-else clause
        String elseLabel = generalUtils.popLabel();
        String endLabel = generalUtils.popLabel();

        //only end will be used
        String codeForEndIF = ";LABEL " + endLabel;
        generalUtils.storeCode(codeForEndIF);
    }


    /**Base Statement Generation*/
    @Override
    public void exitBase_stmt(MicroParser.Base_stmtContext ctx)
    {
        ArrayList<String> temp = getFlatTokenList(ctx);
        //base_stmt contains ";" at the end, remove
        temp.remove(temp.size() - 1);
        convertTokensToAST(temp);
    }


    public void convertTokensToAST(ArrayList<String> temp)
    {
        String[] tokens = new String[temp.size()];
        int i = 0;
        while(!temp.isEmpty()) {
            tokens[i] = temp.remove(0);
            i++;
        }
        tokens = postOrderConverter.infixToRPN(tokens);
        ArrayList<String> result = handleTokens(tokens);
        /**generateAST*/
        generalUtils.ASTgenerator(result);
    }

    public ArrayList<String> handleTokens(String[] tokens) {
        ArrayList<String> s = new ArrayList<>();
        int i=0;
        while(i < tokens.length) {
            String str = tokens[i];
            if(!str.equals(",")) s.add(str);
            i++;
        }
        //System.out.println();
        return s;
    }

    public ArrayList<String> getFlatTokenList(ParseTree tree) {
        ArrayList<String> tokens = new ArrayList<>();
        inOrderTraversal(tokens, tree);
        return tokens;
    }

    private void inOrderTraversal(ArrayList<String> tokens, ParseTree parent) {

        // Iterate over all child nodes of `parent`.
        for (int i = 0; i < parent.getChildCount(); i++) {

            // Get the i-th child node of `parent`.
            ParseTree child = parent.getChild(i);

            if (child instanceof TerminalNode) {
                // We found a leaf/terminal, add its Token to our list.
                TerminalNode node = (TerminalNode) child;
                tokens.add(node.getText());
            }
            else {
                // No leaf/terminal node, recursively call this method.
                inOrderTraversal(tokens, child);
            }
        }
    }


    @Override public void enterFunc_body(MicroParser.Func_bodyContext ctx)
    {
        MicroParser.Func_declContext ct = (MicroParser.Func_declContext) ctx.getParent();
        String func_name = ct.id().getText();
        generalUtils.storeCode(";LABEL "+func_name);
        generalUtils.storeCode(";LINK");
    }


    @Override
    public void exitProgram(MicroParser.ProgramContext programContext)
    {
        //print IR
        generalUtils.compile();
    }

}
