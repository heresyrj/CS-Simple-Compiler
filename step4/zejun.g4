grammar Micro;
/*@header{
	import java.util.*;
	import org.antlr.v4.runtime.*;
}*/

/* Program */
//program           : 'PROGRAM' pgm_id 'BEGIN' pgm_body 'END' ;
program           : 'PROGRAM' id 'BEGIN' pgm_body 'END' ;
//pgm_id		  	  : IDENTIFIER ;
id                returns [string c]
                    : IDENTIFIER;
func_id 		  : IDENTIFIER;
pgm_body          : decl func_declarations ;
decl		      : string_decl decl | var_decl decl | ;

/* Global String Declaration */
//string_decl       : 'STRING' id ':=' str ';';
string_decl       : 'STRING' id_decl ':=' str ';';
id_decl			  : IDENTIFIER;
varid_decl		  : IDENTIFIER;
str               : STRINGLITERAL ;

/* Variable Declaration */
var_decl          : var_type varid_list ';' ;
var_type	      : 'FLOAT' | 'INT' ;
any_type          : var_type | 'VOID' ;
//id_list           : id id_tail ;
//id_list           : id_decl id_tail ;
id_list           : id id_tail ;
varid_list           : varid_decl varid_tail ;
//id_tail           : ',' id id_tail | ;
//id_tail           : ',' id_decl id_tail | ;
id_tail           : ',' id id_tail | ;
varid_tail           : ',' varid_decl varid_tail | ;


/* Function Paramater List */
param_decl_list   : param_decl param_decl_tail | ;
//param_decl        : var_type id ;
param_decl        : var_type varid_decl ;
param_decl_tail   : ',' param_decl param_decl_tail | ;

/* Function Declarations */
func_declarations : func_decl func_declarations | ;
//func_decl         : 'FUNCTION' any_type id '(' param_decl_list ')'  'BEGIN' func_body 'END' ;
func_decl         : 'FUNCTION' any_type func_id '(' param_decl_list ')'  'BEGIN' func_body 'END' ;
//func_id 		  : IDENTIFIER;
func_body         : decl stmt_list ;

/* Statement List */
stmt_list         : stmt stmt_list | ;
stmt              : base_stmt | if_stmt | for_stmt ;
base_stmt         : assign_stmt | read_stmt | write_stmt | return_stmt ;

/* Basic Statements */
assign_stmt       : assign_expr ';' ; //{System.out.println($assign_expr.c);} ;
assign_expr       returns [Myastnode ass_expr_node]
                    : id ':=' expr {$ass_expr_node = generatenode($id.ass, op, )} ;
read_stmt         : 'READ' '(' id_list ')' ';' ;
write_stmt        : 'WRITE' '(' id_list ')' ';' ;
return_stmt       : 'RETURN' expr ';' ;

/* Expressions */
expr              returns [MyASTnode expr_node]
				  : expr_prefix factor ;
expr_prefix       : expr_prefix factor addop | ;
factor            : factor_prefix postfix_expr ;
factor_prefix     : factor_prefix postfix_expr mulop | ;
postfix_expr      returns [MyASTnode post_node]
				  : primary {$post_node = $primary.pri_node;}
				  | call_expr ;
call_expr         : id '(' expr_list ')' {System.out.println($id.text);};
expr_list         : expr expr_list_tail | ;
expr_list_tail    : ',' expr expr_list_tail | ;
primary           returns [MyASTnode pri_node]
				  : '(' expr ')' {$pri_name = $expr.expr_node;}
				  | id {
				  		$pri_node = CreateIdASTnode($id.text)}
				  		System.out.println($id.text);}
				  }
				  | INTLITERAL {System.out.println($INTLITERAL.text);}
				  | FLOATLITERAL ;
addop             :
                   '+'
                   | '-' ;
mulop             : '*' | '/' ;

/* Complex Statements and Condition */
if_stmt           : 'IF' '(' cond ')' decl stmt_list else_part 'FI' ;
else_part         : 'ELSE' decl stmt_list | ;
cond              : expr compop expr ;
compop            : '<' | '>' | '=' | '!=' | '<=' | '>=' ;

init_stmt         : assign_expr | ;
incr_stmt         : assign_expr | ;

/* ECE 468 students use this version of for_stmt */
for_stmt          : 'FOR' '(' init_stmt ';' cond ';' incr_stmt ')' decl stmt_list 'ROF' ;

intliteral: INTLITERAL;
floatliteral: FLOATLITERAL;
stringliteral: STRINGLITERAL;
comment: COMMENT;
identifier: IDENTIFIER;

keywords: KEYWORD;

operators: OPERATOR;

nothing: NOTHING;

INTLITERAL: [0-9]+;
FLOATLITERAL: [0-9]*'.'[0-9]+;
STRINGLITERAL: '"'~('"')*'"';
COMMENT: '--'~('\n')*'\n' -> skip;

KEYWORD: 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT' | 'ROF';

OPERATOR: ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=';

IDENTIFIER: [A-Za-z][A-Za-z0-9]*;

fragment NOTHING: ' ' | '\n' | '\r' | '\t';

WHITESPACE : NOTHING -> skip;
