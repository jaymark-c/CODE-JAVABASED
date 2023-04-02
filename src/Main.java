import java.io.*;
import java.util.Scanner;
import java.util.*;
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String sourceCode = "";
        Scanner input = new Scanner(System.in);
        //Ask user for file name
        String fileName = input.nextLine();
        input.close();
        try{
            File f = new File("C:\\Users\\JMARK\\Git Repositories\\Programming Language Interpreter\\CODE_FINAL_INTERPRETER\\src\\tests\\"+fileName);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String temp;
            while((temp = br.readLine())!=null)
                sourceCode += temp + '\n';
            br.close();
            sourceCode.trim();
        }catch(FileNotFoundException e){
            System.out.println(e);
            System.exit(65);
        }

        print(sourceCode);
        Lexer lex = new Lexer(sourceCode);
        List<Token> parsed = lex.scanTokens();
        printToken(parsed);
    }
    public static void print(String sourceCode){
        System.out.println("CODE");
        System.out.println("--------------------");
        System.out.println(sourceCode);
        System.out.println("INTERPRETER STARTING");
        System.out.println("--------------------");
    }
    
    public static void printToken(List<Token> tokens){
        for (Token token: tokens) {
            System.out.println(token);
        }
    }

    public static void expressionExample(){
        Expression ex = new Expression.Binary(
                new Expression.Unary(
                        new Token(TokenType.SUBTRACTION,"-", null, 1),new Expression.Literal(123)
                ),
                new Token(TokenType.MULTIPLICATION, "*", null, 1),
                new Expression.Grouping(new Expression.Literal(45.67))
        );
        ParserAST ap = new ParserAST();
        System.out.println(ap.print(ex));
    }
}