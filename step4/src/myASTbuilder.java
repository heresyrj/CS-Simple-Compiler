import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

/**
 * Created by jianruan on 10/16/15.
 */
public class myASTbuilder extends MicroBaseListener {

    @Override
    public void exitBase_stmt(MicroParser.Base_stmtContext ctx)
    {
        ArrayList<String> tokens = getFlatTokenList(ctx);
        String assignExpr = tokens.toString();
        assignExpr = assignExpr.replaceAll(",","");
        assignExpr = assignExpr.substring(1, assignExpr.indexOf(";"));
        System.out.println(assignExpr);
        char[] symbols = assignExpr.toCharArray();

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



}
