import symbolTable.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;


/**
 * Created by jianruan on 9/20/15.
 */
public class myMicroListener extends MicroBaseListener {
    private Scope wrapperScope;
    private Scope global;
    private Scope currentScope;
    private Scope parentScope;
    private Symbol prevSymbol;
    private String currentType;
    private ArrayList<Symbol> symBuffer;
    private ArrayList<String> idStack;
    private int blockcounter;


    public myMicroListener(Scope wrapperScope)
    {
        this.wrapperScope = wrapperScope;
        symBuffer = new ArrayList<>();
        idStack = new ArrayList<>();
        blockcounter = 1;
    }

    /**************************************************************************
     ************************ Symbol Table Generation *************************
     **************************************************************************/

    //Setter & Getter for currentScope
    void setCurrentScope(Scope cs) {
        currentScope = cs;
    }
    Scope getCurrentScope() {
        return currentScope;
    }

    //Setter & Getter for parentScope
    void setParentScope(Scope ps) {
        parentScope = ps;
    }
    Scope getParentScope() {
        return parentScope;
    }

    //Setter & Getter  the currentSymbol
    void setCurrentType(String t) {
        currentType = t;
    }
    String getCurrentType() {return currentType;}

    //Setter & Getter  the currentType
    void setCurrentSymbol(Symbol s) {
        prevSymbol = s;
    }
    Symbol getCurrentSymbol() {
        return prevSymbol;
    }

    void saveBuffertoCurrentScope() {
        while(!symBuffer.isEmpty())
        {
            Symbol s = symBuffer.remove(0);
            getCurrentScope().addSymbol(s);
        }
    }


    /**
     ************************** Program Symbol Handler ************************
     */
    @Override
    public void enterProgram(MicroParser.ProgramContext programContext) {
        //create a new and only program symbol
        programSymbol ps = new programSymbol("temp", null);
        wrapperScope.addSymbol(ps);
        //Program scope is essentially also the global scope
        //right now the parent scope is null or global;
        global = ps.getOwnScope();
        setCurrentScope(global);
        //set currentSymbol as null
        setCurrentSymbol(ps);
        //set currentType as PROGRAM
        setCurrentType("PROGRAM");
    }

    @Override
    public void exitProgram(MicroParser.ProgramContext programContext)
    {
        //dump all the global level symbols into the list
        saveBuffertoCurrentScope();
    }

    /**
     *********************** Primitive Symbol Handler ************************
     */

    /**
        STRING
     */
    @Override
    public void enterString_decl(MicroParser.String_declContext string_declContext)
    {
        //set currentType as PROGRAM
        setCurrentType("STRING");
    }

    @Override
    public void exitString_decl(MicroParser.String_declContext string_declContext) {
        String name = string_declContext.id().getText();
        String value = string_declContext.str().getText();
        //create str symbol
        strSymbol newStr = new strSymbol(name, value, getCurrentScope());
        //add to stack
        symBuffer.add(newStr);
    }
    /**
        INT and FLOAT
     */
    @Override
    public void enterVar_decl(MicroParser.Var_declContext var_declContext) { /* compiled code */ }

    @Override
    public void exitVar_decl(MicroParser.Var_declContext var_declContext)
    {
        String type = var_declContext.var_type().getText();

        //get id_list handler
        MicroParser.Id_listContext id_list = var_declContext.id_list();
        //get num of id

        //dump id in stack, create symbol, and add to symbol list
        add_symbol_from_idStack(type);
    }
    public void enterVar_type(MicroParser.Var_typeContext ctx)
    {
        setCurrentType("UNDEFINED");
    }

    public void enterStmt_list(MicroParser.Stmt_listContext stmt_listContext)
    {
        setCurrentType("UNDEFINED");
    }


    @Override
    public void exitVar_type(MicroParser.Var_typeContext ctx)
    {
        //set type before encounter steam of ids
        //such that can react accordingly
        //check exitID()
        String type = ctx.getText();
        setCurrentType(type);
    }

    @Override
    public void enterParam_decl_list(MicroParser.Param_decl_listContext param_decl_listContext)
    {
        //when return type of the function is not void,
        //the current strategy will generate wrong symbol as if it's part of parameters
        //eg FUNCTION INT main()
        //will give    name main type INT
        //but it doesn't exit
        //clea the idstack before enter into the paralist can avoid it.
        idStack.clear();
    }


    @Override
    public void exitId(MicroParser.IdContext ctx)
    {
        String id = ctx.getText();
        switch (currentType) {
            case "PROGRAM":
                wrapperScope.getSymbol(0).sym_setName(id);
                break;
            case "INT":
                idStack.add(id);
                break;
            case "FLOAT":
                idStack.add(id);
                break;
            case "STRING":
                break;
            default:
                break;
        }
    }

    /**
     *********************** Block Level Symbol Handler ************************
     */
    /**
        FUNCTION
     */
    @Override
    public void enterFunc_decl(MicroParser.Func_declContext func_declContext) {
        //create a new func symbol
        Scope current = getCurrentScope();
        funcSymbol fs = new funcSymbol("temp", current);
        symBuffer.add(fs);

        //dump symbols got so far into current scope
        saveBuffertoCurrentScope();

        //set the parent scope
        setParentScope(current);
        //set the current scope is that of the program, the real global scope
        setCurrentScope(fs.getOwnScope());

        //set currentType as FUNCTION
        setCurrentType("FUNCTION");
    }

    @Override
    public void exitFunc_decl(MicroParser.Func_declContext func_declContext) {
        //get properties of functions
        String funcName = func_declContext.id().getText();
        String returnType = func_declContext.any_type().getText();

        //dump id in stack, create symbol, and add to symbol list
        add_symbol_from_idStack("INT");//hardCoded as INT
        
        //dump all this func level symbols into its list
        saveBuffertoCurrentScope();


        //change the scope to parent scope when exit a func decl
        setCurrentScope(getCurrentScope().getParentScope());
        //if parent is not null, get its parent
        if (getCurrentScope() != null) {
            setParentScope(getCurrentScope().getParentScope());
        }

        //set the name and return type of the function
        //now, it's the last element in the parent symbol list
        funcSymbol thisFunc = (funcSymbol)getCurrentScope().getLastSymbol();
        thisFunc.sym_setName(funcName);
        thisFunc.setReturnType(returnType);

    }

    /**
        IF statement
     */
    @Override
    public void enterIf_stmt(MicroParser.If_stmtContext if_stmtContext)
    {
        if_else_for_ENTER("IF");
    }

    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext if_stmtContext)
    {
        if_else_for_EXIT();
    }

    @Override
    public void enterElse_part(MicroParser.Else_partContext else_partContext)
    {
        if_else_for_ENTER("ELSE");
    }

    @Override
    public void exitElse_part(MicroParser.Else_partContext else_partContext)
    {
        if_else_for_EXIT();
    }


    /**
     FOR statement
     */
    @Override
    public void enterFor_stmt(MicroParser.For_stmtContext for_stmtContext)
    {
        if_else_for_ENTER("FOR");
    }

    @Override
    public void exitFor_stmt(MicroParser.For_stmtContext for_stmtContext)
    {
        if_else_for_EXIT();
    }

    /**

     */
    private void if_else_for_ENTER(String which)
    {
        //create a new block symbol
        Scope current = getCurrentScope();
        String name = "BLOCK " + blockcounter;
        blockSymbol bs = new blockSymbol(name, current);
        symBuffer.add(bs);

        //save symbol got so far into current scope list
        saveBuffertoCurrentScope();

        //set the parent scope
        setParentScope(current);
        //set the current scope is that of the program, the real global scope
        setCurrentScope(bs.getOwnScope());

        //increment counter
        blockcounter++;

        //set currentType as "which"
        setCurrentType(which);
    }

    private void if_else_for_EXIT()
    {
        //dump all the global level symbols into the list
        saveBuffertoCurrentScope();

        //change the scope to parent scope when exit a block
        setCurrentScope(getCurrentScope().getParentScope());
        //now current is parent
        //if parent is not null, get its parent
        if (getCurrentScope() != null) {
            setParentScope(getCurrentScope().getParentScope());
        }
    }

    private void add_symbol_from_idStack(String type) {

        Symbol newVar = null;
        while (!idStack.isEmpty())
        {
            //get single id
            //MicroParser.IdContext id = id_list.getChild(id_list.id().getClass(), index);
            String name = idStack.remove(0);

            //create symbol with found type and push to stack
            switch (type) {
                case "INT":
                    newVar = new intSymbol(name, currentScope);
                    break;
                case "FLOAT":
                    newVar = new floatSymbol(name, currentScope);
                    break;
                default:
                    System.out.println("Error, found type is " + type);
                    break;
            }
            if (newVar != null) {
                symBuffer.add(newVar);
            } else {
                System.out.println("Error occurs in listener class line 128");
            }
        }

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
