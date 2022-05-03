# jlox-interpreter
Java implementation of interpreter for lox language

This is my implementation of an interpreter for the lox language
following the book "Crafting Interpreters" by Robert Nystrom.
I am going through this book to deepen my level of understanding of 
compilers and interpreters and to supplement previous course work
on compilers. This version of the interpreter is written in java.

Lox Language Grammar (This will expand as I make my way through the book)
 
program -> statement* EOF ;

statement -> exprStmt | printStmt ;

exprStmt -> expression ";" ;

printStmt -> "print" expression ";" ;

expression -> equality

equality -> comparison( ( "!=" | "==") comparison)*;

comparison => term (( ">" | ">=" | "<" | "<=") term )*;

term -> factor ( ( "-" | "+" ) factor )*;

factor => unary ( ( "/" | "*" ) unary)*;

unary -> ("!" | "-") unary | primary;

primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")";

grouping -> "(" expression ")";

unary -> ( "-" | "!" ) expression;

binary -> expression operator expression;

operator -> "==" | "!=" | "<" | "<=" | ">" | ">="
    | "+" | "-" | "*" | "/";
