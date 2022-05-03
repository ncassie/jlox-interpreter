import java.util.List;

abstract class Stmt {
 interface Visitor<R> {
 R visitExpressionStmt(Expression stmt);
 R visitPrintStmt(print stmt);
 }
 static class Expression extends Stmt {
 Expression(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visitExpressionStmt(this);
}

 final Expr expression;
 }
 static class print extends Stmt {
 print(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visitPrintStmt(this);
}

 final Expr expression;
 }

 abstract <R> R accept(Visitor<R> visitor);
}
