lexer grammar MicroLexer;

KEYWORD
    : 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE'
    | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'ROF' | 'CONTINUE' | 'BREAK'
    | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT'
    ;

OPERATOR
    : '=' | ':' | '+' | '-' | '*' | '/' | ':=' | '!='
    | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>='
    ;

fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment NUMBER : ('0'..'9');
fragment WHITESPACE : (' ');
fragment NEWLINE : ('\n' | '\r' | '\t');

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
    ;

FORMAT
    : (WHITESPACE | NEWLINE)+
    ;