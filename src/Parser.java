import java.util.List;
import java.util.ArrayList;

public class Parser{

    private static class ParseError extends RuntimeException{}

    // list of tokens to parse
    // because we are storing tokens in this list, methods don't receive an expression
    // as an argument -- different from some other implementations
    // this also gives more robust access to list of tokens
    private final List<Token> tokens;
    // pointer to token waiting to be parsed
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens=tokens;
    }

    // placeholder for now; will expand
    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }

        return statements;
    }

    // parse statements
    private Stmt statement(){
        if(match(TokenType.PRINT)){
            return printStatement();
        }
        return expressionStatement();
    }

    private Stmt printStatement(){
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ; after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration(){
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        if(match(TokenType.EQUAL)){
            initializer = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ; after value.");
        return new Stmt.Expression(expr);
    }

    // begin parsing of grammar rules for expressions
    // set up in this manner to allow proper associativity and precedence
    // parse expression grammar rule
    private Expr expression(){
        return equality();
    }

    private Stmt declaration(){
        try{
            if(match(TokenType.VAR)){
                return varDeclaration();
            }
            return statement();
        }catch(ParseError error){
            synchronize();
            return null;
        }
    }
    // parse equality grammar rule
    private Expr equality(){
        Expr expr = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // parse comparison grammar rule
    private Expr comparison(){
        Expr expr = term();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // parse term grammar rule
    private Expr term(){
        Expr expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // parse factor grammar rule
    private Expr factor(){
        Expr expr = unary();

        while(match(TokenType.SLASH, TokenType.STAR)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // parse unary grammar rule
    private Expr unary(){
        if(match(TokenType.BANG, TokenType.MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    // parse primary grammar rule
    private Expr primary(){
        if(match(TokenType.FALSE)){
            return new Expr.Literal(false);
        }
        if(match(TokenType.TRUE)){
            return new Expr.Literal(true);
        }
        if(match(TokenType.NIL)){
            return new Expr.Literal(null);
        }
        if(match(TokenType.NUMBER, TokenType.STRING)){
            return new Expr.Literal(previous().literal);
        }

        if(match(TokenType.IDENTIFIER)){
            return new Expr.Variable(previous());
        }

        if(match(TokenType.LEFT_PAREN)){
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        // error checking for improper expression
        throw error(peek(), "Expect expression.");

    }

    // check to see if current token matches any of types in argument list
    // if there is a match, consume current token
    private boolean match(TokenType... types){
        for(TokenType type : types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message){
        if(check(type)){
            return advance();
        }
        throw error(peek(), message);
    }

    // compare current token to given token
    // does not consume current token
    private boolean check(TokenType type){
        if(isAtEnd()){
            return false;
        }
        return peek().type == type;
    }

    // consume current token and return it
    // allows us to move through token list
    private Token advance(){
        if(!isAtEnd()){
            current++;
        }
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == TokenType.EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current-1);
    }

    private ParseError error(Token token, String message){
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if(previous().type == TokenType.SEMICOLON){
                return;
            }
            switch(peek().type){
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;

            }

            advance();
        }
    }
}
