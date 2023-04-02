import java.util.*;
public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    private Expression expression(){
        return equality();
    }
    private Expression equality() {
        Expression expr = comparison();

        while(match(TokenType.NOT_EQUAL, TokenType.EQUAL)){
            Token operator = previous();
            Expression right = comparison();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }
    private Expression comparison(){
        Expression expr = term();
        while(match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LESSER_THAN, TokenType.LESSER_EQUAL)){
            Token operator = previous();
            Expression right = term();
            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression term() {
        Expression expr = factor();
        while(match(TokenType.SUBTRACTION,TokenType.ADDTION)){
            Token operator = previous();
            Expression right = factor();
            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while(match(TokenType.DIVISION,TokenType.MULTIPLICATION)){
            Token operator = previous();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression unary() {
        if(match(TokenType.NOT, TokenType.SUBTRACTION)){
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        return primary();
    }

    private Expression primary() {
        if(match(TokenType.BOOL_LIT)){
            if(previous().lexeme == "FALSE")
                return new Expression.Literal(false);
            else if(previous().lexeme == "TRUE")
                return new Expression.Literal(true);
        }
        if(match(TokenType.INT_LIT, TokenType.FLOAT_LIT))
            return new Expression.Literal(previous().literal);
        if(match(TokenType.LEFT_PARENTHESIS)){
            Expression expr = expression();
            //consume(TokenType.RIGHT_PARENTHESIS,"Expect ')' after expression");
            return new Expression.Grouping(expr);
        }
        return null;
    }

    /*private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
            throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            re
        System.out.println("Error " + token + " " + message);
        return new ParseError();
    }*/

    private Token previous() {

        return tokens.get(current-1);
    }

    private boolean match(TokenType... types) {
        for(TokenType type:types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if(!isAtEnd())
            current++;
        return previous();
    }

    private boolean check(TokenType type) {
        if(isAtEnd())
            return false;

        return peek().type == type;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

}
