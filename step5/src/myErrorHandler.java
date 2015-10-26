/**
 * Created by jianruan on 9/12/15.
 */
import org.antlr.v4.runtime.*;

class MyErrorHandler extends DefaultErrorStrategy {

    @Override
    public void reportError(Parser parser, RecognitionException e) {
        /*
        System.out.println(e);
        System.out.println("Error thrown at RuleContext: " + e.getCtx());
        System.out.println("Expected Token: " + e.getExpectedTokens());
        System.out.println("InputStream: " + e.getInputStream());
        System.out.println("which recognizer: " +e.getRecognizer());
        System.out.println(e.getOffendingToken());
        */

        System.out.println("Not accepted");
        System.exit(9);
    }
}
