lexer grammar MicroLexer;

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