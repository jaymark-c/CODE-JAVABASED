public abstract class Expression {

    abstract <R> R accept(Visitor<R> visitor);
    interface Visitor<R> {
        R visitBinaryExpression(Binary expression);
        R visitGroupingExpression(Grouping expression);
        R visitLiteralExpression(Literal expression);
        R visitUnaryExpression(Unary expression);
    }
    static class Binary extends Expression{
        final Expression left;
        final Token operator;
        final Expression right;
        Binary(Expression left, Token operator, Expression right){
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    static class Grouping extends Expression{

        final Expression expression;
        Grouping(Expression expression){
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }

    static class Literal extends Expression{
        final Object value;
        Literal(Object value){
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    static class Unary extends Expression{
        final Token operator;
        final Expression right;
        Unary(Token operator, Expression right){
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }
}
