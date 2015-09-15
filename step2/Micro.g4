grammar Micro;

/*******************************************************************************************************************
  *                                                     LEXER PART
  *****************************************************************************************************************/

options {
  // We're going to output an AST.
  // output = AST;
  // We're going to use the tokens defined in our MathLexer grammar.
  //tokenVocab = MicroLexer; //unnecessary since lexer and parser grammer put together
}


//From PART 1 to PART 3, the defination getting smaller and smaller in terms of the scope of perspective.


//PART 1:
/*the overall structure of a program file */

/* Program */
//1. the biggest strcuture in a file
//2. decl are 3 types of declarations(excluding function_declarations)
//3. a program body has to be dec followed by functions_declarations (the order matters)
program : 'PROGRAM' id 'BEGIN' pgm_body 'END';
id: IDENTIFIER;
pgm_body:  decl func_declarations;
decl: string_decl decl | var_decl decl | empty;//recursive def. potentially can be replaced with regex

//further define the 3 types of decl
/* Global String Declaration */
string_decl: 'STRING' id ':=' str ';';// STRING name := "john";
str: STRINGLITERAL;


/* Variable Declaration */
var_decl: var_type id_list ';';//  INT i, end, result;
var_type: 'FLOAT' | 'INT';
any_type: var_type | 'VOID';
id_list : id id_tail;//recursive def for tail
id_tail : ',' id id_tail | empty;



//PART 2:
/*specifically for defination of a function */

/* Function Paramater List */
param_decl_list: param_decl param_decl_tail | empty;//again, recursive def
param_decl: var_type id;
param_decl_tail: ',' param_decl param_decl_tail | empty;

/* Function Declarations */
func_declarations: func_decl func_declarations | empty;//recursive, to support nested functions
func_decl: 'FUNCTION' any_type id '(' param_decl_list ')' 'BEGIN' func_body 'END';
func_body: decl stmt_list;


//PART 3:
/*specifically for defination of statements */

/* Statement List */
stmt_list : stmt stmt_list | empty;
stmt : (base_stmt | if_stmt | for_stmt) ';';//statement has to be ended with ";"
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements */
assign_stmt : assign_expr;
assign_expr : id ':=' expr;
read_stmt   : 'READ' ( id_list );
write_stmt  : 'WRITE' ( id_list );
return_stmt : 'RETURN' expr ; //    RETURN F(n-1)+F(n-2);--- the ";" is taken care of by "stmt" def


/*
   {
*/
/*specifically for defination of expressions */

/* Expressions */
// eg. id  ASN    expr
//     x2  :=   x1 - fx/dfx;

//expr:    x1 - fx/dfx
//expr:    F(n-1) + F(n-2)

expr_list: expr expr_list_tail| caller_expr| empty;
expr_list_tail: ',' expr expr_list_tail | empty;

expr : ( (id ops id) | INTLITERAL | FLOATLITERAL | id | caller_expr) expr_tail| empty;
expr_tail: ops expr | empty;

caller_expr: id '(' expr_list ')';

/*
expr        : expr_prefix factor;
expr_prefix : expr_prefix factor addop | empty;

factor      : factor_prefix postfix_expr;
factor_prefix : factor_prefix postfix_expr mulop | empty;
postfix_expr : primary | call_expr;

primary :  (expr) | id | INTLITERAL | FLOATLITERAL;


//when expression is function call
call_expr : id ( expr_list ); //id is func name
expr_list : expr expr_list_tail | empty;
expr_list_tail : COMA expr expr_list_tail | empty;
*/

//define two sets of operations
ops : addop | mulop;
addop   : '+' | '-';// add/minus operation
mulop   : '*' | '/';// muliply/divide operation

/*
    }
*/


/* Complex Statements and Condition */
if_stmt  : 'IF' '(' cond ')' decl stmt_list else_part 'FI';
else_part : 'ELSE' decl stmt_list | empty;
cond : expr compop expr;
compop : '<' | '>' | '=' | '!=' | '<=' | '>=' ;//compop: compare operation


/* For_stmt */
for_stmt : 'FOR' ( init_stmt ';' cond ';' incr_stmt ) decl stmt_list 'ROF';
init_stmt : assign_expr | empty;
incr_stmt : assign_expr | empty;


//define empty
//empty: (WHITESPACE| NEWLINE)* ;
empty:;



/*******************************************************************************************************************
  *                                                     LEXER PART
  *****************************************************************************************************************/
KEYWORD
    : 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE'
    | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'ROF' | 'CONTINUE' | 'BREAK'
    | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT'
    ;

OPERATOR
    : EQ | COL | ADD | SUB | MUL | DIV | ASN | NE
    | LT | GT | LP | RP | SEM | COMA | LE | GE
    ;

IDENTIFIER
    : (LETTER+)(LETTER | NUMBER)*
    ;

INTLITERAL
    : (NUMBER+)
    ;

FLOATLITERAL
    : (NUMBER*)('.')(NUMBER+)
    ;

STRINGLITERAL
    //: ('"')(LETTER | NUMBER | OPERATOR)*('"')
    :('"')(~('"'))*('"')
    ;

COMMENT
    : ('--')(~('\n'|'\r'))*
    -> channel(HIDDEN)
    ;

FORMAT
    : (WHITESPACE | NEWLINE)+
    -> channel(HIDDEN)
    ;

//fragments section
fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment NUMBER : ('0'..'9');
fragment WHITESPACE : (' ');
fragment NEWLINE : ('\n' | '\r' | '\t');

fragment EQ : '=';
fragment GT : '>';
fragment LT : '<';
fragment GE : '>=';
fragment LE : '<=';
fragment NE : '!=';

fragment ADD: '+';
fragment SUB: '-';
fragment MUL: '*';
fragment DIV: '/';
fragment ASN: ':=';//assign

fragment COMA: ',';
fragment COL: ':';
fragment LP : '(';
fragment RP : ')';
fragment SEM: ';';