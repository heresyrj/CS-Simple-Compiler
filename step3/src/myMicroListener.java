import symbolTable.*;


/**
 * Created by jianruan on 9/20/15.
 */
public class myMicroListener extends MicroBaseListener {
    private Scope global;
    private Scope current;
    private Symbol immediatePrev;

    public myMicroListener() {
        global = new Scope("GLOBAL", null);
        current = global;
        immediatePrev = null;
    }

    //getter for globol scope
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

    @Override
    public void enterProgram(MicroParser.ProgramContext programContext) {
        //create a new and only program symbol
        programSymbol ps = new programSymbol("temp", global);
        //add program symbol to the global scope, its scope is essentially also the global scope
        global.addSymbol(ps);
        //set the current scope is that of the program, the real global scope
        setCurrentScope(ps.getOwnScope());
        //set immediatePrev symbol as program
        setimmediatePrev(ps);
    }

    @Override
    public void exitProgram(MicroParser.ProgramContext programContext) {
    }

    @Override
    public void enterId(MicroParser.IdContext idContext) {
    }

    @Override
    public void exitId(MicroParser.IdContext idContext) {


        Symbol prev = getimmediatePrev();
        //check if the immediatePrev symbol's name is set
        if (prev.sym_getName().equals("temp")) {
            //if true, it means id the first symbol in this type, which has been created when the type is entered
            //set the name of the immediatePrev symbol as the value of ID
            prev.sym_setName(idContext.getText());
        } else {
            //if false, the id the not the first symbol in this type, new symbol should be created and add to list
            //1. get TYPE and NAME from immediatePrev
            String type = prev.sym_getType();
            String name = idContext.getText();

            //2. to handle new symbol creation. assume this situation will only happen for int, float, string
            //in this case the value will of course not be given
            switch (type) {
                case "INT": {
                    //get current scope
                    Scope current = getCurrentScope();
                    //create symbol
                    Symbol s = new intSymbol(name, null, current);
                    //add to list in current scope
                    addSymboltoCurrentScope(s);

                    break;
                }
                case "FLOAT": {
                    Scope current = getCurrentScope();
                    Symbol s = new floatSymbol(name, null, current);
                    addSymboltoCurrentScope(s);

                    break;
                }
                case "STRING": {
                    Scope current = getCurrentScope();
                    Symbol s = new strSymbol(name, null, current);
                    addSymboltoCurrentScope(s);

                    break;
                }
//                case "PROGRAM": {
//                    System.out.println(prev.sym_getType());
//                    prev.sym_setName(idContext.getText());
//
//                    break;
//                }
                default:
                    System.out.println("type is "+ type);
                    System.out.println("id creation failure. incorrect type.");
                    System.exit(11);
            }
        }

    }

    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext pgm_bodyContext) { /*nothing changes*/ }

    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext pgm_bodyContext) {
        //when parse tree exit pgm_body, it'll see END
    }

    @Override
    public void enterDecl(MicroParser.DeclContext declContext) {
        /*decl will be rewrite to specific decl grammer.
        no need to take actions before that */
    }

    @Override
    public void exitDecl(MicroParser.DeclContext declContext) { /* compiled code */ }

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

    @Override
    public void enterStr(MicroParser.StrContext strContext) { /* compiled code */ }

    @Override
    public void exitStr(MicroParser.StrContext strContext) {/* compiled code */ }

    @Override
    public void enterVar_decl(MicroParser.Var_declContext var_declContext) { /* compiled code */ }

    @Override
    public void exitVar_decl(MicroParser.Var_declContext var_declContext) { /* compiled code */ }

    @Override
    public void enterVar_type(MicroParser.Var_typeContext var_typeContext) { /* compiled code */ }

    @Override
    public void exitVar_type(MicroParser.Var_typeContext var_typeContext) {
    }

    @Override
    public void enterAny_type(MicroParser.Any_typeContext any_typeContext) { /* compiled code */ }

    @Override
    public void exitAny_type(MicroParser.Any_typeContext any_typeContext) {
        //var_type only contains INT/FLOAT
        //any_type add VOID -> can only be return type
        //string is assumed not be a func return type
        String type = any_typeContext.getText();
        Symbol prev = getimmediatePrev();

        if (prev.sym_getType().equals("FUNCTION")) {

            ((funcSymbol) prev).setReturnType(type);

        } else {
            //if the type is not return type in a function decl
            switch (type) {
                case "INT": {
                    //get current scope
                    Scope current = getCurrentScope();
                    //create symbol
                    Symbol s = new intSymbol("temp", null, current);
                    //add to list in current scope
                    addSymboltoCurrentScope(s);

                    break;
                }
                case "FLOAT": {
                    Scope current = getCurrentScope();
                    Symbol s = new floatSymbol("temp", null, current);
                    addSymboltoCurrentScope(s);

                    break;
                }
                default:
                    System.out.println("var_type symbol creation failure. ");
                    System.exit(11);
            }
        }

    }

    @Override
    public void enterId_list(MicroParser.Id_listContext id_listContext) { /* compiled code */ }

    @Override
    public void exitId_list(MicroParser.Id_listContext id_listContext) { /* compiled code */ }

    @Override
    public void enterId_tail(MicroParser.Id_tailContext id_tailContext) { /* compiled code */ }

    @Override
    public void exitId_tail(MicroParser.Id_tailContext id_tailContext) { /* compiled code */ }

    @Override
    public void enterParam_decl_list(MicroParser.Param_decl_listContext param_decl_listContext) { /* compiled code */ }

    @Override
    public void exitParam_decl_list(MicroParser.Param_decl_listContext param_decl_listContext) { /* compiled code */ }

    @Override
    public void enterParam_decl(MicroParser.Param_declContext param_declContext) { /* compiled code */ }

    @Override
    public void exitParam_decl(MicroParser.Param_declContext param_declContext) { /* compiled code */ }

    @Override
    public void enterParam_decl_tail(MicroParser.Param_decl_tailContext param_decl_tailContext) { /* compiled code */ }

    @Override
    public void exitParam_decl_tail(MicroParser.Param_decl_tailContext param_decl_tailContext) { /* compiled code */ }

    @Override
    public void enterFunc_declarations(MicroParser.Func_declarationsContext func_declarationsContext) { /* compiled code */ }

    @Override
    public void exitFunc_declarations(MicroParser.Func_declarationsContext func_declarationsContext) { /* compiled code */ }

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

    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext func_bodyContext) { /* compiled code */ }

    @Override
    public void exitFunc_body(MicroParser.Func_bodyContext func_bodyContext) { /* compiled code */ }

    @Override
    public void enterStmt_list(MicroParser.Stmt_listContext stmt_listContext) { /* compiled code */ }

    @Override
    public void exitStmt_list(MicroParser.Stmt_listContext stmt_listContext) { /* compiled code */ }

    @Override
    public void enterStmt(MicroParser.StmtContext stmtContext) { /* compiled code */ }

    @Override
    public void exitStmt(MicroParser.StmtContext stmtContext) { /* compiled code */ }

    @Override
    public void enterBase_stmt(MicroParser.Base_stmtContext base_stmtContext) { /* compiled code */ }

    @Override
    public void exitBase_stmt(MicroParser.Base_stmtContext base_stmtContext) { /* compiled code */ }

    @Override
    public void enterAssign_stmt(MicroParser.Assign_stmtContext assign_stmtContext) { /* compiled code */ }

    @Override
    public void exitAssign_stmt(MicroParser.Assign_stmtContext assign_stmtContext) { /* compiled code */ }

    @Override
    public void enterAssign_expr(MicroParser.Assign_exprContext assign_exprContext) { /* compiled code */ }

    @Override
    public void exitAssign_expr(MicroParser.Assign_exprContext assign_exprContext) { /* compiled code */ }

    @Override
    public void enterRead_stmt(MicroParser.Read_stmtContext read_stmtContext) { /* compiled code */ }

    @Override
    public void exitRead_stmt(MicroParser.Read_stmtContext read_stmtContext) { /* compiled code */ }

    @Override
    public void enterWrite_stmt(MicroParser.Write_stmtContext write_stmtContext) { /* compiled code */ }

    @Override
    public void exitWrite_stmt(MicroParser.Write_stmtContext write_stmtContext) { /* compiled code */ }

    @Override
    public void enterReturn_stmt(MicroParser.Return_stmtContext return_stmtContext) { /* compiled code */ }

    @Override
    public void exitReturn_stmt(MicroParser.Return_stmtContext return_stmtContext) { /* compiled code */ }

    @Override
    public void enterExpr(MicroParser.ExprContext exprContext) { /* compiled code */ }

    @Override
    public void exitExpr(MicroParser.ExprContext exprContext) { /* compiled code */ }

    @Override
    public void enterExpr_prefix(MicroParser.Expr_prefixContext expr_prefixContext) { /* compiled code */ }

    @Override
    public void exitExpr_prefix(MicroParser.Expr_prefixContext expr_prefixContext) { /* compiled code */ }

    @Override
    public void enterFactor(MicroParser.FactorContext factorContext) { /* compiled code */ }

    @Override
    public void exitFactor(MicroParser.FactorContext factorContext) { /* compiled code */ }

    @Override
    public void enterFactor_prefix(MicroParser.Factor_prefixContext factor_prefixContext) { /* compiled code */ }

    @Override
    public void exitFactor_prefix(MicroParser.Factor_prefixContext factor_prefixContext) { /* compiled code */ }

    @Override
    public void enterPostfix_expr(MicroParser.Postfix_exprContext postfix_exprContext) { /* compiled code */ }

    @Override
    public void exitPostfix_expr(MicroParser.Postfix_exprContext postfix_exprContext) { /* compiled code */ }

    @Override
    public void enterCall_expr(MicroParser.Call_exprContext call_exprContext) { /* compiled code */ }

    @Override
    public void exitCall_expr(MicroParser.Call_exprContext call_exprContext) { /* compiled code */ }

    @Override
    public void enterExpr_list(MicroParser.Expr_listContext expr_listContext) { /* compiled code */ }

    @Override
    public void exitExpr_list(MicroParser.Expr_listContext expr_listContext) { /* compiled code */ }

    @Override
    public void enterExpr_list_tail(MicroParser.Expr_list_tailContext expr_list_tailContext) { /* compiled code */ }

    @Override
    public void exitExpr_list_tail(MicroParser.Expr_list_tailContext expr_list_tailContext) { /* compiled code */ }

    @Override
    public void enterPrimary(MicroParser.PrimaryContext primaryContext) { /* compiled code */ }

    @Override
    public void exitPrimary(MicroParser.PrimaryContext primaryContext) { /* compiled code */ }

    @Override
    public void enterAddop(MicroParser.AddopContext addopContext) { /* compiled code */ }

    @Override
    public void exitAddop(MicroParser.AddopContext addopContext) { /* compiled code */ }

    @Override
    public void enterMulop(MicroParser.MulopContext mulopContext) { /* compiled code */ }

    @Override
    public void exitMulop(MicroParser.MulopContext mulopContext) { /* compiled code */ }

    @Override
    public void enterIf_stmt(MicroParser.If_stmtContext if_stmtContext) { /* compiled code */ }

    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext if_stmtContext) {
        //change the scope when exit a if block
        setCurrentScope(getCurrentScope().getParentScope());
    }

    @Override
    public void enterElse_part(MicroParser.Else_partContext else_partContext) { /* compiled code */ }

    @Override
    public void exitElse_part(MicroParser.Else_partContext else_partContext) {
        //change the scope when exit a else block
        setCurrentScope(getCurrentScope().getParentScope());
    }

    @Override
    public void enterCond(MicroParser.CondContext condContext) { /* compiled code */ }

    @Override
    public void exitCond(MicroParser.CondContext condContext) { /* compiled code */ }

    @Override
    public void enterCompop(MicroParser.CompopContext compopContext) { /* compiled code */ }

    @Override
    public void exitCompop(MicroParser.CompopContext compopContext) { /* compiled code */ }

    @Override
    public void enterInit_stmt(MicroParser.Init_stmtContext init_stmtContext) { /* compiled code */ }

    @Override
    public void exitInit_stmt(MicroParser.Init_stmtContext init_stmtContext) { /* compiled code */ }

    @Override
    public void enterIncr_stmt(MicroParser.Incr_stmtContext incr_stmtContext) { /* compiled code */ }

    @Override
    public void exitIncr_stmt(MicroParser.Incr_stmtContext incr_stmtContext) { /* compiled code */ }

    @Override
    public void enterFor_stmt(MicroParser.For_stmtContext for_stmtContext) { /* compiled code */ }

    @Override
    public void exitFor_stmt(MicroParser.For_stmtContext for_stmtContext) {
        //return to parent scope after exit for stmt
        setCurrentScope(getCurrentScope().getParentScope());
    }

    @Override
    public void enterEveryRule(org.antlr.v4.runtime.ParserRuleContext parserRuleContext) { /* compiled code */ }

    @Override
    public void exitEveryRule(org.antlr.v4.runtime.ParserRuleContext parserRuleContext) { /* compiled code */ }

    @Override
    public void visitTerminal(org.antlr.v4.runtime.tree.TerminalNode terminalNode) { /* compiled code */ }

    @Override
    public void visitErrorNode(org.antlr.v4.runtime.tree.ErrorNode errorNode) { /* compiled code */ }
}
