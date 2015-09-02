lexer grammar Micro;

KEYWORDS
    : 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE'
    | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'ROF' | 'CONTINUE' | 'BREAK'
    | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT'
    ;

OPERATORS
    : '=' | ':' | '+' | '-' | '*' | '/' | '=' | '!='
    | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>='
    ;

fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment NUMBER : ('0'..'9');

IDENTIFIER
    : (LETTER+)(LETTER | NUMBER)*
    ;

INTLITERAL
    : NUMBER+
    ;

FLOATLITERAL
    : (NUMBER*) ('.') (NUMBER+)
    ;

STRINGLITERAL
    : ('"') (LETTER | NUMBER | OPERATORS)* ('"') ~('"')
    ;

COMMENT
    : ('--') (~('\n'|'\r'))*
    ;