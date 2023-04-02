import javax.xml.namespace.QName;

public class ParserAST implements Expression.Visitor<String>{

    String print(Expression expression){
        return expression.accept(this);
    }
    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group",expression.expression);
    }
    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if(expression.value == null)
            return "null";
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    private String parenthesize(String lexeme, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(lexeme);
        for(Expression exp:expressions){
            builder.append(" ");
            builder.append(exp.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }


}
