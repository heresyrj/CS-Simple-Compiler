grammar Micro;

/*******************************************************************************************************************
  *                                                     PARSER PART
  *****************************************************************************************************************/
options {
    // antlr will generate java lexer and parser
    language = Java;
    // generated parser should create abstract syntax tree
}

@header {
    import src.*;
}

@member {

}


/* Program */
program           : 'PROGRAM' id 'BEGIN' pgm_body 'END' ;
id                : IDENTIFIER;
pgm_body          : decl func_declarations ;
decl		      : string_decl decl | var_decl decl | ;

/* Global String Declaration */
string_decl       : 'STRING' id ':=' str ';';
str               : STRINGLITERAL ;


/* Variable Declaration */
var_decl          : var_type id_list ';' ;
var_type	      : 'FLOAT' | 'INT' ;
any_type          : var_type | 'VOID' ;
id_list           : id id_tail ;
id_tail           : ',' id id_tail | ;

/* Function Paramater List */
param_decl_list   : param_decl param_decl_tail | ;
param_decl        : var_type id ;
param_decl_tail   : ',' param_decl param_decl_tail | ;

/* Function Declarations */
func_declarations : func_decl func_declarations | ;
func_decl         : 'FUNCTION' any_type id '(' param_decl_list ')'  'BEGIN' func_body 'END' ;
func_body         : decl stmt_list ;

/* Statement List */
stmt_list         : stmt stmt_list | ;
stmt              : base_stmt | if_stmt | for_stmt ;
base_stmt         : assign_stmt | read_stmt | write_stmt | return_stmt ;

/* Basic Statements */
assign_stmt       : assign_expr ';' ;
assign_expr
                  : id ':=' expr

                  ;
read_stmt         : 'READ' '(' id_list ')' ';' ;
write_stmt        : 'WRITE' '(' id_list ')' ';' ;
return_stmt       : 'RETURN' expr ';' ;

/* Expressions */
expr              returns [ASTnode node] //this rule will ALWAYS return a node
                  : expr_prefix factor
                  {

                  }
                  ;
expr_prefix       returns [ASTnode node] //this rule MAY NOT return a node
                  : expr_prefix factor addop
                  {
                    /*opNode opnode = new opNode($addop.text, $expr_prefix.node, $factor.node)*/
                    $node = (ASTnode)opnode;
                  }
                  |
                  ;
factor            returns [ASTnode node] //this rule will ALWAYS return a node
                  : factor_prefix postfix_expr
                  {

                    $node = $postfix_expr.node;
                  }
                  ;
factor_prefix     returns [ASTnode node] //this rule MAY NOT return a node
                  : factor_prefix postfix_expr mulop
                  {
                    /*opNode opnode = new opNode($mulop.text, $factor_prefix.node, $postfix_expr.node)*/
                    $node = (ASTnode)opnode;
                  }
                  |
                  ;
postfix_expr      returns [ASTnode node] //this rule will ALWAYS return a node
                  : primary
                  {
                    $node = $primary.node;
                  }
                  | call_expr
                  {
                  }
                  ;
call_expr         //this will NOT generate Node
                  : id '(' expr_list ')'
                  ;
expr_list         : expr expr_list_tail | ;
expr_list_tail    : ',' expr expr_list_tail | ;
primary           returns [ASTnode node]//this rule will ALWAYS return a node
                  :
                  '(' expr ')'
                  {
                    $node = $expr.node;
                  }
                  | id
                  {
                    simpleNode snode = new simpleNode("VAR", $id.text);
                    $node = (ASTnode)snode;
                    //System.out.println($node.getType()+" "+$node.getValue() );
                  }
                  | INTLITERAL
                  {
                    simpleNode snode = new simpleNode("VALUE", $INTLITERAL.text);
                    $node = (ASTnode)snode;
                    //System.out.println($node.getType()+" "+$node.getValue());

                  }
                  | FLOATLITERAL
                  {
                    simpleNode snode = new simpleNode("VALUE", $FLOATLITERAL.text);
                    $node = (ASTnode)snode;
                    //System.out.println($node.getType()+" "+$node.getValue());
                  }
                  ;
addop             : '+'
                  | '-'
                  ;
mulop             : '*'
                  | '/'
                  ;

/* Complex Statements and Condition */
if_stmt           : 'IF' '(' cond ')' decl stmt_list else_part 'FI' ;
else_part         : 'ELSE' decl stmt_list | ;
cond              : expr compop expr ;
compop            : '<' | '>' | '=' | '!=' | '<=' | '>=' ;

init_stmt         : assign_expr | ;
incr_stmt         : assign_expr | ;


for_stmt          : 'FOR' '(' init_stmt ';' cond ';' incr_stmt ')' decl stmt_list 'ROF' ;


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