import java.util.ArrayList;
import java.util.*;

public class Lexer {
    private boolean hadError = false;
    final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("IF", TokenType.IF);
        keywords.put("ELSE IF", TokenType.ELSE_IF);
        keywords.put("ELSE", TokenType.ELSE);
        keywords.put("AND", TokenType.AND);
        keywords.put("NOT", TokenType.NOT);
        keywords.put("OR", TokenType.OR);
        keywords.put("DISPLAY", TokenType.DISPLAY);
        keywords.put("SCAN", TokenType.SCAN);
        keywords.put("BEGIN CODE", TokenType.BEGIN);
        keywords.put("END CODE", TokenType.END);
        keywords.put("INT", TokenType.INT);
        keywords.put("BOOL", TokenType.BOOL);
        keywords.put("CHAR", TokenType.CHAR);
        keywords.put("FLOAT", TokenType.FLOAT);
    }
    public Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF,"",null,line));
        //check if BEGIN CODE && END CODE structure is followed
        if(tokens.get(0).type != TokenType.BEGIN){
            error(0, "Missing BEGIN CODE");
            hadError = true;
        }
        if(tokens.get(tokens.size()-3).type != TokenType.END){
            error(line, "Missing END CODE");
            hadError = true;
        }
        if(hadError)
            System.exit(65);
        return tokens;
    }
    private boolean isAtEnd(){
        return current>=source.length();
    }

    public void scanToken(){
        char c = advance();
        switch(c){
            case '(':
                addToken(TokenType.LEFT_PARENTHESIS);
                break;
            case ')':
                addToken(TokenType.RIGHT_PARENTHESIS);
                break;
            case '[':
                addToken(TokenType.LEFT_BRACE);
                break;
            case ']':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                break;
            case ':':
                if(tokens.get(tokens.size()-1).type == TokenType.DISPLAY){
                    break;
                }else if(tokens.get(tokens.size()-1).type != TokenType.SCAN){
                    break;
                }else{
                    error(line, "Wrong use of colon expecting DISPLAY|SCAN");
                    addToken(TokenType.ERROR);
                }
                break;
            case '#'://comment
                while(peek()!='\n' && !isAtEnd())
                    advance();
                advance();
                line++;
                break;
            case '$'://new line
                addToken(TokenType.NEXT_LINE);
                break;
            case '&'://concatenate
                addToken(TokenType.AMPERSAND);
                break;
            case '=': //assignment | equal
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGNMENT);
                break;
            case '<': //less than | not equal <> | less than equal
                if(match('='))
                    addToken(TokenType.LESSER_EQUAL);
                else if(match('>'))
                    addToken(TokenType.NOT_EQUAL);
                else
                    addToken(TokenType.LESSER_THAN);
                break;
            case '>': //greater than | greater than equal
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;
            case '+': //unary or addition
                addToken(TokenType.ADDTION);
                break;
            case '-':
                addToken(TokenType.SUBTRACTION);
                break;
            case '/':
                addToken(TokenType.DIVISION);
                break;
            case '*':
                addToken(TokenType.MULTIPLICATION);
                break;
            case '"': stringCheck();
                break;
            case '\n': //check if on the arraylist before we add this
                if(tokens.size() == 0){
                    error(line, "Line does not have code");
                    addToken(TokenType.ERROR);
                }
                if(tokens.get(tokens.size()-1).type == TokenType.EOL)
                    error(line, "Line doesnt contain any");
                else
                    addToken(TokenType.EOL);
                line++;
                break;
            case '\'':
                if(peekNext() == '\''){
                    advance();
                    advance();
                    addToken(TokenType.CHAR_LIT, source.substring(start,current));

                }else{
                    addToken(TokenType.ERROR);
                    error(line, "Character but it seems like you did not terminate/its a string");
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            default:
                if(isDigit(c))
                    number();
                else if(isAlpha(c))
                    identifier();
                else 
                    error(line, "Unexpected Character");
                break;
        }
    }

    private void identifier() {
        while(isAlphaNumeric(peek()))
            advance();
        String text = source.substring(start,current);
        TokenType type = keywords.get(text);
        if(text.equals("BEGIN") || text.equals("END")){
            if(peek() == ' ' && peekNext() == 'C'){
                advance();
                while(isAlphaNumeric(peek()))
                    advance();
                text = source.substring(start,current);
                type = keywords.get(text);
                if(type == null){
                    addToken(TokenType.ERROR);
                    error(line, "reserved words used as identifiers");
                    return;
                }
                addToken(type);
                return;
            }else{
                addToken(TokenType.ERROR);
                error(line, "Unrecognized reserved words | missing something");
                return;
            }
        }
        if(type == null){
            if(keywords.get(text.toUpperCase()) == null)
                type = TokenType.IDENTIFIER;
            else {
                addToken(TokenType.ERROR);
                error(line, "You used a reserved word");
                return;
            }
        }
        addToken(type);
    }

    private boolean isAlpha(char s) {
        return (s >= 'a' && s <= 'z') ||
                (s >= 'A' && s <= 'Z') ||
                s == '_';
    }

    private boolean isAlphaNumeric(char s){
        return isAlpha(s) || isDigit(s);
    }
    private void number() {
        while(isDigit(peek())){
            advance();
        }
        //Check fractional .
        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek()))
                advance();
            //if(peek() == '\n' || peek() == ',' || peek() == '\0'){
                addToken(TokenType.FLOAT_LIT, Double.parseDouble(source.substring(start,current)));
                return;
            //}//else{
                //addToken(TokenType.ERROR);
                //error(line, "Unidentified character on digit float");
                //return;
            //}
        }
        //Check if there are no comma or unidentified chacter
        //if(peek() == '\n' || peek() == ',' || peek() == '\0' || peek() == '+' || peek() == '-' || peek() == '*' || peek() == '%' | peek() == ')'){
            addToken(TokenType.INT_LIT, Integer.parseInt(source.substring(start,current)));
            //return;
        //}else{
            //addToken(TokenType.ERROR);
            //error(line, "Unidentified character on digit integer");
            //return;
        //}
    }

    private char peekNext() {
        if(current + 1 >= source.length())
            return '\0';
        return source.charAt(current+1);
    }

    private boolean isDigit(char s) {
        return s >= '0' && s <= '9';
    }

    private void stringCheck() {
        //consume all characters after "
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n')
                line++;
            advance();
        }
        //check if the reason for termination is EOF
        if(isAtEnd()){
            error(line, "Unterminated String \"");
            return;
        }
        //end of "
        advance();
        String value = source.substring(start + 1, current - 1);
        if(value.equals("TRUE"))
            addToken(TokenType.BOOL_LIT, value);
        else if(value.equals("FALSE"))
            addToken(TokenType.BOOL_LIT, value);
        else
            addToken(TokenType.STRING_LIT, value);
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }
    private boolean match(char s) {
        if(isAtEnd())
            return false;
        if(source.charAt(current) != s)
            return false;

        current++;
        return true;
    }

    private char advance() {
        current++;
        return source.charAt(current-1);
    }

    private void addToken(TokenType tokenType) {
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, null, line));
    }

    private void addToken(TokenType tokenType, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }
    private void error(int line, String message){
        System.out.println("[Line " + line + "] Error " + message);
        hadError = true;
    }
}
