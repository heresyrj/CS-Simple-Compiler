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
        ArrayList<String> temp = getFlatTokenList(ctx);
        temp.remove(temp.size()-1);
        String[] tokens = new String[temp.size()];
        int i = 0;
        while(!temp.isEmpty()) {
            tokens[i] = temp.remove(0);
            i++;
        }
        tokens = postOrderConverter.infixToRPN(tokens);
        ArrayList<String> result = handleTokens(tokens);
        generalUtils.ASTgenerator(result);

    }

    public ArrayList<String> handleTokens(String[] tokens) {
        ArrayList<String> s = new ArrayList<>();
        int i=0;
        while(i < tokens.length) {
            System.out.print(tokens[i]+ " ");
            s.add(tokens[i]);
            i++;
        }
        System.out.println();
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

}
