import symbolTable.*;

import java.util.Stack;


/**
 * Created by jianruan on 9/20/15.
 */
public class myMicroListener extends MicroBaseListener {
    private Scope global;
    private Scope current;
    private Scope parent;
    private Symbol immediatePrev;
    private Stack<Symbol> symStack;

    public myMicroListener() {
        global = null;
        current = null;
        immediatePrev = null;
        parent = null;
        symStack = new Stack<>();
    }

    /**************************************************************************
     ************************ Symbol Table Generation *************************
     **************************************************************************/

    //getter for global scope
    public Scope getGlobal() {
        return global;
    }

    //setter for current scope
    void setCurrentScope(Scope sc) {
        current = sc;
    }

    //getter for current scope
    Scope getCurrentScope() {
        return current;
    }

    //setter for the symbol immediate before id
    void setimmediatePrev(Symbol s) {
        immediatePrev = s;
    }

    //getter for the symbol immediate before id
    Symbol getimmediatePrev() {
        return immediatePrev;
    }

    //add symbol
    void addSymboltoCurrentScope(Symbol s) {
        current.addSymbol(s);
    }


    /**
     ************************** Program Symbol Handler ************************
     */
    @Override
    public void enterProgram(MicroParser.ProgramContext programContext) {
        //create a new and only program symbol
        programSymbol ps = new programSymbol("temp", null);
        //Program scope is essentially also the global scope
        //right now the parent scope is null or global;
        global = ps.getOwnScope();
        current = global;
        //set immediatePrev symbol as null
        setimmediatePrev(null);
    }

    @Override
    public void exitProgram(MicroParser.ProgramContext programContext) {
        programContext.id();
    }

    /**
     *********************** Primitive Symbol Handler ************************
     */

    /**
        STRING
     */
    @Override
    public void enterString_decl(MicroParser.String_declContext string_declContext) { /* compiled code */ }

    @Override
    public void exitString_decl(MicroParser.String_declContext string_declContext) {
        String name = string_declContext.id().getText();
        String value = string_declContext.str().getText();
        //create str symbol
        strSymbol newStr = new strSymbol(name, value, getCurrentScope());
        //add to current scope
        addSymboltoCurrentScope(newStr);
    }
    /**
        INT and FLOAT
     */
    @Override
    public void enterVar_decl(MicroParser.Var_declContext var_declContext) { /* compiled code */ }

    @Override
    public void exitVar_decl(MicroParser.Var_declContext var_declContext)
    {
    	
    }

    /**
     *********************** Block Level Symbol Handler ************************
     */
    /**
        FUNCTION
     */
    @Override
    public void enterFunc_decl(MicroParser.Func_declContext func_declContext) {
        Scope current = getCurrentScope();
        //create a new func symbol
        funcSymbol fs = new funcSymbol("temp", current);
        //set the current scope is that of the program, the real global scope
        setCurrentScope(fs.getOwnScope());
        //set immediatePrev symbol as this func
        setimmediatePrev(fs);
    }

    @Override
    public void exitFunc_decl(MicroParser.Func_declContext func_declContext) {
        //change the scope when exit a func decl
        setCurrentScope(getCurrentScope().getParentScope());
    }

    /**
        IF statement
     */
    @Override
    public void enterIf_stmt(MicroParser.If_stmtContext if_stmtContext) {  
    	Scope current = getCurrentScope();
    	blockSymbol bs = new blockSymbol("temp", current);
    	setCurrentScope(bs.getOwnScope());
    	setimmediatePrev(bs);
    }

    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext if_stmtContext) {
        //change the scope when exit a if block
        setCurrentScope(getCurrentScope().getParentScope());
    }
    /**
        ELSE statement
     */
    @Override
    public void enterElse_part(MicroParser.Else_partContext else_partContext) { 
    	Scope current = getCurrentScope();
    	blockSymbol bs = new blockSymbol("temp", current);
    	setCurrentScope(bs.getOwnScope());
    	setimmediatePrev(bs); 
    }

    @Override
    public void exitElse_part(MicroParser.Else_partContext else_partContext) {
        //change the scope when exit a else block
        setCurrentScope(getCurrentScope().getParentScope());
    }
    /**
        FOR statement
     */
    @Override
    public void enterFor_stmt(MicroParser.For_stmtContext for_stmtContext) { 
    	Scope current = getCurrentScope();
    	blockSymbol bs = new blockSymbol("temp", current);
    	setCurrentScope(bs.getOwnScope());
    	setimmediatePrev(bs);
    }

    @Override
    public void exitFor_stmt(MicroParser.For_stmtContext for_stmtContext) {
        //return to parent scope after exit for stmt
        setCurrentScope(getCurrentScope().getParentScope());
    }

    /**************************************************************************
     ***************************    End of  ***********************************
     ************************ Symbol Table Generation *************************
     **************************************************************************/


    /**
     *********************** Other Utilities Handler ************************
     */
    @Override
    public void enterEveryRule(org.antlr.v4.runtime.ParserRuleContext parserRuleContext) { /* compiled code */ }

    @Override
    public void exitEveryRule(org.antlr.v4.runtime.ParserRuleContext parserRuleContext) { /* compiled code */ }

    @Override
    public void visitTerminal(org.antlr.v4.runtime.tree.TerminalNode terminalNode) { /* compiled code */ }

    @Override
    public void visitErrorNode(org.antlr.v4.runtime.tree.ErrorNode errorNode) { /* compiled code */ }
}
